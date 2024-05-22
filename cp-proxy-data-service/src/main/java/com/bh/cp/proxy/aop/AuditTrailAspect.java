package com.bh.cp.proxy.aop;

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
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.dto.request.AuditTrailUserActionDTO;
import com.bh.cp.proxy.dto.request.AuditUsageRequestDTO;
import com.bh.cp.proxy.dto.request.PerformanceRequestDTO;
import com.bh.cp.proxy.pojo.AuditDate;
import com.bh.cp.proxy.service.AsyncAuditService;
import com.bh.cp.proxy.util.JwtUtil;
import com.bh.cp.proxy.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;

@Component
@Aspect
public class AuditTrailAspect {

	Logger logger = LoggerFactory.getLogger(AuditTrailAspect.class);

	private String applicationName;

	private String usageUri;

	private String performanceUri;

	private JwtUtil jwtUtil;

	private AsyncAuditService asyncAuditService;
	
	private String schema;
	
	private String userActionUri;

	public AuditTrailAspect(@Autowired AsyncAuditService asyncAuditService, @Autowired JwtUtil jwtUtil,
			@Value("${cp.audit.trail.performance.uri}") String performanceUri,
			@Value("${cp.audit.trail.usage.uri}") String usageUri,
			@Value("${spring.datasource.schema}") String schemaName,
			@Value("${cp.audit.trail.useraction.uri}") String userActionUri,
			@Value("${spring.application.name}") String applicationName) {
		this.applicationName = applicationName;
		this.asyncAuditService = asyncAuditService;
		this.jwtUtil = jwtUtil;
		this.performanceUri = performanceUri;
		this.usageUri = usageUri;
		this.schema=schemaName;
		this.userActionUri=userActionUri;
	}

	public void saveAuditTrailPerformance(String functionality, JSONObject json, AuditDate auditDate,
			boolean statusFlag) {

		PerformanceRequestDTO performanceRequest = new PerformanceRequestDTO();
		performanceRequest.setThreadName(MDC.get(ProxyConstants.PERF_AUDIT_THREAD_ID));
		performanceRequest.setServiceName(this.applicationName);
		performanceRequest.setModule(functionality);
		performanceRequest.setModuleDescription(getModuleDescription(functionality));
		performanceRequest.setStartTime(auditDate.getStartTime());
		performanceRequest.setEndTime(auditDate.getEndTime());
		performanceRequest.setTotalExecutionTimeMs(auditDate.getExecutionTime());
		performanceRequest.setStatus(statusFlag);
		performanceRequest.setSso(extractSso());
		performanceRequest.setUri(extractUri(json));
		if (MDC.get(ProxyConstants.PERF_AUDIT_WIDGET_ID) != null) {
			performanceRequest.setWidgetId(Integer.parseInt(MDC.get(ProxyConstants.PERF_AUDIT_WIDGET_ID)));
		} else if (MDC.get(ProxyConstants.PERF_AUDIT_SERVICE_ID) != null) {
			performanceRequest.setServiceId(Integer.parseInt(MDC.get(ProxyConstants.PERF_AUDIT_SERVICE_ID)));
		}
		performanceRequest.setInputDetails(extractInputDetails(json));
		HttpEntity<PerformanceRequestDTO> entity = new HttpEntity<>(performanceRequest, getHttpHeaders());
		asyncAuditService.saveAuditTrailPerformance(performanceUri, entity);
	}

	public void loglUsageAuditTrai(JoinPoint joinPoint, boolean checkBeforeOrAfter) {
		Timestamp entryTime = new Timestamp(System.currentTimeMillis());
		String sso = extractSso();
		boolean status = true;
		String functionality = joinPoint.getSignature().getName();
		String activity = getActivity();
		logger.info("[{}] {} ()=> {}", sso, functionality, activity);
		saveAuditTrailUsage(sso, activity, functionality, status, entryTime, checkBeforeOrAfter,
				MDC.get(ProxyConstants.PERF_AUDIT_THREAD_ID));
	}

	@After("execution(* com.bh.cp.proxy.controller.GenericDataController.casesAttachmentFile(..) )")
	public void logAuditUserActionGenericDataControllerCasesAttachmentFile(JoinPoint joinPoint) {
		logDataAuditTrail(joinPoint,"UPLOAD_FILES");
	}
	
