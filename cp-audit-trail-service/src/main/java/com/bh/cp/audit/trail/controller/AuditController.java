package com.bh.cp.audit.trail.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.audit.trail.dto.request.AuditTrailUserActionDTO;
import com.bh.cp.audit.trail.dto.request.AuditUsageRequestDTO;
import com.bh.cp.audit.trail.dto.request.PerformanceRequestDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedAuditTrailUserActionDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedAuditUsageRequestDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedPerformanceRequestDTO;
import com.bh.cp.audit.trail.dto.response.PerformanceResponseDTO;
import com.bh.cp.audit.trail.service.AuditTrailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1")
public class AuditController {

	private AuditTrailService auditTrailService;

	private ModelMapper mapper;

	public AuditController(@Autowired AuditTrailService auditTrailService, @Autowired ModelMapper mapper) {
		super();
		this.auditTrailService = auditTrailService;
		this.mapper = mapper;
	}

	@GetMapping("/audit/performances")
	public List<PerformanceResponseDTO> auditTrailPerformances(HttpServletRequest httpServletRequest) {
		return auditTrailService.auditTrailPerformances(httpServletRequest);
	}

	@PostMapping("/audit/performances")
	public void saveAuditTrailPerformances(@Valid @RequestBody PerformanceRequestDTO auditTrailPerformance) {
		auditTrailService.saveAuditTrailPerformances(mapper.map(auditTrailPerformance, SanitizedPerformanceRequestDTO.class));
	}

	@PostMapping("/audit/usage")
	public void saveAuditTrailUsage(@Valid @RequestBody AuditUsageRequestDTO auditTrailUsage) {
		auditTrailService.saveAuditTrailUsage(mapper.map(auditTrailUsage, SanitizedAuditUsageRequestDTO.class));
	}

	@PostMapping("/audit/action")
	public void saveAuditTrailUserAction(@Valid @RequestBody AuditTrailUserActionDTO auditTrailUserActionDTO) {
		auditTrailService.saveAuditTrailUserAction(mapper.map(auditTrailUserActionDTO, SanitizedAuditTrailUserActionDTO.class));
	}

}
