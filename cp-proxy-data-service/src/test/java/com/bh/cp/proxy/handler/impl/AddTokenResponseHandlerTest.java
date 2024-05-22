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

import jakarta.servlet.http.HttpServletRequest;

class AddTokenResponseHandlerTest {

	private static class TestAddTokenResponseHandlerTest extends AddTokenResponseHandler<Map<String, Object>> {
		protected TestAddTokenResponseHandlerTest(HttpServletRequest httpServletRequest) {
			super(httpServletRequest);
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
	void testParse() {

		TestAddTokenResponseHandlerTest handler = new TestAddTokenResponseHandlerTest(null);

		JSONArray array = new JSONArray().put(new JSONObject().put("message", "Complete Insert"));

		Map<String, Object> response = new HashMap<>();
		response.put("message", array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestAddTokenResponseHandlerTest handler = new TestAddTokenResponseHandlerTest(null);
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put("data1", new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();

		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testConstructor() throws Exception {
		TestAddTokenResponseHandlerTest handler = new TestAddTokenResponseHandlerTest(null);
		assertNotNull(handler);
	}

}
