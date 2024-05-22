package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

class CSASCaseHistoryResponseHandlerTest {

	@Mock
	private ProxyService proxyService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@InjectMocks
	private CSASCaseHistoryResponseHandler<?> handler;

	Map<String, Object> request;
	Map<String, Object> response;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtils.setField(handler, "t", response);
		ReflectionTestUtils.setField(handler, "proxyService", proxyService);
		ReflectionTestUtils.setField(handler, "httpServletRequest", httpServletRequest);
		ReflectionTestUtils.setField(handler, "kpiTokenId", 73);
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParse_Date() throws Exception {

		JSONArray array = new JSONArray()
				.put(new JSONObject().put("openDateUTC", "29-12-2010 23:00:00").put(ProxyConstants.DATE_CLOSED, "")
						.put(WidgetConstants.STATUS, "OPEN").put(ProxyConstants.PROBLEM_STATEMENTS, "")
						.put(ProxyConstants.IMPLEMENTATION_DATES, "").put(ProxyConstants.ACTION_TAKENS, "")
						.put(ProxyConstants.TITLE, "").put(ProxyConstants.TITLE, "Turbine exhaust temperature sensor")
						.put(ProxyConstants.ATTCHMENTID, "").put(ProxyConstants.LINEUP_IDS, "")
						.put(WidgetConstants.TOKEN, "140").put(WidgetConstants.CASEIDR, "108"));

		String jsonobject = "{\"hideWidget\":false,\"showGreyImage\":false,\"showLiveData\":true,\"data\":{\"tokenCount\":140,\"tokenList\":[{\"caseId\":108,\"token\":140}]}}";

		response = new HashMap<>();
		response.put(WidgetConstants.RESOURCES, array);

		ReflectionTestUtils.setField(handler, "t", response);
		request = new HashMap<>();

		when(proxyService.execute(any(Map.class), any(HttpServletRequest.class)))
				.thenReturn(new JSONObject(jsonobject));

		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() throws Exception {

		String jsonobject = "{\"hideWidget\":false,\"showGreyImage\":false,\"showLiveData\":true,\"data\":{\"tokenCount\":140,\"tokenList\":[{\"caseId\":108,\"token\":140}]}}";

		when(proxyService.execute(any(Map.class), any(HttpServletRequest.class)))
				.thenReturn(new JSONObject(jsonobject));
		response = new HashMap<>();
		response.put("resources1", new JSONArray());
		ReflectionTestUtils.setField(handler, "t", response);
		JSONObject result = (JSONObject) handler.parse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));
	}
}
