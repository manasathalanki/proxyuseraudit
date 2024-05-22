package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class KPITripsResponseHandlerTest {

	@Mock
	KPIStartsAndTripsResponseHandler kpiStartsAndTripsResponseHandler;

	@InjectMocks
	KPITripsResponseHandler<?> reader;

	Map<String, Object> request = new HashMap<>();
	HashMap<String, Object> mockResponseData;
	JSONArray responseArray;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockResponseData = createSampleResponseData();
		ReflectionTestUtils.setField(reader, "t", mockResponseData);
		ReflectionTestUtils.setField(reader, "kpiStartsAndTripsResponseHandler", kpiStartsAndTripsResponseHandler);
	}

	@Test
	void testParseSingleMatch() {
		when(kpiStartsAndTripsResponseHandler.parse(mockResponseData)).thenReturn(mockResponseData);
		Object result = reader.parse(request);
		assertNotNull(result);
		
	
	}

	private HashMap<String, Object> createSampleResponseData() {
		HashMap<String, Object> responseData = new HashMap<>();
		responseData.put("data", creatSampleJsonData());
		return responseData;

	}

	private JSONObject creatSampleJsonData() {
		JSONObject mainassert = new JSONObject().put("totalResult", 3);
		JSONArray resourcesArray = new JSONArray();
		JSONObject resourceObject = new JSONObject();
		resourceObject.put("id", 2435);
		JSONArray caseList = new JSONArray();
		resourceObject.put("caselist", caseList);
		resourcesArray.put(resourceObject);
		mainassert.put("resorces", resourcesArray);

		return mainassert;

	}
}
