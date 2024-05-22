package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

class CMCaseStatusResponseHandlerTest {

	@Mock
	private ProxyService proxyService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@InjectMocks
	private CMCaseStatusResponseHandler<?> handler;
	Map<String, Object> userResponse;
	Map<String, Object> request;
	Map<String, Object> response;
	JSONObject proxyJsonObject;

	Map<String, Object> taskListResponse;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		JSONArray array = new JSONArray().put(new JSONObject().put("title", "INTERNAL").put("status", "OPEN"));

		taskListResponse = new HashMap<>();
		taskListResponse.put(ProxyConstants.OPEN_CASE_RES, 0);
		taskListResponse.put("ClosedCaseResponse", 21);
		taskListResponse.put("DeletedCaseResponse", 0);

		proxyJsonObject = new JSONObject().put(WidgetConstants.DATA, taskListResponse);

		response = new HashMap<>();
		request = new HashMap<>();

		request.put(ProxyConstants.OPEN_CASE_RESULT, ProxyConstants.OPEN_CASE_RES);
		request.put("CloseCaseResult", "21");
		request.put("DeleteCaseResult", "0");

		response.put("resources", array);

		userResponse = new HashMap<>();

		userResponse.put(WidgetConstants.TITLE, "INTERNAL");

		ReflectionTestUtils.setField(handler, "t", response);
		ReflectionTestUtils.setField(handler, "proxyService", proxyService);
		ReflectionTestUtils.setField(handler, "httpServletRequest", httpServletRequest);

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParse_title() throws Exception {
		when(proxyService.execute(request, httpServletRequest))
		.thenReturn(userResponse);

		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(3, result.getJSONObject(WidgetConstants.DATA).length());

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output for CLOSED and DELETED")
	void testParse_Date() throws Exception {

		JSONObject proxy_array_delete = new JSONObject().put("title", "INTERNAL").put("status", "DELETED");

		JSONObject proxy_array_closed = new JSONObject().put("title", "INTERNAL").put("status", "CLOSED");

		JSONArray jsonobj = new JSONArray().put(proxy_array_closed).put(proxy_array_delete);

		response.put("resources", jsonobj);

		ReflectionTestUtils.setField(handler, "t", response);

		request = new HashMap<>();
		request.put(WidgetConstants.STARTDATE, "2023-10-09 0:0:0");
		request.put(WidgetConstants.ENDDATE, "2024-01-09 0:0:0");

		when(proxyService.execute(request, httpServletRequest)).thenReturn(proxyJsonObject);

		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);

	}

	@Test
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() throws Exception {
		when(proxyService.execute(request, httpServletRequest)).thenReturn(proxyJsonObject);
		 response = new HashMap<>();
		response.put("resources1", new JSONArray());
		ReflectionTestUtils.setField(handler, "t", response);
		JSONObject result = (JSONObject) handler.parse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

}