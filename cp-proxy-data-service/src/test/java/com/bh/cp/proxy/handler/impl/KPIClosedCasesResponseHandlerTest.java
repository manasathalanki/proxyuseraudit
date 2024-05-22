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

class KPIClosedCasesResponseHandlerTest {

	private static class TestableKpiClosedCasesResponseHandler
			extends KPIClosedCasesResponseHandler<Map<String, Object>> {
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
		TestableKpiClosedCasesResponseHandler handler = new TestableKpiClosedCasesResponseHandler();

		JSONArray array = new JSONArray().put(new JSONObject().put(WidgetConstants.STATUS, WidgetConstants.CLOSEDC))
				.put(new JSONObject().put(WidgetConstants.STATUS, WidgetConstants.CLOSEDC))
				.put(new JSONObject().put(WidgetConstants.STATUS, WidgetConstants.CLOSEDC));

		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.RESOURCES, array);
		handler.setResponseData(response);
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);

		assertEquals("3", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.VALUE));
	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestableKpiClosedCasesResponseHandler handler = new TestableKpiClosedCasesResponseHandler();
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));

	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableKpiClosedCasesResponseHandler handler = new TestableKpiClosedCasesResponseHandler();
		handler.setResponseData(new HashMap<>());

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));

	}

	@Test
	void testConstructor() throws Exception {
		TestableKpiClosedCasesResponseHandler handler = new TestableKpiClosedCasesResponseHandler();
		assertNotNull(handler);
	}

}
