package com.bh.cp.user.aop;

import java.sql.Timestamp;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.user.constants.SecurityUtilConstants;
import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.request.AuditTrailUserActionDTO;
import com.bh.cp.user.dto.request.AuditUsageRequestDTO;
import com.bh.cp.user.service.impl.AsyncAuditService;
import com.bh.cp.user.util.JwtUtil;
import com.bh.cp.user.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuditTrailAspect {

	private static final Logger logger = LoggerFactory.getLogger(AuditTrailAspect.class);

	private JwtUtil jwtUtil;

	private AsyncAuditService asyncAuditService;

	private final String applicationName;

	private final String usageUri;

	private String userActionUri;

	public AuditTrailAspect(@Autowired JwtUtil jwtUtil, @Autowired AsyncAuditService asyncAuditService,
			@Value("${spring.application.name}") String applicationName,
			@Value("${cp.audit.trail.usage.uri}") String usageUri,
			@Value("${cp.audit.trail.useraction.uri}") String userActionUri) {
		super();
		this.jwtUtil = jwtUtil;
		this.asyncAuditService = asyncAuditService;
		this.applicationName = applicationName;
		this.usageUri = usageUri;
		this.userActionUri = userActionUri;
	}

	public void logAuditTrail(JoinPoint joinPoint, boolean checkBeforeOrAfter) {
		Timestamp entryTime = new Timestamp(System.currentTimeMillis());
		String sso = extractSso();
		boolean status = true;
		String functionality = joinPoint.getSignature().getName();
		String activity = getActivity();
		logger.info("[{}] {} ()=> {}", sso, functionality, activity);
		saveAuditTrailUsage(sso, activity, functionality, status, entryTime, checkBeforeOrAfter,
				MDC.get(UMSConstants.PERF_AUDIT_THREAD_ID));
	}

	private void logDataAuditTrail(JoinPoint joinPoint, String functionality, String action) {
		AuditTrailUserActionDTO auditTrailUserActionDTO = new AuditTrailUserActionDTO();
		auditTrailUserActionDTO.setApplication(applicationName);
		auditTrailUserActionDTO.setSchema("keycloak");
		auditTrailUserActionDTO.setTableName(functionality);
		auditTrailUserActionDTO.setPrimaryKey(1);
		auditTrailUserActionDTO.setUserAction(action);
		auditTrailUserActionDTO.setActionDate(new Timestamp(System.currentTimeMillis()));
		auditTrailUserActionDTO.setSso(extractSso());
		JSONObject json = null;
		if (joinPoint.getArgs()[0] instanceof HttpServletRequest) {
			if (joinPoint.getArgs()[1] != null) {
				json= new JSONObject(joinPoint.getArgs()[1]);
			}
		} else {
			json= new JSONObject(joinPoint.getArgs()[0]);
		}
		auditTrailUserActionDTO.setData(json == null ? "":json.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.set(SecurityUtilConstants.KEY_AUTHORIZATION,
				getHttpServletRequest().getHeader(SecurityUtilConstants.KEY_AUTHORIZATION));
		HttpEntity<AuditTrailUserActionDTO> entity = new HttpEntity<>(auditTrailUserActionDTO, headers);
		asyncAuditService.saveAuditTrailUserAction(userActionUri, entity);
	}

	@Before("execution(* com.bh.cp.user.controller.DomainController.*(..))")
	public void logAuditTrailBeforeDomainController(JoinPoint joinPoint) {
		logAuditTrail(joinPoint, false);
	}
	
	@After("execution(* com.bh.cp.user.controller.DomainController.*(..))")
	public void logAuditTrailBeforeDomainControllerAfter(JoinPoint joinPoint) {
		logAuditTrail(joinPoint, true);
		if(joinPoint.getSignature().getName().equals("createDomain")) {
			logDataAuditTrail(joinPoint, "Create Domains", UMSConstants.CREATE);
		}
		else if(joinPoint.getSignature().getName().equals("updateDomain")) {
			logDataAuditTrail(joinPoint, "Update Domains", UMSConstants.UPDATE);
		}
		else if(joinPoint.getSignature().getName().equals("deleteDomain")) {
			logDataAuditTrail(joinPoint, "Delete Domains", UMSConstants.DELETE);
		}
	}

	@Before("execution(* com.bh.cp.user.controller.RoleController.*(..))")
	public void logAuditTrailBeforeRoleController(JoinPoint joinPoint) {
		logAuditTrail(joinPoint, false);
	}
	
	@After("execution(* com.bh.cp.user.controller.RoleController.*(..))")
	public void logAuditTrailBeforeRoleControllerAfter(JoinPoint joinPoint) {
		logAuditTrail(joinPoint, true);
		if(joinPoint.getSignature().getName().equals("createRoleAssociatePrivileges")) {
			logDataAuditTrail(joinPoint, "Create Roles", UMSConstants.CREATE);
		}
		else if(joinPoint.getSignature().getName().equals("updateRoleAssociatePrivileges")) {
			logDataAuditTrail(joinPoint, "Update Roles", UMSConstants.UPDATE);
		}
		else if(joinPoint.getSignature().getName().equals("deleteRole")) {
			logDataAuditTrail(joinPoint, "Delete Roles", UMSConstants.DELETE);
		}
	}

	@Before("execution(* com.bh.cp.user.controller.UserController.*(..))")
	public void logAuditTrailBeforeUserController(JoinPoint joinPoint) {
		logAuditTrail(joinPoint, false);
	}
	
	@After("execution(* com.bh.cp.user.controller.UserController.*(..))")
	public void logAuditTrailBeforeUserControllerAfter(JoinPoint joinPoint) {
		logAuditTrail(joinPoint, true);
		if(joinPoint.getSignature().getName().equals("createUsers")) {
			logDataAuditTrail(joinPoint, "Create user", UMSConstants.CREATE);
		}
		else if(joinPoint.getSignature().getName().equals("editUsers")) {
			logDataAuditTrail(joinPoint, "Update user", UMSConstants.UPDATE);
		}
		else if(joinPoint.getSignature().getName().equals("enableDisableUser")) {
			logDataAuditTrail(joinPoint, "Delete user", UMSConstants.DELETE);
		}
	}
	
	@Before("execution(* com.bh.cp.user.controller.GroupController.*(..))")
	public void logAuditTrailBeforeGroupController(JoinPoint joinPoint) {
		logAuditTrail(joinPoint, false);
	}


	@After("execution(* com.bh.cp.user.controller.GroupController.*(..))")
	public void logAuditTrailBeforeGroupControllerAfter(JoinPoint joinPoint) {
		logAuditTrail(joinPoint, false);
		if(joinPoint.getSignature().getName().equals("createGroup")) {
			logDataAuditTrail(joinPoint, "Create Groups", UMSConstants.CREATE);
		}
		else if(joinPoint.getSignature().getName().equals("editGroup")) {
			logDataAuditTrail(joinPoint, "Update Groups", UMSConstants.UPDATE);
		}
		else if(joinPoint.getSignature().getName().equals("deleteGroup")) {
			logDataAuditTrail(joinPoint, "Delete Groups", UMSConstants.DELETE);
		}
	}
	

	private void saveAuditTrailUsage(String sso, String activity, String functionality, boolean statusFlag,
			Timestamp entryTime, boolean checkBeforeOrAfter, String threadName) {

		AuditUsageRequestDTO auditTrailUsage = new AuditUsageRequestDTO();
		if (checkBeforeOrAfter) {
			auditTrailUsage.setExitTime(new Timestamp(System.currentTimeMillis()));
		}
		auditTrailUsage.setSso(sso);
		auditTrailUsage.setActivity(activity);
		auditTrailUsage.setFunctionality(functionality);
		auditTrailUsage.setStatus(statusFlag);
		auditTrailUsage.setEntryTime(entryTime);
		auditTrailUsage.setServiceName(applicationName);
		auditTrailUsage.setThreadName(threadName);

		HttpHeaders headers = new HttpHeaders();
		headers.set(SecurityUtilConstants.KEY_AUTHORIZATION,
				getHttpServletRequest().getHeader(SecurityUtilConstants.KEY_AUTHORIZATION));
		HttpEntity<AuditUsageRequestDTO> entity = new HttpEntity<>(auditTrailUsage, headers);
		asyncAuditService.saveAuditTrailUsage(usageUri, entity);
	}

	public String getActivity() {
		return getHttpServletRequest().getMethod();
	}

	private String extractSso() {
		Map<String, Claim> claims = SecurityUtil.getClaims(getHttpServletRequest(), jwtUtil);
		return SecurityUtil.getSSO(claims);
	}

	private HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

	}
}