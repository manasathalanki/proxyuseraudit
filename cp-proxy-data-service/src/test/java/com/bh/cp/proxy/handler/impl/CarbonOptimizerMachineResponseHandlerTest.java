package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

class CarbonOptimizerMachineResponseHandlerTest {

	@Mock
	private ProxyService proxyService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@InjectMocks
	private CarbonOptimizerMachineResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	String inputObject;
	String valuesApiResponse;
	String actualSuggestedNull;
	Map<String, Object> mockResponseData;
	Map<String, String> replaceValues;
	String assetId = "";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockResponseData = new HashMap<>();
		inputObject = "[{\"projectid\":\"project1\",\"plantid\":\"plant1\",\"tabs\":[{\"tabid\":\"01\",\"tabname\":\"WarmMR(MR1)\",\"machines\":[\"GT1461\",\"GT1462\"]}]}]";
		mockResponseData.put(WidgetConstants.DATA, new JSONArray(inputObject));
		actualSuggestedNull = "{\"data\":[{\"assetId\":\"GT1461\",\"actualValue\":null,\"suggestedValue\":null,\"unitMeasure\":\"rpm\",\"running\":null,\"message\":{\"label\":\"Optimalsolutionnotfound\"}}]}";
		valuesApiResponse = "{\"data\":[{\"assetId\":\"GT1461\",\"actualValue\":9928.587891,\"suggestedValue\":9923.587891,\"unitMeasure\":\"rpm\",\"running\":true,\"message\":{\"label\":\"Optimalsolutionnotfound\"}},{\"assetId\":\"GT1462\",\"actualValue\":9422.413086,\"suggestedValue\":9433.413086,\"unitMeasure\":\"kW\",\"running\":false,\"message\":{\"label\":\"Optimizationcannotstart:invalidbatchininput\"}}]}";
		replaceValues = new HashMap<>();
		assetId = "GT1461";
		replaceValues.put(ProxyConstants.KEY_ASSET_ID, assetId);
		request.put(WidgetConstants.WIDGETID, 38);
		request.put(ProxyConstants.REPLACE_VALUES, replaceValues);
		request.put(ProxyConstants.LEVEL, ProxyConstants.MACHINES);
		request.put(ProxyConstants.VID, "MC_GT1461");
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		ReflectionTestUtils.setField(handler, "carbonOptimizerWidgetId", -38);
		ReflectionTestUtils.setField(handler, "runningImageId", 5);
		ReflectionTestUtils.setField(handler, "stoppedImageId", 6);
		ReflectionTestUtils.setField(handler, "incrementImageId", 7);
		ReflectionTestUtils.setField(handler, "decrementImageId", 8);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with Suggested Value and Actual Value as not null")
	void testParseWithActualValueSuggestedValueNotNull() throws Exception {
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, new JSONObject(valuesApiResponse)));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(true, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.RUNNING));
		assertEquals("9929rpm", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.ACTUALVALUE));
		assertEquals("9924rpm", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.SUGGESTEDVALUE));
		assertEquals(WidgetConstants.DECREEMENTREQUIRED, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.CARBONSTATUS));
		assertEquals(assetId, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.UNIT));
		assertEquals(5, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.RUNNINGIMAGEID));
	}

	@SuppressWarnings("unchecked")
	
	@Test
	@DisplayName("Parse the Response - Giving response with Suggested Value,Actual Value and Running as null")
	void testParseWithSuggestedValueActualValueRunningNull() throws Exception {
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, new JSONObject(actualSuggestedNull)));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NOTAPPLICABLE, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.ACTUALVALUE));
		assertEquals(WidgetConstants.NOTAPPLICABLE, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.SUGGESTEDVALUE));
		assertEquals(WidgetConstants.EMPTYSTRING, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.CARBONSTATUS));
		assertEquals(6, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.RUNNINGIMAGEID));
		assertEquals(false, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.RUNNING));
		assertEquals(WidgetConstants.EMPTYSTRING, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.CHANGEREQUIRED));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with One Value  as null")
	void testParseWithEitherSuggestedValueOrActualValueNull() throws Exception {
		valuesApiResponse = "{\"data\":[{\"assetId\":\"GT1461\",\"actualValue\":9928.587891,\"suggestedValue\":null,\"unitMeasure\":\"rpm\",\"running\":true,\"message\":{\"label\":\"Optimalsolutionnotfound\"}},{\"assetId\":\"GT1462\",\"actualValue\":null,\"suggestedValue\":9433.413086,\"unitMeasure\":\"kW\",\"running\":false,\"message\":{\"label\":\"Optimizationcannotstart:invalidbatchininput\"}}]}";
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, new JSONObject(valuesApiResponse)));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals("9929rpm", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.ACTUALVALUE));
		assertEquals(WidgetConstants.NOTAPPLICABLE, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.SUGGESTEDVALUE));
		assertEquals(WidgetConstants.EMPTYSTRING, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.CARBONSTATUS));
		assertEquals(5, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.RUNNINGIMAGEID));
		assertEquals(true, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.RUNNING));
		assertEquals(WidgetConstants.EMPTYSTRING, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.CHANGEREQUIRED));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with SuggestedValue and ActualValue as Equal")
	void testParseWithEitherSuggestedValueActualValueEqual() throws Exception {
		valuesApiResponse = "{\"data\":[{\"assetId\":\"GT1461\",\"actualValue\":9928.587891,\"suggestedValue\":9928.587891,\"unitMeasure\":\"rpm\",\"running\":true,\"message\":{\"label\":\"Optimalsolutionnotfound\"}}]}";
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, new JSONObject(valuesApiResponse)));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(true, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.RUNNING));
		assertEquals("9929rpm", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.ACTUALVALUE));
		assertEquals("9929rpm", result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.SUGGESTEDVALUE));
		assertEquals(WidgetConstants.EMPTYSTRING, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.CARBONSTATUS));
		assertEquals(5, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.RUNNINGIMAGEID));
		assertEquals(WidgetConstants.EMPTYSTRING, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.COLORCODE));
		assertEquals(BigDecimal.valueOf(0), result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.CHANGEREQUIRED));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Message as Empty and Label Not Found")
	void testParseWithMessageAsEmptyAndLabelNotFound() throws Exception {
		valuesApiResponse = "{\"data\":[{\"assetId\":\"GT1461\",\"actualValue\":9928.587891,\"suggestedValue\":null,\"unitMeasure\":\"rpm\",\"running\":true,\"message\":{\"desc\":\"Optimalsolutionnotfound\"}},{\"assetId\":\"GT1462\",\"actualValue\":null,\"suggestedValue\":9433.413086,\"unitMeasure\":\"kW\",\"running\":false,\"message\":{}}]}";
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, new JSONObject(valuesApiResponse)));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.EMPTYSTRING, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0)
				.getJSONArray(WidgetConstants.MACHINES).getJSONObject(0).get(WidgetConstants.LABEL));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - if widgetId is matched")
	void testParseWithValuesArrayAsEmpty() throws Exception {
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, new JSONObject()));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA).toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - if widgetId is matched")
	void testParseWithSameWidgetId() throws Exception {
		request.put(WidgetConstants.WIDGETID, -38);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, new JSONObject(valuesApiResponse)));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

}
