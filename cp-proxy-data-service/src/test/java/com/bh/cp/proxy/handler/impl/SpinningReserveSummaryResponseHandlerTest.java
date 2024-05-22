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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;

class SpinningReserveSummaryResponseHandlerTest {

	@Mock
	private AssetHierarchyFilterService assetHierarchyFilterService;

	@InjectMocks
	private SpinningReserveSummaryResponseHandler<?> handler;

	List<Map<String, Object>> filterHierarchyList = new ArrayList<>();
	Map<String, Object> request = new HashMap<>();
	Map<String, Object> filterhierarchyMap = new HashMap<>();
	String inputObject;
	Map<String, Object> mockResponseData = new HashMap<>();
	Map<String, Object> assetsMap = new HashMap<>();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		inputObject = "{\"data\":[{\"assetId\":\"GT0578\",\"powerPercentValue\":68.9999,\"tableData\":[{\"parameter\":\"Actual power\",\"value\":34725.51641,\"unit\":\"kW\"}]}]}";
		mockResponseData.put(WidgetConstants.DATA, new JSONObject(inputObject));
		assetsMap.put(JSONUtilConstants.LEVEL_LINEUPS, Arrays.asList("L0574", "L0576"));
		filterhierarchyMap.put("vid", Arrays.asList("PR_ABG_SOL"));
		filterHierarchyList.add(filterhierarchyMap);
		request.put(WidgetConstants.FILTERASSETHIERARCHY, filterHierarchyList);
		request.put(ProxyConstants.ASSETSIDMAP, assetsMap);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		ReflectionTestUtils.setField(handler, "assetHierarchyFilterService", assetHierarchyFilterService);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Actual Power as Normal")
	void testParseWithNormal() {
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0578", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(ColorConstants.SPINNINGRESERVEGREEN, result.getJSONObject(WidgetConstants.DATA).getJSONArray(WidgetConstants.CHARTDATA).getJSONObject(0).get(WidgetConstants.COLOR));
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Actual Power as OverLoad")
	void testParseWithOverLoad() {
		String inputObject = "{\"data\":[{\"assetId\":\"GT0578\",\"powerPercentValue\":100.9999,\"tableData\":[{\"parameter\":\"Actual power\",\"value\":34725.51641,\"unit\":\"W\"}]}]}";
		mockResponseData.put(WidgetConstants.DATA, new JSONObject(inputObject));
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0578", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(ColorConstants.SPINNINGRESERVEORANGE, result.getJSONObject(WidgetConstants.DATA)
				.getJSONArray(WidgetConstants.CHARTDATA).getJSONObject(0).get(WidgetConstants.COLOR));
	}

	@Test
	@DisplayName("Parse the Response - Giving response with powerPercentValue As Zero")
	void testParseWithPowerPercentAsZero() {
		String inputObject = "{\"data\":[{\"assetId\":\"GT0578\",\"powerPercentValue\":0,\"tableData\":[{\"parameter\":\"power\",\"value\":34725.51641,\"unit\":\"MW\"}]}]}";
		mockResponseData.put(WidgetConstants.DATA, new JSONObject(inputObject));
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0578", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(10, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.VALUEPERBLOCK));
		assertEquals(0, result.getJSONObject(WidgetConstants.DATA).getJSONArray(WidgetConstants.CHARTDATA).length());
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Lineup Not Matching")
	void testParseWithLineupsListNotContainingLineup() {
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0578", WidgetConstants.ID))
				.thenReturn("L0578");
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(0, result.getJSONObject(WidgetConstants.DATA).getJSONArray(WidgetConstants.CHARTDATA).length());
	}

	@Test
	@DisplayName("Parse the Response - Giving response with TableData Length As Zero")
	void testParseWithTableDataLengthAsZero() {
		String inputObject = "{\"data\":[{\"assetId\":\"GT0578\",\"powerPercentValue\":0,\"tableData\":[]}]}";
		mockResponseData.put(WidgetConstants.DATA, new JSONObject(inputObject));
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0578", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(0, result.getJSONObject(WidgetConstants.DATA).getJSONArray(WidgetConstants.CHARTDATA).length());
	}

	@Test
	@DisplayName("Parse the Response - Giving response with TableData Not Found")
	void testParseWithTableDataNotFound() {
		String inputObject = "{\"data\":[{\"assetId\":\"GT0578\",\"powerPercentValue\":0}]}";
		mockResponseData.put(WidgetConstants.DATA, new JSONObject(inputObject));
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0578", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA).toString());
	}

}
