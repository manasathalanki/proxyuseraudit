package com.bh.cp.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.dashboard.dto.request.UserSettingsRequestDTO;
import com.bh.cp.dashboard.dto.response.UserSettingsResponseDTO;
import com.bh.cp.dashboard.service.UserSettingsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;

@RestController
@RequestMapping("v1/settings")
@Tag(name = "User Settings Controller")
public class UserSettingsController {

	private UserSettingsService userSettingsService;

	public UserSettingsController(@Autowired UserSettingsService userSettingsService) {
		super();
		this.userSettingsService = userSettingsService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Getting user settings.", description = "Getting settings for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<UserSettingsResponseDTO> getUserSettings(HttpServletRequest httpServletRequest)
			throws NotFoundException {
		return ResponseEntity.ok(userSettingsService.getUserSettings(httpServletRequest));
	}

	@PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update user settings", description = "Update settings data for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<UserSettingsResponseDTO> updateUserSettings(HttpServletRequest httpServletRequest,
			@Valid @RequestBody UserSettingsRequestDTO settingsRequestDTO) throws NotFoundException {
		UserSettingsResponseDTO updateUserSettings = userSettingsService.updateUserSettings(httpServletRequest,
				settingsRequestDTO);
		updateUserSettings.setStatus("Settings saved successfully");
		return ResponseEntity.ok(updateUserSettings);
	}

}
