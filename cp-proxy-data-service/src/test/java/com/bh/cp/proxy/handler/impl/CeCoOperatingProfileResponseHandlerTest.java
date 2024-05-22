package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.WidgetConstants;

class CeCoOperatingProfileResponseHandlerTest {

	@InjectMocks
	private CeCoOperatingProfileResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	Map<String, Object> dataMap;
	String inputObject;
	Map<String, Object> mockResponseData;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		dataMap = new HashMap<>();
		mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.DATA, prepareJSONObject());
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
	}

	private Map<String, Object> prepareJSONObject() {
		Map<String, Object> histrogramDat = new HashMap<>();
		histrogramDat.put("histBarBlueValues", new ArrayList<>());
		histrogramDat.put("histBarWhiteValues", new ArrayList<>());
		Map<String, Object> polyTropicHead = new HashMap<>();
		polyTropicHead.put(WidgetConstants.GRAPHDISPLAYNAME, WidgetConstants.POLITROPICHEADDATA);
		polyTropicHead.put(WidgetConstants.HISTOGRAMDATA, histrogramDat);
		Map<String, Object> polyTropicEff = new HashMap<>();
		polyTropicEff.put(WidgetConstants.GRAPHDISPLAYNAME,WidgetConstants.POLITROPICEFFICIENCYDATA );
		polyTropicEff.put(WidgetConstants.HISTOGRAMDATA, histrogramDat);
		Map<String, Object> pressureRatio = new HashMap<>();
		pressureRatio.put(WidgetConstants.GRAPHDISPLAYNAME, WidgetConstants.PRESSURERATIOGRAPHDATA);
		pressureRatio.put(WidgetConstants.HISTOGRAMDATA, histrogramDat);
		Map<String, Object> politropicHeadGraphData = new HashMap<>();
		politropicHeadGraphData.put(WidgetConstants.POLITROPICHEADDATA, polyTropicHead);
		politropicHeadGraphData.put(WidgetConstants.POLITROPICEFFICIENCYDATA, polyTropicEff);
		politropicHeadGraphData.put(WidgetConstants.PRESSURERATIOGRAPHDATA, pressureRatio);
		politropicHeadGraphData.put(WidgetConstants.PHASE, 1);
		List<Map<String, Object>> cecoDataList = new ArrayList<>();
		cecoDataList.add(politropicHeadGraphData);
		Map<String, Object> cecoData = new HashMap<>();
		cecoData.put(WidgetConstants.CECODATA, cecoDataList);
		return cecoData;
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParseWithDataFound() throws Exception {
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(1, result.getJSONArray(WidgetConstants.DATA).length());
		assertEquals(1, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.PHASE));
	}
	@Test
	@DisplayName("Parse the Response - When CecoData is empty")
	void testParseWithCecoDataIsEmpty() throws Exception {
		dataMap.put(WidgetConstants.CECODATA, new ArrayList<>());
		mockResponseData.put(WidgetConstants.DATA, dataMap);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}
	@Test
	@DisplayName("Parse the Response - When Data is empty")
	void testParseWithDataIsEmpty() throws Exception {
		mockResponseData.put(WidgetConstants.DATA, new HashMap<>());
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}


}
