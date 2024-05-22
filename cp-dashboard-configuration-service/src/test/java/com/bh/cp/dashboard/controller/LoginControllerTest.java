package com.bh.cp.dashboard.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.dashboard.dto.request.LoginRequestDTO;
import com.bh.cp.dashboard.dto.response.LoginResponseDTO;
import com.bh.cp.dashboard.util.JwtUtil;

 class LoginControllerTest {
	
	@InjectMocks
	LoginController loginController;

	private MockMvc mockMvc;
	
	@Mock
	private JwtUtil jwtUtil;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
	
	}
	@Test
	void testLogin() throws Exception{
		LoginRequestDTO requestDTO=new LoginRequestDTO();
		requestDTO.setUsername("username");
		requestDTO.setPassword("password");
		LoginResponseDTO responseDTO=new LoginResponseDTO();
		responseDTO.setToken("token");
		responseDTO.setUsername("username");
		when(jwtUtil.generateAccessToken(requestDTO)).thenReturn(responseDTO);
		mockMvc.perform(post("/v1/token").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"username\",\"password\":\"password\"}")).andExpect(status().isOk());
	}
}
