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
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

class DeviceDetailsResponseHandlerTest {

	private static class TestDeviceDetailsResponseHandlerTest
			extends DeviceDetailsResponseHandler<Map<String, Object>> {
		protected TestDeviceDetailsResponseHandlerTest(HttpServletRequest httpServletRequest,
				ProxyService proxyService) {
			super(httpServletRequest, proxyService);

		}

		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output with deviceStatus status ")
	void testParse_DeviceStatus() {

		TestDeviceDetailsResponseHandlerTest handler = new TestDeviceDetailsResponseHandlerTest(null, null);

		JSONArray array = new JSONArray().put(new JSONObject().put("lastUpdate", 1709290183000L)
				.put("lineupDescription", "ABENGOA SOLAR TRAIN").put("hardwareDeviceTypeDescription", "HP xxx")
				.put("hardwareDeviceType", "CTRLSERVER").put("deviceName", "TESTSELF").put("deviceId", "76")
				.put("status", "Updated").put("deviceStatus", "ok"));

		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.DATA, array.toList());
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output without deviceStatus status")
	void testParse() {

		TestDeviceDetailsResponseHandlerTest handler = new TestDeviceDetailsResponseHandlerTest(null, null);

		JSONArray array = new JSONArray()
				.put(new JSONObject().put("lastUpdate", 1709290183000L).put("lineupDescription", "ABENGOA SOLAR TRAIN")
						.put("hardwareDeviceTypeDescription", "HP xxx").put("hardwareDeviceType", "CTRLSERVER")
						.put("deviceName", "TESTSELF").put("deviceId", "76").put("status", "Updated"));

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
		TestDeviceDetailsResponseHandlerTest handler = new TestDeviceDetailsResponseHandlerTest(null, null);
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put("data1", new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

	@Test
	void testConstructor() throws Exception {
		TestDeviceDetailsResponseHandlerTest handler = new TestDeviceDetailsResponseHandlerTest(null, null);
		assertNotNull(handler);
	}
}
