package com.bh.cp.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.user.dto.request.CreateGroupRequestDTO;
import com.bh.cp.user.dto.request.DeleteGroupRequestDTO;
import com.bh.cp.user.dto.request.EditGroupRequestDTO;
import com.bh.cp.user.dto.response.GroupResponseDTO;
import com.bh.cp.user.dto.response.SelectedResponseDTO;
import com.bh.cp.user.service.GroupService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class GroupControllerTest {

	@InjectMocks
	private GroupController groupController;

	@Mock
	private GroupService groupService;

	@Autowired
	private MockMvc mockMvc;

	private GroupResponseDTO groupResponseDTO;

	private SelectedResponseDTO selectedResponseDTO;

	private SelectedResponseDTO notSelectedResponseDTO;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();

		groupResponseDTO = new GroupResponseDTO("test_id", "test_group");
		selectedResponseDTO = new SelectedResponseDTO();
		selectedResponseDTO.setId("selected_id");
		selectedResponseDTO.setName("selected_name");
		selectedResponseDTO.setSelected(true);

		notSelectedResponseDTO = new SelectedResponseDTO();
		notSelectedResponseDTO.setId("notselected_id");
		notSelectedResponseDTO.setName("notselected_name");
		notSelectedResponseDTO.setSelected(false);

	}

