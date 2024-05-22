package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;

class ThrustBearingLoadSummaryResponseHandlerTest {

	@Mock
	private AssetHierarchyFilterService assetHierarchyFilterService;

	@InjectMocks
	private ThrustBearingLoadSummaryResponseHandler handler;

	List<Map<String, Object>> filterHierarchyList = new ArrayList<>();
	Map<String, Object> request = new HashMap<>();
	Map<String, Object> filterhierarchyMap = new HashMap<>();
	String output;
	Map<String, Object> assetsMap = new HashMap<>();
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
		filterhierarchyMap.put("vid", Arrays.asList("PR_ABG_SOL"));
		assetsMap.put(JSONUtilConstants.LEVEL_LINEUPS, Arrays.asList("L0574", "L0576"));
		filterHierarchyList.add(filterhierarchyMap);
		request.put(ProxyConstants.ASSETSIDMAP, assetsMap);
		request.put(WidgetConstants.FILTERASSETHIERARCHY, filterHierarchyList);
		ReflectionTestUtils.setField(handler, "assetHierarchyFilterService", assetHierarchyFilterService);
	}

	@DisplayName("Parse the Response - Giving response with Normal Recoup")
	@ParameterizedTest
	@CsvSource({ ProxyConstants.THRUST_1B,
			ProxyConstants.THRUST_4B,ProxyConstants.THRUST_7B })
	void testParseWithNormal(String thrustType) {
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0574", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(mockResponseData,request,thrustType);
		assertNotNull(result);
		assertEquals(ColorConstants.THRUSTNORMAL, result.getJSONObject(WidgetConstants.DATA).getJSONArray(WidgetConstants.CHARTDATA).getJSONObject(0).get(WidgetConstants.COLOR));
	}

	@Test
	@DisplayName("Parse the Response - Giving response with UnderLoad Recoup")
	void testParseWithUnderLoad() {
		inputObject = new ArrayList<>();
		scaleMap = new HashMap<>();
		assetMap = new HashMap<>();
		scaleMap.put(WidgetConstants.LOADMIN, 2500.0);
		scaleMap.put(WidgetConstants.LOADMAX, 5000.0);
		scaleMap.put(WidgetConstants.LOADMAXSCALE, 6000);
		scaleMap.put(WidgetConstants.LOADMINSCALE, 0.0);
		assetMap.put(WidgetConstants.ASSETID, "GT0574");
		assetMap.put(WidgetConstants.RECOUPTYPE, "HP");
		assetMap.put(WidgetConstants.RECOUP, 2000.00);
		assetMap.put(WidgetConstants.ORIFICEINSTALLED, "Change Required");
		assetMap.put(WidgetConstants.SCALE, scaleMap);
		inputObject.add(assetMap);
		mockResponseData.put(WidgetConstants.DATA, inputObject);
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0574", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(mockResponseData, request, ProxyConstants.THRUST_4B);
		assertNotNull(result);
		assertEquals(ColorConstants.THRUSTUNDER, result.getJSONObject(WidgetConstants.DATA)
				.getJSONArray(WidgetConstants.CHARTDATA).getJSONObject(0).get(WidgetConstants.COLOR));
	}

	@Test
	@DisplayName("Parse the Response - Giving response with OverLoad Recoup")
	void testParseWithOverLoad() {
		inputObject = new ArrayList<>();
		scaleMap = new HashMap<>();
		assetMap = new HashMap<>();
		scaleMap.put(WidgetConstants.LOADMIN, 1000.0);
		scaleMap.put(WidgetConstants.LOADMAX, 1100.0);
		scaleMap.put(WidgetConstants.LOADMAXSCALE, 6000);
		scaleMap.put(WidgetConstants.LOADMINSCALE, 0.0);
		assetMap.put(WidgetConstants.ASSETID, "GT0574");
		assetMap.put(WidgetConstants.RECOUPTYPE, "HP");
		assetMap.put(WidgetConstants.RECOUP, 1200.00);
		assetMap.put(WidgetConstants.ORIFICEINSTALLED, "Change Required");
		assetMap.put(WidgetConstants.SCALE, scaleMap);
		inputObject.add(assetMap);
		mockResponseData.put(WidgetConstants.DATA, inputObject);
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0574", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(mockResponseData, request, ProxyConstants.THRUST_4B);
		assertNotNull(result);
		assertEquals(ColorConstants.THRUSTOVER, result.getJSONObject(WidgetConstants.DATA)
				.getJSONArray(WidgetConstants.CHARTDATA).getJSONObject(0).get(WidgetConstants.COLOR));
	}

	@Test
	@DisplayName("Parse the Response - No Data Found")
	void testParseWithNoDataFound() {
		inputObject = new ArrayList<>();
		scaleMap = new HashMap<>();
		assetMap = new HashMap<>();
		scaleMap.put(WidgetConstants.LOADMIN, 1000.0);
		scaleMap.put(WidgetConstants.LOADMAX, 1100.0);
		scaleMap.put(WidgetConstants.LOADMAXSCALE, 6000);
		scaleMap.put(WidgetConstants.LOADMINSCALE, 0.0);
		assetMap.put(WidgetConstants.ASSETID, "GT0574");
		assetMap.put(WidgetConstants.RECOUPTYPE, "HP");
		assetMap.put(WidgetConstants.ORIFICEINSTALLED, "Change Required");
		assetMap.put(WidgetConstants.SCALE, scaleMap);
		inputObject.add(assetMap);
		mockResponseData.put(WidgetConstants.DATA, inputObject);
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0574", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(mockResponseData, request, ProxyConstants.THRUST_4B);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA).toString());
	}

	@Test
	@DisplayName("Parse the Response - when Data Not found Not found in the Input")
	void testParseWithException() {
		mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCES, new ArrayList<>());
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0574", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(mockResponseData, request, ProxyConstants.THRUST_4B);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA).toString());
	}

}
