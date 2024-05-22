package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

class CaseStatusResponseHandlerTest {

	@Mock
	private ProxyService proxyService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@InjectMocks
	private CaseStatusResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	String inputObject;
	Map<String, Object> mockResponseData = new HashMap<>();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockResponseData.put(WidgetConstants.TOTALRESULTS, 25);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		ReflectionTestUtils.setField(handler, "closedCasesId", -7);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with closed and open cases")
	void testParseWithExpectedOutput() throws Exception {
		request.put(WidgetConstants.WIDGETID, 7);
		Map<String, Object> closedResponse = new HashMap<>();
		closedResponse.put(WidgetConstants.TOTALRESULTS, 15);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, closedResponse));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(15, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.NOOFCASES));
		assertEquals(WidgetConstants.CLOSED,
				result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.CATEGORYNAME));
		assertEquals(ColorConstants.CLOSEDCASES,
				result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.COLOR));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with zero closed and Zero open cases")
	void testParseWithExpectedOutputWithZerCases() throws Exception {
		mockResponseData.put(WidgetConstants.TOTALRESULTS, 0);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		request.put(WidgetConstants.WIDGETID, 7);
		Map<String, Object> closedResponse = new HashMap<>();
		closedResponse.put(WidgetConstants.TOTALRESULTS, 0);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, closedResponse));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with open cases")
	void testParseWithClosedResponseEmpty() throws Exception {
		request.put(WidgetConstants.WIDGETID, 7);
		Map<String, Object> closedResponse = new HashMap<>();
		closedResponse.put(WidgetConstants.TOTALRESULTS, 0);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, closedResponse));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(25, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.NOOFCASES));
		assertEquals(WidgetConstants.OPEN,
				result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.CATEGORYNAME));
		assertEquals(ColorConstants.OPENCASES,
				result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.COLOR));

	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with closed cases")
	void testParseWithOpenResponseEmpty() throws Exception {
		request.put(WidgetConstants.WIDGETID, 7);
		mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.TOTALRESULTS, 0);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		Map<String, Object> closedResponse = new HashMap<>();
		closedResponse.put(WidgetConstants.TOTALRESULTS, 15);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, closedResponse));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(15, result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.NOOFCASES));
		assertEquals(WidgetConstants.CLOSED,
				result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.CATEGORYNAME));
		assertEquals(ColorConstants.CLOSEDCASES,
				result.getJSONArray(WidgetConstants.DATA).getJSONObject(0).get(WidgetConstants.COLOR));

	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with NoDataFound")
	void testParseWithOpenResponseAndClosedResponseEmpty() throws Exception {
		request.put(WidgetConstants.WIDGETID, 7);
		mockResponseData = new HashMap<>();
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, new HashMap<>()));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA).toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - if widgetId is matched")
	void testParseWithSameWidgetId() throws Exception {
		request.put(WidgetConstants.WIDGETID, -7);
		Map<String, Object> closedResponse = new HashMap<>();
		closedResponse.put(WidgetConstants.TOTALRESULTS, 15);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, closedResponse));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

}