//	GroupResponseDTO groupResponseDTO;
//	RoleResponseDTO roleResponseDTO;
//	UserResponseDTO userResponseDTO;
//
//	List<GroupResponseDTO> groupsList = new ArrayList<>();
//	List<RoleResponseDTO> rolesList = new ArrayList<>();
//	List<UserResponseDTO> usersList = new ArrayList<>();
//	List<Domain> domainsList = new ArrayList<>();
//
//	@BeforeEach
//	public void setUp() {
//		MockitoAnnotations.openMocks(this);
//		mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
//		groupResponseDTO = new GroupResponseDTO();
//		groupResponseDTO.setId("1");
//		groupResponseDTO.setName("group1");
//		rolesList.add(new RoleResponseDTO("1", "role1"));
//		rolesList.add(new RoleResponseDTO("2", "role2"));
//		usersList.add(new UserResponseDTO("1", "user1", "user1@gmail.com"));
//		usersList.add(new UserResponseDTO("2", "user2", "user2@gmail.com"));
//		usersList.add(new UserResponseDTO("3", "user3", "user3@gmail.com"));
//		domainsList.add(new Domain("1", "domain1"));
//		domainsList.add(new Domain("2", "domain3"));
//		groupsList.add(groupResponseDTO);
//	}
//
//	@Test
////	@Disabled
//     void testGroups() throws JsonProcessingException, Exception {
//        when(groupService.getAllGroups(any(HttpServletRequest.class))).thenReturn(groupsList);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/v1/groups/").header("Authorization", "Bearer abc")
//				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
//						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
//						MockMvcResultMatchers.content()
//								.string(new ObjectMapper().writeValueAsString(groupsList)));
//        verify(groupService).getAllGroups(any(HttpServletRequest.class));
//    }
//
//	@Test
////	@Disabled
//	void testViewGroupDetails() throws JsonProcessingException, Exception {
//		groupResponseDTO.setDomains(domainsList);
//		groupResponseDTO.setRoles(rolesList);
//		groupResponseDTO.setUsers(usersList);
//		when(groupService.getGroupDetails(any(HttpServletRequest.class), any(String.class)))
//				.thenReturn(groupResnponseDTO);
//
//		mockMvc.perform(MockMvcRequestBuilders.get("/v1/groups/123").header("Authorization", "Bearer abc")
//				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
//						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
//						MockMvcResultMatchers.content()
//								.string(new ObjectMapper().writeValueAsString(groupResponseDTO)));
//		verify(groupService).getGroupDetails(any(HttpServletRequest.class),any(String.class));
//	}

	@Test
	void testGetAllGroups() throws Exception {
		when(groupService.getAllGroups(any(HttpServletRequest.class))).thenReturn(List.of(groupResponseDTO));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/groups")
				).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(objectMapper
								.writeValueAsString(List.of(groupResponseDTO))));
	}

	@Test
	void testGetDetailsForGroup() throws Exception {
		when(groupService.getGroupDetails(any(HttpServletRequest.class),anyString())).thenReturn(groupResponseDTO);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/groups/{groupId}","test_id")
				).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(objectMapper
								.writeValueAsString(new GroupResponseDTO("test_id","test_group"))));
	}

	@Test
	void testCreateGroup() throws Exception {
		CreateGroupRequestDTO requestDto = new CreateGroupRequestDTO();
		requestDto.setDisplayName("test_group");
		requestDto.setSource("local");
		when(groupService.createGroup(any(HttpServletRequest.class), any(CreateGroupRequestDTO.class)))
				.thenReturn("Created successfully");
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/groups").content(objectMapper.writeValueAsString(requestDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string("Created successfully"));
	}

	@Test
	void testEditGroup() throws Exception {
		EditGroupRequestDTO requestDto = new EditGroupRequestDTO();
		requestDto.setId("test_id");
		requestDto.setDisplayName("test_group");
		when(groupService.editGroup(any(HttpServletRequest.class), any(EditGroupRequestDTO.class)))
				.thenReturn("Updated successfully");
		mockMvc.perform(MockMvcRequestBuilders.put("/v1/groups").content(objectMapper.writeValueAsString(requestDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string("Updated successfully"));
	}

	@Test
	void testGetUsersForGroup() throws Exception {
		selectedResponseDTO.setEmail("selected@gmail.com");
		notSelectedResponseDTO.setEmail("notselected@gmail.com");

		when(groupService.getUserDetailsFromGroup(any(HttpServletRequest.class), anyString()))
				.thenReturn(List.of(selectedResponseDTO, notSelectedResponseDTO));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/groups/{groupId}/users", "test_id")).andExpectAll(
				MockMvcResultMatchers.status().isOk(),
				MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
				MockMvcResultMatchers.content()
						.string(objectMapper.writeValueAsString(List.of(selectedResponseDTO, notSelectedResponseDTO))));
	}

	@Test
	void testGetRolesForGroup() throws Exception {
		when(groupService.getRoleDetailsFromGroup(any(HttpServletRequest.class), anyString()))
				.thenReturn(List.of(selectedResponseDTO, notSelectedResponseDTO));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/groups/{groupId}/roles", "test_id")).andExpectAll(
				MockMvcResultMatchers.status().isOk(),
				MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
				MockMvcResultMatchers.content()
						.string(objectMapper.writeValueAsString(List.of(selectedResponseDTO, notSelectedResponseDTO))));
	}

	@Test
	void testGetDomainsForGroup() throws Exception {
		when(groupService.getDomainDetailsFromGroup(any(HttpServletRequest.class), anyString()))
				.thenReturn(List.of(selectedResponseDTO, notSelectedResponseDTO));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/groups/{groupId}/domains", "test_id")).andExpectAll(
				MockMvcResultMatchers.status().isOk(),
				MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
				MockMvcResultMatchers.content()
						.string(objectMapper.writeValueAsString(List.of(selectedResponseDTO, notSelectedResponseDTO))));
	}

	@Test
	void testDeleteGroup() throws Exception {
		DeleteGroupRequestDTO requestDto = new DeleteGroupRequestDTO();
		requestDto.setId("test_id");
		when(groupService.deleteGroup(any(HttpServletRequest.class), any(DeleteGroupRequestDTO.class)))
				.thenReturn("[{\"status\": \"200\",\"details\": \"Deleted successfully\"}]");
		mockMvc.perform(MockMvcRequestBuilders.delete("/v1/groups").content(objectMapper.writeValueAsString(requestDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content()
								.string("[{\"status\": \"200\",\"details\": \"Deleted successfully\"}]"));
	}

}
