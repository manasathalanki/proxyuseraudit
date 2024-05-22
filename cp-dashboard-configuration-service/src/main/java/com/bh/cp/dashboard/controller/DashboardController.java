package com.bh.cp.dashboard.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.dashboard.dto.request.LayoutRequestDTO;
import com.bh.cp.dashboard.dto.response.LayoutResponseDTO;
import com.bh.cp.dashboard.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

@RestController
@RequestMapping("v1")
@Tag(name = "User Dashboard Controller")
public class DashboardController {

	private DashboardService dashboardService;

	public DashboardController(@Autowired DashboardService dashboardService) {
		super();
		this.dashboardService = dashboardService;
	}

	@PostMapping(value = "/layouts", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Getting dashboard layout.", description = "Getting dashboard layout for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<LayoutResponseDTO> getLayout(HttpServletRequest httpServletRequest,
			@RequestBody LayoutRequestDTO requestDto) throws IOException, NotFoundException {
		return ResponseEntity.ok(dashboardService.getUserDashboardLayout(httpServletRequest, requestDto));
	}
}
