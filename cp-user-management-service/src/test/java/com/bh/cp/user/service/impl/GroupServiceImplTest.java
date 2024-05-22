package com.bh.cp.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import com.bh.cp.user.dto.body.OpBody;
import com.bh.cp.user.dto.body.RoleNameBody;
import com.bh.cp.user.dto.body.RoleOpBody;
import com.bh.cp.user.dto.request.CreateGroupRequestDTO;
import com.bh.cp.user.dto.request.DeleteGroupRequestDTO;
import com.bh.cp.user.dto.request.EditGroupRequestDTO;
import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.dto.response.GroupResponseDTO;
import com.bh.cp.user.dto.response.RoleResponseDTO;
import com.bh.cp.user.dto.response.SelectedResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.exception.DeletionNotPermissableException;
import com.bh.cp.user.service.DomainService;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.service.RoleService;
import com.bh.cp.user.service.UserService;
import com.bh.cp.user.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

class GroupServiceImplTest {

	@Spy
	@InjectMocks
	private GroupServiceImpl groupService;

	@Mock
	private RestClientWrapperService restClientWrapperService;

	@Mock
	private RoleService roleService;

	@Mock
	private UserService userService;

	@Mock
	private DomainService domainService;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	@Mock
	private JwtUtil jwtUtil;

	private List<AttributeBody> attributesRequestList;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(groupService, "getAllGroupsUri", "/v1/groups");
		ReflectionTestUtils.setField(groupService, "groupDetailsUri", "/v1/groups/<group_id>");
		ReflectionTestUtils.setField(groupService, "createGroupUri", "/v1/groups");
		ReflectionTestUtils.setField(groupService, "updateGroupUri", "/v1/groups");
		ReflectionTestUtils.setField(groupService, "deleteGroupUri", "/v1/groups");
		ReflectionTestUtils.setField(groupService, "domainsMasterGroupId", "defg");
		ReflectionTestUtils.setField(groupService, "clientIdPk", "abcd");
		ReflectionTestUtils.setField(groupService, "groupRoleMappingUri", "/v1/groups/{groupId}/roles");

		attributesRequestList = new ArrayList<>();
		attributesRequestList.add(new AttributeBody("test_attribute", new ArrayList<>()));
	}

	@Test
	void testGetAllGroups() {

		String outputJson = "{\"groups\": [{\"id\": \"test-groupid1\",\"displayName\": \"test-group1\"},{\"id\": \"test-groupid2\",\"displayName\": \"test-group2\"}]}";
		when(restClientWrapperService.getResponseFromUrl(eq(mockHttpServletRequest), anyString()))
				.thenReturn(ResponseEntity.of(Optional.of(outputJson)));

		List<GroupResponseDTO> actualResponse = groupService.getAllGroups(mockHttpServletRequest);
		assertEquals(2, actualResponse.size());
		assertEquals("test-groupid1", actualResponse.get(0).getId());
		assertEquals("test-groupid2", actualResponse.get(1).getId());
	}

