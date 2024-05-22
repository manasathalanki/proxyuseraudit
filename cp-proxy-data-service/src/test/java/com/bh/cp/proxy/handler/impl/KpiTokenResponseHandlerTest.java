package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bh.cp.proxy.constants.WidgetConstants;

class KpiTokenResponseHandlerTest {

	private static class TestKpiTokenResponseHandlerTest extends KpiTokenResponseHandler<Map<String, Object>> {
		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParse() {

		TestKpiTokenResponseHandlerTest handler = new TestKpiTokenResponseHandlerTest();

		List<Map<String, String>> array = new ArrayList<>();

		Map<String, String> map = new HashMap<>();
		Map<String, String> mapValue = new HashMap<>();
		Map<String, String> mapValues = new HashMap<>();
		map.put("caseId", "166761786");
		map.put("token", "10");

		mapValue.put("caseId", "166761617");
		mapValue.put("token", "560");

		mapValues.put("caseId", "166761816");
		mapValues.put("token", "636");

		array.add(map);
		array.add(mapValue);
		array.add(mapValues);

		Map<String, Object> response = new HashMap<>();
		response.put("data", array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestKpiTokenResponseHandlerTest handler = new TestKpiTokenResponseHandlerTest();
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put("data1", new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();

		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));
	}

	@Test
	void testConstructor() throws Exception {
		TestKpiTokenResponseHandlerTest handler = new TestKpiTokenResponseHandlerTest();
		assertNotNull(handler);
	}

}
