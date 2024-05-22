package com.bh.cp.user.service.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.body.AttributeBody;
import com.bh.cp.user.dto.body.IdBody;
import com.bh.cp.user.dto.body.RoleNameBody;
import com.bh.cp.user.dto.body.RoleOpBody;
import com.bh.cp.user.dto.request.CreateGroupRequestDTO;
import com.bh.cp.user.dto.request.DeleteGroupRequestDTO;
import com.bh.cp.user.dto.request.EditGroupRequestDTO;
import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.dto.response.GroupResponseDTO;
import com.bh.cp.user.dto.response.PrivilegesResponseDTO;
import com.bh.cp.user.dto.response.RoleResponseDTO;
import com.bh.cp.user.dto.response.SelectedResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.exception.DeletionNotPermissableException;
import com.bh.cp.user.service.DomainService;
import com.bh.cp.user.service.GroupService;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.service.RoleService;
import com.bh.cp.user.service.UserService;
import com.bh.cp.user.util.CustomHttpServletRequestWrapper;
import com.bh.cp.user.util.JwtUtil;
import com.bh.cp.user.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class GroupServiceImpl implements GroupService {

	private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

	private final Random random = new SecureRandom();

	private String getAllGroupsUri;

	private String groupDetailsUri;

	private String createGroupUri;

	private String updateGroupUri;

	private String deleteGroupUri;

	private String domainsMasterGroupId;

	private String clientIdPk;

	private String groupRoleMappingUri;

	private RestClientWrapperService restClientWrapperService;

	private RoleService roleService;

	private UserService userService;

	private DomainService domainService;

	private JwtUtil jwtUtil;

	public GroupServiceImpl(@Autowired JwtUtil jwtUtil, @Autowired RoleService roleService,
			@Lazy @Autowired UserService userService, @Lazy @Autowired DomainService domainService,
			@Autowired RestClientWrapperService restClientWrapperService,
			@Value("${keycloak.group.role.mapping.uri}") String groupRoleMappingUri,
			@Value("${keycloak.domains.master.group-id}") String domainsMasterGroupId,
			@Value("${sparq.ums.get.all.groups.uri}") String getAllGroupsUri,
			@Value("${sparq.ums.group.details.uri}") String groupDetailsUri,
			@Value("${sparq.ums.create.group.uri}") String createGroupUri,
			@Value("${sparq.ums.update.group.uri}") String updateGroupUri,
			@Value("${sparq.ums.delete.group.uri}") String deleteGroupUri,
			@Value("${keycloak.client-id-pk}") String clientIdPk) {
		super();
		this.jwtUtil = jwtUtil;
		this.roleService = roleService;
		this.userService = userService;
		this.restClientWrapperService = restClientWrapperService;
		this.domainService = domainService;
		this.groupRoleMappingUri = groupRoleMappingUri;
		this.domainsMasterGroupId = domainsMasterGroupId;
		this.getAllGroupsUri = getAllGroupsUri;
		this.groupDetailsUri = groupDetailsUri;
		this.createGroupUri = createGroupUri;
		this.updateGroupUri = updateGroupUri;
		this.deleteGroupUri = deleteGroupUri;
		this.clientIdPk = clientIdPk;
	}

	@Override
	public List<GroupResponseDTO> getAllGroups(HttpServletRequest httpServletRequest) {
		List<GroupResponseDTO> groupList = new ArrayList<>();
		JSONObject groupObject = null;
		ResponseEntity<String> groupsResponse = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				getAllGroupsUri);
		JSONArray groupsArray = new JSONArray(
				new JSONObject(groupsResponse.getBody()).get(UMSConstants.GROUPS).toString());
		String groupId = null;
		for (Object obj : groupsArray) {
			groupObject = (JSONObject) (obj);
			groupId = (String) groupObject.get(UMSConstants.ID);
			if (groupId.equals(domainsMasterGroupId)) {
				continue;
			}
			groupList.add(new GroupResponseDTO(groupId, (String) groupObject.get(UMSConstants.DISPLAYNAME)));
		}
		return groupList;
	}

	@Override
	public GroupResponseDTO getGroupDetails(HttpServletRequest httpServletRequest, String groupId)
			throws InterruptedException, ExecutionException {
		ResponseEntity<String> response = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				SecurityUtil.sanitizeUrl(groupDetailsUri, Map.of(UMSConstants.GROUP_ID_PLACEHOLDER, groupId)));
		JSONObject responseObject = new JSONObject(response.getBody());
		JSONArray metaDataArray = responseObject.getJSONArray(UMSConstants.METADATA);
		JSONArray usersArray = responseObject.getJSONArray(UMSConstants.MEMBERS);
		GroupResponseDTO groupResponseDTO = new GroupResponseDTO();
		List<RoleResponseDTO> rolesList = new ArrayList<>();
		List<UserDetailsResponseDTO> usersList = new ArrayList<>();
		Set<PrivilegesResponseDTO> privilegeList = new HashSet<>();
		List<DomainResponseDTO> domainsList = new ArrayList<>();
		JSONObject role = null;
		JSONObject user = null;
		JSONObject domain = null;
		String roleId = null;

		groupResponseDTO.setAttributes(metaDataArray.toList().stream()
				.map(attributeObj -> new ObjectMapper().convertValue(attributeObj, AttributeBody.class)).toList());

		CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
				httpServletRequest, jwtUtil);
		ResponseEntity<String> roleResponse = restClientWrapperService.getResponseFromUrl(modifiedHttpServletRequest,
				SecurityUtil.sanitizeUrl(groupRoleMappingUri, Map.of(UMSConstants.GROUP_ID_PLACEHOLDER, groupId)));

		JSONArray rolesArray = new JSONArray(roleResponse.getBody());
		RoleResponseDTO roleResponseDto = null;
		for (Object roleObj : rolesArray) {
			role = (JSONObject) roleObj;
			roleId = (String) role.get(UMSConstants.ID);
			roleResponseDto = runRoleAPIAsync(httpServletRequest, roleId);
			rolesList.add(
					new RoleResponseDTO(roleId, (String) role.get(UMSConstants.NAME), roleResponseDto.getPrivileges()));
			privilegeList.addAll(roleResponseDto.getPrivileges());
		}

		for (Object userObj : usersArray) {
			user = (JSONObject) userObj;
			if (user.has(UMSConstants.TYPE) && user.getString(UMSConstants.TYPE).equals("USER")) {
				usersList.add(new UserDetailsResponseDTO((String) user.get(UMSConstants.ID),
						(String) user.get(UMSConstants.NAME), (String) user.get(UMSConstants.EMAIL)));
			}
		}

		Set<String> valuesList = new HashSet<>();
		for (Object domainObj : metaDataArray) {
			domain = (JSONObject) domainObj;
			if ((domain.get(UMSConstants.KEY).toString()).contains(UMSConstants.ASSOCIATED_DOMAINS)) {
				valuesList.addAll(Arrays.asList(domain.get(UMSConstants.VALUE).toString().split(",")).stream()
						.map(entry -> entry.replaceAll("[\\W&&[^\\-]]", "")).toList());
			}
		}

		Map<String, DomainResponseDTO> domainIdMap = domainService.getDomainIdMap(httpServletRequest);
		for (String domainId : valuesList) {
			try {
				if (domainIdMap.containsKey(domainId)) {
					domainsList.add(domainIdMap.get(domainId));
				}

			} catch (RestClientException restClientException) {
				SecurityUtil.sanitizeLogging(logger, Level.INFO,
						"Error fetching Domains while getting group details for {}", groupId);
			}
		}
		groupResponseDTO.setId(groupId);
		groupResponseDTO.setName((String) responseObject.get(UMSConstants.DISPLAYNAME));
		groupResponseDTO.setRoles(rolesList);
		groupResponseDTO.setUsers(usersList);
		groupResponseDTO.setDomains(domainsList);
		groupResponseDTO.setPrivileges(privilegeList);
		return groupResponseDTO;
	}

	private RoleResponseDTO runRoleAPIAsync(HttpServletRequest httpServletRequest, String roleId)
			throws InterruptedException, ExecutionException {

		final String id = roleId;
		CompletableFuture<RoleResponseDTO> roleFuture = CompletableFuture
				.supplyAsync(() -> roleService.getPrivilegesForRole(httpServletRequest, id));

		return roleFuture.get();
	}

	@Override
	public String createGroup(HttpServletRequest httpServletRequest, CreateGroupRequestDTO requestDto)
			throws JsonProcessingException {

		requestDto.setBulkId(String.valueOf(random.nextInt(100)));

		if (requestDto.getAttributes() == null) {
			requestDto.setAttributes(new ArrayList<>());
		}

		requestDto.getAttributes().add(new AttributeBody(UMSConstants.ASSOCIATED_DOMAINS,
				requestDto.getDomains().stream().map(IdBody::getId).toList()));

		String jsonBody = SecurityUtil.convertFieldNameInJsonArrayBody(requestDto, UMSConstants.ATTRIBUTES,
				UMSConstants.METADATA);
		ResponseEntity<String> groupCreatedResponse = restClientWrapperService.postBodyToUrl(httpServletRequest,
				createGroupUri, jsonBody);

		List<RoleNameBody> rolesRequest = requestDto.getRoles();
		JSONArray responseArray = rolesToGroupAssociation(httpServletRequest, rolesRequest, groupCreatedResponse,
				UMSConstants.ADD);
		return responseArray.toString();
	}

	private JSONArray rolesToGroupAssociation(HttpServletRequest httpServletRequest,
			List<? extends RoleNameBody> rolesRequest, ResponseEntity<String> groupResponse, String operationToPerform)
			throws JsonProcessingException {
		JSONArray responseArray = new JSONArray(groupResponse.getBody());
		JSONObject responseObject = responseArray.getJSONObject(0);
		if (responseObject.has(UMSConstants.ID)) {
			String groupId = (String) responseObject.get(UMSConstants.ID);
			List<RoleRepresentation> roleRepresentationList = rolesRequest.stream().map(role -> {
				RoleRepresentation roleRepresentation = new RoleRepresentation();
				roleRepresentation.setId(role.getId());
				roleRepresentation.setName(role.getName());
				roleRepresentation.setContainerId(clientIdPk);
				roleRepresentation.setClientRole(true);
				roleRepresentation.setComposite(false);
				return roleRepresentation;
			}).toList();

			String jsonBody = new ObjectMapper().writeValueAsString(roleRepresentationList);
			CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
					httpServletRequest, jwtUtil);
			if (operationToPerform.equals(UMSConstants.ADD)) {
				restClientWrapperService.postBodyToUrl(modifiedHttpServletRequest, SecurityUtil.sanitizeUrl(
						groupRoleMappingUri, Map.of(UMSConstants.GROUP_ID_PLACEHOLDER, groupId)), jsonBody);
			} else if (operationToPerform.equals(UMSConstants.REMOVE)) {
				restClientWrapperService.deleteBodyToUrl(modifiedHttpServletRequest, SecurityUtil.sanitizeUrl(
						groupRoleMappingUri, Map.of(UMSConstants.GROUP_ID_PLACEHOLDER, groupId)), jsonBody);
			}

			if (responseObject.has(UMSConstants.ROLES)) {
				responseObject.remove(UMSConstants.ROLES);
			}
		}
		return responseArray;
	}

	@Override
	public String editGroup(HttpServletRequest httpServletRequest, EditGroupRequestDTO requestDto)
			throws JsonProcessingException, InterruptedException, ExecutionException {

		GroupResponseDTO fetchGroupResponse = getGroupDetails(httpServletRequest, requestDto.getId());
		if (requestDto.getDomains() != null) {
			setDomainsAttributeToRequestDTO(httpServletRequest, requestDto, fetchGroupResponse);
		}

		if (requestDto.getAttributes() != null && fetchGroupResponse.getAttributes() != null) {
			Set<String> newKeys = requestDto.getAttributes().stream().map(AttributeBody::getKey)
					.collect(Collectors.toSet());
			requestDto.getAttributes().addAll(fetchGroupResponse.getAttributes().stream()
					.filter(attribute -> !newKeys.contains(attribute.getKey())).toList());
		}

		String jsonBody = SecurityUtil.convertFieldNameInJsonArrayBody(requestDto, UMSConstants.ATTRIBUTES,
				UMSConstants.METADATA);
		ResponseEntity<String> groupEditedResponse = restClientWrapperService.putBodyToUrl(httpServletRequest,
				updateGroupUri, jsonBody);

		JSONArray responseArray = null;
		if (requestDto.getRoles() != null && !requestDto.getRoles().isEmpty()) {
			List<RoleOpBody> addRolesRequest = requestDto.getRoles().stream()
					.filter(role -> role.getOp().equals(UMSConstants.ADD)).toList();
			if (!addRolesRequest.isEmpty()) {
				responseArray = rolesToGroupAssociation(httpServletRequest, addRolesRequest, groupEditedResponse,
						UMSConstants.ADD);
			}

			List<RoleOpBody> deleteRolesRequest = requestDto.getRoles().stream()
					.filter(role -> role.getOp().equals(UMSConstants.REMOVE)).toList();
			if (!deleteRolesRequest.isEmpty()) {
				responseArray = rolesToGroupAssociation(httpServletRequest, deleteRolesRequest, groupEditedResponse,
						UMSConstants.REMOVE);

			}
		}

		return responseArray != null ? responseArray.toString() : groupEditedResponse.getBody();
	}

	private void setDomainsAttributeToRequestDTO(HttpServletRequest httpServletRequest, EditGroupRequestDTO requestDto,
			GroupResponseDTO fetchGroupResponse) {

		if (requestDto.getAttributes() == null) {
			requestDto.setAttributes(new ArrayList<>());
		}

		Set<String> existingDomains = new HashSet<>();
		if (fetchGroupResponse.getAttributes() != null) {
			Optional<AttributeBody> domainAttribute = fetchGroupResponse.getAttributes().stream()
					.filter(attribute -> attribute.getKey().equals(UMSConstants.ASSOCIATED_DOMAINS)).findFirst();
			if (domainAttribute.isPresent()) {
				domainAttribute.get().getValue().stream().forEach(domainId -> {
					try {
						domainService.getDomain(domainId, httpServletRequest);
						existingDomains.add(domainId);
					} catch (Exception e) {
						SecurityUtil.sanitizeLogging(logger, Level.INFO, "Obselete Domain Id {} Removed from Group {}",
								domainId, requestDto.getId());
					}
				});
			}
		}

		requestDto.getDomains().stream().forEach(domain -> {
			if (domain.getOp().equals(UMSConstants.ADD)) {
				existingDomains.add(domain.getId());
			} else if (domain.getOp().equals(UMSConstants.REMOVE)) {
				existingDomains.remove(domain.getId());
			}
		});

		requestDto.getAttributes()
				.add(new AttributeBody(UMSConstants.ASSOCIATED_DOMAINS, new ArrayList<>(existingDomains)));
	}

	@Override
	public List<SelectedResponseDTO> getUserDetailsFromGroup(HttpServletRequest httpServletRequest, String groupId)
			throws JsonProcessingException, InterruptedException, ExecutionException {
		GroupResponseDTO fetchGroupResponse = getGroupDetails(httpServletRequest, groupId);
		List<UserDetailsResponseDTO> usersResponse = userService.getAllUsers(httpServletRequest);
		List<String> selectedUsers = fetchGroupResponse.getUsers().stream().map(UserDetailsResponseDTO::getId).toList();

		return usersResponse.stream()
				.map(user -> new SelectedResponseDTO(user.getId(), user.getName(), user.getEmail(),
						selectedUsers.contains(user.getId()), true))
				.sorted(Comparator.comparing(SelectedResponseDTO::isSelected).reversed()).toList();
	}

	@Override
	public List<SelectedResponseDTO> getRoleDetailsFromGroup(HttpServletRequest httpServletRequest, String groupId)
			throws JsonProcessingException, InterruptedException, ExecutionException {
		GroupResponseDTO fetchGroupResponse = getGroupDetails(httpServletRequest, groupId);
		List<RoleResponseDTO> rolesResponse = roleService.getAllRoles(httpServletRequest);
		List<String> selectedRoles = fetchGroupResponse.getRoles().stream().map(RoleResponseDTO::getId).toList();

		return rolesResponse
				.stream().map(role -> new SelectedResponseDTO(role.getId(), role.getName(),
						selectedRoles.contains(role.getId()), true))
				.sorted(Comparator.comparing(SelectedResponseDTO::isSelected).reversed()).toList();
	}

	@Override
	public List<SelectedResponseDTO> getDomainDetailsFromGroup(HttpServletRequest httpServletRequest, String groupId)
			throws JsonProcessingException, InterruptedException, ExecutionException {
		GroupResponseDTO fetchGroupResponse = getGroupDetails(httpServletRequest, groupId);
		List<DomainResponseDTO> domainsResponse = domainService.getAllDomains(httpServletRequest);
		List<String> selectedDomains = fetchGroupResponse.getDomains().stream().map(DomainResponseDTO::getId).toList();

		return domainsResponse.stream()
				.map(domain -> new SelectedResponseDTO(domain.getId(), domain.getName(),
						selectedDomains.contains(domain.getId()), true))
				.sorted(Comparator.comparing(SelectedResponseDTO::isSelected).reversed()).toList();
	}

	@Override
	public String deleteGroup(HttpServletRequest httpServletRequest, DeleteGroupRequestDTO requestDto)
			throws JsonProcessingException, DeletionNotPermissableException, InterruptedException, ExecutionException {

		GroupResponseDTO fetchGroupResponse = getGroupDetails(httpServletRequest, requestDto.getId());

		if (!fetchGroupResponse.getUsers().isEmpty()) {
			throw new DeletionNotPermissableException("Group associated with Users can't be deleted");
		}

		String jsonBody = new ObjectMapper().writeValueAsString(Arrays.asList(requestDto));
		ResponseEntity<String> groupDeletedResponse = restClientWrapperService.deleteBodyToUrl(httpServletRequest,
				deleteGroupUri, jsonBody);
		return groupDeletedResponse.getBody();
	}

}
