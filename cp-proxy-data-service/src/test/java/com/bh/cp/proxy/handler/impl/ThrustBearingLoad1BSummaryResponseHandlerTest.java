package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;

class ThrustBearingLoad1BSummaryResponseHandlerTest {

	@Mock
	private ThrustBearingLoadSummaryResponseHandler thrustBearingLoadSummaryResponseHandler;

	@InjectMocks
	private ThrustBearingLoad1BSummaryResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	String output;
	List<Map<String, Object>> inputObject;
	Map<String, Object> assetMap;
	Map<String, Object> scaleMap;
	HashMap<String, Object> mockResponseData;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockResponseData = new HashMap<>();
		inputObject = new ArrayList<>();
		scaleMap = new HashMap<>();
		assetMap = new HashMap<>();
		scaleMap.put(WidgetConstants.LOADMIN, 2000.0);
		scaleMap.put(WidgetConstants.LOADMAX, 5000.0);
		scaleMap.put(WidgetConstants.LOADMAXSCALE, 6000);
		scaleMap.put(WidgetConstants.LOADMINSCALE, 0.0);
		assetMap.put(WidgetConstants.ASSETID, "GT0574");
		assetMap.put(WidgetConstants.RECOUPTYPE, "LP");
		assetMap.put(WidgetConstants.RECOUP, 2000.00);
		assetMap.put(WidgetConstants.ORIFICEINSTALLED, "Change Required");
		assetMap.put(WidgetConstants.SCALE, scaleMap);
		inputObject.add(assetMap);
		mockResponseData.put(WidgetConstants.DATA, inputObject);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		ReflectionTestUtils.setField(handler, "thrustBearingLoadSummaryResponseHandler",
				thrustBearingLoadSummaryResponseHandler);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Normal Recoup")
	void testParseWithNormal() {
		when(thrustBearingLoadSummaryResponseHandler.parse(mockResponseData, request, ProxyConstants.THRUST_1B)).thenReturn(new JSONObject(mockResponseData));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}
}
