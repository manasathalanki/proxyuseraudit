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

class KPIOpenCasesResponseHandlerTest {

	private static class TestableKpiOpenCasesResponseHandler extends KPIOpenCasesResponseHandler<Map<String, Object>> {
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
		TestableKpiOpenCasesResponseHandler handler = new TestableKpiOpenCasesResponseHandler();

		JSONArray array = new JSONArray().put(new JSONObject().put(WidgetConstants.STATUS, WidgetConstants.OPENC))
				.put(new JSONObject().put(WidgetConstants.STATUS, WidgetConstants.OPENC));

		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.RESOURCES, array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);

		assertEquals("2", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.VALUE));
	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestableKpiOpenCasesResponseHandler handler = new TestableKpiOpenCasesResponseHandler();
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
		TestableKpiOpenCasesResponseHandler handler = new TestableKpiOpenCasesResponseHandler();
		handler.setResponseData(new HashMap<>());

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testConstructor() throws Exception {
		TestableKpiOpenCasesResponseHandler handler = new TestableKpiOpenCasesResponseHandler();
		assertNotNull(handler);
	}

}
