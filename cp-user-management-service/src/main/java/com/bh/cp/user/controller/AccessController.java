package com.bh.cp.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.user.dto.request.AssetHierarchyRequestDTO;
import com.bh.cp.user.dto.request.LoginRequestDTO;
import com.bh.cp.user.dto.request.WidgetAccessRequestDTO;
import com.bh.cp.user.dto.request.WidgetApplicableRequestDTO;
import com.bh.cp.user.dto.response.AssetResponseDTO;
import com.bh.cp.user.dto.response.LoginResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.dto.response.UserResponseDTO;
import com.bh.cp.user.dto.response.WidgetAccessResponseDTO;
import com.bh.cp.user.dto.response.WidgetApplicableResponseDTO;
import com.bh.cp.user.exception.AttributeNotFoundException;
import com.bh.cp.user.mixin.AssetHierarchyMixIn;
import com.bh.cp.user.service.AccessService;
import com.bh.cp.user.service.UserAssetHierarchyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.NotFoundException;

@RestController
@RequestMapping("v1/me")
@Tag(name = "Access Controller")
public class AccessController {

	private AccessService accessService;

	private UserAssetHierarchyService userAssetHierarchyService;

	public AccessController(@Autowired AccessService accessService,
			@Autowired UserAssetHierarchyService userAssetHierarchyService) {
		super();
		this.accessService = accessService;
		this.userAssetHierarchyService = userAssetHierarchyService;
	}

	@Operation(summary = "Token generation end point.")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping("/token")
	public LoginResponseDTO login(@RequestBody LoginRequestDTO loginCredential) {
		return accessService.generateAccessToken(loginCredential);
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Getting Current Logged in User details in short", description = "Getting Current Logged in User details in short")
	@SecurityRequirement(name = "Keycloak Token")
	public UserResponseDTO getUserDetails(HttpServletRequest httpServletRequest)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException {
		return accessService.getUserDetails(httpServletRequest);
	}

	@GetMapping(value = "/details", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Getting Current Logged in User details", description = "Getting Current Logged in User details")
	@SecurityRequirement(name = "Keycloak Token")
	public UserDetailsResponseDTO retrieveCurrentUserDetail(HttpServletRequest httpServletRequest)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException {
		return accessService.getCurrentUserCombinedDetails(httpServletRequest);
	}

	@GetMapping(value = "/hierarchy", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Getting User's Asset Hierarchy", description = "Getting Asset hierarchy of Current Logged in User")
	@SecurityRequirement(name = "Keycloak Token")
	public String retrieveCurrentUserHierarchy(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.addMixIn(Map.class, AssetHierarchyMixIn.class);
		return mapper.writeValueAsString(accessService.getCurrentUserFilteredHierarchy(httpServletRequest));
	}

	@GetMapping(value = "/privileges", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Getting Current User Privileges", description = "Getting Privileges of Current Logged in User")
	@SecurityRequirement(name = "Keycloak Token")
	public List<String> retrieveCurrentUserPrivileges(HttpServletRequest httpServletRequest)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException {
		return accessService.getCurrentUserPrivileges(httpServletRequest);
	}

	@PostMapping(value = "/widget/access", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Validate Widget Access", description = "Getting Access Information of Widget for Current Logged in User")
	@SecurityRequirement(name = "Keycloak Token")
	public WidgetAccessResponseDTO checkWidgetAccess(HttpServletRequest httpServletRequest,
			@RequestBody WidgetAccessRequestDTO requestDto) throws AttributeNotFoundException, NotFoundException,
			InterruptedException, ExecutionException, IOException {
		return userAssetHierarchyService.checkWidgetAccess(httpServletRequest, requestDto);
	}

	@PostMapping(value = "/widget/machines", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Get Applicable Machines for Widget", description = "Get Applicable Machines for Widget from Current Logged in User Asset Hierarchy")
	@SecurityRequirement(name = "Keycloak Token")
	public WidgetApplicableResponseDTO getUserApplicableMachinesForWidget(HttpServletRequest httpServletRequest,
			@RequestBody WidgetApplicableRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		return userAssetHierarchyService.getUserApplicableMachinesForWidget(httpServletRequest, requestDto);
	}

	@GetMapping(value = "/widget/{widgetId}/subscription", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Check Subscribed Widget for the User", description = "Getting Widget Subscription for the Current Logged in User")
	@SecurityRequirement(name = "Keycloak Token")
	public Map<String, Boolean> checkWidgetKeycloakAccess(HttpServletRequest httpServletRequest,
			@PathVariable("widgetId") Integer widgetId)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException {
		return userAssetHierarchyService.checkWidgetKeycloakAccess(httpServletRequest, widgetId);
	}
	
	@PostMapping(value = "/widget/access/advanceservices", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Check Advance Service Access for the Widget", description = "Getting Advance Services Access for the Widget")
	@SecurityRequirement(name = "Keycloak Token")
	public Map<String, Boolean> checkWidgetAdvanceServicesAccess(HttpServletRequest httpServletRequest,
			@RequestBody WidgetAccessRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		return userAssetHierarchyService.checkWidgetAdvanceServicesAccess(httpServletRequest, requestDto);
	}

	@PostMapping(value = "/hierarchy/children", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Getting Child Hierarchy from User's Asset Hierarchy", description = "Getting Child hierarchy of provided VID from Current Logged in User's Asset Hierarchy")
	@SecurityRequirement(name = "Keycloak Token")
	public List<Map<String, Object>> retrieveUserAssetHierarchyChildren(HttpServletRequest httpServletRequest,
			@RequestBody AssetHierarchyRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		return userAssetHierarchyService.getUserAssetHierarchyChildren(httpServletRequest, requestDto);
	}

	@PostMapping(value = "/hierarchy/field", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Getting Parent Asset's Field Value from User's Asset Hierarchy", description = "Getting Field Value from Parent of provided VID in Current Logged in User's Asset Hierarchy")
	@SecurityRequirement(name = "Keycloak Token")
	public Map<String, String> retrieveUserAssetHierarchyParentField(HttpServletRequest httpServletRequest,
			@RequestBody AssetHierarchyRequestDTO requestDto,
			@RequestParam(defaultValue = "false", required = false) boolean parent)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		return userAssetHierarchyService.getUserAssetHierarchyField(httpServletRequest, requestDto, parent);
	}

	@PostMapping(value = "/hierarchy/assets", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Getting Assets as Map from User's Asset Hierarchy", description = "Getting Assets as Map from User's Asset Hierarchy")
	@SecurityRequirement(name = "Keycloak Token")
	public AssetResponseDTO retrieveUserAssetHierarchyAssets(HttpServletRequest httpServletRequest,
			@RequestBody AssetHierarchyRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		return userAssetHierarchyService.getUserAssetHierarchyAssets(httpServletRequest, requestDto);
	}

}