//	@Test
//	void testGetGroupDetails() throws JsonProcessingException, InterruptedException, ExecutionException {
//
//		RoleResponseDTO roleResponseDTO = new RoleResponseDTO("role_id", "read-role");
//		roleResponseDTO.setPrivileges(Set.of(new PrivilegesResponseDTO("privilege-id1", "privilege1"),
//				new PrivilegesResponseDTO("privilege-id2", "privilege2")));
//
//		String outputJson = "{\"id\": \"test-groupid1\",\"displayName\": \"test-group1\",\"source\": \"LOCAL\","
//				+ "\"metaData\": [{\"key\": \"associated_domains\",\"value\": [ \"test_domain_id\"]}],"
//				+ "\"roles\": [{\"id\": \"role_id\",\"name\": \"read-role\"}],"
//				+ "\"members\": [{\"id\": \"user_id\",\"name\": \"test_user1\",\"email\": \"testuser1@gmail.com\",\"type\": \"USER\"}]}";
//		when(restClientWrapperService.getResponseFromUrl(eq(mockHttpServletRequest), anyString()))
//				.thenReturn(ResponseEntity.of(Optional.of(outputJson)));
//
//		String roleJson = "[{\"id\": \"role_id\",\"name\":\"read-role\"}]";
//		when(restClientWrapperService.getResponseFromUrl(any(HttpServletRequest.class),
//				eq("/v1/groups/{groupId}/roles".replace(UMSConstants.GROUP_ID_PLACEHOLDER, "test-groupid1"))))
//				.thenReturn(ResponseEntity.of(Optional.of(roleJson)));
//
//		when(jwtUtil.generateAdminToken()).thenReturn("Bearer abc");
//		when(roleService.getPrivilegesForRole(eq(mockHttpServletRequest), anyString())).thenReturn(roleResponseDTO);
//
//		GroupRepresentation groupRepresentation = new GroupRepresentation();
//		groupRepresentation.setId("test_domain_id");
//		groupRepresentation.setName("test-domain");
//
//		when(domainService.getDomain("test_domain_id", mockHttpServletRequest))
//				.thenReturn(ResponseEntity.of(Optional.of(groupRepresentation)));
//
//		GroupResponseDTO actualResponse = groupService.getGroupDetails(mockHttpServletRequest, "test-groupid1");
//
//		assertEquals("test-groupid1", actualResponse.getId());
//		assertEquals("test-group1", actualResponse.getName());
//		assertEquals(1, actualResponse.getAttributes().size());
//		assertEquals(1, actualResponse.getRoles().size());
//		assertEquals(1, actualResponse.getUsers().size());
//		assertEquals(2, actualResponse.getPrivileges().size());
////		assertEquals(1, actualResponse.getDomains().size());
//
//	}

	@Nested
	class CreateGroup {

		private CreateGroupRequestDTO requestDto;

		private List<IdBody> domainsRequestList;

		private List<RoleNameBody> rolesRequestList;

		private List<IdBody> usersRequestList;

		@BeforeEach
		void setUp() throws Exception {

			domainsRequestList = List.of(new IdBody("domain_id1"), new IdBody("domain_id2"));
			rolesRequestList = List.of(new RoleNameBody("role1"), new RoleNameBody("role2"));
			usersRequestList = List.of(new IdBody("user_id1"), new IdBody("user_id2"));

			requestDto = new CreateGroupRequestDTO();
			requestDto.setDisplayName("test_group");
			requestDto.setSource("local");
			requestDto.setDomains(domainsRequestList);
			requestDto.setRoles(rolesRequestList);
			requestDto.setMembers(usersRequestList);

			when(restClientWrapperService.postBodyToUrl(any(HttpServletRequest.class),
					eq("/v1/groups/<group_id>/roles"), anyString())).thenReturn(null);
			when(jwtUtil.generateAdminToken()).thenReturn("Bearer abc");
		}

		@Test
		void testCreateGroup_WithoutAttributes() throws JsonProcessingException {
			String expectedResponse = "[{\"details\":\"Created successfully\",\"id\":\"test_group\",\"status\":\"201\"}]";
			when(restClientWrapperService.postBodyToUrl(eq(mockHttpServletRequest), anyString(), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(expectedResponse)));
			String actualResponse = groupService.createGroup(mockHttpServletRequest, requestDto);
			assertEquals(expectedResponse, actualResponse);
		}

		@Test
		void testCreateGroup_WithAttributes() throws JsonProcessingException {
			requestDto.setAttributes(attributesRequestList);
			String expectedResponse = "[{\"details\":\"Created successfully\",\"id\":\"test_group\",\"status\":\"201\"}]";
			when(restClientWrapperService.postBodyToUrl(eq(mockHttpServletRequest), anyString(), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(expectedResponse)));
			String actualResponse = groupService.createGroup(mockHttpServletRequest, requestDto);
			assertEquals(expectedResponse, actualResponse);
		}
	}

	@Nested
	class EditGroup {

		private EditGroupRequestDTO requestDto;

		private List<RoleOpBody> rolesOpList;

		private List<OpBody> domainsOpList;

		private List<OpBody> usersOpList;

		private GroupResponseDTO groupResponse;

		@BeforeEach
		void setUp() throws Exception {

			rolesOpList = List.of(new RoleOpBody("add"), new RoleOpBody("remove"));
			usersOpList = List.of(new OpBody("add_user"), new OpBody("rm_user"));
			domainsOpList = List.of(new OpBody("add_domain"), new OpBody("rm_domain"));

			requestDto = new EditGroupRequestDTO();
			requestDto.setId("test-groupid1");
			requestDto.setDisplayName("test_group");
			requestDto.setDomains(domainsOpList);
			requestDto.setRoles(rolesOpList);
			requestDto.setMembers(usersOpList);

			groupResponse = new GroupResponseDTO();
			groupResponse.setId("test-groupid1");
			groupResponse.setDomains(List.of(new DomainResponseDTO("domain_id1", "domain_name1")));
			attributesRequestList.add(new AttributeBody("associated_domains", List.of("domain1", "domain2")));
			groupResponse.setAttributes(attributesRequestList);

			when(restClientWrapperService.postBodyToUrl(any(HttpServletRequest.class),
					eq("/v1/groups/<group_id>/roles"), anyString())).thenReturn(null);
//			when(restClientWrapperService.deleteBodyToUrl(any(HttpServletRequest.class),
//					eq("/v1/groups/<group_id>/roles"), anyString())).thenReturn(null);
			when(jwtUtil.generateAdminToken()).thenReturn("Bearer abc");
			when(domainService.getDomain(anyString(), any(HttpServletRequest.class))).thenReturn(null);

		}

		@Test
		void testEditGroup_WithoutExistingAttributes()
				throws JsonProcessingException, InterruptedException, ExecutionException {
			String expectedResponse = "[{\"details\":\"Updated successfully\",\"id\":\"test_group\",\"status\":\"200\"}]";
			groupResponse.setAttributes(null);
			doReturn(groupResponse).when(groupService).getGroupDetails(eq(mockHttpServletRequest), anyString());
			when(restClientWrapperService.putBodyToUrl(eq(mockHttpServletRequest), anyString(), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(expectedResponse)));
			String actualResponse = groupService.editGroup(mockHttpServletRequest, requestDto);
			assertEquals(expectedResponse, actualResponse);
		}

		@Test
		void testEditGroup_WithoutNoAttributes()
				throws JsonProcessingException, InterruptedException, ExecutionException {
			String expectedResponse = "[{\"details\":\"Updated successfully\",\"id\":\"test_group\",\"status\":\"200\"}]";
			doReturn(groupResponse).when(groupService).getGroupDetails(eq(mockHttpServletRequest), anyString());
			when(restClientWrapperService.putBodyToUrl(eq(mockHttpServletRequest), anyString(), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(expectedResponse)));
			String actualResponse = groupService.editGroup(mockHttpServletRequest, requestDto);
			assertEquals(expectedResponse, actualResponse);
		}

		@Test
		void testEditGroup_WithAttributes() throws JsonProcessingException, InterruptedException, ExecutionException {
			String expectedResponse = "[{\"details\":\"Updated successfully\",\"id\":\"test_group\",\"status\":\"200\"}]";
			requestDto.setAttributes(attributesRequestList);
			doReturn(groupResponse).when(groupService).getGroupDetails(eq(mockHttpServletRequest), anyString());
			when(restClientWrapperService.putBodyToUrl(eq(mockHttpServletRequest), anyString(), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(expectedResponse)));
			String actualResponse = groupService.editGroup(mockHttpServletRequest, requestDto);
			assertEquals(expectedResponse, actualResponse);
		}
	}

	@Nested
	class GetParticularDetailsFromGroup {

		private GroupResponseDTO groupResponse;

		@BeforeEach
		void setUp() throws Exception {
			groupResponse = new GroupResponseDTO();
			groupResponse.setId("test-groupid1");
			doReturn(groupResponse).when(groupService).getGroupDetails(eq(mockHttpServletRequest), anyString());
		}

		@Test
		void testGetUserDetailsFromGroup() throws JsonProcessingException, InterruptedException, ExecutionException {
			List<UserDetailsResponseDTO> userResponseList = List.of(
					new UserDetailsResponseDTO("user_id1", "user1", "user1@gmail.com"),
					new UserDetailsResponseDTO("user_id2", "user2", "user2@gmail.com"));
			groupResponse.setUsers(userResponseList.subList(0, 1));
			when(userService.getAllUsers(mockHttpServletRequest)).thenReturn(userResponseList);
			List<SelectedResponseDTO> actualResponse = groupService.getUserDetailsFromGroup(mockHttpServletRequest,
					"test-groupid1");
			assertEquals(2, actualResponse.size());
			assertEquals("user_id1", actualResponse.get(0).getId());
			assertEquals("user_id2", actualResponse.get(1).getId());
			assertTrue(actualResponse.get(0).isSelected());
			assertFalse(actualResponse.get(1).isSelected());
		}

		@Test
		void testGetRoleDetailsFromGroup() throws JsonProcessingException, InterruptedException, ExecutionException {
			List<RoleResponseDTO> roleResponseList = List.of(new RoleResponseDTO("role_id1", "role1"),
					new RoleResponseDTO("role_id2", "role2"));
			groupResponse.setRoles(roleResponseList.subList(0, 1));
			when(roleService.getAllRoles(mockHttpServletRequest)).thenReturn(roleResponseList);
			List<SelectedResponseDTO> actualResponse = groupService.getRoleDetailsFromGroup(mockHttpServletRequest,
					"test-groupid1");
			assertEquals(2, actualResponse.size());
			assertEquals("role_id1", actualResponse.get(0).getId());
			assertEquals("role_id2", actualResponse.get(1).getId());
			assertTrue(actualResponse.get(0).isSelected());
			assertFalse(actualResponse.get(1).isSelected());
		}

		@Test
		void testGetDomainDetailsFromGroup() throws JsonProcessingException, InterruptedException, ExecutionException {
			List<DomainResponseDTO> domainResponseList = List.of(new DomainResponseDTO("domain_id1", "domain_name1"),
					new DomainResponseDTO("domain_id2", "domain_name2"));
			groupResponse.setDomains(domainResponseList.subList(0, 1));
			attributesRequestList.add(new AttributeBody("associated_domains", List.of("domain1", "domain2")));
			groupResponse.setAttributes(attributesRequestList);
			when(domainService.getAllDomains(mockHttpServletRequest)).thenReturn(domainResponseList);
			List<SelectedResponseDTO> actualResponse = groupService.getDomainDetailsFromGroup(mockHttpServletRequest,
					"test-groupid1");
			assertEquals(2, actualResponse.size());
			assertEquals("domain_id1", actualResponse.get(0).getId());
			assertEquals("domain_id2", actualResponse.get(1).getId());
			assertTrue(actualResponse.get(0).isSelected());
			assertFalse(actualResponse.get(1).isSelected());
		}
	}

	@Nested
	class DeleteGroup {

		private GroupResponseDTO groupResponse;

		@BeforeEach
		void setUp() throws Exception {
			groupResponse = new GroupResponseDTO();
			groupResponse.setId("delete_group_id");
			groupResponse.setUsers(List.of(new UserDetailsResponseDTO("user_id1", "user1", "user@gmail.com")));
			doReturn(groupResponse).when(groupService).getGroupDetails(eq(mockHttpServletRequest), anyString());

		}

		@Test
		void testDeleteGroupWithUsers() throws JsonProcessingException {
			DeleteGroupRequestDTO requestDto = new DeleteGroupRequestDTO();
			requestDto.setId("delete_group_id");
			assertEquals("Group associated with Users can't be deleted",
					assertThrows(DeletionNotPermissableException.class,
							() -> groupService.deleteGroup(mockHttpServletRequest, requestDto)).getMessage());
		}

		@Test
		void testDeleteGroupWithoutUsers() throws JsonProcessingException, DeletionNotPermissableException,
				InterruptedException, ExecutionException {
			String expectedResponse = "[{\"status\":\"200\",\"details\":\"Deleted\"}]";
			groupResponse.setUsers(new ArrayList<>());
			DeleteGroupRequestDTO requestDto = new DeleteGroupRequestDTO();
			requestDto.setId("delete_group_id");
			when(restClientWrapperService.deleteBodyToUrl(eq(mockHttpServletRequest), anyString(), anyString()))
					.thenReturn(ResponseEntity.of(Optional.of(expectedResponse)));
			String actualResponse = groupService.deleteGroup(mockHttpServletRequest, requestDto);
			assertEquals(expectedResponse, actualResponse);
		}
	}

}
