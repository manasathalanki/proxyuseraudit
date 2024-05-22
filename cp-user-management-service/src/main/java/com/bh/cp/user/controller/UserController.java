package com.bh.cp.user.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.user.dto.request.CreateUserRequestDTO;
import com.bh.cp.user.dto.request.DeleteUserRequestDTO;
import com.bh.cp.user.dto.request.EditUserRequestDTO;
import com.bh.cp.user.dto.response.SelectedResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.exception.AttributeNotFoundException;
import com.bh.cp.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("v1/users")
@Tag(name = "User Controller")
public class UserController {

	private UserService userService;

	public UserController(@Autowired UserService userService) {
		super();
		this.userService = userService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all Keycloak Users", description = "Retrieve all Keycloak Users")
	public List<UserDetailsResponseDTO> getAllUsers(HttpServletRequest httpServletRequest) {
		return userService.getAllUsers(httpServletRequest);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create new Keycloak User", description = "Create a User with Domain,Group,Role associated")
	public ResponseEntity<String> createUsers(HttpServletRequest httpServletRequest,
			@RequestBody CreateUserRequestDTO requestDto) throws JsonProcessingException {
		return ResponseEntity.of(Optional.of(userService.createUser(httpServletRequest, requestDto)));
	}

	@GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get User details along with associated Group Roles and Domains for particular Keycloak User", description = "Retrieve associated Groups with Roles and Domains,Roles and Domains of particular Keycloak User")
	public UserDetailsResponseDTO getUsersDetails(HttpServletRequest httpServletRequest,
			@PathVariable("userId") String userId)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException {
		return userService.getUsersCombinedDetails(httpServletRequest, userId);
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Edit existing Keycloak User", description = "Edit associated Domains,Roles,Groups in existing Keycloak User")
	public ResponseEntity<String> editUsers(HttpServletRequest httpServletRequest,
			@RequestBody EditUserRequestDTO requestDto) throws JsonProcessingException, AttributeNotFoundException {
		return ResponseEntity.of(Optional.of(userService.editUsers(httpServletRequest, requestDto)));
	}

	@GetMapping(value = "/{userId}/details", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get User details for particular Keycloak User", description = "Retrieve User details of Keycloak User")
	public UserDetailsResponseDTO getDetailsForUser(HttpServletRequest httpServletRequest,
			@PathVariable("userId") String userId) throws AttributeNotFoundException {
		return userService.getDetailsFromUser(httpServletRequest, userId);
	}

	@GetMapping(value = "/{userId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get existing Roles for particular Keycloak User", description = "Retrieve Roles previously associated to particular Keycloak User")
	public List<SelectedResponseDTO> getRolesForUser(HttpServletRequest httpServletRequest,
			@PathVariable("userId") String userId) throws JsonProcessingException {
		return userService.getRoleDetailsFromUser(httpServletRequest, userId);
	}

	@GetMapping(value = "/{userId}/domains", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get existing Domains for particular Keycloak User", description = "Retrieve Domains previously associated to particular Keycloak User")
	public List<SelectedResponseDTO> getDomainsForUser(HttpServletRequest httpServletRequest,
			@PathVariable("userId") String userId) throws JsonProcessingException {
		return userService.getDomainDetailsFromUser(httpServletRequest, userId);
	}

	@GetMapping(value = "/{userId}/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get existing Group for particular Keycloak User", description = "Retrieve Groups previously associated to particular Keycloak User")
	public List<SelectedResponseDTO> getGroupsForUsers(HttpServletRequest httpServletRequest,
			@PathVariable("userId") String userId) throws JsonProcessingException {
		return userService.getGroupDetailsFromUser(httpServletRequest, userId);
	}

	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Enable/Disable existing Keycloak User", description = "Enable/Disable existing Keycloak User")
	public ResponseEntity<String> enableDisableUser(HttpServletRequest httpServletRequest,
			@RequestBody DeleteUserRequestDTO requestDto) throws JsonProcessingException {
		return ResponseEntity.of(Optional.of(userService.enableDisableUser(httpServletRequest, requestDto)));
	}
}
