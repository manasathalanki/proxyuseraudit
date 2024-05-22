package com.bh.cp.user.controller;

import java.util.List;
import java.util.Optional;

import javax.management.relation.InvalidRoleInfoException;

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

import com.bh.cp.user.dto.response.RoleResponseDTO;
import com.bh.cp.user.exception.RoleException;
import com.bh.cp.user.pojo.Role;
import com.bh.cp.user.service.RoleService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("v1/roles")
@Tag(name = "Role Controller")
public class RoleController {

	private RoleService rolesService;

	public RoleController(@Autowired RoleService rolesService) {
		super();
		this.rolesService = rolesService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get All Roles", description = "Retrieve all the Roles")
	public List<RoleResponseDTO> getAllRoles(HttpServletRequest httpServletRequest) throws JsonProcessingException {
		return rolesService.getAllRoles(httpServletRequest);
	}

	@GetMapping(value = "/{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get Privileges for Particular Role", description = "Retrieve Privileges under Particular Role")
	public RoleResponseDTO getPrivilegesForRole(HttpServletRequest httpServletRequest,
			@PathVariable("roleId") String roleId) {
		return rolesService.getPrivilegesForRole(httpServletRequest, roleId);
	}

	@PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create role", description = "Create role and associate privileges")
	public ResponseEntity<Object> createRoleAssociatePrivileges(@RequestBody Role role)
			throws JsonProcessingException, RoleException {
		return ResponseEntity.of(Optional.of(rolesService.createRoleAssociatePrivileges(role)));
	}

	@PutMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update role privileges", description = "update privileges to respective role")
	public ResponseEntity<Object> updateRoleAssociatePrivileges(@RequestBody Role role)
			throws JsonProcessingException, RoleException {
		return rolesService.updateRoleAssociatePrivileges(role);
	}

	@DeleteMapping(value = "/{roleId}")
	@Operation(summary = "Delete role", description = "Client Role will be deleted and if the role is associated with group/user/privileges will be Unlinked")
	public ResponseEntity<Object> deleteRole(@PathVariable String roleId)
			throws RoleException, InvalidRoleInfoException {
		return rolesService.deleteRole(roleId);
	}
}
