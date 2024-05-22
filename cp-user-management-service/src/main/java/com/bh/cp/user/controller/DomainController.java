package com.bh.cp.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.keycloak.representations.idm.GroupRepresentation;
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

import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.exception.DeletionNotPermissableException;
import com.bh.cp.user.pojo.DomainAttribute;
import com.bh.cp.user.service.DomainService;
import com.bh.cp.user.service.GenericAssetHierarchyFilterService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("v1/domains")
@Tag(name = "Domain Controller")
public class DomainController {

	private DomainService domainService;

	private GenericAssetHierarchyFilterService assetHierarchyFilterService;

	public DomainController(@Autowired DomainService domainService,
			@Autowired GenericAssetHierarchyFilterService assetHierarchyFilterService) {
		super();
		this.domainService = domainService;
		this.assetHierarchyFilterService = assetHierarchyFilterService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Getting All Domains", description = "Retrieve all the Domains")
	@SecurityRequirement(name = "Keycloak Token")
	public List<DomainResponseDTO> getDomains(HttpServletRequest httpServletRequest) {
		return domainService.getAllDomains(httpServletRequest);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create a new Domain", description = "Create a new Domain with Assets Associated")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<String> createDomain(@RequestBody GroupRepresentation request,
			HttpServletRequest httpServletRequest) throws JsonProcessingException {
		return domainService.createDomain(request, httpServletRequest);
	}

	@GetMapping(value = "/{domainId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get a Domain", description = "Getting exiting Domain with Assets Associated")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<GroupRepresentation> updateDomain(@PathVariable String domainId,
			HttpServletRequest httpServletRequest) throws JsonProcessingException {
		return domainService.getDomain(domainId, httpServletRequest);
	}

	@GetMapping(value = "/{domainId}/view", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "View a Domain", description = "Viewing exiting Domain with Assets Associated")
	@SecurityRequirement(name = "Keycloak Token")
	public Map<String, List<DomainAttribute>> viewDomain(@PathVariable String domainId,
			HttpServletRequest httpServletRequest) throws IOException {
		return domainService.viewDomain(domainId, httpServletRequest);
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update Domain", description = "update exiting Domain with Assets Associated")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<String> updateDomain(@RequestBody GroupRepresentation request,
			HttpServletRequest httpServletRequest) throws JsonProcessingException {
		return domainService.updateDomain(request, httpServletRequest);
	}

	@DeleteMapping(value = "/{domainId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete a Domain", description = "Delete exiting Domain")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<String> deleteDomain(@PathVariable String domainId, HttpServletRequest httpServletRequest)
			throws JsonProcessingException, DeletionNotPermissableException, InterruptedException, ExecutionException {
		return domainService.deleteDomain(domainId, httpServletRequest);
	}

	@GetMapping(value = "/hierarchy/{level}", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Getting all Assets by level", description = "Getting all Assets under each level of catageory")
	@SecurityRequirement(name = "Keycloak Token")
	public String retrieveFullHierarchyByLevel(@PathVariable("level") String level) throws IOException {
		return assetHierarchyFilterService.getAssetHierarchyByLevel(level);
	}

}
