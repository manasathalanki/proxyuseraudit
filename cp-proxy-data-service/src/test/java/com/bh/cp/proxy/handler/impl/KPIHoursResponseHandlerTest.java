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
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.WidgetConstants;

class KPIHourstResponseHandlerTest {

	@InjectMocks
	private KPIHoursResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	String valuesApiResponse;
	Map<String, Object> ResponseData = new HashMap<>();
	String runningvalues;
	Map<String, Object> mockResponseData = new HashMap<>();
	Map<String, Object> noDataResponseData = new HashMap<>();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		valuesApiResponse = "[{\"lineup_id\":\"L0276\",\"modules\":[{\"id\":\"LINEUP\",\"name\":\"LINEUP\",\"sections\":[{\"id\":\"LINEUP\",\"name\":\"LINEUP\",\"calculations\":[{\"id\":\"FFH\",\"name\":\"FFH\",\"outputs\":[{\"id\":\"FH\",\"name\":\"FH\",\"summary\":{\"from_date\":\"2010-04-15T00:00:00+0000\",\"to_date\":\"2023-11-18T00:00:00+0000\",\"first_value\":0.0,\"last_value\":106952.52659333336,\"is_offset\":false,\"samples_with_error\":0,\"error_messages\":[]}}]}]}]}]}]";
		mockResponseData.put(WidgetConstants.DATA, new JSONArray(valuesApiResponse));
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		runningvalues = "[{\"lineup_id\":\"L0276\",\"modules\":[{\"id\":\"LINEUP\",\"name\":\"LINEUP\",\"sections\":[{\"id\":\"LINEUP\",\"name\":\"LINEUP\",\"calculations\":[{\"id\":\"FFH\",\"name\":\"FFH\",\"outputs\":[{\"id\":\"FH\",\"name\":\"FH\",\"summary\":{\"from_date\":\"2010-04-15T00:00:00+0000\",\"to_date\":\"2023-11-18T00:00:00+0000\",\"first_value\":null,\"last_value\":null,\"is_offset\":false,\"samples_with_error\":0,\"error_messages\":[]}}]}]}]}]}]";
		noDataResponseData.put(WidgetConstants.DATA, new JSONArray(runningvalues));
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Suggested Value and Actual Value as not null")
	void testParseWithActualValueSuggestedValueNotNull() throws Exception {
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);

	}

	@Test
	@DisplayName("Parse the Response - if widgetId is matched")
	void testParseWithValuesNoDataFound() throws Exception {
		ReflectionTestUtils.setField(handler, "t", noDataResponseData);
		JSONObject result = (JSONObject) handler.parse(ResponseData);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA).toString());
	}
	
	@Test
	@DisplayName("Parse the Response - if widgetId is matched")
	void testParseWithValuesArtrayAsEmpty() throws Exception {
		mockResponseData.put(WidgetConstants.DATA, new JSONArray());
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA).toString());
	}


}