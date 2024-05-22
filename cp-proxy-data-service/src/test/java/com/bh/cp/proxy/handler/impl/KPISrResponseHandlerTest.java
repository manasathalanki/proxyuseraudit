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

class KPISrResponseHandlerTest {
	private static class TestableSrResponseHandler extends KPISrResponseHandler<Map<String, Object>> {
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
		TestableSrResponseHandler handler = new TestableSrResponseHandler();
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "YTD");
		JSONArray input = new JSONArray().put(new JSONObject().put(WidgetConstants.SR, "44.0").put(WidgetConstants.PRESET, "YTD"));
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESPONSES, input);
		handler.setResponseData(mockResponseData);

		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals("44.0", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.VALUE));
	}
	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestableSrResponseHandler handler = new TestableSrResponseHandler();
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCESR, new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));

	}
	@Test
	@DisplayName("Parse the Response - InputDateRange is Not Matched with Preset")
	void testParse_PresetNotMatchedWithInputDateRange() {
		TestableSrResponseHandler handler = new TestableSrResponseHandler();
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "1M");
		JSONArray input = new JSONArray().put(new JSONObject().put(WidgetConstants.SR, "44").put(WidgetConstants.PRESET, "Latest"));
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCESR, input);
		handler.setResponseData(mockResponseData);

		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - Input is Empty")
	void testParse_InputIsEmpty() {
		TestableSrResponseHandler handler = new TestableSrResponseHandler();
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "1M");
		handler.setResponseData(new HashMap<>());

		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Sr is Not Found in the Input")
	void testParse_SRNotFoundInInput() {
		TestableSrResponseHandler handler = new TestableSrResponseHandler();
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "Latest");
		JSONArray input = new JSONArray().put(new JSONObject().put(WidgetConstants.MTBT, "44").put(WidgetConstants.PRESET, "Latest"));
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCESR, input);
		handler.setResponseData(mockResponseData);

		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
	}

	@Test
	void testProtectedConstructor() throws Exception {

		TestableSrResponseHandler handler = new TestableSrResponseHandler();
		assertNotNull(handler);
	}

}
