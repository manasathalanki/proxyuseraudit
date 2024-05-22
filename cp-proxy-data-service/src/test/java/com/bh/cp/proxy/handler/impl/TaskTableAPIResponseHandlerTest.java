package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.controller.GenericDataController;
import com.bh.cp.proxy.service.ProxyService;
import com.bh.cp.proxy.util.CaseDataAppender;

import jakarta.servlet.http.HttpServletRequest;

class TaskTableAPIResponseHandlerTest {
	@InjectMocks
	private GenericDataController dataController;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private ProxyService proxyService;
	@Mock
	private AssetHierarchyFilterService assetHierarchyFilterService;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private CaseDataAppender caseDataAppender;
	
	@Mock
	private AuditTrailAspect auditTrailAspect;
	
	@InjectMocks
	private TaskTableAPIResponseHandler<?> caseLockResponseHandler;
	HashMap<String, Object> map = new HashMap<>();
	List<HashMap<String, Object>> mapList = new ArrayList<>();
	HashMap<String, Object> resultMap = new HashMap<>();
	Map<String, Object> request = new HashMap<>();
	List<Map<String, Object>> reposnseList = new ArrayList<>();
	JSONArray list = new JSONArray();
	JSONObject outputObject = new JSONObject();
	JSONObject newOutputObject = new JSONObject();
	String valuesApiResponse;
	String vid = "";
	String mechineData = null;
	Map<String, List<Map<String, Object>>> bodyMap = new HashMap<>();
	HttpHeaders headers = new HttpHeaders();
	HttpEntity<String> httpEntity = new HttpEntity<>(headers);

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		map.put("suggestedDate", "14-05-2021 15:20:09");
		map.put("isRootCause", "NO");
		map.put("setpoint", "0");
		map.put("toolTip", "<6Months");
		map.put("type", "CORRECTIVE ACTION");
		map.put("uom", "");
		map.put("S.No", 1);
		map.put("alarmThreshold_L", "0");
		map.put("colorCode", "#AF74B9");
		map.put("shortDesc", "Filter swap and cleaning");
		map.put("pidTag", "");
		map.put("alarmThreshold_H", "0");
		map.put("alarmThreshold_LL", "0");
		map.put("alarmThreshold_HH", "0");
		map.put("maintenance", "NA");
		map.put("taskId", "5912");
		map.put("status", "OPEN");
		map.put("isUrgent","false");
		mapList.add(map);
		resultMap.put("tasks", mapList);

		request.put("parentCaseId", 166761556);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("id", "1345");
		responseMap.put("name", "Mahaveer");
		responseMap.put("email", "name.surname@bakerhughes.com");
		responseMap.put("userName", "00u8n40nv3ruehtuw1d7");
		responseMap.put("surName", "Penna");
		responseMap.put("enabled", "Y");
		reposnseList.add(responseMap);

		outputObject.put("list", list);
		outputObject.put("EWSOpenCount", 0);
		outputObject.put("EWSCloseCount", 0);
		outputObject.put("ETSCloseCount", 0);
		outputObject.put("ETSOpenCount", 0);
		outputObject.put("assetId", "");
		newOutputObject.put("data", outputObject);

		mechineData = "{\"data\":{\"EWSCloseCount\":0,\"assetId\":,\"TwoWeeksDueTaskCount\":0,\"list\":[],\"ETSOpenCount\":0,\"OpenTaskCount\":0,\"EWSOpenCount\":0,\"ETSCloseCount\":0}}";

		List<Map<String, Object>> bodyRes = new ArrayList<>();
		Map<String, Object> bodymp = new HashMap<>();
		bodymp.put("rmdEventId", 123);
		bodymp.put("eventTypeDesc", "desc");
		bodyMap.put("data", bodyRes);

		ReflectionTestUtils.setField(caseLockResponseHandler, "t", resultMap);
		ReflectionTestUtils.setField(caseLockResponseHandler, "httpServletRequest", httpServletRequest);
		ReflectionTestUtils.setField(caseLockResponseHandler, "dataController", dataController);
		ReflectionTestUtils.setField(caseLockResponseHandler, "assetHierarchyFilterService",
				assetHierarchyFilterService);
		ReflectionTestUtils.setField(dataController, "proxyService", proxyService);
		ReflectionTestUtils.setField(caseLockResponseHandler, "restTemplate", restTemplate);
		ReflectionTestUtils.setField(caseLockResponseHandler, "eveMaintUri",
				"https://mercurius.np-0000029.npaeuw1.bakerhughes.com/event/eventMaintenance?");

	}

	private static class TestableTaskTableAPIResponseHandler extends TaskTableAPIResponseHandler<Map<String, Object>> {
		public TestableTaskTableAPIResponseHandler(GenericDataController dataController,
				HttpServletRequest httpServletRequest, AssetHierarchyFilterService assetHierarchyFilterService,
				RestTemplate restTemplate, String eveMaintUri) {
			super(dataController, httpServletRequest, assetHierarchyFilterService, restTemplate, eveMaintUri);
			// TODO Auto-generated constructor stub
		}

		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void parse() throws Exception {
		
		when(restTemplate.exchange(anyString(),eq(HttpMethod.GET),eq(httpEntity),eq(Map.class)))
		.thenReturn(new ResponseEntity<>(bodyMap,HttpStatus.OK));
		
		when(proxyService.execute(anyMap(),eq(httpServletRequest)))
		.thenReturn(newOutputObject);
		
		JSONObject result = (JSONObject) caseLockResponseHandler.parse(request);
		
		assertEquals(resultMap.size(), result.length());
	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableTaskTableAPIResponseHandler handler = new TestableTaskTableAPIResponseHandler(dataController, httpServletRequest, assetHierarchyFilterService, restTemplate, "1");
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		request.put("issueId", "16676147");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Resources Found In the Input But Length is Zero")
	void testParseLevel_ResourcesLengthZero() {
		TestableTaskTableAPIResponseHandler handler = new TestableTaskTableAPIResponseHandler(dataController, httpServletRequest, assetHierarchyFilterService, restTemplate, "1");
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		request.put("issueId", "16676147");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testProtectedConstructor() throws Exception {
		TestableTaskTableAPIResponseHandler handler = new TestableTaskTableAPIResponseHandler(dataController, httpServletRequest, assetHierarchyFilterService, restTemplate, "1");
		assertNotNull(handler);
	}
}
