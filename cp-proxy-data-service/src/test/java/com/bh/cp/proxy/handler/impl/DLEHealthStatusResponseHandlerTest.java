package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

class DLEHealthStatusResponseHandlerTest {

	@InjectMocks
	DLEHealthStatusResponseHandler<?> reader;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		Map<String, Object> mockResponseData = createSampleResponseData();
		ReflectionTestUtils.setField(reader, "t", mockResponseData);
	}

	@Test
	void testParseSingleMatch() {
		Map<String, Object> mockResponseData = createSampleResponseData();
		JSONObject result = (JSONObject) reader.parse(mockResponseData);
		assertEquals(2108.4, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.TOTAL_HOURS));
	}
	@Test
	void testParseWithLastDleIsNull() {
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.DATA, creatSampleJsonData1());
		ReflectionTestUtils.setField(reader, "t", mockResponseData);
		JSONObject result = (JSONObject) reader.parse(mockResponseData);
		assertEquals(2108.4, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.TOTAL_HOURS));
	}

	@Test
	void testParseOperatingHoursLengthIsEqualsZero() {
		JSONObject operatingHours = new JSONObject();
		operatingHours.put(WidgetConstants.OPERATINGHOURS, new JSONArray());
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.DATA, operatingHours);
		ReflectionTestUtils.setField(reader, "t", mockResponseData);
		JSONObject result = (JSONObject) reader.parse(mockResponseData);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testParseDataLengthIsEqualsZero() {
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.DATA, new JSONArray());
		ReflectionTestUtils.setField(reader, "t", mockResponseData);
		JSONObject result = (JSONObject) reader.parse(mockResponseData);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	private Map<String, Object> createSampleResponseData() {
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("data", creatSampleJsonData());
		return responseData;

	}

	private JSONObject creatSampleJsonData() {
		JSONObject mainassert = new JSONObject().put("gtStatus", "Running").put("burnerMode", "ABC")
				.put("lastDle", 169528842000L).put("runningHours", 2108.4)
				.put("lastAlert", "Acoustics from IDLE to MOS").put("lastTimestamp", 169528842000L)
				.put("lastBurnerMode", "-").put("suggestedMapping", "NO").put("mappingNeeded", "yes");
		JSONArray childArray = new JSONArray();
		childArray.put(createOperatingHours(7.3, "ABC"));
		childArray.put(createOperatingHours(7.6, "AB9C"));
		childArray.put(createOperatingHours(7.5, "AB"));
		childArray.put(createOperatingHours(7.7, "BC"));
		childArray.put(createOperatingHours(7.8, "BC/2"));
		childArray.put(createOperatingHours(7.8, "B"));
		childArray.put(createOperatingHours(7.4, "OTHERS"));
		childArray.put(createOperatingHours(2.4, "yy"));
		mainassert.put("operatingHours", childArray);
		return mainassert;

	}
	private JSONObject creatSampleJsonData1() {
		JSONObject mainassert = new JSONObject().put("gtStatus", "NOT Running").put("burnerMode", "ABC")
				.put("lastDle", JSONObject.NULL).put("runningHours", 2108.4)
				.put("lastAlert", "Acoustics from IDLE to MOS").put("lastTimestamp", JSONObject.NULL)
				.put("lastBurnerMode", "-").put("suggestedMapping", "NO").put("mappingNeeded", "yes");
		JSONArray childArray = new JSONArray();
		childArray.put(createOperatingHours(7.3, "ABC"));
		childArray.put(createOperatingHours(7.6, "AB9C"));
		childArray.put(createOperatingHours(7.5, "AB"));
		childArray.put(createOperatingHours(7.7, "BC"));
		childArray.put(createOperatingHours(7.8, "BC/2"));
		childArray.put(createOperatingHours(7.8, "B"));
		childArray.put(createOperatingHours(7.4, "OTHERS"));
		childArray.put(createOperatingHours(2.4, "yy"));
		mainassert.put("operatingHours", childArray);
		return mainassert;

	}

	private static JSONObject createOperatingHours(Double opHours, String opBurnerMode) {
		JSONObject operatingHour = new JSONObject().put("opHours", opHours).put("opBurnerMode", opBurnerMode);
		return operatingHour;

	}
}
