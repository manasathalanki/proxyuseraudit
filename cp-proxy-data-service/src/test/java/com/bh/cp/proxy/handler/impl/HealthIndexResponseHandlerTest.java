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

 class HealthIndexResponseHandlerTest {
	
	
	@SuppressWarnings("rawtypes")
	@InjectMocks
	HealthIndexResponseHandler reader;


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
		assertEquals(85, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.VALUE));
		
	
	}
	@Test
	void testParseNoDataFound() {
		Map<String, Object> mockResponseData = createSampleResponseNoData();
		HealthIndexResponseHandler<Object> reader = new HealthIndexResponseHandler<>();
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

	private JSONArray creatSampleJsonData() {
		JSONObject mainassert = new JSONObject()
				.put("assetId", "NOBLE")
				.put("value", 85)
				.put("color", "red")
				.put("status", "Intervene");
		JSONArray childArray = new JSONArray();
		JSONObject childassert = new JSONObject()
				.put("assetId", "NOBLE")
				.put("value", 85)
				.put("color", "red")
				.put("status", "Intervene");
		childArray.put(childassert);
		mainassert.put("children", childArray);
		JSONArray dataArray = new JSONArray().put(mainassert);
		return dataArray ;

	}

}
