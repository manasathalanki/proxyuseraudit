package com.bh.cp.dashboard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.dashboard.dto.request.UserSettingsRequestDTO;
import com.bh.cp.dashboard.dto.response.UserSettingsResponseDTO;
import com.bh.cp.dashboard.service.UserSettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class UserSettingsControllerTest {

	@InjectMocks
	private UserSettingsController userSettingsController;

	@Mock
	private UserSettingsService userSettingsService;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	private ObjectMapper objectMapper = new ObjectMapper();

	private MockMvc mockMvc;

	private UserSettingsResponseDTO settingsResponseDTO;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userSettingsController).build();
		settingsResponseDTO = new UserSettingsResponseDTO();
		settingsResponseDTO.setDisplayAssetName("0");
	}

	@Test
	@DisplayName("GetUserSettings - Positive")
	void testGetUserSettings_Positive() throws Exception {
		when(userSettingsService.getUserSettings(any(HttpServletRequest.class))).thenReturn(settingsResponseDTO);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/settings")
				).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(objectMapper
								.writeValueAsString(settingsResponseDTO)));
	}

	@Test
	@DisplayName("UpdateUserSettings - Positive")
	void testUpdateUserSettings_Positive() throws Exception {
		settingsResponseDTO.setDisplayAssetName("1");
		when(userSettingsService.updateUserSettings(any(HttpServletRequest.class), any(UserSettingsRequestDTO.class)))
				.thenReturn(settingsResponseDTO);
		mockMvc.perform(MockMvcRequestBuilders.patch("/v1/settings").content(
				"{\"timeZone\":\"UTC\",\"displayAssetName\":\"Customer\",\"retiredAssets\":\"AllAssets\",\"uom\":\"UK\",\"personaId\":1}")
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(settingsResponseDTO)));
	}

}
