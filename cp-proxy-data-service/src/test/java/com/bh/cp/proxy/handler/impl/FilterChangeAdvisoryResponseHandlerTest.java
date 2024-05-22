package com.bh.cp.proxy.handler.impl;

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

class FilterChangeAdvisoryResponseHandlerTest {

	@InjectMocks
	private FilterChangeAdvisoryResponseHandler<?> handler;

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

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParseWithDataFound() throws Exception {
		List<Map<String, Object>> result = (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
	}
	@Test
	@DisplayName("Parse the Response - Giving response with No Data Found")
	void testParseWithNoDataFound() throws Exception {
		dataMap.put(WidgetConstants.DATA, new ArrayList<>());
		mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.DATA, dataMap);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

	private Map<String, Object> prepareJSONObject() {
		Map<String, Object> data = new HashMap<>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<Map<String, Object>> chartData = new ArrayList<>();
		Map<String, Object> data1 = new HashMap<>();
		data1.put(WidgetConstants.FILTERNAME, "Pre-Filter");
		data1.put(WidgetConstants.DIFFERENTIALPRESSUREVALUE, 5.0);
		data1.put(WidgetConstants.THRESHOLDMINVALUE, JSONObject.NULL);
		data1.put(WidgetConstants.THRESHOLDMAXVALUE, 8.08888);
		data1.put(WidgetConstants.THRESHOLDMAXNAME, "HH");
		data1.put(WidgetConstants.THRESHOLDMINNAME, "H");
		data1.put(WidgetConstants.UOM, "mmH20");
		data1.put(WidgetConstants.NEXTFILTERCHANGE, 1715871803000l);
		data1.put(WidgetConstants.LASTFILTERCHANGE, 1634515200000l);
		chartData.add(data1);
		Map<String, Object> data2 = new HashMap<>();
		data2.put(WidgetConstants.FILTERNAME, "High Efficiency");
		data2.put(WidgetConstants.DIFFERENTIALPRESSUREVALUE, 6.5);
		data2.put(WidgetConstants.THRESHOLDMINVALUE, JSONObject.NULL);
		data2.put(WidgetConstants.THRESHOLDMAXVALUE, JSONObject.NULL);
		data2.put(WidgetConstants.THRESHOLDMAXNAME, "HH");
		data2.put(WidgetConstants.THRESHOLDMINNAME, "H");
		data2.put(WidgetConstants.UOM, "mmH20");
		data2.put(WidgetConstants.NEXTFILTERCHANGE, 1715871797000l);
		data1.put(WidgetConstants.LASTFILTERCHANGE, 1634515200000l);
		chartData.add(data2);
		Map<String, Object> data3 = new HashMap<>();
		data3.put(WidgetConstants.FILTERNAME, "HEPA");
		data3.put(WidgetConstants.DIFFERENTIALPRESSUREVALUE, 27.000);
		data3.put(WidgetConstants.THRESHOLDMINVALUE, 8.0000);
		data3.put(WidgetConstants.THRESHOLDMAXVALUE, JSONObject.NULL);
		data3.put(WidgetConstants.THRESHOLDMAXNAME, "HH");
		data3.put(WidgetConstants.THRESHOLDMINNAME, "H");
		data3.put(WidgetConstants.UOM, "mmH20");
		data3.put(WidgetConstants.NEXTFILTERCHANGE, 1702406087000l);
		data1.put(WidgetConstants.LASTFILTERCHANGE, 1634515200000l);
		chartData.add(data3);
		Map<String, Object> dataObject = new HashMap<String, Object>();
		dataObject.put(WidgetConstants.ASSETID, "MCTEST");
		dataObject.put(WidgetConstants.FILTERDATA, chartData);
		dataList.add(dataObject);
		data.put(WidgetConstants.DATA, dataList);
		return data;
	}

}
