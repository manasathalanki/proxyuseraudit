package com.bh.cp.audit.trail.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.audit.trail.dto.request.SanitizedAuditTrailUserActionDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedAuditUsageRequestDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedPerformanceRequestDTO;
import com.bh.cp.audit.trail.dto.response.PerformanceResponseDTO;
import com.bh.cp.audit.trail.service.AuditTrailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class AuditControllerTest {

	@InjectMocks
	AuditController auditController;

	SanitizedAuditUsageRequestDTO auditTrailUsage;

	SanitizedPerformanceRequestDTO performanceRequest;

	@Mock
	private AuditTrailService auditTrailService;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	private MockMvc mockMvc;

	List<PerformanceResponseDTO> listPerformanceDto;

	PerformanceResponseDTO performanceResponse;
	
	private SanitizedAuditTrailUserActionDTO auditTrailUserActionDto;
	
	@Mock
	private ModelMapper modelMapper;

	@BeforeEach
	void setup() {

		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(auditController).build();
		performanceRequest = new SanitizedPerformanceRequestDTO();
		performanceRequest.setThreadName("thread_name");
		performanceRequest.setServiceName("applicationName");
		performanceRequest.setModule("functionality");
		performanceRequest.setModuleDescription("moduleDescription");
		performanceRequest.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceRequest.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceRequest.setTotalExecutionTimeMs(123l);
		performanceRequest.setStatus(true);
		performanceRequest.setSso("sanjay");
		performanceRequest.setUri("uri");
		performanceRequest.setInputDetails("inputDetails");
		auditTrailUsage = new SanitizedAuditUsageRequestDTO();
		auditTrailUsage.setSso("sso");
		auditTrailUsage.setActivity("activity");
		auditTrailUsage.setFunctionality("functionality");
		auditTrailUsage.setStatus(true);
		auditTrailUsage.setEntryTime(new Timestamp(System.currentTimeMillis()));
		auditTrailUsage.setServiceName("applicationName");
		auditTrailUsage.setThreadName("functionality");
		auditTrailUsage.setThreadName("thread_name");
		listPerformanceDto = new ArrayList<>();

		performanceResponse = new PerformanceResponseDTO();
		performanceResponse.setId(1);
		performanceResponse.setSso("SSO");
		performanceResponse.setInputDetails("Input");
		performanceResponse.setServiceName("service");
		performanceResponse.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceResponse.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceResponse.setTotalExecutionTime(123l);
		performanceResponse.setStatus("Success");
		listPerformanceDto.add(performanceResponse);
		
		auditTrailUserActionDto= new SanitizedAuditTrailUserActionDTO();
		auditTrailUserActionDto.setActionDate(Timestamp.valueOf(LocalDateTime.now()));
		auditTrailUserActionDto.setData("Data");
		auditTrailUserActionDto.setApplication("testApplication");
		auditTrailUserActionDto.setPrimaryKey(1);
		auditTrailUserActionDto.setSchema("testSchema");
		auditTrailUserActionDto.setSso("sso");
		auditTrailUserActionDto.setTableName("testTable");
		auditTrailUserActionDto.setUserAction("CREATE");

	}

	@Test
	void testAuditTrailPerformances() throws Exception {
         when(auditTrailService.auditTrailPerformances(any(HttpServletRequest.class))).thenReturn(listPerformanceDto);
          mockMvc.perform(MockMvcRequestBuilders.get("/v1/audit/performances").contentType(MediaType.APPLICATION_JSON)
		.header("Authorization", "Bearer abc")
		.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void testSaveAuditTrailPerformances() throws Exception {
		auditTrailService.saveAuditTrailPerformances(any(SanitizedPerformanceRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/audit/performances").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer abc")
				.content(new ObjectMapper().writeValueAsString(performanceRequest)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void testSaveAuditTrailUsage() throws Exception {
		auditTrailService.saveAuditTrailUsage(any(SanitizedAuditUsageRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/audit/usage").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer abc")
				.content(new ObjectMapper().writeValueAsString(auditTrailUsage)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void testSaveAuditTrailUserAction() throws JsonProcessingException, Exception {
		auditTrailService.saveAuditTrailUserAction(any(SanitizedAuditTrailUserActionDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/audit/action").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer abc")
				.content(new ObjectMapper().writeValueAsString(auditTrailUserActionDto)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