	@After("execution(* com.bh.cp.proxy.controller.GenericDataController.saveCaseDetailsData(..) )")
	public void logAuditUserActionGenericDataControllerSaveCaseDetailsData(JoinPoint joinPoint) {
		logDataAuditTrail(joinPoint,"SAVE_CASE");
	}
	
	@After("execution(* com.bh.cp.proxy.controller.GenericDataController.casesAttachment(..) )")
	public void logAuditUserActionGenericDataControllerCasesAttachment(JoinPoint joinPoint) {
		logDataAuditTrail(joinPoint,"DOWNLOAD_ATTACHMENT");
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
		headers.set(ProxyConstants.KEY_AUTHORIZATION,
				getHttpServletRequest().getHeader(ProxyConstants.KEY_AUTHORIZATION));
		HttpEntity<AuditUsageRequestDTO> entity = new HttpEntity<>(auditTrailUsage, headers);
		asyncAuditService.saveAuditTrailUsage(usageUri, entity);
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
		headers.set(ProxyConstants.KEY_AUTHORIZATION,
				getHttpServletRequest().getHeader(ProxyConstants.KEY_AUTHORIZATION));
		HttpEntity<AuditTrailUserActionDTO> entity = new HttpEntity<>(auditTrailUserActionDTO, headers);
		asyncAuditService.saveAuditTrailUserAction(userActionUri, entity);
	}

	@Before("execution(* com.bh.cp.proxy.controller.GenericDataController.*(..))")
	public void logAuditTrailBeforeGenericController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, false);
	}

	@After("execution(* com.bh.cp.proxy.controller.GenericDataController.*(..))")
	public void logAuditTrailAfterGenericController(JoinPoint joinPoint) {
		loglUsageAuditTrai(joinPoint, true);
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

	private HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(ProxyConstants.KEY_AUTHORIZATION,
				getHttpServletRequest().getHeader(ProxyConstants.KEY_AUTHORIZATION));
		return headers;
	}

	private String extractInputDetails(JSONObject json) {
		if (json != null) {
			return json.toString().substring(0, json.toString().length() > 2000 ? 1999 : json.toString().length());
		}
		return null;
	}

	private String extractUri(JSONObject json) {
		if (json != null && json.has("uri")) {
			return json.get("uri").toString();
		}
		return null;
	}

	private String getModuleDescription(String methodName) {
		switch (methodName) {
		case "com.bh.cp.proxy.controller.GenericDataController.widgetsData":
			return "Proxy Data controller Layer Widgets Data";
		case "com.bh.cp.proxy.controller.GenericDataController.casesData":
			return "Proxy Data controller Layer Cases Data";
		case "com.bh.cp.proxy.controller.GenericDataController.casesAttachment":
			return "Proxy Data controller Layer Cases Attachment";
		case "com.bh.cp.proxy.controller.GenericDataController.retrieveCasesData":
			return "Proxy Data controller Layer Retrieve Cases Data";
		case "com.bh.cp.proxy.controller.GenericDataController.casesAttachmentFile":
			return "Proxy Data controller Layer Cases Attachment File";
		case "com.bh.cp.proxy.asset.service.impl.UMSClientServiceImpl.getUserAssetHierarchy":
			return "Perform REST Call to UMS for user accessable assets";
		case "com.bh.cp.proxy.asset.service.impl.UMSClientServiceImpl.getWidgetAccess":
			return "Perform REST Call to UMS for user accessable widgets";
		case "com.bh.cp.proxy.adapter.impl.RestServicesAdapter.execute":
			return "Perform REST call for External API";
		case "com.bh.cp.proxy.handler.JsonResponseHandler.format":
			return "Response Handlers used to format output from external APIs output";
		case "com.bh.cp.proxy.service.ProxyService.execute":
			return "Proxy data service Layer";
		default:
			if (methodName.startsWith("com.bh.cp.proxy.handler.impl")) {
				return "Response Handlers used to format output from external APIs output";
			}
			else if(methodName.contains("CaseDetailsAppender")) {
				return "Case Details Appender";
			}
			return null;
		}
	}
}