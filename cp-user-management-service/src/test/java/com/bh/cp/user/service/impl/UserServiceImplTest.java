package com.bh.cp.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.user.dto.body.AttributeBody;
import com.bh.cp.user.dto.body.IdBody;
import com.bh.cp.user.dto.body.NameBody;
import com.bh.cp.user.dto.body.RoleNameBody;
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
import com.bh.cp.user.service.DomainService;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.service.RoleService;
import com.bh.cp.user.util.CustomHttpServletRequestWrapper;
import com.bh.cp.user.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

class UserServiceImplTest {

	@Spy
	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private RestClientWrapperService restClientWrapperService;

	@Mock
	private CustomHttpServletRequestWrapper modifiedHttpServletRequest;

	@Mock
	private RoleService roleService;

	@Mock
	private GroupServiceImpl groupservice;

	@Mock
	private DomainService domainService;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	private List<AttributeBody> attributesRequestList;

	@Mock
	private JwtUtil jwtUtil;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(userService, "getAllUsersUri", "/v1/users");
		ReflectionTestUtils.setField(userService, "userDetailsUri", "/v1/users/<user_id>");
		ReflectionTestUtils.setField(userService, "createUserUri", "/v1/users");
		ReflectionTestUtils.setField(userService, "editUserUri", "/v1/users");
		ReflectionTestUtils.setField(userService, "clientIdPk", "abcd");
		ReflectionTestUtils.setField(userService, "userRoleMappingUri", "/v1/users/<user_id>/roles");

