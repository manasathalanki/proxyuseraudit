package com.bh.cp.dashboard.aop;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.dto.request.AuditTrailUserActionDTO;
import com.bh.cp.dashboard.dto.request.AuditUsageRequestDTO;
import com.bh.cp.dashboard.service.impl.AsyncAuditService;
import com.bh.cp.dashboard.util.JwtUtil;
import com.bh.cp.dashboard.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuditTrailAspect {

	private static final Logger logger = LoggerFactory.getLogger(AuditTrailAspect.class);

	private JwtUtil jwtUtil;

	private AsyncAuditService asyncAuditService;

	private final String applicationName;

	private final String usageUri;

	@SuppressWarnings("unused")
	private final String performanceUri;

	private String schema;

	private String userActionUri;

	public AuditTrailAspect(@Autowired JwtUtil jwtUtil, @Autowired AsyncAuditService asyncAuditService,
			@Value("${spring.application.name}") String applicationName,
			@Value("${spring.datasource.schema}") String schemaName,
			@Value("${cp.audit.trail.usage.uri}") String usageUri,
			@Value("${cp.audit.trail.performance.uri}") String performanceUri,
			@Value("${cp.audit.trail.useraction.uri}") String userActionUri) {
		super();
		this.jwtUtil = jwtUtil;
		this.asyncAuditService = asyncAuditService;
		this.applicationName = applicationName;
		this.schema = schemaName;
		this.usageUri = usageUri;
		this.performanceUri = performanceUri;
		this.userActionUri = userActionUri;
	}

	public void loglUsageAuditTrai(JoinPoint joinPoint, boolean checkBeforeOrAfter) {
		Timestamp entryTime = new Timestamp(System.currentTimeMillis());
		String sso = extractSso();
		boolean status = true;
		String functionality = joinPoint.getSignature().getName();
		String activity = getActivity();
		logger.info("[{}] {} ()=> {}", sso, functionality, activity);
		saveAuditTrailUsage(sso, activity, functionality, status, entryTime, checkBeforeOrAfter,
				(getHttpServletRequest().getAttribute(DashboardConstants.PERF_AUDIT_THREAD_ID)).toString());
	}

	public void logDataAuditTrail(JoinPoint joinPoint, String action) {
		AuditTrailUserActionDTO auditTrailUserActionDTO = new AuditTrailUserActionDTO();
		auditTrailUserActionDTO.setApplication(applicationName);
		auditTrailUserActionDTO.setSchema(schema);
		auditTrailUserActionDTO.setSso(extractSso());
		auditTrailUserActionDTO.setData(applicationName);
		JSONObject json = new JSONObject();
		if (joinPoint.getArgs()[0] instanceof HttpServletRequest) {
			if (joinPoint.getArgs()[1] != null) {
				json.put("data",joinPoint.getArgs()[1]);
			}
		} else {
			json.put("data",joinPoint.getArgs()[0]);
		}
		auditTrailUserActionDTO.setTableName(joinPoint.getTarget().getClass().toString());
		auditTrailUserActionDTO.setUserAction(action);
		auditTrailUserActionDTO.setActionDate(Timestamp.valueOf(LocalDateTime.now()));
		auditTrailUserActionDTO.setData(json.toString());
		logger.info("##################### logAuditTrailAfterUserAction()-> {}", auditTrailUserActionDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.set(DashboardConstants.KEY_AUTHORIZATION,
				getHttpServletRequest().getHeader(DashboardConstants.KEY_AUTHORIZATION));
		HttpEntity<AuditTrailUserActionDTO> entity = new HttpEntity<>(auditTrailUserActionDTO, headers);
		asyncAuditService.saveAuditTrailUserAction(userActionUri, entity);
	}

	@Before("execution(* com.bh.cp.dashboard.controller.AssetController.*(..))")
	public void logAuditTrailBeforeAssetController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, false);
	}

	@Before("execution(* com.bh.cp.dashboard.controller.DashboardController.*(..))")
	public void logAuditTrailBeforeDashboardController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, false);
	}

	@Before("execution(* com.bh.cp.dashboard.controller.UserSettingsController.*(..))")
	public void logAuditTrailBeforeUserSettingsController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, false);
	}

	@Before("execution(* com.bh.cp.dashboard.controller.WidgetController.*(..))")
	public void logAuditTrailBeforeWidgetController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, false);
	}

	@After("execution(* com.bh.cp.dashboard.controller.AssetController.*(..))")
	public void logAuditTrailAfterAssetController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, true);
	}

	@After("execution(* com.bh.cp.dashboard.controller.DashboardController.*(..))")
	public void logAuditTrailAfterDashboardController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, true);
	}

	@After("execution(* com.bh.cp.dashboard.controller.UserSettingsController.*(..))")
	public void logAuditTrailAfterUserSettingsController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, true);
	}

	@After("execution(* com.bh.cp.dashboard.controller.WidgetController.*(..))")
	public void logAuditTrailAfterWidgetController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, true);
	}

	@After("execution(* com.bh.cp.dashboard.service.impl.WidgetServiceImpl.addWidgets(..))")
	public void logAuditTrailAfterAddWidgets(JoinPoint joinPoint) {
		logDataAuditTrail(joinPoint, DashboardConstants.CREATE);
	}

	@After("execution(* com.bh.cp.dashboard.service.impl.WidgetServiceImpl.deleteWidgets(..))")
	public void logAuditTrailAfterDeleteWidgets(JoinPoint joinPoint) {
		logDataAuditTrail(joinPoint, DashboardConstants.DELETE);
	}

	@After("execution(* com.bh.cp.dashboard.service.impl.AssetServiceImpl.addAssets(..))")
	public void logAuditTrailAfterAddAssets(JoinPoint joinPoint) {
		logDataAuditTrail(joinPoint, DashboardConstants.CREATE);
	}

	@After("execution(* com.bh.cp.dashboard.service.impl.AssetServiceImpl.deleteAssets(..))")
	public void logAuditTrailAfterDeleteAssets(JoinPoint joinPoint) {
		logDataAuditTrail(joinPoint, DashboardConstants.DELETE);
	}

	@After("execution(* com.bh.cp.dashboard.service.impl.UserSettingsServiceImpl.updateUserSettings(..))")
	public void logAuditTrailAfterBaseEntitDelete(JoinPoint joinPoint) {
		logDataAuditTrail(joinPoint, DashboardConstants.UPDATE);
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
		headers.set(DashboardConstants.KEY_AUTHORIZATION,
				getHttpServletRequest().getHeader(DashboardConstants.KEY_AUTHORIZATION));
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