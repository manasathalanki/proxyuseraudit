package com.bh.cp.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.user.dto.request.CreateUserRequestDTO;
import com.bh.cp.user.dto.request.DeleteUserRequestDTO;
import com.bh.cp.user.dto.request.EditUserRequestDTO;
import com.bh.cp.user.dto.response.SelectedResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserService userService;

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper = new ObjectMapper();

	private UserDetailsResponseDTO userResponseDTO;

	private SelectedResponseDTO selectedResponseDTO;

	private SelectedResponseDTO notSelectedResponseDTO;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

		userResponseDTO = new UserDetailsResponseDTO("test_id", "test_user", "test@gmail");
		selectedResponseDTO = new SelectedResponseDTO();
		selectedResponseDTO.setId("selected_id");
		selectedResponseDTO.setName("selected_name");
		selectedResponseDTO.setSelected(true);

		notSelectedResponseDTO = new SelectedResponseDTO();
		notSelectedResponseDTO.setId("notselected_id");
		notSelectedResponseDTO.setName("notselected_name");
		notSelectedResponseDTO.setSelected(false);

	}

	@Test
	void testgetUsersDetails() throws Exception {
		when(userService.getUsersCombinedDetails(any(HttpServletRequest.class),anyString())).thenReturn(userResponseDTO);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{userId}","test_id")
				).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(objectMapper
								.writeValueAsString(new UserDetailsResponseDTO("test_id","test_user","test@gmail"))));
	}

	@Test
	void testGetAllUsers() throws Exception {
		when(userService.getAllUsers(any(HttpServletRequest.class))).thenReturn(List.of(userResponseDTO));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/users")
				).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(objectMapper
								.writeValueAsString(List.of(userResponseDTO))));
	}

	@Test
	void testCreateUsers() throws Exception {
		CreateUserRequestDTO requestDto = new CreateUserRequestDTO();
		requestDto.setUserName("test_user");
		requestDto.setEmail("test@gamil.com");
		when(userService.createUser(any(HttpServletRequest.class), any(CreateUserRequestDTO.class)))
				.thenReturn("[{\"status\": \"201\",\"details\": \"Created successfully\"}]");
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/users").content(objectMapper.writeValueAsString(requestDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content()
								.string("[{\"status\": \"201\",\"details\": \"Created successfully\"}]"));
	}

	@Test
	void testEditGroup() throws Exception {
		EditUserRequestDTO requestDto = new EditUserRequestDTO();
		requestDto.setEmail("test@gamil.com");
		when(userService.editUsers(any(HttpServletRequest.class), any(EditUserRequestDTO.class)))
				.thenReturn("[{\"status\": \"200\",\"details\": \"Updated successfully\"}]");
		mockMvc.perform(MockMvcRequestBuilders.put("/v1/users").content(objectMapper.writeValueAsString(requestDto))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content()
								.string("[{\"status\": \"200\",\"details\": \"Updated successfully\"}]"));
	}

	@Test
	void testGetRolesForUser() throws Exception {
		when(userService.getRoleDetailsFromUser(any(HttpServletRequest.class), anyString()))
				.thenReturn(List.of(selectedResponseDTO, notSelectedResponseDTO));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{userId}/roles", "test_id")).andExpectAll(
				MockMvcResultMatchers.status().isOk(),
				MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
				MockMvcResultMatchers.content()
						.string(objectMapper.writeValueAsString(List.of(selectedResponseDTO, notSelectedResponseDTO))));
	}

	@Test
	void testGetDomainsForUser() throws Exception {
		when(userService.getDomainDetailsFromUser(any(HttpServletRequest.class), anyString()))
				.thenReturn(List.of(selectedResponseDTO, notSelectedResponseDTO));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{userId}/domains", "test_id")).andExpectAll(
				MockMvcResultMatchers.status().isOk(),
				MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
				MockMvcResultMatchers.content()
						.string(objectMapper.writeValueAsString(List.of(selectedResponseDTO, notSelectedResponseDTO))));
	}

	@Test
	void testGetUsersForGroup() throws Exception {
		selectedResponseDTO.setEmail("selected@gmail.com");
		notSelectedResponseDTO.setEmail("notselected@gmail.com");
		when(userService.getGroupDetailsFromUser(any(HttpServletRequest.class), anyString()))
				.thenReturn(List.of(selectedResponseDTO, notSelectedResponseDTO));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{userId}/groups", "test_id")).andExpectAll(
				MockMvcResultMatchers.status().isOk(),
				MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
				MockMvcResultMatchers.content()
						.string(objectMapper.writeValueAsString(List.of(selectedResponseDTO, notSelectedResponseDTO))));
	}

	@Test
	void testEnableDisableUser() throws Exception {
		DeleteUserRequestDTO requestDto = new DeleteUserRequestDTO();
		requestDto.setId("test_id");
		when(userService.enableDisableUser(any(HttpServletRequest.class), any(DeleteUserRequestDTO.class)))
				.thenReturn("[{\"status\": \"200\",\"details\": \"Deleted successfully\"}]");
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/v1/users")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)));
		resultActions.andExpect(status().isOk());
		String responseContent = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("[{\"status\": \"200\",\"details\": \"Deleted successfully\"}]", responseContent);
	}

	@Test
		void testgetDetailsForUser() throws Exception {
			when(userService.getDetailsFromUser(any(HttpServletRequest.class),anyString())).thenReturn(userResponseDTO);
			mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{userId}/details","test_id")
					).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper
									.writeValueAsString(new UserDetailsResponseDTO("test_id","test_user","test@gmail"))));
		}

}
