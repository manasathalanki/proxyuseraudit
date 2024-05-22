package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.WidgetConstants;

class CeCoMapResponseHandlerTest {

	@InjectMocks
	private CeCoMapResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	Map<String, Object> dataMap;
	String inputObject;
	Map<String, Object> mockResponseData;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockResponseData = new HashMap<>();
		dataMap = new HashMap<>();
		inputObject = "{\"cecoData\":[{\"phase\":1,\"timeseriesData\":{\"timestampValues\":[1689213600000,1689220800000,1697155200000],\"data\":[{\"displayName\":\"PolytropicEfficiency\",\"color\":\"rgb(77,184,123)\",\"yValues\":[null,59.59]}]}},{\"phase\":2,\"timeseriesData\":{\"timestampValues\":[1689213600000,1689220800000,1697155200000],\"data\":[{\"displayName\":\"PolytropicHead\",\"color\":\"rgb(77,184,123)\",\"yValues\":[65.33,null]}]}},{\"phase\":3,\"timeseriesData\":{\"timestampValues\":[1689213600000],\"data\":[{\"displayName\":\"PressureRatio\",\"color\":\"rgb(77,184,123)\",\"yValues\":[null,100.245]}]}}]}";
		dataMap = convertToMap(new JSONObject(inputObject));
		mockResponseData.put(WidgetConstants.DATA, dataMap);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
	}
	
	private static Map<String,Object> convertToMap(JSONObject jsonObject){
		Map<String, Object> dataMap = new HashMap<>();
		for(String key: jsonObject.keySet()) {
			dataMap.put(key, jsonObject.get(key));
		}
		return dataMap;
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParseWithDataFound() throws Exception {
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}
	@Test
	@DisplayName("Parse the Response - When phase Not Found")
	void testParseWithPhaseNotFound() throws Exception {
		inputObject ="{\"cecoData\":[{\"timeseriesData\":{\"timestampValues\":[1689213600000,1689220800000,1697155200000],\"data\":[{\"displayName\":\"PolytropicEfficiency\",\"color\":\"rgb(77,184,123)\",\"yValues\":[null,59.59]}]}}]}" ;
		dataMap = convertToMap(new JSONObject(inputObject));
		mockResponseData.put(WidgetConstants.DATA, dataMap);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
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


}
