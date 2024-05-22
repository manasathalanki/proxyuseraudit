package com.bh.cp.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.user.constants.JSONUtilConstants;
import com.bh.cp.user.dto.request.AssetHierarchyRequestDTO;
import com.bh.cp.user.dto.request.LoginRequestDTO;
import com.bh.cp.user.dto.request.WidgetAccessRequestDTO;
import com.bh.cp.user.dto.request.WidgetApplicableRequestDTO;
import com.bh.cp.user.dto.response.AssetResponseDTO;
import com.bh.cp.user.dto.response.LoginResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.dto.response.UserResponseDTO;
import com.bh.cp.user.dto.response.WidgetAccessResponseDTO;
import com.bh.cp.user.dto.response.WidgetApplicableResponseDTO;
import com.bh.cp.user.service.AccessService;
import com.bh.cp.user.service.UserAssetHierarchyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class AccessControllerTest {

	@InjectMocks
	private AccessController accessController;

	@Mock
	private AccessService accessService;

	@Mock
	private UserAssetHierarchyService userAssetHierarchyService;

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private HttpServletRequest httpServletRequest;

	private LoginRequestDTO loginRequestDTO;

	private LoginResponseDTO loginResponseDTO;

	private UserResponseDTO userResponseDTO;

	private UserDetailsResponseDTO userDetailsResponseDTO;

	private WidgetAccessResponseDTO widgetAccessResponseDTO;

	private WidgetAccessRequestDTO widgetAccessRequestDTO;

	private WidgetApplicableRequestDTO widgetApplicableRequestDTO;

	private WidgetApplicableResponseDTO widgetApplicableResponseDTO;

	private AssetHierarchyRequestDTO assetHierarchyRequestDTO;

	private AssetResponseDTO assetResponseDTO;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(accessController).build();
		loginRequestDTO = new LoginRequestDTO();
		loginRequestDTO.setPassword("test@password");
		loginRequestDTO.setUsername("testUser");
		loginResponseDTO = new LoginResponseDTO();
		loginResponseDTO.setToken("token");
		loginResponseDTO.setUsername("testUser");
		userResponseDTO = new UserResponseDTO();
		userResponseDTO.setEmail("test@gmail.com");
		userResponseDTO.setFirstName("test");
		userResponseDTO.setLastName("test");
		userResponseDTO.setName("test");
		userResponseDTO.setTitle("Internal");
		userResponseDTO.setPrivileges(new ArrayList<>());
		userDetailsResponseDTO = new UserDetailsResponseDTO();
		userDetailsResponseDTO.setAttributes(new ArrayList<>());
		widgetAccessResponseDTO = new WidgetAccessResponseDTO();
		widgetAccessRequestDTO = new WidgetAccessRequestDTO();
		widgetApplicableRequestDTO = new WidgetApplicableRequestDTO();
		widgetApplicableResponseDTO = new WidgetApplicableResponseDTO(new ArrayList<>());
		assetHierarchyRequestDTO = new AssetHierarchyRequestDTO();
		assetResponseDTO = new AssetResponseDTO();
		ReflectionTestUtils.setField(accessController, "accessService", accessService);
	}

	@Test
	@DisplayName("Login - generating token")
	void testLogin() throws JsonProcessingException, Exception {
		   when(accessService.generateAccessToken(any(LoginRequestDTO.class))).thenReturn(loginResponseDTO);
		   mockMvc.perform(MockMvcRequestBuilders.post("/v1/me/token").content(objectMapper.writeValueAsString(loginRequestDTO))
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(loginResponseDTO)));
	}

	@Test
	@DisplayName("GetUserDetails - User Details")
	void testGetUserDetails() throws JsonProcessingException, Exception {
		   when(accessService.getUserDetails(any(HttpServletRequest.class))).thenReturn(userResponseDTO);
		   mockMvc.perform(MockMvcRequestBuilders.get("/v1/me/")
					).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(userResponseDTO)));
	}

	@Test
	@DisplayName("RetrieveCurrentUserDetail - Current User Details")
	void testRetrieveCurrentUserDetail() throws JsonProcessingException, Exception {
		   when(accessService.getCurrentUserCombinedDetails(any(HttpServletRequest.class))).thenReturn(userDetailsResponseDTO);
		   mockMvc.perform(MockMvcRequestBuilders.get("/v1/me/details")
					).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(userDetailsResponseDTO)));
	}

	@Test
	@DisplayName("RetrieveCurrentUserHierarchy - Getting User's Asset Hierarchy")
	void testRetrieveCurrentUserHierarchy() throws JsonProcessingException, Exception {
		   when(accessService.getCurrentUserFilteredHierarchy(any(HttpServletRequest.class))).thenReturn(new ArrayList<>());
		   mockMvc.perform(MockMvcRequestBuilders.get("/v1/me/hierarchy")
					).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new ArrayList<>())));
	}

	@Test
	@DisplayName("RetrieveCurrentUserPrivileges - Getting Current User Privileges")
	void testRetrieveCurrentUserPrivileges() throws JsonProcessingException, Exception {
		   when(accessService.getCurrentUserPrivileges(any(HttpServletRequest.class))).thenReturn(new ArrayList<>());
		   mockMvc.perform(MockMvcRequestBuilders.get("/v1/me/privileges")
					).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new ArrayList<>())));
	}

	@Test
	@DisplayName("CheckWidgetAccess - Validate Widget Access")
	void testCheckWidgetAccess() throws JsonProcessingException, Exception {
		   when(userAssetHierarchyService.checkWidgetAccess(any(HttpServletRequest.class),any(WidgetAccessRequestDTO.class))).thenReturn(widgetAccessResponseDTO);
		   mockMvc.perform(MockMvcRequestBuilders.post("/v1/me/widget/access").content(objectMapper.writeValueAsString(widgetAccessRequestDTO))
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(widgetAccessResponseDTO)));
	}

	@Test
	@DisplayName("GetUserApplicableMachinesForWidget - Get Applicable Machines for Widget")
	void testGetUserApplicableMachinesForWidget() throws JsonProcessingException, Exception {
		   when(userAssetHierarchyService.getUserApplicableMachinesForWidget(any(HttpServletRequest.class),any(WidgetApplicableRequestDTO.class))).thenReturn(widgetApplicableResponseDTO);
		   mockMvc.perform(MockMvcRequestBuilders.post("/v1/me/widget/machines").content(objectMapper.writeValueAsString(widgetApplicableRequestDTO))
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(widgetApplicableResponseDTO)));
	}

	@Test
	@DisplayName("CheckWidgetKeycloakAccess - Check Subscribed Widget for the User")
	void testCheckWidgetKeycloakAccess() throws JsonProcessingException, Exception {
		   when(userAssetHierarchyService.checkWidgetKeycloakAccess(any(HttpServletRequest.class),any(Integer.class))).thenReturn(new HashMap<>());
		   mockMvc.perform(MockMvcRequestBuilders.get("/v1/me/widget/1/subscription")
					).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new HashMap<>())));
	}

	@Test
	@DisplayName("RetrieveUserAssetHierarchyChildren - Getting Child Hierarchy from User's Asset Hierarchy")
	void testRetrieveUserAssetHierarchyChildren() throws JsonProcessingException, Exception {
		   when(userAssetHierarchyService.getUserAssetHierarchyChildren(any(HttpServletRequest.class),any(AssetHierarchyRequestDTO.class))).thenReturn(new ArrayList<>());
		   mockMvc.perform(MockMvcRequestBuilders.post("/v1/me/hierarchy/children").content(objectMapper.writeValueAsString(assetHierarchyRequestDTO))
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new ArrayList<>())));
	}

	@Test
	@DisplayName("RetrieveUserAssetHierarchyParentField - Getting Parent Asset's Field Value from User's Asset Hierarchy")
	void testRetrieveUserAssetHierarchyParentField() throws JsonProcessingException, Exception {
		   when(userAssetHierarchyService.getUserAssetHierarchyField(any(HttpServletRequest.class),any(AssetHierarchyRequestDTO.class),any(Boolean.class))).thenReturn(new HashMap<>());
		   mockMvc.perform(MockMvcRequestBuilders.post("/v1/me/hierarchy/field").content(objectMapper.writeValueAsString(assetHierarchyRequestDTO))
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new HashMap<>())));
	}

	@Test
	@DisplayName("RetrieveUserAssetHierarchyAssets - Getting Assets as Map from User's Asset Hierarchy")
	void testRetrieveUserAssetHierarchyAssets() throws JsonProcessingException, Exception {
		   when(userAssetHierarchyService.getUserAssetHierarchyAssets(any(HttpServletRequest.class),any(AssetHierarchyRequestDTO.class))).thenReturn(assetResponseDTO);
		   mockMvc.perform(MockMvcRequestBuilders.post("/v1/me/hierarchy/assets").content(objectMapper.writeValueAsString(assetHierarchyRequestDTO))
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(assetResponseDTO)));
	}


	@Test
	@DisplayName("CheckWidgetAdvanceServicesAccess - Validate Widget Advance Services Access")
	void testCheckWidgetAdvanceServicesAccess() throws JsonProcessingException, Exception {
		Map<String, Boolean> expectedResponse = new HashMap<>();
		expectedResponse.put(JSONUtilConstants.ENABLEDSERVICES, true);
		when(userAssetHierarchyService.checkWidgetAdvanceServicesAccess(any(HttpServletRequest.class),
				any(WidgetAccessRequestDTO.class))).thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/me/widget/access/advanceservices")
				.content(objectMapper.writeValueAsString(widgetAccessRequestDTO))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedResponse)));
	}
}
