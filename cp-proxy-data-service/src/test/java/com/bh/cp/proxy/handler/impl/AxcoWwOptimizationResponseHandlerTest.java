package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.WidgetConstants;

 class AxcoWwOptimizationResponseHandlerTest {
	
	@SuppressWarnings("rawtypes")
	@InjectMocks
	AxCoWwOptimizationResponseHandler reader;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		Map<String, Object> mockResponseData = createSampleResponseData();
		ReflectionTestUtils.setField(reader, "t", mockResponseData);
	}
	@Test
	void testParseSingleMatch() {
		Map<String, Object> mockResponseData = createSampleResponseData();
		@SuppressWarnings("unchecked")
		JSONObject result = (JSONObject) reader.parse(mockResponseData);
		assertEquals("84.9", result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.AXCO_EFFICIENCY));
		
	
	}
	@Test
	void testParseNoDataFound() {
		Map<String, Object> mockResponseData = createSampleResponseNoData();
		AxCoWwOptimizationResponseHandler<Object> reader = new AxCoWwOptimizationResponseHandler<>();
		Object result = reader.parse(mockResponseData);
		assertTrue(result instanceof JSONObject, "result should be a jsonObject");
		JSONObject parsedResult = (JSONObject) result;
		assertEquals("No data found", parsedResult.getString("data"));
	}

	private Map<String, Object> createSampleResponseData() {
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("data", creatSampleJsonData());
		return responseData;

	}
	
	
	
	private Map<String, Object> createSampleResponseNoData() {
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("data", new JSONArray());
		return responseData;

	}

	private JSONObject creatSampleJsonData() {
		JSONObject mainassert = new JSONObject()
				.put("linkedCase", "Yes");
		JSONArray linkedCaseList = new JSONArray();
		linkedCaseList.put(1234);
		 mainassert.put("linkedCaseList", linkedCaseList);
		 mainassert.put("axco_Efficiency", 0.8488);
		 mainassert.put("last_Axco_WW_Efficiency", 0.8499);
		 mainassert.put("suggested_Hours_Next_WW", 8488);
		 mainassert.put("last_Axco_WW_Timestamp", 17013672);
		
		return mainassert ;

	}

}
