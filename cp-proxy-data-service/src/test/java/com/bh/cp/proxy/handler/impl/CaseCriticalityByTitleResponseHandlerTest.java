package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bh.cp.proxy.constants.WidgetConstants;

class CaseCriticalityByTitleResponseHandlerTest {

	private static class TestCaseCriticalityByTitleResponseHandlerTest
			extends CaseCriticalityByTitleResponseHandler<Map<String, Object>> {
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

		TestCaseCriticalityByTitleResponseHandlerTest handler = new TestCaseCriticalityByTitleResponseHandlerTest();

		JSONArray array = new JSONArray()
				.put(new JSONObject().put("type", "Early Warning").put("type", "Event Troubleshooting"));

		Map<String, Object> response = new HashMap<>();
		response.put("resources", array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);

		assertNotNull(result);
		assertEquals(array.length(), result.length());

	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestCaseCriticalityByTitleResponseHandlerTest handler = new TestCaseCriticalityByTitleResponseHandlerTest();
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put("resources", new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();

		JSONObject result = (JSONObject) handler.callParse(request);

		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestCaseCriticalityByTitleResponseHandlerTest handler = new TestCaseCriticalityByTitleResponseHandlerTest();
		handler.setResponseData(new HashMap<>());

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);

		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

	@Test
	void testConstructor() throws Exception {
		TestCaseCriticalityByTitleResponseHandlerTest handler = new TestCaseCriticalityByTitleResponseHandlerTest();
		assertNotNull(handler);
	}

}
