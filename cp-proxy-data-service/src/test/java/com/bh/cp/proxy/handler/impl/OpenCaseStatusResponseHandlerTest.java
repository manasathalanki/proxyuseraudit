
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

class OpenCaseStatusResponseHandlerTest {
	private static class TestableOpenCaseStatusResponseHandler extends OpenCaseStatusResponseHandler<Map<String, Object>> {
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
		TestableOpenCaseStatusResponseHandler handler = new TestableOpenCaseStatusResponseHandler();
		List<HashMap<String, Object>> list=new ArrayList<>();
		HashMap<String, Object> map=new HashMap<>();
		map.put("color", "#b49566");
		map.put("noOfCases", "4");
		map.put("categoryName", "Data Quality");
		list.add(map);
		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.RESOURCES, list);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		JSONObject openCases= result.getJSONObject(WidgetConstants.DATA);
		JSONArray jsonArray=openCases.getJSONArray(WidgetConstants.OPENCASES);
		assertEquals(list.size(), jsonArray.length());

	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableOpenCaseStatusResponseHandler handler = new TestableOpenCaseStatusResponseHandler();
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Resources Found In the Input But Length is Zero")
	void testParseLevel_ResourcesLengthZero() {
		TestableOpenCaseStatusResponseHandler handler = new TestableOpenCaseStatusResponseHandler();
		HashMap<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testProtectedConstructor() throws Exception {
		TestableOpenCaseStatusResponseHandler handler = new TestableOpenCaseStatusResponseHandler();
		assertNotNull(handler);
	}
}