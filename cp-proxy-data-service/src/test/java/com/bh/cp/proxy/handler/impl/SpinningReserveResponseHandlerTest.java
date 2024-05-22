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

class SpinningReserveResponseHandlerTest {

	@SuppressWarnings("rawtypes")
	@InjectMocks
	private SpinningReserveResponseHandler reader;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		Map<String, Object> mockResponseData = createSampleResponseData();
		ReflectionTestUtils.setField(reader, "t", mockResponseData);
	}

	@Test
	@SuppressWarnings("unchecked")
	void testParseSingleMatch() {
		Map<String, Object> mockResponseData = createSampleResponseData();
		Object result = reader.parse(mockResponseData);
		assertTrue(result instanceof JSONObject, "result should be a jsonObject");
		JSONObject parsedResult = (JSONObject) result;
		assertEquals(85.5, parsedResult.getJSONObject("data").getDouble("load"));

		JSONArray tableDataArrayeFormated = parsedResult.getJSONObject("data").getJSONArray("timestamp");
		JSONObject tableDataObjectFormated = tableDataArrayeFormated.getJSONObject(0);
		assertEquals("value", tableDataObjectFormated.getString("parameter"));

	}

	@Test
	void testParseNoDataFound() {
		Map<String, Object> mockResponseData = createSampleResponseNoData();
		SpinningReserveResponseHandler<Object> reader = new SpinningReserveResponseHandler<>();
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
		JSONArray dataArray = new JSONArray();
		JSONObject dataObject = new JSONObject();
		dataObject.put("powerPercentValue", 85.5);
		dataObject.put("powerTimestamp", Long.parseLong("1701881593"));
		JSONArray tableDataArray = new JSONArray();
		JSONObject tableDataObject = new JSONObject();
		tableDataObject.put("parameter", "Maximum Power");
		tableDataObject.put("value", 2456.45);
		tableDataObject.put("unit", "MW");
		tableDataArray.put(tableDataObject);
		dataObject.put("tableData", tableDataArray);
		dataArray.put(dataObject);
		return new JSONObject().put("data", dataArray);
	}

}
