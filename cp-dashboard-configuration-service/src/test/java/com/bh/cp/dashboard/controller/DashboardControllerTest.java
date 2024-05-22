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

import com.bh.cp.dashboard.dto.request.LayoutRequestDTO;
import com.bh.cp.dashboard.dto.response.LayoutResponseDTO;
import com.bh.cp.dashboard.service.AssetService;
import com.bh.cp.dashboard.service.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class DashboardControllerTest {

	@InjectMocks
	private DashboardController dashboardController;

	@Mock
	private DashboardService dashboardService;

	@Mock
	private AssetService assetService;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
	}

	@Test
	@DisplayName("GetLayout - with input vid as null and level as Projects -Positive")
	void testGetLayout_Positive() throws Exception {
		LayoutResponseDTO expectedResponse = new LayoutResponseDTO();
		expectedResponse.setDateRange("test");
		when(dashboardService.getUserDashboardLayout(any(HttpServletRequest.class), any(LayoutRequestDTO.class)))
				.thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/layouts").content("{\"vid\":null,\"level\":\"projects\"}")
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content()
								.string(new ObjectMapper().writeValueAsString(expectedResponse)));
	}

}
