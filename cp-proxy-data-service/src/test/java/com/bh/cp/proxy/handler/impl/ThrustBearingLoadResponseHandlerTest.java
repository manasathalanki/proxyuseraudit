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

import com.bh.cp.proxy.constants.WidgetConstants;
import com.fasterxml.jackson.core.JsonProcessingException;

class ThrustBearingLoadResponseHandlerTest {

	@InjectMocks
	private ThrustBearingLoadResponseHandler handler;

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

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output and Recoup as Not Zero")
	void testParseWithProperOutput() throws JsonProcessingException {
		JSONObject result = (JSONObject) handler.parse(mockResponseData);
		assertNotNull(result);
		assertEquals(JSONObject.NULL, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.ORIFICEINSTALLED));
	}

	@Test
	@DisplayName("Parse the Response - Orifice Not Found in the Input")
	void testParseWithOrificeNotFound() throws JsonProcessingException {
		responseArray = new JSONArray().put(new JSONObject().put(WidgetConstants.RECOUPTYPE, "HP")
				.put(WidgetConstants.SCALE,
						new JSONObject().put(WidgetConstants.LOADMINSCALE, 0.0).put(WidgetConstants.LOADMAX, 4000.0)
								.put(WidgetConstants.LOADMIN, 1000.0).put(WidgetConstants.LOADMAXSCALE, 5000.0))
				.put(WidgetConstants.RECOUP, 3000.000));
		mockResponseData.put(WidgetConstants.DATA, responseArray);
		JSONObject result = (JSONObject) handler.parse(mockResponseData);
		assertNotNull(result);
		assertEquals(JSONObject.NULL, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.ORIFICEINSTALLED));
	}

	@Test
	@DisplayName("Parse the Response -No Data Found")
	void testParseWithNoDataFound() throws JsonProcessingException {
		responseArray = new JSONArray().put(new JSONObject().put(WidgetConstants.RECOUP, 0.000));
		mockResponseData.put(WidgetConstants.DATA, responseArray);
		JSONObject result = (JSONObject) handler.parse(mockResponseData);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - OrificeInstalled as Not Null")
	void testParseWithOrificeInstalledAsNotNull() throws JsonProcessingException {
		responseArray = new JSONArray().put(new JSONObject().put(WidgetConstants.RECOUPTYPE, "HP")
				.put(WidgetConstants.SCALE,
						new JSONObject().put(WidgetConstants.LOADMINSCALE, 0.0).put(WidgetConstants.LOADMAX, 4000.0)
								.put(WidgetConstants.LOADMIN, 1000.0).put(WidgetConstants.LOADMAXSCALE, 5000.0))
				.put(WidgetConstants.RECOUP, 2500.000).put(WidgetConstants.ORIFICEINSTALLED, "Change Required"));
		mockResponseData.put(WidgetConstants.DATA, responseArray);
		JSONObject result = (JSONObject) handler.parse(mockResponseData);
		assertNotNull(result);
		assertEquals("Change Required",
				result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.ORIFICEINSTALLED));
	}

	@Test
	@DisplayName("Parse the Response - Recoup as Zero")
	void testParseWithRecoupZero() throws JsonProcessingException {
		responseArray = new JSONArray().put(new JSONObject().put(WidgetConstants.RECOUPTYPE, "HP")
				.put(WidgetConstants.SCALE,
						new JSONObject().put(WidgetConstants.LOADMINSCALE, 0.0).put(WidgetConstants.LOADMAX, 4000.0)
								.put(WidgetConstants.LOADMIN, 1000.0).put(WidgetConstants.LOADMAXSCALE, 5000.0))
				.put(WidgetConstants.RECOUP, 00.000).put(WidgetConstants.ORIFICEINSTALLED, "Change Required"));
		mockResponseData.put(WidgetConstants.DATA, responseArray);
		JSONObject result = (JSONObject) handler.parse(mockResponseData);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

}
