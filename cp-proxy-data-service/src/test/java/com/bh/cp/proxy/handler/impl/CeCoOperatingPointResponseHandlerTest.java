package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.WidgetConstants;

class CeCoOperatingPointResponseHandlerTest {

	@InjectMocks
	private CeCoOperatingPointResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	Map<String, Object> dataMap;
	String inputObject;
	Map<String, Object> mockResponseData;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		dataMap = new HashMap<>();
		mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.DATA, prepareJSONObject());
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
	}

	private Map<String, Object> prepareJSONObject() {
		List<JSONObject> cecoData = new ArrayList<>();
		JSONObject categoryObject = new JSONObject().put(WidgetConstants.DISPLAYNAME, "test_efficiency")
				.put(WidgetConstants.COLOR, "rgb(77,184,5)")
				.put(WidgetConstants.YVALUES, new JSONArray().put(JSONObject.NULL).put(59.99));
		JSONObject timeSeriesData = new JSONObject()
				.put("timestampValues", new JSONArray().put(168).put(168922).put(1697155))
				.put("data", new JSONArray().put(categoryObject));
		JSONObject data1 = new JSONObject().put(WidgetConstants.PHASE, 1).put(WidgetConstants.TIMESERIESDATA,
				timeSeriesData);
		cecoData.add(data1);
		JSONObject cecoData1 = new JSONObject().put(WidgetConstants.CECODATA, cecoData);
		return convertToMap(cecoData1);
	}

	private static Map<String, Object> convertToMap(JSONObject jsonObject) {
		Map<String, Object> dataMap = new HashMap<>();
		for (String key : jsonObject.keySet()) {
			dataMap.put(key, jsonObject.get(key));
		}
		return dataMap;
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParseWithDataFound() throws Exception {
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(1, result.getJSONArray(WidgetConstants.DATA).length());
	}

	@Test
	@DisplayName("Parse the Response - When phase Not Found")
	void testParseWithPhaseNotFound() throws Exception {
		inputObject = "{\"cecoData\":[{\"timeseriesData\":{\"timestampValues\":[1689213600000,1689220800000,1697155200000],\"data\":[{\"displayName\":\"PolytropicEfficiency\",\"color\":\"rgb(77,184,123)\",\"yValues\":[null,59.59]}]}}]}";
		dataMap = convertToMap(new JSONObject(inputObject));
		mockResponseData.put(WidgetConstants.DATA, dataMap);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - When timeseriesData Not Found")
	void testParseWithTimeseriesDataNotFound() throws Exception {
		inputObject = "{\"cecoData\":[{\"phase\":1}]}";
		dataMap = convertToMap(new JSONObject(inputObject));
		mockResponseData.put(WidgetConstants.DATA, dataMap);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(1, result.getJSONArray(WidgetConstants.DATA).length());
		assertEquals(1, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.PHASE));
		assertEquals(0, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONObject(WidgetConstants.TIMESERIESDATA).length());
	}

	@Test
	@DisplayName("Parse the Response - When CecoData is empty")
	void testParseWithCecoDataIsEmpty() throws Exception {
		dataMap.put(WidgetConstants.CECODATA, new ArrayList<>());
		mockResponseData.put(WidgetConstants.DATA, dataMap);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - When Data is empty")
	void testParseWithDataIsEmpty() throws Exception {
		mockResponseData.put(WidgetConstants.DATA, new HashMap<>());
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response")
	void testParseWithOutput() throws Exception {
		inputObject = "{\"cecoData\":[{\"timeseriesData\":{\"timestampValues\":[1689213600000,1689220800000,1697155200000],\"data\":[{\"displayName\":\"PolytropicEfficiency\",\"color\":\"rgb(77,184,123)\",\"yValues\":[null]}]}}]}";
		dataMap = convertToMap(new JSONObject(inputObject));
		mockResponseData.put(WidgetConstants.DATA, dataMap);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals("PolytropicEfficiency",
				result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONObject(WidgetConstants.TIMESERIESDATA)
						.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.DISPLAYNAME));
	}

}
