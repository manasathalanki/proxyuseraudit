package com.bh.cp.proxy.handler.impl;

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

class ThrustBearingLoad1BResponseHandlerTest {

	@Mock
	private ThrustBearingLoadResponseHandler thrustBearingLoadResponseHandler;

	@InjectMocks
	private ThrustBearingLoad1BResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	HashMap<String, Object> mockResponseData = new HashMap<>();
	JSONArray responseArray;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		responseArray = new JSONArray().put(new JSONObject().put(WidgetConstants.RECOUPTYPE, "HP")
				.put(WidgetConstants.SCALE,
						new JSONObject().put(WidgetConstants.LOADMINSCALE, 0.0).put(WidgetConstants.LOADMAX, 4000.0)
								.put(WidgetConstants.LOADMIN, 1000.0).put(WidgetConstants.LOADMAXSCALE, 5000.0))
				.put(WidgetConstants.RECOUP, 3000.000).put(WidgetConstants.ORIFICEINSTALLED, JSONObject.NULL));
		mockResponseData.put(WidgetConstants.DATA, responseArray);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		ReflectionTestUtils.setField(handler, "thrustBearingLoadResponseHandler", thrustBearingLoadResponseHandler);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output and Recoup as Not Zero")
	void testParseWithProperOutput() throws JsonProcessingException {
		when(thrustBearingLoadResponseHandler.parse(mockResponseData)).thenReturn(new JSONObject(mockResponseData));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

}
