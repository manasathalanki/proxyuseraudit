package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

class CaseListResponseHandlerTest {

	@Mock
	private ProxyService proxyService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private AssetHierarchyFilterService assetHierarchyFilterService;

	@Mock
	private UMSClientService umsClientService;

	@InjectMocks
	private CaseListResponseHandler<?> handler;
	Map<String, Object> userResponse;
	Map<String, Map<String, Set<String>>> fieldsEnabledServicesMap;
	Map<String, Object> request;
	List<Map<String, Object>> list;
	Map<String, Object> response;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		JSONArray array = new JSONArray().put(new JSONObject().put(WidgetConstants.STATUS, "OPEN")
				.put("anomalyCategory", "Performance").put("caseId", "166760788").put("criticality", "LOW")
				.put("customer", "NOBLE ENERGY EG LIMITED").put("title", "title")
				.put("linkedCustomerCaseIds", JSONObject.NULL).put("trainDescription", "NOBLE_Train")
				.put("lineupId", "L0673").put("machineSerialNum", "GT0673").put("eventDateUTC", "06-11-2022 12:00:00")
				.put("lastUpdateDateUTC", "23-11-2023 16:46:57").put("closeDate", "04-08-2023 12:25:42")
				.put("trpCaseId", JSONObject.NULL).put("contributingTags", "[L0515.CC0515-1.A20AS3_ENUM]")
				.put("analysis", "fmea").put("ebs", "AD_GT_2").put("type", "Early Warning"));

		response = new HashMap<>();
		response.put("resources", array);
		Set<String> setvalue = new HashSet<>();

		setvalue.add("MAINT_OPT");
		setvalue.add("DLE_HEALTH");
		setvalue.add("TRP");
		setvalue.add("HEALTH_INDEX");
		setvalue.add("HP_RECOUP");
		setvalue.add("PDM");
		setvalue.add("FILT_CHANGE_ADV");
		request = new HashMap<>();

		Map<String, Set<String>> vidMap = new HashMap<String, Set<String>>();
		vidMap.put("enabledServices", setvalue);
		list = new ArrayList<>();
		list.add(new HashMap<>());
		request.put(ProxyConstants.FILTEREDASSETHIERARCHY, list);

		userResponse = new HashMap<>();
		fieldsEnabledServicesMap = new HashMap<>();

		userResponse.put("privileges", Arrays.asList("test"));
		fieldsEnabledServicesMap.put("MC_GT0673", vidMap);

		ReflectionTestUtils.setField(handler, "t", response);
		ReflectionTestUtils.setField(handler, "proxyService", proxyService);
		ReflectionTestUtils.setField(handler, "httpServletRequest", httpServletRequest);
		ReflectionTestUtils.setField(handler, "assetHierarchyFilterService", assetHierarchyFilterService);
		ReflectionTestUtils.setField(handler, "umsClientService", umsClientService);
		ReflectionTestUtils.setField(handler, "kpiTaskId", 37);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for privileges")
	void testParse_privileges() throws Exception {
		when(umsClientService.getUserDetails(httpServletRequest))
		.thenReturn(userResponse);

		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output for vid")
	void testParse_vid() throws Exception {
		when(umsClientService.getUserDetails(httpServletRequest))
		.thenReturn(userResponse);
		when(assetHierarchyFilterService.getFieldsAndEnabledServicesToMap(list)).thenReturn(fieldsEnabledServicesMap);
		
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	
	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() throws Exception {
		when(umsClientService.getUserDetails(httpServletRequest))
		.thenReturn(userResponse);
		when(assetHierarchyFilterService.getFieldsAndEnabledServicesToMap(list)).thenReturn(fieldsEnabledServicesMap);
		 response = new HashMap<>();
		response.put("resources1", new JSONArray());
		ReflectionTestUtils.setField(handler, "t", response);
		JSONObject result = (JSONObject) handler.parse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() throws Exception {
		when(umsClientService.getUserDetails(httpServletRequest))
		.thenReturn(userResponse);
		when(assetHierarchyFilterService.getFieldsAndEnabledServicesToMap(list)).thenReturn(fieldsEnabledServicesMap);
		response = new HashMap<>();
		response.put("resources", new JSONArray());
		ReflectionTestUtils.setField(handler, "t", response);
		JSONObject result = (JSONObject) handler.parse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

}
