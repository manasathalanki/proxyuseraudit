package com.bh.cp.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import com.bh.cp.user.pojo.PolicyResponse;
import com.bh.cp.user.service.impl.PrivilegeServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class PrivilegesControllerTest {

	@InjectMocks
	private PrivilegeController privilegesController;

	@Mock
	private PrivilegeServiceImpl privilegesService;

	private List<PolicyResponse> listPolicyResponse;

	private PolicyResponse policyResponse;

	@Mock
	private HttpServletRequest httpServletRequest;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(privilegesController).build();
		policyResponse = new PolicyResponse();
		policyResponse.setDescription("test-data");
		policyResponse.setId("test-id");
		policyResponse.setName("test-name");
		listPolicyResponse = new ArrayList<>();
		listPolicyResponse.add(policyResponse);
	}

	@Test
	void testGetAllPrivileges() throws JsonProcessingException, Exception {
		        when(privilegesService.getAllPrivilege(any(HttpServletRequest.class))).thenReturn(listPolicyResponse);
		        mockMvc.perform(MockMvcRequestBuilders.get("/v1/privileges").header("Authorization", "Bearer abc")
						.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
								MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
								MockMvcResultMatchers.content()
										.string(new ObjectMapper().writeValueAsString(listPolicyResponse)));
		        verify(privilegesService).getAllPrivilege(any(HttpServletRequest.class));
	}
}
