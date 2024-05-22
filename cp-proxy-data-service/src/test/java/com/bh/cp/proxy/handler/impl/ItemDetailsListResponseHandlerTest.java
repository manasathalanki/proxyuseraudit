package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.WidgetConstants;

import jakarta.servlet.http.HttpServletRequest;

class ItemDetailsListResponseHandlerTest {

	private static class TestItemDetailsListResponseHandlerTest
			extends ItemDetailsListResponseHandler<Map<String, Object>> {
		protected TestItemDetailsListResponseHandlerTest(HttpServletRequest httpServletRequest) {
			super(httpServletRequest);

		}

		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output with status OK ")
	void testParse_DeviceList() {

		TestItemDetailsListResponseHandlerTest handler = new TestItemDetailsListResponseHandlerTest(null);

		JSONArray array = new JSONArray()
				.put(new JSONObject().put("lastUpdate", "1704731369000").put("groupDesc", "Operative System Patches")
						.put("nameItem", "OS Patches").put(JSONUtilConstants.STATUS, "OK"));

		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.DATA, array.toList());
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output without status OK ")
	void testParse() {

		TestItemDetailsListResponseHandlerTest handler = new TestItemDetailsListResponseHandlerTest(null);

		JSONArray array = new JSONArray()
				.put(new JSONObject().put("lastUpdate", "1704731369000").put("groupDesc", "Operative System Patches")
						.put("nameItem", "OS Patches").put(JSONUtilConstants.STATUS, ""));

		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.DATA, array.toList());
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestItemDetailsListResponseHandlerTest handler = new TestItemDetailsListResponseHandlerTest(null);
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put("data1", new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

}
