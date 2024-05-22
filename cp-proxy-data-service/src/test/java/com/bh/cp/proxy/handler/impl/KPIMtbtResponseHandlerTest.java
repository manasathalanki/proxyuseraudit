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

class KPIMtbtResponseHandlerTest {

	private static class TestableMtbtResponseHandler extends KPIMtbtResponseHandler<Map<String, Object>> {
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
		TestableMtbtResponseHandler handler = new TestableMtbtResponseHandler();
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGEYTD);
		JSONArray input = new JSONArray().put(new JSONObject().put(WidgetConstants.MTBT, "44.0")
				.put(WidgetConstants.PRESET, WidgetConstants.DATERANGEYTD));
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESPONSES, input);
		handler.setResponseData(mockResponseData);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals("44.0", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.VALUE));
	}		

	@Test
	@DisplayName("Parse the Response - InputDateRange is Not Matched with Preset")
	void testParse_PresetNotMatchedWithInputDateRange() {
		TestableMtbtResponseHandler handler = new TestableMtbtResponseHandler();
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "1M");
		JSONArray input = new JSONArray()
				.put(new JSONObject().put(WidgetConstants.MTBT, "44").put(WidgetConstants.PRESET, "Latest"));
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCESR, input);
		handler.setResponseData(mockResponseData);

		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - InputDateRange is ALL")
	void testParse_DateRangeAsALL() {
		TestableMtbtResponseHandler handler = new TestableMtbtResponseHandler();
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "All");
		JSONArray input = new JSONArray()
				.put(new JSONObject().put(WidgetConstants.MTBT, "44.0").put(WidgetConstants.PRESET, "Latest"));
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESPONSES, input);
		handler.setResponseData(mockResponseData);

		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals("44.0", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.VALUE));
	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestableMtbtResponseHandler handler = new TestableMtbtResponseHandler();
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCESR, new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));

	}

	@Test
	@DisplayName("Parse the Response - Input is Empty")
	void testParse_InputIsEmpty() {
		TestableMtbtResponseHandler handler = new TestableMtbtResponseHandler();
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "1M");
		handler.setResponseData(new HashMap<>());

		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Mtbt is Not Found in the Input")
	void testParse_SRNotFoundInInput() {
		TestableMtbtResponseHandler handler = new TestableMtbtResponseHandler();
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONArray input = new JSONArray().put(
				new JSONObject().put(WidgetConstants.SR, "44").put(WidgetConstants.PRESET, WidgetConstants.DATERANGE3));
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCESR, input);
		handler.setResponseData(mockResponseData);

		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testProtectedConstructor() throws Exception {

		KPIMtbtResponseHandler<Object> responseHandler = new KPIMtbtResponseHandler<>();
		assertNotNull(responseHandler);
	}
}