		Arrays.asList(new IdBody("domain_id1"), new IdBody("domain_id2"));
		Arrays.asList(new RoleNameBody("role1", "001"), new RoleNameBody("role2", "002"));
		Arrays.asList(new IdBody("group_id1"), new IdBody("group_id2"));
		attributesRequestList = new ArrayList<>();
		attributesRequestList.add(new AttributeBody("test_attribute", new ArrayList<>()));
	}

	@Nested
	class GetParticularDetailsFromUsers {

		private UserDetailsResponseDTO userResponse;
		private CreateUserRequestDTO userRequestDTO;
		private DomainResponseDTO domainResponseDTO;
		private RoleResponseDTO roleTResponseDTO;
		private PrivilegesResponseDTO privilegesResponseDTO;
		private AttributeBody attributeBody;
		private GroupResponseDTO groupResponseDTO;
		private List<DomainResponseDTO> domains;
		private List<GroupResponseDTO> groups;
		private List<RoleResponseDTO> roles;
		private Set<PrivilegesResponseDTO> privileges;
		private List<AttributeBody> attributes;
		private NameBody body;
		private EditUserRequestDTO editUserRequestDTO;
		List<String> value = new ArrayList<>();

		@BeforeEach
		void setUp() throws Exception {
			body = new NameBody();
			editUserRequestDTO = new EditUserRequestDTO();
			userRequestDTO = new CreateUserRequestDTO();
			userResponse = new UserDetailsResponseDTO();
			roleTResponseDTO = new RoleResponseDTO();
			groupResponseDTO = new GroupResponseDTO();
			privilegesResponseDTO = new PrivilegesResponseDTO();
			attributeBody = new AttributeBody();
			domainResponseDTO = new DomainResponseDTO("domai01", "domainTest");
			roles = new ArrayList<>();
			groups = new ArrayList<>();
			attributes = new ArrayList<>();
			domains = new ArrayList<>();
			privileges = new HashSet<>();
			userResponse.setId("test_user01");
			userResponse.setName("test");
			userResponse.setStatus("active");
			userResponse.setSurName("unit");
			userResponse.setUserName("unittestcase");
			userResponse.setTitle("JunitTestCases");
			userResponse.setEnabled("disable");
			userResponse.setEmail("test@gmail.com");
			domainResponseDTO.setId("Domain_o1");
			domainResponseDTO.setEditable(true);
			domainResponseDTO.setName("testDomain");
			domains.add(domainResponseDTO);
			userResponse.setDomains(domains);
			groupResponseDTO.setId("GroupId");
			groupResponseDTO.setName("TestGroup");
			groupResponseDTO.setAttributes(attributes);
			groupResponseDTO.setDomains(domains);
			groupResponseDTO.setPrivileges(privileges);
			groupResponseDTO.setRoles(roles);
			groups.add(groupResponseDTO);
			userResponse.setGroups(groups);
			roleTResponseDTO.setEditable(true);
			roleTResponseDTO.setId("roleId");
			roleTResponseDTO.setName("testRole");
			roleTResponseDTO.setPrivileges(privileges);
			roles.add(roleTResponseDTO);
			userResponse.setRoles(roles);
			privilegesResponseDTO.setId("privillageId");
			privilegesResponseDTO.setName("privilegesTest");
			privileges.add(privilegesResponseDTO);
			userResponse.setPrivileges(privileges);
			attributeBody.setKey("testattribut");
			value.add("test");
			value.add("test2");
			attributeBody.setValue(value);
			attributes.add(attributeBody);
			userResponse.setAttributes(attributes);
			body.setFirstName("user");
			body.setLastName("test");
			userRequestDTO.setName(body);
			userRequestDTO.setActive(true);
			userRequestDTO.setBulkId("bulked_ID");
			userRequestDTO.setAttributes(attributes);
			userRequestDTO.setEmail("test@gmail.com");
			userRequestDTO.setAttributes(attributes);
			editUserRequestDTO.setId("test_user01");
			editUserRequestDTO.setAttributes(attributes);
			editUserRequestDTO.setDomains(null);
			editUserRequestDTO.setGroups(null);
			editUserRequestDTO.setRoles(null);
			editUserRequestDTO.setActive(false);
			editUserRequestDTO.setName(body);
			editUserRequestDTO.setEmail("testemial.com");
			mockHttpServletRequest = new MockHttpServletRequest();
			mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
			// doReturn(userResponse).when(userService).getUsersCombinedDetails(eq(mockHttpServletRequest),
			// anyString());
		}

		@Test
		void testgetAllUsers() {

			String outputJson = "{ \"users\":[ {\"id\": \"test1\", \"active\": true, \"userName\": \"test01\", \"name\": {\"firstName\": \"test\", \"lastName\": \"testo1\" }, \"email\": \"test@gmail.com\", \"roles\": [ { \"id\": \"testoo1\", \"name\": \"admin\" } ], \"groups\": [ { \"id\": \"testoo2\", \"name\": \"\" } ], \"attributes\": [ { \"key\": \"title\", \"value\": [ \"EXTERNAL\" ] } ] },{ \"id\": \"test2\", \"active\": true, \"userName\": \"test02\", \"name\": { \"firstName\": \"test\", \"lastName\": \"testo2\" }, \"email\": \"test@gmail.com\", \"roles\": [ { \"id\": \"testoo2\", \"name\": \"admin\" } ], \"groups\": [ { \"id\": \"testoo1\", \"name\": \"\" } ], \"attributes\": [ { \"key\": \"title\", \"value\": [ \"EXTERNAL\" ] } ] }] }";
			when(restClientWrapperService.getResponseFromUrl(eq(mockHttpServletRequest), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(outputJson)));

			List<UserDetailsResponseDTO> actualResponse = userService.getAllUsers(mockHttpServletRequest);
			assertEquals(2, actualResponse.size());
			assertEquals("test1", actualResponse.get(0).getId());
		}

		@Test
		void testUserDetailsFromUsers() throws JsonProcessingException, AttributeNotFoundException {

			String outputJson = "{\"name\":{\"firstName\":\"test\",\"lastName\":\"user\"},\"active\":true,\"groups\":[{\"name\":\"test_group\",\"id\":\"test01\"}],\"attributes\":[{\"value\":[\"external\"],\"key\":\"title\"}],\"id\":\"test_user01\",\"userName\":\"test_user\",\"email\":\"testuse@gmail.com\"}";
			when(restClientWrapperService.getResponseFromUrl(eq(mockHttpServletRequest), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(outputJson)));
			UserDetailsResponseDTO actualResponse = userService.getDetailsFromUser(mockHttpServletRequest,
					"test-userid1");
			assertEquals("test_user01", actualResponse.getId());

		}

		@Test
		void testGetRoleDetailsFromUsers() throws JsonProcessingException, AttributeNotFoundException {
			List<RoleResponseDTO> roleResponseList = Arrays.asList(new RoleResponseDTO("role_id1", "role1"),
					new RoleResponseDTO("role_id2", "role2"));
			userResponse.setRoles(roleResponseList.subList(0, 1));
			when(roleService.getAllRoles(mockHttpServletRequest)).thenReturn(roleResponseList);
			String roleJson = "[{\"id\": \"role_id\",\"name\":\"read-role\"}]";
			when(restClientWrapperService.getResponseFromUrl(any(CustomHttpServletRequestWrapper.class), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(roleJson)));
			List<SelectedResponseDTO> actualResponse = userService.getRoleDetailsFromUser(mockHttpServletRequest,
					"test-userid1");
			assertEquals(2, actualResponse.size());
			assertEquals("role_id1", actualResponse.get(0).getId());
			assertEquals("role_id2", actualResponse.get(1).getId());

		}

		@Test
		void testGetDomainDetailsFromUsers() throws JsonProcessingException, AttributeNotFoundException {
			List<DomainResponseDTO> domainResponseList = Arrays.asList(
					new DomainResponseDTO("domain_id1", "domain_name1"),
					new DomainResponseDTO("domain_id2", "domain_name2"));
			userResponse.setDomains(domainResponseList.subList(0, 1));
			attributesRequestList.add(new AttributeBody("associated_domains", List.of("domain1", "domain2")));
			userResponse.setAttributes(attributesRequestList);
			when(domainService.getAllDomains(mockHttpServletRequest)).thenReturn(domainResponseList);
			String outputJson = "{\"name\":{\"firstName\":\"test\",\"lastName\":\"user\"},\"active\":true,\"groups\":[{\"name\":\"test_group\",\"id\":\"test01\"}],\"attributes\":[{\"value\":[\"external\"],\"key\":\"title\"}],\"id\":\"test_user01\",\"userName\":\"test_user\",\"email\":\"testuse@gmail.com\"}";
			when(restClientWrapperService.getResponseFromUrl(eq(mockHttpServletRequest), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(outputJson)));
			List<SelectedResponseDTO> actualResponse = userService.getDomainDetailsFromUser(mockHttpServletRequest,
					"test-userid1");
			assertEquals(2, actualResponse.size());
			assertEquals("domain_id1", actualResponse.get(0).getId());
			assertEquals("domain_id2", actualResponse.get(1).getId());

		}

		@Test
		void testGetUserDetailsFromGroup() throws JsonProcessingException, AttributeNotFoundException {
			List<GroupResponseDTO> groupResponseList = Arrays.asList(new GroupResponseDTO("group_id1", "group1"),
					new GroupResponseDTO("group_id2", "group2"));
			userResponse.setGroups(groupResponseList.subList(0, 1));
			when(groupservice.getAllGroups(mockHttpServletRequest)).thenReturn(groupResponseList);
			String outputJson = "{\"name\":{\"firstName\":\"test\",\"lastName\":\"user\"},\"active\":true,\"groups\":[{\"name\":\"test_group\",\"id\":\"test01\"}],\"attributes\":[{\"value\":[\"external\"],\"key\":\"title\"}],\"id\":\"test_user01\",\"userName\":\"test_user\",\"email\":\"testuse@gmail.com\"}";
			when(restClientWrapperService.getResponseFromUrl(eq(mockHttpServletRequest), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(outputJson)));
			List<SelectedResponseDTO> actualResponse = userService.getGroupDetailsFromUser(mockHttpServletRequest,
					"test-userid1");
			assertEquals(2, actualResponse.size());
			assertEquals("group_id1", actualResponse.get(0).getId());
			assertEquals("group_id2", actualResponse.get(1).getId());

		}

		@Test
		void testDeleteUsers() throws JsonProcessingException {
			String expectedResponse = "[{\"status\":\"200\",\"details\":\"Deleted\"}]";
			DeleteUserRequestDTO requestDto = new DeleteUserRequestDTO();
			requestDto.setId("delete_user_id");
			when(restClientWrapperService.putBodyToUrl(eq(mockHttpServletRequest), anyString(), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(expectedResponse)));
			String actualResponse = userService.enableDisableUser(mockHttpServletRequest, requestDto);
			assertEquals(expectedResponse, actualResponse);
		}

		@Test
		void testgetUsersCombinedDetails()
				throws InterruptedException, ExecutionException, JsonProcessingException, AttributeNotFoundException {
			String outputJson = "{\"name\":{\"firstName\":\"test\",\"lastName\":\"user\"},\"active\":true,\"groups\":[{\"name\":\"test_group\",\"id\":\"test01\"}],\"attributes\":[{\"value\":[\"external\"],\"key\":\"title\"}],\"id\":\"test_user01\",\"userName\":\"test_user\",\"email\":\"testuse@gmail.com\"}";
			when(restClientWrapperService.getResponseFromUrl(eq(mockHttpServletRequest), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(outputJson)));
			Map<String, DomainResponseDTO> domainResponse = new HashMap<>();
			domainResponse.put("83248jhjdhndsf", domainResponseDTO);
			when(domainService.getDomainIdMap(mockHttpServletRequest)).thenReturn(domainResponse);
			when(groupservice.getGroupDetails(any(), any())).thenReturn(groupResponseDTO);
			String roleJson = "[{\"id\": \"role_id\",\"name\":\"read-role\"}]";
			when(restClientWrapperService.getResponseFromUrl(any(CustomHttpServletRequestWrapper.class), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(roleJson)));
			when(roleService.getPrivilegesForRole(any(), any())).thenReturn(roleTResponseDTO);
			UserDetailsResponseDTO actualResponse = userService.getUsersCombinedDetails(mockHttpServletRequest,
					"test_user01");
			assertEquals("test_user01", actualResponse.getId());
		}

	}

}
