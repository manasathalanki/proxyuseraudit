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

class OpenStatusResponseHandlerTest {
	private static class TestableOpenStatusResponseHandler extends OpenStatusResponseHandler<Map<String, Object>> {
		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParse() throws Exception {
		TestableOpenStatusResponseHandler handler = new TestableOpenStatusResponseHandler();
		JSONArray array = new JSONArray()
				.put(new JSONObject().put(WidgetConstants.ANOMALYCATEGORY, "Performance")
						.put(WidgetConstants.CRITICALITY, "high"))
				.put(new JSONObject().put(WidgetConstants.ANOMALYCATEGORY, "Filters"))
				.put(new JSONObject().put(WidgetConstants.ANOMALYCATEGORY, "test"));

		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.RESOURCES, array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(array.length(), result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.TOTAL));
		JSONArray openCasesArray = result.getJSONObject(WidgetConstants.DATA).getJSONArray(WidgetConstants.OPENCASES);
		assertEquals(array.length(), openCasesArray.length());

	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableOpenStatusResponseHandler handler = new TestableOpenStatusResponseHandler();
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Resources Found In the Input But Length is Zero")
	void testParseLevel_ResourcesLengthZero() {
		TestableOpenStatusResponseHandler handler = new TestableOpenStatusResponseHandler();
		HashMap<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));

	}

	@Test
	void testProtectedConstructor() throws Exception {
		TestableOpenStatusResponseHandler handler = new TestableOpenStatusResponseHandler();
		assertNotNull(handler);
	}
}