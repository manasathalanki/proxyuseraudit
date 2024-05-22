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

class CaseNotificationAPIResponseHandlerTest {
	private static class TestableCaseNotificationAPIResponseHandler extends CaseNotificationAPIResponseHandler<Map<String, Object>> {
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
		TestableCaseNotificationAPIResponseHandler handler = new TestableCaseNotificationAPIResponseHandler();
		List<HashMap<String, Object>> list=new ArrayList<>();
		HashMap<String, Object> map=new HashMap<>();
		map.put("userEmail", "alessandro1.sarti@bakerhughes.com");
		map.put("userId", "105046221");
		map.put("mailType", "CASE");
		map.put("trendName", "TEST");
		list.add(map);
		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.DATA, list);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put("serviceId",45);
		request.put("issueId","166760720");
		JSONObject result = (JSONObject) handler.callParse(request);
		JSONArray openCasesArray = result.getJSONArray(WidgetConstants.DATA);
		assertEquals(list.size(), openCasesArray.length());

	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableCaseNotificationAPIResponseHandler handler = new TestableCaseNotificationAPIResponseHandler();
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		request.put("issueId","166760527");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Resources Found In the Input But Length is Zero")
	void testParseLevel_ResourcesLengthZero() {
		TestableCaseNotificationAPIResponseHandler handler = new TestableCaseNotificationAPIResponseHandler();
		HashMap<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		request.put("issueId","166760527");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testProtectedConstructor() throws Exception {
		TestableCaseNotificationAPIResponseHandler handler = new TestableCaseNotificationAPIResponseHandler();
		assertNotNull(handler);
	}
}