package com.bh.cp.audit.trail.service;

import java.util.List;

import com.bh.cp.audit.trail.dto.request.SanitizedAuditTrailUserActionDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedAuditUsageRequestDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedPerformanceRequestDTO;
import com.bh.cp.audit.trail.dto.response.PerformanceResponseDTO;
import com.bh.cp.audit.trail.entity.AuditTrailTables;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditTrailService {

	public AuditTrailTables saveAuditTrailTables(AuditTrailTables auditTrailTables);

	public void saveAuditTrailUsage(SanitizedAuditUsageRequestDTO auditTrailUsage);

	public void saveAuditTrailUserAction(SanitizedAuditTrailUserActionDTO auditTrailUserAction);

	public List<PerformanceResponseDTO> auditTrailPerformances(HttpServletRequest httpServletRequest);

	public void saveAuditTrailPerformances(SanitizedPerformanceRequestDTO auditTrailPerformance) ;


}
