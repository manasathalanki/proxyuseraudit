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

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import jakarta.servlet.http.HttpServletRequest;

class TotalDeviceCountTestCase {
	private static class TestableTotalDeviceCountResponseHandler extends TotalDeviceCountResponse<Map<String, Object>> {
		protected TestableTotalDeviceCountResponseHandler(HttpServletRequest httpServletRequest) {
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
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParse() throws Exception {
		TestableTotalDeviceCountResponseHandler handler = new TestableTotalDeviceCountResponseHandler(null);
		List<HashMap<String, Object>> list=new ArrayList<>();
		HashMap<String, Object> map=new HashMap<>();
		map.put("data",20);
		list.add(map);
		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.DATA, list);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put("serviceId",65);
		request.put("plantId","ABENGOA");
		JSONObject result = (JSONObject) handler.callParse(request);
		JSONObject jsonObject = (JSONObject) result.get(ProxyConstants.DATA);
		JSONArray openCasesArray = jsonObject.getJSONArray(WidgetConstants.DATA);
		assertEquals(list.size(), openCasesArray.length());

	}
	
	
	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableTotalDeviceCountResponseHandler handler = new TestableTotalDeviceCountResponseHandler(null);
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		request.put("plantId","ABENGOA");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Resources Found In the Input But Length is Zero")
	void testParseLevel_ResourcesLengthZero() {
		TestableTotalDeviceCountResponseHandler handler = new TestableTotalDeviceCountResponseHandler(null);
		HashMap<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		request.put("plantId","ABENGOA");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testProtectedConstructor() throws Exception {
		TestableTotalDeviceCountResponseHandler handler = new TestableTotalDeviceCountResponseHandler(null);
		assertNotNull(handler);
	}
}