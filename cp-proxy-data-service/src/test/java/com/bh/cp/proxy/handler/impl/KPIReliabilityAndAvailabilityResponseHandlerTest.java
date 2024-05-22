package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.fasterxml.jackson.core.JsonProcessingException;

class KPIReliabilityAndAvailabilityResponseHandlerTest {

	@InjectMocks
	private KPIReliabilityAndAvailabilityResponseHandler handler;

	Map<String, Object> request;
	HashMap<String, Object> mockResponseData = new HashMap<>();
	JSONArray responseArray;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		request = new HashMap<>();
		responseArray = new JSONArray()
				.put(new JSONObject().put(WidgetConstants.SINGLE, new JSONObject().put(WidgetConstants.VALUE, 100.00).put(WidgetConstants.ENDDATE, "2017-03-09 00:00:00"))
						.put(WidgetConstants.MAX, JSONObject.NULL).put(WidgetConstants.MIN, JSONObject.NULL));
		mockResponseData.put(WidgetConstants.DATA, responseArray);

	}

	@ParameterizedTest
	@CsvSource({ "lineups,machines" })
	@DisplayName("Parse the Response - Level As Lineups And Machines")
	void testParseWithLevelAsLineups(String level) throws JsonProcessingException {
		request.put(WidgetConstants.LEVEL, level);
		JSONObject result = (JSONObject) handler.parse(mockResponseData, request);
		assertNotNull(result);
		assertEquals(100.00, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.SINGLE));
		assertEquals(JSONObject.NULL, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.MAX));
		assertEquals(JSONObject.NULL, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.MIN));
	}

	@ParameterizedTest
	@CsvSource({ "projects,plants,trains" })
	@DisplayName("Parse the Response - Level As Projects,Plants And Trains")
	void testParseWithLevelAsProjects(String level) throws JsonProcessingException {
		request.put(WidgetConstants.LEVEL, level);
		responseArray = new JSONArray().put(new JSONObject().put(WidgetConstants.SINGLE, JSONObject.NULL)
				.put(WidgetConstants.MAX, new JSONObject().put(WidgetConstants.VALUE, 99.91).put(WidgetConstants.ENDDATE, "2017-03-09 00:00:00"))
				.put(WidgetConstants.MIN, new JSONObject().put(WidgetConstants.VALUE, 68.00).put(WidgetConstants.ENDDATE,"2017-03-09 00:00:00")));
		mockResponseData.put(WidgetConstants.DATA, responseArray);
		JSONObject result = (JSONObject) handler.parse(mockResponseData, request);
		assertNotNull(result);
		assertEquals(JSONObject.NULL, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.SINGLE));
		assertEquals(99.9, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.MAX));
		assertEquals(68.0, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.MIN));
		assertEquals(WidgetConstants.DESCAVAILIABILITY+"09-03-2017", result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.ENDDATE));
	}

	@Test
	@DisplayName("Parse the Response - Data Array As Empty")
	void testParseWithDataArrayAsEmpty() throws JsonProcessingException {
		mockResponseData.put(WidgetConstants.DATA, new JSONArray());
		JSONObject result = (JSONObject) handler.parse(mockResponseData, request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

}
