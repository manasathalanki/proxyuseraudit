package com.bh.cp.proxy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.proxy.dto.request.FleetRequestDTO;
import com.bh.cp.proxy.service.FleetService;
import com.bh.cp.proxy.service.ProxyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class FleetControllerTest {

	@InjectMocks
	FleetController fleetController;
	@Mock
	private ProxyService proxyService;

	@Mock
	private FleetService fleetService;

	@Autowired
	private MockMvc mockMvc;
	private FleetRequestDTO fleetRequestDTO;
	ResponseEntity<String> responseEntity;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(fleetController).build();
		fleetRequestDTO = new FleetRequestDTO();
		fleetRequestDTO.setVid("PR_NOBLE");
		responseEntity = new ResponseEntity<String>("ok", HttpStatus.OK);
		ReflectionTestUtils.setField(fleetController, "fleetDataWidgetId", -1);
		ReflectionTestUtils.setField(fleetController, "proxyService", proxyService);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testFleetData() throws JsonProcessingException, Exception {
		 when(proxyService.execute(any(Map.class),any(HttpServletRequest.class))).thenReturn("ok");
		 mockMvc.perform(MockMvcRequestBuilders.post("/v1/fleetsdata").header("Authorization", "Bearer GFldXcxLmJha2Vya")
				.content(new ObjectMapper().writeValueAsString(fleetRequestDTO)).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string("ok"));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testFleetDataFromFleettService() throws JsonProcessingException, Exception {
		 when(fleetService.execute(any(Map.class),any(HttpServletRequest.class))).thenReturn("ok");
		 mockMvc.perform(MockMvcRequestBuilders.post("/v2/fleetsdata").header("Authorization", "Bearer GFldXcxLmJha2Vya")
				.content(new ObjectMapper().writeValueAsString(fleetRequestDTO)).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string("ok"));
	}

}
