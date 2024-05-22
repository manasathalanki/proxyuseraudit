package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.fasterxml.jackson.core.JsonProcessingException;

class KPIReliabilityResponseHandlerTest {

	@Mock
	private KPIReliabilityAndAvailabilityResponseHandler reliabilityAndAvailabilityResponseHandler;

	@InjectMocks
	private KPIReliabilityResponseHandler<?> handler;

	Map<String, Object> request;
	HashMap<String, Object> mockResponseData;
	JSONArray responseArray;

	private JSONObject responseArray1;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		request = new HashMap<>();
		responseArray = new JSONArray()
				.put(new JSONObject().put(WidgetConstants.SINGLE, new JSONObject().put(WidgetConstants.VALUE, 100.00))
						.put(WidgetConstants.MAX, JSONObject.NULL).put(WidgetConstants.MIN, JSONObject.NULL));
		mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.DATA, responseArray);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		ReflectionTestUtils.setField(handler, "reliabilityAndAvailabilityResponseHandler",
				reliabilityAndAvailabilityResponseHandler);
	}

	@Test
	@DisplayName("Parse the Response")
	void testParseWithLevelAsLineups() throws JsonProcessingException {
		request.put(WidgetConstants.LEVEL, "lineups");
		responseArray1 = new JSONObject().put(WidgetConstants.SINGLE, 100.00).put(WidgetConstants.MAX, JSONObject.NULL)
				.put(WidgetConstants.MIN, JSONObject.NULL);
		when(reliabilityAndAvailabilityResponseHandler.parse(mockResponseData, request))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, responseArray1));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(100.00, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.SINGLE));
		assertEquals(JSONObject.NULL, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.MAX));
		assertEquals(JSONObject.NULL, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.MIN));
	}

}
