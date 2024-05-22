package com.bh.cp.user.aop;

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
import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.request.AuditTrailUserActionDTO;
import com.bh.cp.user.dto.request.AuditUsageRequestDTO;
import com.bh.cp.user.service.impl.AsyncAuditService;
import com.bh.cp.user.util.JwtUtil;

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

	private AuditTrailUserActionDTO auditTrailUserActionDto;

	private Object[] args = { "addWidget", "deleteWidgets", "addAssets" };

	private AuditUsageRequestDTO auditTrailUsage;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		claims = new HashMap<>();
		claims.put("preferred_username", new NullClaim());
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		mockHttpServletRequest.setAttribute((UMSConstants.PERF_AUDIT_THREAD_ID), "threadName");
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
	}

	@ParameterizedTest
	@ValueSource(strings = {"createDomain","updateDomain","deleteDomain","createGroup","editGroup","deleteGroup","createRoleAssociatePrivileges","updateRoleAssociatePrivileges","deleteRole","createUsers","editUsers","enableDisableUser"} )
	void testLogAuditTrailUserActionHttpServletRequest(String functionality) {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		JoinPoint joinPoint = mock(JoinPoint.class);
		Signature signature = mock(Signature.class);
		args[0]=mockHttpServletRequest;
		args[1]="data";
		when(joinPoint.getArgs()).thenReturn(args);
		when(joinPoint.getTarget()).thenReturn(new Object());
		when(joinPoint.getSignature()).thenReturn(signature);
		when(joinPoint.getSignature().getName()).thenReturn(functionality);
		auditTrailAspect.logAuditTrailBeforeDomainControllerAfter(joinPoint);
		auditTrailAspect.logAuditTrailBeforeGroupControllerAfter(joinPoint);
		auditTrailAspect.logAuditTrailBeforeRoleControllerAfter(joinPoint);
		auditTrailAspect.logAuditTrailBeforeUserControllerAfter(joinPoint);
		assertNotNull(signature);
		assertNotNull(sso);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"createDomain","updateDomain","deleteDomain","createGroup","editGroup","deleteGroup","createRoleAssociatePrivileges","updateRoleAssociatePrivileges","deleteRole","createUsers","editUsers","enableDisableUser"} )
	void testLogAuditTrailUserAction(String functionality) {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		JoinPoint joinPoint = mock(JoinPoint.class);
		Signature signature = mock(Signature.class);
		args[0]="data";
		when(joinPoint.getArgs()).thenReturn(args);
		when(joinPoint.getTarget()).thenReturn(new Object());
		when(joinPoint.getSignature()).thenReturn(signature);
		when(joinPoint.getSignature().getName()).thenReturn(functionality);
		auditTrailAspect.logAuditTrailBeforeDomainControllerAfter(joinPoint);
		auditTrailAspect.logAuditTrailBeforeGroupControllerAfter(joinPoint);
		auditTrailAspect.logAuditTrailBeforeRoleControllerAfter(joinPoint);
		auditTrailAspect.logAuditTrailBeforeUserControllerAfter(joinPoint);
		assertNotNull(signature);
		assertNotNull(sso);
	}
	
	@Test
	void testLogAuditTrailUsageBeforeAndAfter() {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		JoinPoint joinPoint = mock(JoinPoint.class);
		Signature signatureObj=mock(Signature.class);
		when(joinPoint.getSignature()).thenReturn(signatureObj);
		auditTrailAspect.logAuditTrailBeforeDomainController(joinPoint);
		auditTrailAspect.logAuditTrailBeforeRoleController(joinPoint);
		auditTrailAspect.logAuditTrailBeforeUserController(joinPoint);
		auditTrailAspect.logAuditTrailBeforeUserController(joinPoint);
		auditTrailAspect.logAuditTrailBeforeGroupController(joinPoint);
		assertNotNull(signatureObj);
		assertNotNull(sso);
	}
}
