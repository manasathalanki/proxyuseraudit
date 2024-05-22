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

class TaskListResponseHandlerTest {

	private static class TestUrgentTaskListResponseHandlerTest extends TaskListResponseHandler<Map<String, Object>> {
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

		TestUrgentTaskListResponseHandlerTest handler = new TestUrgentTaskListResponseHandlerTest();

		JSONArray array = new JSONArray().put(new JSONObject().put(WidgetConstants.STATUS, "OPEN").put("isUrgent", true)
				.put("suggestedDate", "20-11-2023 23:00:00"));

		Map<String, Object> response = new HashMap<>();
		response.put("tasks", array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "3M");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output for false")
	void testParseFalse() {

		TestUrgentTaskListResponseHandlerTest handler = new TestUrgentTaskListResponseHandlerTest();

		JSONArray array = new JSONArray().put(new JSONObject().put(WidgetConstants.STATUS, "OPEN")
				.put("isUrgent", false).put("suggestedDate", "20-11-2023 23:00:00"));

		Map<String, Object> response = new HashMap<>();
		response.put("tasks", array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "3M");
		JSONObject result = (JSONObject) handler.callParse(request);

		assertEquals("0", result.getJSONObject(WidgetConstants.DATA).get("OpenTaskCount").toString());

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Null Values")
	void testParseForNullValues() {

		TestUrgentTaskListResponseHandlerTest handler = new TestUrgentTaskListResponseHandlerTest();

		JSONArray array = new JSONArray().put(new JSONObject().put(WidgetConstants.STATUS, JSONObject.NULL)
				.put("isUrgent", JSONObject.NULL).put("suggestedDate", JSONObject.NULL));

		Map<String, Object> response = new HashMap<>();
		response.put("tasks", array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "3M");
		JSONObject result = (JSONObject) handler.callParse(request);

		assertEquals("0", result.getJSONObject(WidgetConstants.DATA).get("OpenTaskCount").toString());
		assertEquals("0", result.getJSONObject(WidgetConstants.DATA).get("TwoWeeksDueTaskCount").toString());

	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestUrgentTaskListResponseHandlerTest handler = new TestUrgentTaskListResponseHandlerTest();
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put("tasks", new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "3M");

		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

	@Test
	void testConstructor() throws Exception {
		TestUrgentTaskListResponseHandlerTest handler = new TestUrgentTaskListResponseHandlerTest();
		assertNotNull(handler);
	}

}
