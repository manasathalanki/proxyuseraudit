package com.bh.cp.audit.trail.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.audit.trail.dto.request.SanitizedAuditTrailUserActionDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedAuditUsageRequestDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedPerformanceRequestDTO;
import com.bh.cp.audit.trail.dto.response.PerformanceResponseDTO;
import com.bh.cp.audit.trail.entity.AuditTrailPerformance;
import com.bh.cp.audit.trail.entity.AuditTrailTables;
import com.bh.cp.audit.trail.entity.AuditTrailUsage;
import com.bh.cp.audit.trail.entity.AuditTrailUserAction;
import com.bh.cp.audit.trail.entity.Statuses;
import com.bh.cp.audit.trail.entity.Users;
import com.bh.cp.audit.trail.repository.AuditTrailPerformanceRepository;
import com.bh.cp.audit.trail.repository.AuditTrailTablesRepository;
import com.bh.cp.audit.trail.repository.AuditTrailUsageRepository;
import com.bh.cp.audit.trail.repository.AuditTrailUserActionRepository;
import com.bh.cp.audit.trail.repository.UsersRepository;
import com.bh.cp.audit.trail.service.AuditTrailService;
import com.bh.cp.audit.trail.util.JwtUtil;
import com.bh.cp.audit.trail.util.SecurityUtil;
import com.bh.cp.audit.trail.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuditTrailServiceImpl implements AuditTrailService {

	private AuditTrailTablesRepository auditTrailTablesRepository;

	private AuditTrailUsageRepository auditTrailUsageRepository;

	private AuditTrailUserActionRepository auditTrailUserActionRepository;

	private AuditTrailPerformanceRepository auditPerformanceRepository;

	private JwtUtil jwtUtil;

	private UsersRepository usersRepository;

	public AuditTrailServiceImpl(@Autowired UsersRepository usersRepository,
			@Autowired AuditTrailPerformanceRepository auditPerformanceRepository,
			@Autowired AuditTrailUserActionRepository auditTrailUserActionRepository,
			@Autowired AuditTrailUsageRepository auditTrailUsageRepository,
			@Autowired AuditTrailTablesRepository auditTrailTablesRepository, @Autowired JwtUtil jwtUtil) {
		super();
		this.usersRepository = usersRepository;
		this.auditPerformanceRepository = auditPerformanceRepository;
		this.auditTrailTablesRepository = auditTrailTablesRepository;
		this.auditTrailUsageRepository = auditTrailUsageRepository;
		this.auditTrailUserActionRepository = auditTrailUserActionRepository;
		this.jwtUtil = jwtUtil;
	}

	private PerformanceResponseDTO performanceResponse = null;

	public AuditTrailTables saveAuditTrailTables(AuditTrailTables auditTrailTables) {
		return auditTrailTablesRepository.save(auditTrailTables);
	}

	public void saveAuditTrailUsage(SanitizedAuditUsageRequestDTO auditTrailUsage) {

		AuditTrailUsage usage = new AuditTrailUsage();
		AuditTrailUsage auditUsage = auditTrailUsageRepository.findByThreadName(auditTrailUsage.getThreadName());
		if (auditUsage != null) {
			usage.setId(auditUsage.getId());
			usage.setExitTime(auditTrailUsage.getExitTime());
		}
		usage.setActivity(auditTrailUsage.getActivity());
		usage.setEntryTime(auditTrailUsage.getEntryTime());
		usage.setFunctionality(auditTrailUsage.getFunctionality());
		usage.setStatuses(getStatus(auditTrailUsage.getStatus()));
		usage.setUsers(extractUser(auditTrailUsage.getSso()));
		usage.setServiceName(auditTrailUsage.getServiceName());
		usage.setThreadName(auditTrailUsage.getThreadName());

		auditTrailUsageRepository.save(usage);
	}

	public void saveAuditTrailUserAction(SanitizedAuditTrailUserActionDTO auditTrailUserAction) {

		AuditTrailTables auditTrailTables = new AuditTrailTables();
		auditTrailTables.setApplication(auditTrailUserAction.getApplication());
		auditTrailTables.setSchema(auditTrailUserAction.getSchema());
		auditTrailTables.setTableName(auditTrailUserAction.getTableName());
		auditTrailTables.setPrimaryKey(auditTrailUserAction.getPrimaryKey());
		if ("CREATE".equals(auditTrailUserAction.getUserAction())) {
			auditTrailTables.setCreatedBySso(auditTrailUserAction.getSso());
			auditTrailTables.setCreatedDate(auditTrailUserAction.getActionDate());
		} else {
			auditTrailTables.setUpdatedBySso(auditTrailUserAction.getSso());
			auditTrailTables.setUpdatedDate(auditTrailUserAction.getActionDate());
		}
		auditTrailTablesRepository.save(auditTrailTables);

		AuditTrailUserAction action = new AuditTrailUserAction();
		action.setUsers(extractUser(auditTrailUserAction.getSso()));
		action.setSchema(auditTrailUserAction.getSchema());
		action.setTableName(auditTrailUserAction.getTableName());
		action.setUserAction(auditTrailUserAction.getUserAction());
		action.setApplication(auditTrailUserAction.getApplication());
		action.setData(StringUtil.limitChars(auditTrailUserAction.getData(), 1999));
		action.setActionDate(auditTrailUserAction.getActionDate());
		auditTrailUserActionRepository.save(action);
	}

	@Override
	public List<PerformanceResponseDTO> auditTrailPerformances(HttpServletRequest httpServletRequest) {
		Map<String, Claim> claims = SecurityUtil.getClaims(httpServletRequest, jwtUtil);

		return auditPerformanceRepository.findByUserSso(SecurityUtil.getSSO(claims)).stream().map(performanceData -> {
			performanceResponse = new PerformanceResponseDTO();
			performanceResponse.setId(performanceData.getId());
			performanceResponse.setSso(performanceData.getUser().getSso());
			performanceResponse.setInputDetails(performanceData.getInputDetails());
			performanceResponse.setServiceName(performanceData.getServiceName());
			performanceResponse.setEndTime(performanceData.getEndTime());
			performanceResponse.setStartTime(performanceData.getStartTime());
			performanceResponse.setTotalExecutionTime(performanceData.getTotalExecutionTimeMs());
			performanceResponse.setStatus(performanceData.getStatus().getStatusType());
			return performanceResponse;
		}).toList();
	}

	@Override
	public void saveAuditTrailPerformances(SanitizedPerformanceRequestDTO sanitizedPerformanceRequestDTO) {
		AuditTrailPerformance auditTrailPerformance = new AuditTrailPerformance();
		auditTrailPerformance.setWidgetId(sanitizedPerformanceRequestDTO.getWidgetId());
		auditTrailPerformance.setServiceId(sanitizedPerformanceRequestDTO.getServiceId());
		auditTrailPerformance.setEndTime(sanitizedPerformanceRequestDTO.getEndTime());
		auditTrailPerformance.setInputDetails(StringUtil.limitChars(sanitizedPerformanceRequestDTO.getInputDetails(), 2000));
		auditTrailPerformance.setServiceName(sanitizedPerformanceRequestDTO.getServiceName());
		auditTrailPerformance.setStartTime(sanitizedPerformanceRequestDTO.getStartTime());
		auditTrailPerformance.setStatus(getStatus(sanitizedPerformanceRequestDTO.getStatus()));
		auditTrailPerformance.setThreadName(sanitizedPerformanceRequestDTO.getThreadName());
		auditTrailPerformance.setTotalExecutionTimeMs(sanitizedPerformanceRequestDTO.getTotalExecutionTimeMs());
		auditTrailPerformance.setUser(extractUser(sanitizedPerformanceRequestDTO.getSso()));
		auditTrailPerformance.setModule(sanitizedPerformanceRequestDTO.getModule());
		auditTrailPerformance.setModuleDescription(sanitizedPerformanceRequestDTO.getModuleDescription());
		auditTrailPerformance.setUri(StringUtil.limitChars(sanitizedPerformanceRequestDTO.getUri(), 255));
		auditPerformanceRepository.save(auditTrailPerformance);
	}

	private Users extractUser(String sso) {
		return usersRepository.findBySso(sso);
	}
	
	private Statuses getStatus(Boolean statusFlag) {

		Statuses status = new Statuses();
		if (Boolean.TRUE.equals(statusFlag)) {
			status.setDescription("SUCCESS");
			status.setId(5);
			status.setStatusType("SUCCESS_FAILURE");
			status.setStatusIndicator(300);
		} else {
			status.setDescription("FAILURE");
			status.setId(6);
			status.setStatusType("SUCCESS_FAILURE");
			status.setStatusIndicator(301);
		}
		return status;
	}
}
