package com.bh.cp.user.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.management.relation.InvalidRoleInfoException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.response.PrivilegesResponseDTO;
import com.bh.cp.user.dto.response.RoleResponseDTO;
import com.bh.cp.user.exception.RoleException;
import com.bh.cp.user.pojo.Privileges;
import com.bh.cp.user.pojo.Role;
import com.bh.cp.user.pojo.Roles;
import com.bh.cp.user.service.PrivilegeService;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.service.RoleService;
import com.bh.cp.user.util.CustomHttpServletRequestWrapper;
import com.bh.cp.user.util.JwtUtil;
import com.bh.cp.user.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RoleServiceImpl implements RoleService {

	private final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

	private String allRolesUri;

	private String roleDetailsUri;

	private String clientUri;

	private String clientIdPk;

	private String deleteRoleUri;

	private String baseRoleId;

	private JwtUtil jwtUtil;

	private RestTemplate restTemplate;

	private RestClientWrapperService restClientWrapperService;

	private PrivilegeService privilegeService;

	public RoleServiceImpl(@Autowired JwtUtil jwtUtil, @Autowired RestTemplate restTemplate,
			@Autowired RestClientWrapperService restClientWrapperService, @Autowired PrivilegeService privilegeService,
			@Value("${keycloak.client.uri}") String clientUri,
			@Value("${keycloak.get.all.roles.uri}") String allRolesUri,
			@Value("${keycloak.client-id-pk}") String clientIdPk, @Value("${keycloak.base.role-id}") String baseRoleId,
			@Value("${keycloak.delete.role.uri}") String deleteRoleUri,
			@Value("${keycloak.role.details.uri}") String roleDetailsUri) {
		super();
		this.jwtUtil = jwtUtil;
		this.restTemplate = restTemplate;
		this.restClientWrapperService = restClientWrapperService;
		this.privilegeService = privilegeService;
		this.clientUri = clientUri;
		this.allRolesUri = allRolesUri;
		this.clientIdPk = clientIdPk;
		this.baseRoleId = baseRoleId;
		this.deleteRoleUri = deleteRoleUri;
		this.roleDetailsUri = roleDetailsUri;
	}

	@Override
	@SuppressWarnings("unchecked")
	@CacheEvict(value = "privileges", allEntries = true)
	public Role createRoleAssociatePrivileges(Role role) throws RoleException, JsonProcessingException {
		logger.info("Service: create role associate privilege");
		String token = jwtUtil.generateAdminToken();
		logger.info("Service: Generated token");
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + token);
		Roles roles = new Roles();
		roles.setClientRole(role.isClientRole());
		roles.setId(role.getId());
		roles.setName(role.getName());
		HttpEntity<Roles> entity = new HttpEntity<>(roles, headers);

		SecurityUtil.sanitizeLogging(logger, Level.INFO, "URI=>{}/{}/roles", clientUri, clientIdPk);

		restTemplate.exchange(clientUri + "/" + clientIdPk + "/roles", HttpMethod.POST, entity, Object.class);
		logger.info("Role created");

		String roleId = searchForRoleId(role.getName(), clientIdPk);

		if (roleId == null) {
			logger.error("Service: role not present in keycloak");
			throw new RoleException("Role not present!");
		}
		role.setId(roleId);
		List<LinkedHashMap<String, String>> rolesMap = null;
		LinkedHashMap<String, String> newRole = null;
		Map<?, ?> response = null;
		ObjectMapper mapper = null;
		JsonNode actualObj = null;
		Privileges policy = null;
		LinkedHashMap<String, String> roleLinkedList = null;
		for (String privilegeName : role.getPrivileages()) {
			response = searchPrivilegeByName(privilegeName);
			roleLinkedList = ((LinkedHashMap<String, String>) response.get("config"));
			mapper = new ObjectMapper();
			actualObj = mapper.readValue(roleLinkedList.toString().substring(7), JsonNode.class);
			rolesMap = new ArrayList<>();
			for (JsonNode node : actualObj) {
				newRole = new LinkedHashMap<>();
				newRole.put(UMSConstants.ID, node.get(UMSConstants.ID).toString().substring(1,
						node.get(UMSConstants.ID).toString().length() - 1));
				rolesMap.add(newRole);
			}
			newRole = new LinkedHashMap<>();
			newRole.put(UMSConstants.ID, roleId);
			rolesMap.add(newRole);
			String policyId = (String) response.get(UMSConstants.ID);
			policy = new Privileges();
			policy.setDecisionStrategy("UNANIMOUS");
			policy.setId(policyId);
			policy.setLogic("POSITIVE");
			policy.setName(privilegeName);
			policy.setType("role");
			policy.setDescription((String)response.get(UMSConstants.DESCRIPTION));
			policy.setRoles(rolesMap);
			token = jwtUtil.generateAdminToken();
			String privilegesRole = clientUri + "/" + clientIdPk + "/authz/resource-server/policy/role/" + policyId;
			HttpHeaders headersPut = new HttpHeaders();
			headersPut.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + token);

			HttpEntity<Privileges> entityPut = new HttpEntity<>(policy, headersPut);
			restTemplate.exchange(privilegesRole, HttpMethod.PUT, entityPut, Object.class);
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "Role id:{} Associated with:{}", roleId, privilegeName);
		}
		role.setId(roleId);
		return role;
	}

	private String searchForRoleId(String roleName, String clientIdPk) {
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "search for role id:{}", roleName);
		String token = jwtUtil.generateAdminToken();

		String roleUriGet = clientUri + "/" + clientIdPk + "/roles/" + roleName;
		HttpHeaders headersRoleGet = new HttpHeaders();
		headersRoleGet.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + token);
		HttpEntity<Object> entityRoleGet = new HttpEntity<>(headersRoleGet);
		@SuppressWarnings("rawtypes")
		ResponseEntity<LinkedHashMap> responseRole = restTemplate.exchange(roleUriGet, HttpMethod.GET, entityRoleGet,
				LinkedHashMap.class);

		@SuppressWarnings("unchecked")
		Object id = Optional.ofNullable(responseRole.getBody()).orElse(new LinkedHashMap<>())
				.getOrDefault(UMSConstants.ID, null);
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "search for role id response:{}", id);
		return id != null ? id.toString() : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	@CacheEvict(value = "privileges", allEntries = true)
	public ResponseEntity<Object> updateRoleAssociatePrivileges(Role role)
			throws RoleException, JsonProcessingException {
		logger.info("update Role Associate Privileges");
		String token = jwtUtil.generateAdminToken();
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + token);
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String getAllPoliciesUri = clientUri + "/" + clientIdPk + "/authz/resource-server/policy";

		@SuppressWarnings("rawtypes")
		ResponseEntity<ArrayList> response = restTemplate.exchange(getAllPoliciesUri, HttpMethod.GET, httpEntity,
				ArrayList.class);
		logger.info("Get all Privileges");
		boolean roleBool = false;
		boolean privilegeBool = false;
		String privilegeName = null;
		String privilegeId = null;
		String description = null;
		LinkedHashMap<?, ?> policyLinkedListObj = null;
		LinkedHashMap<String, String> roleLinkedList = null;
		for (Object data : response.getBody()) {
			policyLinkedListObj = (LinkedHashMap<?, ?>) data;
			roleLinkedList = ((LinkedHashMap<String, String>) policyLinkedListObj);
			if (roleLinkedList.containsKey(UMSConstants.CONFIG)) {
				roleLinkedList = (LinkedHashMap<String, String>) policyLinkedListObj.get(UMSConstants.CONFIG);
				if (!roleLinkedList.isEmpty() && roleLinkedList.containsKey(UMSConstants.ROLES)) {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode actualObj = mapper.readValue(roleLinkedList.toString().substring(7), JsonNode.class);
					description = (String) policyLinkedListObj.get(UMSConstants.DESCRIPTION);
					privilegeName = policyLinkedListObj.get(UMSConstants.NAME).toString();
					privilegeId = policyLinkedListObj.get(UMSConstants.ID).toString();

					roleBool = evaluateRole(role, actualObj);
					if (role.getPrivileages().contains(privilegeName)) {
						privilegeBool = true;
					} else {
						privilegeBool = false;
					}
					saveOrUpdateRoleAndPrivilege(roleBool, privilegeBool, role, privilegeName, roleLinkedList,
							privilegeId, description);
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	private boolean evaluateRole(Role role, JsonNode actualObj) {
		String responseRoleId = null;
		boolean roleBool = false;
		for (JsonNode node : actualObj) {
			responseRoleId = node.get(UMSConstants.ID).toString().substring(1,
					node.get(UMSConstants.ID).toString().length() - 1);
			if (responseRoleId.equals(role.getId())) {
				roleBool = true;
				break;
			} else {
				roleBool = false;
			}
		}
		return roleBool;
	}

	private void saveOrUpdateRoleAndPrivilege(boolean roleBool, boolean privilegeBool, Role role, String privilegeName,
			LinkedHashMap<String, String> roleLinkedList, String privilegeId, String description)
			throws JsonProcessingException {
		if (!roleBool && privilegeBool) {
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "New association role {} with privilege {}",
					role.getName(), privilegeName);
			getRoleAndPrivileges(privilegeId, roleLinkedList, role, privilegeName, UMSConstants.NEW_ROLE_TO_PRIVILEGE,
					clientIdPk, description);
		} else if (roleBool && !privilegeBool) {
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "Existing association role {} with remove privilege {}",
					role.getName(), privilegeName);
			getRoleAndPrivileges(privilegeId, roleLinkedList, role, privilegeName,
					UMSConstants.EXISTING_ROLE_TO_PRIVILEGE, clientIdPk, description);
		} else if (roleBool) {
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "Existing association role {} with Existing privilege {}",
					role.getName(), privilegeName);
		}
	}

	private void getRoleAndPrivileges(String policyId, LinkedHashMap<String, String> roleLinkedList, Role role,
			String privilegeName, String associateRole, String clientIdPk, String description)
			throws JsonProcessingException {
		logger.info("Associate role to respective privilege");
		String token = jwtUtil.generateAdminToken();
		Privileges policy = null;
		List<LinkedHashMap<String, String>> rolesMap = new ArrayList<>();
		LinkedHashMap<String, String> newRole = null;
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readValue(roleLinkedList.toString().substring(7), JsonNode.class);
		for (JsonNode node : actualObj) {
			newRole = new LinkedHashMap<>();
			if (associateRole.equals(UMSConstants.EXISTING_ROLE_TO_PRIVILEGE) && (node.get(UMSConstants.ID).toString()
					.substring(1, node.get(UMSConstants.ID).toString().length() - 1).equals(role.getId()))) {
				continue;
			}
			newRole.put(UMSConstants.ID, node.get(UMSConstants.ID).toString().substring(1,
					node.get(UMSConstants.ID).toString().length() - 1));
			rolesMap.add(newRole);
		}
		if (associateRole.equals(UMSConstants.NEW_ROLE_TO_PRIVILEGE)) {
			newRole = new LinkedHashMap<>();
			newRole.put(UMSConstants.ID, role.getId());
			rolesMap.add(newRole);
		}
		policy = new Privileges();
		policy.setDecisionStrategy(UMSConstants.UNANIMOUS);
		policy.setId(policyId);
		policy.setLogic(UMSConstants.POSITIVE);
		policy.setName(privilegeName);
		policy.setType(UMSConstants.ROLE);
		policy.setDescription(description);
		policy.setRoles(rolesMap);
		HttpHeaders headersPut = new HttpHeaders();
		headersPut.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + token);
		HttpEntity<Privileges> entityPut = new HttpEntity<>(policy, headersPut);
		restTemplate.exchange(clientUri + "/" + clientIdPk + "/authz/resource-server/policy/role/" + policyId,
				HttpMethod.PUT, entityPut, Object.class);
	}

	@Override
	public List<RoleResponseDTO> getAllRoles(HttpServletRequest httpServletRequest) throws JsonProcessingException {

		CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
				httpServletRequest, jwtUtil);
		ResponseEntity<String> rolesResponse = restClientWrapperService.getResponseFromUrl(modifiedHttpServletRequest,
				allRolesUri);
		JSONArray roles = new JSONArray(rolesResponse.getBody());
		List<RoleResponseDTO> roleList = new ArrayList<>();
		JSONObject role = null;
		String roleId = null;
		for (Object roleObj : roles) {
			role = (JSONObject) roleObj;
			roleId = (String) role.get(UMSConstants.ID);
			if (roleId.equals(baseRoleId)) {
				roleList.add(new RoleResponseDTO(roleId, (String) role.get(UMSConstants.NAME), false));
				continue;
			}
			roleList.add(new RoleResponseDTO(roleId, (String) role.get(UMSConstants.NAME)));
		}
		Collections.sort(roleList, Comparator.comparing(RoleResponseDTO::isEditable));
		return roleList;
	}

	@Override
	public RoleResponseDTO getPrivilegesForRole(HttpServletRequest httpServletRequest, String roleId) {

		CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
				httpServletRequest, jwtUtil);
		ResponseEntity<String> roleResponse = restClientWrapperService.getResponseFromUrl(modifiedHttpServletRequest,
				SecurityUtil.sanitizeUrl(roleDetailsUri, Map.of(UMSConstants.ROLE_ID_PLACEHOLDER, roleId)));
		String policyResponse = privilegeService.getAllPrivileges(modifiedHttpServletRequest);

		RoleResponseDTO outputDto = new RoleResponseDTO();
		outputDto.setId(roleId);
		outputDto.setName((String) new JSONObject(roleResponse.getBody()).get(UMSConstants.NAME));

		Set<PrivilegesResponseDTO> privileges = new HashSet<>();
		JSONArray policies = new JSONArray(policyResponse);
		JSONObject policy = null;
		JSONObject config = null;
		String roles = null;
		for (Object policyObj : policies) {
			policy = (JSONObject) policyObj;
			if (policy.has(UMSConstants.CONFIG)) {
				config = (JSONObject) policy.get(UMSConstants.CONFIG);
				roles = config.has(UMSConstants.ROLES) ? (String) config.get(UMSConstants.ROLES) : "";
				if (!roles.contains(roleId)) {
					continue;
				}
				privileges.add(new PrivilegesResponseDTO((String) policy.get(UMSConstants.ID),
						(String) policy.get(UMSConstants.NAME)));
			}

		}

		outputDto.setPrivileges(privileges);
		return outputDto;
	}

	@Override
	@CacheEvict(value = "privileges", allEntries = true)
	public ResponseEntity<Object> deleteRole(String roleId) throws InvalidRoleInfoException, RoleException {
		logger.info("Delete client Role");
		if (baseRoleId.equals(roleId)) {
			logger.error("Base role cannot be deleted!");
			throw new RoleException("Base role cannot be deleted!");
		}
		String token = jwtUtil.generateAdminToken();
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + token);
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "URI=> {}{}", deleteRoleUri, roleId);
		restTemplate.exchange(deleteRoleUri + roleId, HttpMethod.DELETE, httpEntity, Object.class);
		logger.info("Role deleted!");
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map searchPrivilegeByName(String privilegeName) throws RoleException {
		String token = jwtUtil.generateAdminToken();
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + token);
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String getAllPoliciesUri = clientUri + "/" + clientIdPk + "/authz/resource-server/policy";

		ResponseEntity<ArrayList> response = restTemplate.exchange(getAllPoliciesUri, HttpMethod.GET, httpEntity,
				ArrayList.class);
		logger.info("Get all Privileges");
		LinkedHashMap<?, ?> policyLinkedListObj = null;
		LinkedHashMap<String, String> roleLinkedList = null;
		for (Object data : response.getBody()) {
			policyLinkedListObj = (LinkedHashMap) data;
			roleLinkedList = ((LinkedHashMap<String, String>) policyLinkedListObj);
			if (roleLinkedList.get("name").equals(privilegeName)) {
				return policyLinkedListObj;
			}
		}
		throw new RoleException("Privilege not found");
	}

}
