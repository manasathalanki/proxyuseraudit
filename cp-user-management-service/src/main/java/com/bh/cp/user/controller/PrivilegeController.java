package com.bh.cp.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.user.pojo.PolicyResponse;
import com.bh.cp.user.service.PrivilegeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("v1/privileges")
@Tag(name = "Privilege Controller")
public class PrivilegeController {

	private PrivilegeService privilegesService;

	public PrivilegeController(@Autowired PrivilegeService privilegesService) {
		super();
		this.privilegesService = privilegesService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Getting all Privileges", description = "Retrieve all the Privileges")
	public List<PolicyResponse> getAllPrivileges(HttpServletRequest httpServletRequest) {
		return privilegesService.getAllPrivilege(httpServletRequest);
	}

}
