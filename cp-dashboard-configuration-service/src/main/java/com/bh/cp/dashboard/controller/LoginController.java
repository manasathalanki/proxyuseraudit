package com.bh.cp.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.dashboard.dto.request.LoginRequestDTO;
import com.bh.cp.dashboard.dto.response.LoginResponseDTO;
import com.bh.cp.dashboard.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1")
@Tag(name = "Token Generator")
public class LoginController {

	private JwtUtil jwtUtil;

	public LoginController(@Autowired JwtUtil jwtUtil) {
		super();
		this.jwtUtil = jwtUtil;
	}

	@Operation(summary = "Token generation end point.")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping("/token")
	public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
		return jwtUtil.generateAccessToken(loginRequestDTO);
	}

}