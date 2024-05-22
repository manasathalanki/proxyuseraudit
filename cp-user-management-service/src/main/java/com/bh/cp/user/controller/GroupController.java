package com.bh.cp.user.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.user.dto.request.CreateGroupRequestDTO;
import com.bh.cp.user.dto.request.DeleteGroupRequestDTO;
import com.bh.cp.user.dto.request.EditGroupRequestDTO;
import com.bh.cp.user.dto.response.GroupResponseDTO;
import com.bh.cp.user.dto.response.SelectedResponseDTO;
import com.bh.cp.user.exception.DeletionNotPermissableException;
import com.bh.cp.user.service.GroupService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("v1/groups")
@Tag(name = "Group Controller")
public class GroupController {

	private GroupService groupService;

	public GroupController(@Autowired GroupService groupService) {
		super();
		this.groupService = groupService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get All Groups", description = "Retrieve all the Groups")
	public List<GroupResponseDTO> getAllGroups(HttpServletRequest httpServletRequest) {
		return groupService.getAllGroups(httpServletRequest);
	}

	@GetMapping(value = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get Group Details for Particular Group", description = "Retrieve Roles,Users,Domains Associated To Particular Group")
	public GroupResponseDTO getDetailsForGroup(HttpServletRequest httpServletRequest,
			@PathVariable("groupId") String groupId) throws InterruptedException, ExecutionException {
		return groupService.getGroupDetails(httpServletRequest, groupId);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create a new Group", description = "Create a new Group with Domains,Roles,Users Associated")
	public ResponseEntity<String> createGroup(HttpServletRequest httpServletRequest,
			@RequestBody CreateGroupRequestDTO requestDto) throws JsonProcessingException {
		return ResponseEntity.of(Optional.of(groupService.createGroup(httpServletRequest, requestDto)));
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Edit existing Group", description = "Edit Associated Domains,Roles,Users in existing Group")
	public ResponseEntity<String> editGroup(HttpServletRequest httpServletRequest,
			@RequestBody EditGroupRequestDTO requestDto)
			throws JsonProcessingException, InterruptedException, ExecutionException {
		return ResponseEntity.of(Optional.of(groupService.editGroup(httpServletRequest, requestDto)));
	}

	@GetMapping(value = "/{groupId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get Existing Users for Particular Group", description = "Retrieve Users previously Associated To Particular Group")
	public List<SelectedResponseDTO> getUsersForGroup(HttpServletRequest httpServletRequest,
			@PathVariable("groupId") String groupId)
			throws JsonProcessingException, InterruptedException, ExecutionException {
		return groupService.getUserDetailsFromGroup(httpServletRequest, groupId);
	}

	@GetMapping(value = "/{groupId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get Existing Roles for Particular Group", description = "Retrieve Roles previously Associated To Particular Group")
	public List<SelectedResponseDTO> getRolesForGroup(HttpServletRequest httpServletRequest,
			@PathVariable("groupId") String groupId)
			throws JsonProcessingException, InterruptedException, ExecutionException {
		return groupService.getRoleDetailsFromGroup(httpServletRequest, groupId);
	}

	@GetMapping(value = "/{groupId}/domains", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get Existing Domains for Particular Group", description = "Retrieve Domains previously Associated To Particular Group")
	public List<SelectedResponseDTO> getDomainsForGroup(HttpServletRequest httpServletRequest,
			@PathVariable("groupId") String groupId)
			throws JsonProcessingException, InterruptedException, ExecutionException {
		return groupService.getDomainDetailsFromGroup(httpServletRequest, groupId);
	}

	@DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete existing Group", description = "Delete existing Group")
	public ResponseEntity<String> deleteGroup(HttpServletRequest httpServletRequest,
			@RequestBody DeleteGroupRequestDTO requestDto)
			throws JsonProcessingException, DeletionNotPermissableException, InterruptedException, ExecutionException {
		return ResponseEntity.of(Optional.of(groupService.deleteGroup(httpServletRequest, requestDto)));
	}

}
