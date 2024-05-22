package com.bh.cp.proxy.aop;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.jboss.logging.MDC;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.auth0.jwt.impl.NullClaim;
import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.dto.request.AuditTrailUserActionDTO;
import com.bh.cp.proxy.dto.request.AuditUsageRequestDTO;
import com.bh.cp.proxy.dto.request.PerformanceRequestDTO;
import com.bh.cp.proxy.pojo.AuditDate;
import com.bh.cp.proxy.service.AsyncAuditService;
import com.bh.cp.proxy.util.JwtUtil;

class AuditTrailAspectTest {

	@InjectMocks
	private AuditTrailAspect auditTrailAspect;

	@Mock
	private AsyncAuditService asyncAuditService;

	@Mock
	private JwtUtil jwtUtil;

	private Map<String, Claim> claims;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	private PerformanceRequestDTO performanceRequest;

	private AuditDate auditDate;

	private AuditUsageRequestDTO auditTrailUsage;
	
	private AuditTrailUserActionDTO auditTrailUserActionDto;
	
	Object[] args= {"test1","test2"};

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		MDC.put(ProxyConstants.PERF_AUDIT_WIDGET_ID, "1");
		MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID,"2");
		claims = new HashMap<>();
		claims.put("preferred_username", new NullClaim());
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		auditTrailUserActionDto = new AuditTrailUserActionDTO();
		auditTrailUserActionDto.setActionDate(Timestamp.valueOf(LocalDateTime.now()));
		auditTrailUserActionDto.setData("Data");
		auditTrailUserActionDto.setApplication("testApplication");
		auditTrailUserActionDto.setPrimaryKey(1);
		auditTrailUserActionDto.setSchema("testSchema");
		auditTrailUserActionDto.setSso("sso");
		auditTrailUserActionDto.setTableName("testTable");
		auditTrailUserActionDto.setUserAction("CREATE");
		auditTrailUsage = new AuditUsageRequestDTO();
		auditTrailUsage.setSso("sso");
		auditTrailUsage.setActivity("activity");
		auditTrailUsage.setFunctionality("functionality");
		auditTrailUsage.setStatus(true);
		auditTrailUsage.setEntryTime(new Timestamp(System.currentTimeMillis()));
		auditTrailUsage.setServiceName("applicationName");
		auditTrailUsage.setThreadName("functionality");
		auditTrailUsage.setThreadName("thread_name");
		performanceRequest = new PerformanceRequestDTO();
		performanceRequest.setThreadName("thread_name");
		performanceRequest.setServiceName("applicationName");
		performanceRequest.setModule("functionality");
		performanceRequest.setWidgetId(Integer.parseInt((String) MDC.get(ProxyConstants.PERF_AUDIT_WIDGET_ID)));
		performanceRequest.setServiceId(Integer.parseInt((String)MDC.get(ProxyConstants.PERF_AUDIT_SERVICE_ID)));
		performanceRequest.setModuleDescription("moduleDescription");
		performanceRequest.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceRequest.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceRequest.setTotalExecutionTimeMs(123l);
		performanceRequest.setStatus(true);
		performanceRequest.setSso("sso");
		performanceRequest.setUri("uri");
		performanceRequest.setInputDetails("inputDetails");
		auditDate = new AuditDate(Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()), 0);
	}

	@Test
	void testLogAuditTrailUsageBeforeAndAfter() {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		JoinPoint joinPoint = mock(JoinPoint.class);
		MethodSignature signature = mock(MethodSignature.class);
		Signature signatureObj = mock(Signature.class);
		when(joinPoint.getSignature()).thenReturn(signatureObj);
		auditTrailAspect.logAuditTrailBeforeGenericController(joinPoint);
		auditTrailAspect.logAuditTrailAfterGenericController(joinPoint);
		assertNotNull(signature);
		assertNotNull(sso);
	}

	@ParameterizedTest
	@ValueSource(strings = {"com.bh.cp.proxy.controller.GenericDataController.widgetsData",
			"com.bh.cp.proxy.controller.GenericDataController.casesData",
			"com.bh.cp.proxy.controller.GenericDataController.casesAttachment",
			"com.bh.cp.proxy.controller.GenericDataController.retrieveCasesData",
			"com.bh.cp.proxy.controller.GenericDataController.casesAttachmentFile",
			"com.bh.cp.proxy.asset.service.impl.UMSClientServiceImpl.getUserAssetHierarchy",
			"com.bh.cp.proxy.asset.service.impl.UMSClientServiceImpl.getWidgetAccess",
			"com.bh.cp.proxy.adapter.impl.RestServicesAdapter.execute",
			"com.bh.cp.proxy.handler.JsonResponseHandler.format",
			"com.bh.cp.proxy.service.ProxyService.execute",
			"com.bh.cp.proxy.handler.impl","CaseDetailsAppender"} )
	void testLogAuditTrailPerformanceGenericDataController(String methodName) {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		JSONObject json = new JSONObject();
		json.put("uri", "http://localhost");
		auditTrailAspect.saveAuditTrailPerformance(methodName, json, auditDate, false);
		assertNotNull(performanceRequest);
		assertNotNull(sso);
	}

	@Test
	void testLogAuditTrailPerformanceNullMethodName() {
		String methodName = "test";
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		auditTrailAspect.saveAuditTrailPerformance(methodName, null, auditDate, false);
		assertNotNull(performanceRequest);
		assertNotNull(sso);
	}
	
	@Test
	void testLogAuditTrailUserAction() {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		JoinPoint joinPoint = mock(JoinPoint.class);
		MethodSignature signature = mock(MethodSignature.class);
		when(joinPoint.getArgs()).thenReturn(args);
		when(joinPoint.getTarget()).thenReturn(new Object());
		auditTrailAspect.logAuditUserActionGenericDataControllerCasesAttachmentFile(joinPoint);
		auditTrailAspect.logAuditUserActionGenericDataControllerCasesAttachment(joinPoint);
		auditTrailAspect.logAuditUserActionGenericDataControllerSaveCaseDetailsData(joinPoint);
		assertNotNull(signature);
		assertNotNull(sso);
	}
	
	@Test
	void testLogAuditTrailUserActionHttpServletRequest() {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		JoinPoint joinPoint = mock(JoinPoint.class);
		MethodSignature signature = mock(MethodSignature.class);
		args[0]=mockHttpServletRequest;
		args[1]="data";
		when(joinPoint.getArgs()).thenReturn(args);
		when(joinPoint.getTarget()).thenReturn(new Object());
		auditTrailAspect.logAuditUserActionGenericDataControllerCasesAttachmentFile(joinPoint);
		auditTrailAspect.logAuditUserActionGenericDataControllerCasesAttachment(joinPoint);
		auditTrailAspect.logAuditUserActionGenericDataControllerSaveCaseDetailsData(joinPoint);
		
		assertNotNull(signature);
		assertNotNull(sso);
	}

}
