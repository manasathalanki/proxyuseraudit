package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
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

class AxCoWwOptimizationSummaryResponseHandlerTest {

	@Mock
	private AssetHierarchyFilterService assetHierarchyFilterService;

	@InjectMocks
	AxCoWwOptimizationSummaryResponseHandler<?> reader;

	List<Map<String, Object>> filterHierarchyList = new ArrayList<>();
	Map<String, Object> request = new HashMap<>();
	Map<String, Object> filterhierarchyMap = new HashMap<>();
	String inputObject;
	Map<String, Object> mockResponseData = new HashMap<>();
	Map<String, Object> assetsMap = new HashMap<>();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockResponseData = createSampleResponseData();
		assetsMap.put(JSONUtilConstants.LEVEL_LINEUPS, Arrays.asList("L0574", "L0576"));
		filterhierarchyMap.put("vid", Arrays.asList("PR_ABG_SOL"));
		filterHierarchyList.add(filterhierarchyMap);
		request.put(WidgetConstants.FILTERASSETHIERARCHY, filterHierarchyList);
		request.put(ProxyConstants.ASSETSIDMAP, assetsMap);
		ReflectionTestUtils.setField(reader, "t", mockResponseData);
		ReflectionTestUtils.setField(reader, "assetHierarchyFilterService", assetHierarchyFilterService);
	}

	@Test
	void testParseSingleMatch() {
		mockResponseData = createSampleResponseData();
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0574", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) reader.parse(request);
		assertEquals(ColorConstants.AXCO_WW_COLOR,
				((JSONArray) result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CHARTDATA)).getJSONObject(0)
						.get(WidgetConstants.COLOR));

	}

	@Test
	void testParseWithDataNotFound() {
		String inputObject = "{\"data\":[{\"assetId\":\"GT0578\",\"axco_Efficiency\":0}]}";
		mockResponseData.put(WidgetConstants.DATA, new JSONObject(inputObject));
		when(assetHierarchyFilterService.getImmediateParentField(filterHierarchyList, "MC_GT0578", WidgetConstants.ID))
				.thenReturn("L0574");
		JSONObject result = (JSONObject) reader.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA).toString());
	}

	private Map<String, Object> createSampleResponseData() {
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("data", creatSampleJsonData());
		return responseData;

	}

	private JSONArray creatSampleJsonData() {
		JSONArray response = new JSONArray();
		JSONObject mainassert = new JSONObject().put("linkedCase", "Yes");
		JSONArray linkedCaseList = new JSONArray();
		linkedCaseList.put(1234);
		mainassert.put("linkedCaseList", linkedCaseList);
		mainassert.put("assetId", "GT0574");
		mainassert.put("axco_Efficiency", 0.8488);
		mainassert.put("last_Axco_WW_Efficiency", 0.8499);
		mainassert.put("suggested_Hours_Next_WW", 8488);
		mainassert.put("last_Axco_WW_Timestamp", 17013672);

		return response.put(mainassert);

	}

}
