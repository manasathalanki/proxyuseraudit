package com.bh.cp.user.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.body.AttributeBody;
import com.bh.cp.user.dto.body.OpBody;
import com.bh.cp.user.dto.body.RoleNameBody;
import com.bh.cp.user.dto.body.RoleOpBody;
import com.bh.cp.user.dto.request.CreateUserRequestDTO;
import com.bh.cp.user.dto.request.DeleteUserRequestDTO;
import com.bh.cp.user.dto.request.EditUserRequestDTO;
import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.dto.response.GroupResponseDTO;
import com.bh.cp.user.dto.response.PrivilegesResponseDTO;
import com.bh.cp.user.dto.response.RoleResponseDTO;
import com.bh.cp.user.dto.response.SelectedResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.exception.AttributeNotFoundException;
import com.bh.cp.user.exception.UserNotCreatedException;
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
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private String getAllUsersUri;

	private String userDetailsUri;

	private String createUserUri;

	private String editUserUri;

	private String userRoleMappingUri;

	private final String userGroupMappingUri;

	private String clientIdPk;

	private final String baseRoleId;

	private RestClientWrapperService restClientWrapperService;

	private RoleService roleService;

	private DomainService domainService;

	private GroupService groupService;

	private JwtUtil jwtUtil;

	public UserServiceImpl(@Autowired JwtUtil jwtUtil, @Autowired GroupService groupService,
			@Autowired DomainService domainService, @Autowired RoleService roleService,
			@Autowired RestClientWrapperService restClientWrapperService,
			@Value("${keycloak.base.role-id}") String baseRoleId,
			@Value("${keycloak.user.group.mapping.uri}") String userGroupMappingUri,
			@Value("${sparq.ums.user.details.uri}") String userDetailsUri,
			@Value("${sparq.ums.get.all.users.uri}") String getAllUsersUri,
			@Value("${keycloak.user.role.mapping.uri}") String userRoleMappingUri,
			@Value("${keycloak.create.user.uri}") String createUserUri,
			@Value("${sparq.ums.edit.user.uri}") String editUserUri,
			@Value("${keycloak.client-id-pk}") String clientIdPk) {
		super();
		this.jwtUtil = jwtUtil;
		this.groupService = groupService;
		this.domainService = domainService;
		this.roleService = roleService;
		this.restClientWrapperService = restClientWrapperService;
		this.baseRoleId = baseRoleId;
		this.userGroupMappingUri = userGroupMappingUri;
		this.userDetailsUri = userDetailsUri;
		this.getAllUsersUri = getAllUsersUri;
		this.userRoleMappingUri = userRoleMappingUri;
		this.createUserUri = createUserUri;
		this.editUserUri = editUserUri;
		this.clientIdPk = clientIdPk;
	}

	private void setDetailsToUserResponse(JSONObject userData, UserDetailsResponseDTO userResponseDTO) {
		JSONArray attributes;
		JSONObject attribute;
		userResponseDTO.setId(userData.getString(UMSConstants.ID));
		userResponseDTO.setUserName(userData.getString(UMSConstants.USERNAME));
		userResponseDTO.setEmail(userData.getString(UMSConstants.EMAIL));
		userResponseDTO.setEnabled(userData.getBoolean(UMSConstants.ACTIVE) ? "Y" : "N");
		userResponseDTO.setName(userData.getJSONObject(UMSConstants.NAME).getString(UMSConstants.FIRSTNAME));
		userResponseDTO.setSurName(userData.getJSONObject(UMSConstants.NAME).getString(UMSConstants.LASTNAME));
		if (userData.has(UMSConstants.ATTRIBUTES)) {
			attributes = userData.getJSONArray(UMSConstants.ATTRIBUTES);
			for (Object attributeObj : attributes) {
				attribute = (JSONObject) attributeObj;
				if (UMSConstants.TITLE.equals(attribute.get(UMSConstants.KEY))) {
					for (Object str : (JSONArray) attribute.get(UMSConstants.VALUE)) {
						userResponseDTO.setTitle((String) str);
					}
				}
			}
		}
	}

	private void setAttributeToUserResponse(JSONObject userObj, UserDetailsResponseDTO userResponseDTO)
			throws AttributeNotFoundException {
		try {
			JSONArray metaDataArray = userObj.getJSONArray(UMSConstants.ATTRIBUTES);
			userResponseDTO.setAttributes(metaDataArray.toList().stream()
					.map(attributeObj -> new ObjectMapper().convertValue(attributeObj, AttributeBody.class)).toList());
		} catch (Exception e) {
			throw new AttributeNotFoundException(
					"Internal/External Attribute is missing.Please try again by adding attributes in Keycloak");
		}
	}

	@Override
	public List<UserDetailsResponseDTO> getAllUsers(HttpServletRequest httpServletRequest) {
		ResponseEntity<String> usersResponse = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				getAllUsersUri);
		JSONObject jsonObject = new JSONObject(usersResponse.getBody());
		JSONArray usersArray = jsonObject.getJSONArray(UMSConstants.USERS);
		List<UserDetailsResponseDTO> allUsers = new ArrayList<>();
		for (Object userObj : usersArray) {
			if (userObj instanceof JSONObject userData) {
				UserDetailsResponseDTO userResponseDTO = new UserDetailsResponseDTO();
				setDetailsToUserResponse(userData, userResponseDTO);
				allUsers.add(userResponseDTO);
			}
		}
		return allUsers;
	}

	@Override
	public UserDetailsResponseDTO getUsersCombinedDetails(HttpServletRequest httpServletRequest, String userId)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException {
		ResponseEntity<String> userResponse = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				SecurityUtil.sanitizeUrl(userDetailsUri, Map.of(UMSConstants.USER_ID_PLACEHOLDER, userId)));
		JSONObject userObj = new JSONObject(userResponse.getBody());
		JSONObject role = null;
		JSONObject group = null;
		String groupId = null;
		String roleId = null;
		GroupResponseDTO groupResponse = null;
		List<RoleResponseDTO> rolesList = new ArrayList<>();
		List<GroupResponseDTO> groupList = new ArrayList<>();
		List<DomainResponseDTO> domainsList = new ArrayList<>();
		Set<PrivilegesResponseDTO> privilegeSet = new HashSet<>();
		UserDetailsResponseDTO userResponseDTO = new UserDetailsResponseDTO();
		setAttributeToUserResponse(userObj, userResponseDTO);

		logger.info("Fetching Group/Domain Information from Keycloak....");
		Map<String, DomainResponseDTO> domainIdMap = domainService.getDomainIdMap(httpServletRequest);
		if (userObj.optJSONArray(UMSConstants.GROUPS) != null) {
			JSONArray groupsArray = userObj.getJSONArray(UMSConstants.GROUPS);
			for (Object groupObj : groupsArray) {
				group = (JSONObject) groupObj;
				groupId = (String) group.get(UMSConstants.ID);
				if (domainIdMap.containsKey(groupId)) {
					domainsList.add(domainIdMap.get(groupId));
				} else {
					groupResponse = runGroupAPIAsync(httpServletRequest, groupId);
					rolesList.addAll(groupResponse.getRoles());
					domainsList.addAll(groupResponse.getDomains());
					privilegeSet.addAll(groupResponse.getPrivileges());
					groupList
							.add(new GroupResponseDTO(
									groupId, (String) group
											.get(UMSConstants.NAME),
									groupResponse.getRoles().stream()
											.map(roleResponse -> new RoleResponseDTO(roleResponse.getId(),
													roleResponse.getName()))
											.toList(),
									groupResponse.getDomains()));
				}
			}
		}

		logger.info("Fetching Roles and Privileges Information from Keycloak....");
		CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
				httpServletRequest, jwtUtil);
		ResponseEntity<String> roleResponse = restClientWrapperService.getResponseFromUrl(modifiedHttpServletRequest,
				SecurityUtil.sanitizeUrl(userRoleMappingUri, Map.of(UMSConstants.USER_ID_PLACEHOLDER, userId)));
		JSONArray rolesArray = new JSONArray(roleResponse.getBody());

		List<String> groupRolesList = rolesList.stream().map(RoleResponseDTO::getId).toList();
		for (Object roleObj : rolesArray) {
			role = (JSONObject) roleObj;
			roleId = (String) role.get(UMSConstants.ID);
			if (groupRolesList.contains(roleId) && !roleId.equals(baseRoleId)) {
				SecurityUtil.sanitizeLogging(logger, Level.INFO, "Already User associated with Same role in Group...{}",
						roleId);
				continue;
			}
			RoleResponseDTO roleResponseDto = runRoleAPIAsync(httpServletRequest, roleId);
			if (roleId.equals(baseRoleId)) {
				rolesList.add(new RoleResponseDTO(roleId, (String) role.get(UMSConstants.NAME), false,
						roleResponseDto.getPrivileges()));
			} else {
				rolesList.add(new RoleResponseDTO(roleId, (String) role.get(UMSConstants.NAME),
						roleResponseDto.getPrivileges()));
			}
			privilegeSet.addAll(roleResponseDto.getPrivileges());
		}

		userResponseDTO.setId(userId);
		setDetailsToUserResponse(userObj, userResponseDTO);
		userResponseDTO.setGroups(groupList);
		userResponseDTO.setRoles(new ArrayList<>(new HashSet<>(rolesList)));
		userResponseDTO.setDomains(new ArrayList<>(new HashSet<>(domainsList)));
		userResponseDTO.setPrivileges(privilegeSet);
		return userResponseDTO;

	}

	private GroupResponseDTO runGroupAPIAsync(HttpServletRequest httpServletRequest, String groupId)
			throws InterruptedException, ExecutionException {

		final String id = groupId;
		CompletableFuture<GroupResponseDTO> groupFuture = CompletableFuture.supplyAsync(() -> {
			try {
				return groupService.getGroupDetails(httpServletRequest, id);
			} catch (InterruptedException | ExecutionException e) {
				logger.error("Exception occurred when running Group API parellelly", e);
				Thread.currentThread().interrupt();
			}

			return new GroupResponseDTO();
		});

		return groupFuture.get();
	}

	private RoleResponseDTO runRoleAPIAsync(HttpServletRequest httpServletRequest, String roleId)
			throws InterruptedException, ExecutionException {

		final String id = roleId;
		CompletableFuture<RoleResponseDTO> roleFuture = CompletableFuture
				.supplyAsync(() -> roleService.getPrivilegesForRole(httpServletRequest, id));

		return roleFuture.get();
	}

	@Override
	@Cacheable(value = "usercombineddetails", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public UserDetailsResponseDTO getCurrentUserCombinedDetailsCached(HttpServletRequest httpServletRequest)
			throws AttributeNotFoundException, JsonProcessingException, InterruptedException, ExecutionException {
		Map<String, Claim> claims = jwtUtil.getClaims(httpServletRequest.getHeader("Authorization").substring(7));
		String userId = claims.get(UMSConstants.SUB).asString();
		return getUsersCombinedDetails(httpServletRequest, userId);
	}

	@Override
	public String createUser(HttpServletRequest httpServletRequest, CreateUserRequestDTO requestDto)
			throws JsonProcessingException {
		CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
				httpServletRequest, jwtUtil);
		if (requestDto.getGroups() == null) {
			requestDto.setGroups(new ArrayList<>());
		}
		if (requestDto.getDomains() != null && !requestDto.getDomains().isEmpty()) {
			requestDto.getGroups().addAll(requestDto.getDomains());
		}
		UserRepresentation userRequest = new UserRepresentation();
		userRequest.setUsername(requestDto.getUserName());
		userRequest.setEmail(requestDto.getEmail());
		userRequest.setFirstName(requestDto.getName().getFirstName());
		userRequest.setLastName(requestDto.getName().getLastName());
		userRequest.setAttributes(requestDto.getAttributes().stream()
				.collect(Collectors.toMap(AttributeBody::getKey, AttributeBody::getValue)));
		ResponseEntity<String> userCreatedResponse = restClientWrapperService.postBodyToUrl(modifiedHttpServletRequest,
				createUserUri, new JSONObject(userRequest).put(UMSConstants.ENABLED, true).toString());

		String location = Optional.ofNullable(userCreatedResponse.getHeaders().get(UMSConstants.LOCATION))
				.orElseThrow(() -> new UserNotCreatedException("User not created with given details...")).get(0);
		String userId = location.replaceFirst(".+(?<=/)", "");

		if (requestDto.getDomains() != null && !requestDto.getDomains().isEmpty()) {
			List<OpBody> domainsList = requestDto.getDomains().stream()
					.map(domain -> new OpBody(domain.getId(), UMSConstants.ADD)).toList();
			groupsToUserAssociation(domainsList, userId, modifiedHttpServletRequest);
		}

		if (requestDto.getGroups() != null && !requestDto.getGroups().isEmpty()) {
			List<OpBody> groupsList = requestDto.getGroups().stream()
					.map(group -> new OpBody(group.getId(), UMSConstants.ADD)).toList();
			groupsToUserAssociation(groupsList, userId, modifiedHttpServletRequest);
		}

		if (requestDto.getRoles() == null) {
			requestDto.setRoles(new ArrayList<>());
		}

		List<RoleNameBody> rolesRequest = requestDto.getRoles();
		if (rolesRequest.stream().noneMatch(role -> role.getId().equals(baseRoleId))) {
			rolesRequest.add(new RoleNameBody(baseRoleId, UMSConstants.BASE_ROLE));
		}

		ResponseEntity<String> response = ResponseEntity
				.ok(new JSONObject(requestDto).put(UMSConstants.ID, userId).toString());
		JSONArray responseArray = rolesToUserAssociation(modifiedHttpServletRequest, rolesRequest, response,
				UMSConstants.ADD);

		return responseArray.toString();
	}

	private JSONArray rolesToUserAssociation(HttpServletRequest httpServletRequest,
			List<? extends RoleNameBody> rolesRequest, ResponseEntity<String> userResponseDTO,
			String operationToPerform) throws JsonProcessingException {
		JSONObject responseObject = new JSONObject(userResponseDTO.getBody());
		if (responseObject.has(UMSConstants.ID)) {
			String userId = (String) responseObject.get(UMSConstants.ID);
			CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
					httpServletRequest, jwtUtil);
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

			if (operationToPerform.equals(UMSConstants.ADD)) {
				restClientWrapperService.postBodyToUrl(modifiedHttpServletRequest,
						SecurityUtil.sanitizeUrl(userRoleMappingUri, Map.of(UMSConstants.USER_ID_PLACEHOLDER, userId)),
						jsonBody);
			} else if (operationToPerform.equals(UMSConstants.REMOVE)) {
				restClientWrapperService.deleteBodyToUrl(modifiedHttpServletRequest,
						SecurityUtil.sanitizeUrl(userRoleMappingUri, Map.of(UMSConstants.USER_ID_PLACEHOLDER, userId)),
						jsonBody);
			}

			if (responseObject.has(UMSConstants.ROLES)) {
				responseObject.remove(UMSConstants.ROLES);
			}
		}
		return new JSONArray(Arrays.asList(responseObject));
	}

	@Override
	public String editUsers(HttpServletRequest httpServletRequest, EditUserRequestDTO requestDto)
			throws JsonProcessingException, AttributeNotFoundException {

		String userId = requestDto.getId();
		UserDetailsResponseDTO fetchUserResponse = getDetailsFromUser(httpServletRequest, userId);
		if (requestDto.getAttributes() != null && fetchUserResponse.getAttributes() != null) {
			Set<String> newKeys = requestDto.getAttributes().stream().map(AttributeBody::getKey)
					.collect(Collectors.toSet());
			requestDto.getAttributes().addAll(fetchUserResponse.getAttributes().stream()
					.filter(attribute -> !newKeys.contains(attribute.getKey())).toList());
		}
		String jsonBody = SecurityUtil.convertFieldNameInJsonObjectBody(requestDto, UMSConstants.GROUPS,
				UMSConstants.GROUP);
		ResponseEntity<String> editedUserResponse = restClientWrapperService.putBodyToUrl(httpServletRequest,
				SecurityUtil.sanitizeUrl(editUserUri, Map.of(UMSConstants.USER_ID_PLACEHOLDER, userId)), jsonBody);
		JSONArray responseArray = null;
		CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
				httpServletRequest, jwtUtil);
		if (requestDto.getRoles() != null && !requestDto.getRoles().isEmpty()) {

			List<RoleOpBody> addRolesRequest = requestDto.getRoles().stream()
					.filter(role -> role.getOp().equals(UMSConstants.ADD)).toList();
			if (!addRolesRequest.isEmpty()) {
				responseArray = rolesToUserAssociation(modifiedHttpServletRequest, addRolesRequest, editedUserResponse,
						UMSConstants.ADD);
			}

			List<RoleOpBody> deleteRolesRequest = requestDto.getRoles().stream()
					.filter(role -> !role.getId().equals(baseRoleId) && role.getOp().equals(UMSConstants.REMOVE))
					.toList();
			if (!deleteRolesRequest.isEmpty()) {
				responseArray = rolesToUserAssociation(modifiedHttpServletRequest, deleteRolesRequest,
						editedUserResponse, UMSConstants.REMOVE);
			}

		}

		if (requestDto.getDomains() != null && !requestDto.getDomains().isEmpty()) {
			groupsToUserAssociation(requestDto.getDomains(), userId, modifiedHttpServletRequest);
		}

		if (requestDto.getGroups() != null && !requestDto.getGroups().isEmpty()) {
			groupsToUserAssociation(requestDto.getGroups(), userId, modifiedHttpServletRequest);
		}

		return responseArray != null ? responseArray.toString() : editedUserResponse.getBody();
	}

	private void groupsToUserAssociation(List<OpBody> groupList, String userId,
			CustomHttpServletRequestWrapper modifiedHttpServletRequest) {
		if (!groupList.isEmpty()) {
			List<OpBody> addDomainsRequest = groupList.stream()
					.filter(domain -> domain.getOp().equals(UMSConstants.ADD)).toList();
			if (!addDomainsRequest.isEmpty()) {
				addDomainsRequest.stream()
						.forEach(domain -> restClientWrapperService.putBodyToUrl(modifiedHttpServletRequest,
								SecurityUtil.sanitizeUrl(userGroupMappingUri, Map.of(UMSConstants.GROUP_ID_PLACEHOLDER,
										domain.getId(), UMSConstants.USER_ID_PLACEHOLDER, userId)),
								"{}"));
			}

			List<OpBody> rmDomainsRequest = groupList.stream()
					.filter(domain -> domain.getOp().equals(UMSConstants.REMOVE)).toList();
			if (!rmDomainsRequest.isEmpty()) {
				rmDomainsRequest.stream()
						.forEach(domain -> restClientWrapperService.deleteBodyToUrl(modifiedHttpServletRequest,
								SecurityUtil.sanitizeUrl(userGroupMappingUri, Map.of(UMSConstants.GROUP_ID_PLACEHOLDER,
										domain.getId(), UMSConstants.USER_ID_PLACEHOLDER, userId)),
								"{}"));
			}

		}
	}

	private UserDetailsResponseDTO getUserSpecificGroupDomainDetails(HttpServletRequest httpServletRequest,
			String userId) {
		ResponseEntity<String> userResponse = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				SecurityUtil.sanitizeUrl(userDetailsUri, Map.of(UMSConstants.USER_ID_PLACEHOLDER, userId)));
		JSONObject userObj = new JSONObject(userResponse.getBody());
		JSONObject group = null;
		String groupId = null;
		List<GroupResponseDTO> groupList = new ArrayList<>();
		List<DomainResponseDTO> domainsList = new ArrayList<>();
		UserDetailsResponseDTO userResponseDTO = new UserDetailsResponseDTO();
		Map<String, DomainResponseDTO> domainIdMap = domainService.getDomainIdMap(httpServletRequest);
		if (userObj.optJSONArray(UMSConstants.GROUPS) != null) {
			JSONArray groupsArray = userObj.getJSONArray(UMSConstants.GROUPS);
			for (Object groupObj : groupsArray) {
				group = (JSONObject) groupObj;
				groupId = (String) group.get(UMSConstants.ID);
				if (domainIdMap.containsKey(groupId)) {
					domainsList.add(new DomainResponseDTO(groupId, domainIdMap.get(groupId).getName()));
				} else {
					groupList.add(new GroupResponseDTO(groupId, (String) group.get(UMSConstants.NAME)));
				}
			}
		}

		userResponseDTO.setId(userId);
		userResponseDTO.setGroups(groupList);
		userResponseDTO.setDomains(domainsList);
		return userResponseDTO;

	}

	private UserDetailsResponseDTO getUserSpecificRoleDetails(HttpServletRequest httpServletRequest, String userId) {
		JSONObject role;
		String roleId;
		List<RoleResponseDTO> rolesList = new ArrayList<>();
		CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
				httpServletRequest, jwtUtil);
		ResponseEntity<String> roleResponse = restClientWrapperService.getResponseFromUrl(modifiedHttpServletRequest,
				SecurityUtil.sanitizeUrl(userRoleMappingUri, Map.of(UMSConstants.USER_ID_PLACEHOLDER, userId)));
		JSONArray rolesArray = new JSONArray(roleResponse.getBody());
		for (Object roleObj : rolesArray) {
			role = (JSONObject) roleObj;
			roleId = (String) role.get(UMSConstants.ID);
			if (roleId.equals(baseRoleId)) {
				rolesList.add(new RoleResponseDTO(roleId, (String) role.get(UMSConstants.NAME), false));
			} else {
				rolesList.add(new RoleResponseDTO(roleId, (String) role.get(UMSConstants.NAME)));
			}
		}
		UserDetailsResponseDTO userResponseDTO = new UserDetailsResponseDTO();
		userResponseDTO.setId(userId);
		userResponseDTO.setRoles(rolesList);
		return userResponseDTO;
	}

	@Override
	public UserDetailsResponseDTO getDetailsFromUser(HttpServletRequest httpServletRequest, String userId)
			throws AttributeNotFoundException {
		ResponseEntity<String> userResponse = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				SecurityUtil.sanitizeUrl(userDetailsUri, Map.of(UMSConstants.USER_ID_PLACEHOLDER, userId)));
		UserDetailsResponseDTO userResponseDTO = new UserDetailsResponseDTO();
		JSONObject userData = new JSONObject(userResponse.getBody());
		userResponseDTO.setId(userId);
		setDetailsToUserResponse(userData, userResponseDTO);
		setAttributeToUserResponse(userData, userResponseDTO);
		return userResponseDTO;

	}

	@Override
	public List<SelectedResponseDTO> getRoleDetailsFromUser(HttpServletRequest httpServletRequest, String userId)
			throws JsonProcessingException {
		UserDetailsResponseDTO fetchUserRoleResponse = getUserSpecificRoleDetails(httpServletRequest, userId);
		List<RoleResponseDTO> rolesResponse = roleService.getAllRoles(httpServletRequest);
		List<String> selectedRoles = fetchUserRoleResponse.getRoles().stream().map(RoleResponseDTO::getId).toList();
		return rolesResponse.stream()
				.map(role -> new SelectedResponseDTO(role.getId(), role.getName(), selectedRoles.contains(role.getId()),
						role.isEditable()))
				.sorted(Comparator.comparing(SelectedResponseDTO::isSelected).reversed()).toList();
	}

	@Override
	public List<SelectedResponseDTO> getDomainDetailsFromUser(HttpServletRequest httpServletRequest, String userId)
			throws JsonProcessingException {
		UserDetailsResponseDTO fetchUserResponse = getUserSpecificGroupDomainDetails(httpServletRequest, userId);
		List<DomainResponseDTO> domainsResponse = domainService.getAllDomains(httpServletRequest);
		List<String> selectedDomains = fetchUserResponse.getDomains().stream().map(DomainResponseDTO::getId).toList();
		return domainsResponse.stream()
				.map(domain -> new SelectedResponseDTO(domain.getId(), domain.getName(),
						selectedDomains.contains(domain.getId()), true))
				.sorted(Comparator.comparing(SelectedResponseDTO::isSelected).reversed()).toList();
	}

	@Override
	public List<SelectedResponseDTO> getGroupDetailsFromUser(HttpServletRequest httpServletRequest, String userId)
			throws JsonProcessingException {
		UserDetailsResponseDTO fetchUserResponse = getUserSpecificGroupDomainDetails(httpServletRequest, userId);
		List<GroupResponseDTO> groupResponse = groupService.getAllGroups(httpServletRequest);
		List<String> selectedGroup = fetchUserResponse.getGroups().stream().map(GroupResponseDTO::getId).toList();
		return groupResponse.stream()
				.map(group -> new SelectedResponseDTO(group.getId(), group.getName(),
						selectedGroup.contains(group.getId()), true))
				.sorted(Comparator.comparing(SelectedResponseDTO::isSelected).reversed()).toList();
	}

	@Override
	public String enableDisableUser(HttpServletRequest httpServletRequest, DeleteUserRequestDTO requestDto)
			throws JsonProcessingException {
		String jsonBody = new ObjectMapper().writeValueAsString(Arrays.asList(requestDto));
		ResponseEntity<String> userDeletedResponse = restClientWrapperService.putBodyToUrl(httpServletRequest,
				editUserUri, jsonBody);
		return userDeletedResponse.getBody();
	}

}
