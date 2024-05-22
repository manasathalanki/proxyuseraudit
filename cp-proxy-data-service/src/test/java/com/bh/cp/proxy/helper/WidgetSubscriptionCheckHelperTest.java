package com.bh.cp.proxy.helper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.fasterxml.jackson.core.JsonProcessingException;

class WidgetSubscriptionCheckHelperTest {

	@InjectMocks
	private WidgetSubscriptionCheckHelper checkHelper;

	@Mock
	private UMSClientService umsClientService;

	private MockHttpServletRequest httpServletRequest;

	private List<String> privileagesList;

	private Map<String, Boolean> accessMap;
	private String vid;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		vid = "MC_TEST";
		privileagesList = new ArrayList<>();
		privileagesList.add("Maintenance Optimizer");
		privileagesList.add("Health Index");
		accessMap = new HashMap<>();
		accessMap.put(JSONUtilConstants.ENABLEDSERVICES, true);
		ReflectionTestUtils.setField(checkHelper, "healthIndexWidgetId", 24);
		ReflectionTestUtils.setField(checkHelper, "maintenanceOptimizerWidgetId", 3);
		ReflectionTestUtils.setField(checkHelper, "maintainanceOptimizerPrivilege", "Maintenance Optimizer");
		ReflectionTestUtils.setField(checkHelper, "healthIndexPrivilege", "Health Index");
	}

	@Test
	@DisplayName("CheckAdvanceServicePrivilegeAccess - Positive")
	void testCheckAdvanceServicePrivilegeAccess_Positive() throws JsonProcessingException {
		when(umsClientService.getUserPrivileges(httpServletRequest)).thenReturn(privileagesList);
		when(umsClientService.getWidgetAdvanceServicesAccess(httpServletRequest,vid,24)).thenReturn(accessMap);
		when(umsClientService.getWidgetAdvanceServicesAccess(httpServletRequest,vid,3)).thenReturn(accessMap);
		Boolean actualResponse=checkHelper.checkAdvanceServicePrivilegeAccess(httpServletRequest, "MC_TEST");
		assertTrue(actualResponse);
	}

	@Test
	@DisplayName("CheckAdvanceServicePrivilegeAccess - Negative")
	void testCheckAdvanceServicePrivilegeAccess_Negative() throws JsonProcessingException {
		privileagesList=new ArrayList<>();
		privileagesList.add("Maintenance Optimizer");
		accessMap=new HashMap<>();
		accessMap.put(JSONUtilConstants.ENABLEDSERVICES, true);
		Map<String,Boolean> hiAccess=new HashMap<>();
		hiAccess.put(JSONUtilConstants.ENABLEDSERVICES, false);
		when(umsClientService.getUserPrivileges(httpServletRequest)).thenReturn(privileagesList);
		when(umsClientService.getWidgetAdvanceServicesAccess(httpServletRequest,vid,24)).thenReturn(hiAccess);
		when(umsClientService.getWidgetAdvanceServicesAccess(httpServletRequest,vid,3)).thenReturn(accessMap);
		Boolean actualResponse=checkHelper.checkAdvanceServicePrivilegeAccess(httpServletRequest, "MC_TEST");
		assertTrue(actualResponse);
	}
}
