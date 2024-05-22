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

class DeviceNotUpdatedCounterResponseHandlerTest {

	@Mock
	private ProxyService proxyService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@InjectMocks
	private DeviceNotUpdatedCounterResponseHandler<?> handler;

	Map<String, Object> request;
	Map<String, Object> response;
	JSONObject proxyJsonObject;
	Integer deviceNotUpdatedCount;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtils.setField(handler, "proxyService", proxyService);
		ReflectionTestUtils.setField(handler, "httpServletRequest", httpServletRequest);

	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParse_Date() throws Exception {

		Integer deviceNotUpdatedCount = 2;

		String jsonobject = "{\"hideWidget\":false,\"showGreyImage\":false,\"showLiveData\":true,\"data\":{\"data\":17}}";

		response = new HashMap<>();
		response.put(WidgetConstants.DATA, deviceNotUpdatedCount);

		ReflectionTestUtils.setField(handler, "t", response);

		request = new HashMap<>();
		request.put(ProxyConstants.PLANTID.toString(), "ABENGOA");

		when(proxyService.execute(any(Map.class), any(HttpServletRequest.class)))
				.thenReturn(new JSONObject(jsonobject));

		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);

	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Parse the Response - Resources are Empty In the Input")
	void testParseResourcesIsEmpty() throws Exception {

		String jsonobject = "{\"hideWidget\":false,\"showGreyImage\":false,\"showLiveData\":true,\"data\":{\"data\":17}}";

		when(proxyService.execute(any(Map.class), any(HttpServletRequest.class)))
				.thenReturn(new JSONObject(jsonobject));
		response = new HashMap<>();
		response.put("data1", new JSONArray());
		ReflectionTestUtils.setField(handler, "t", response);
		JSONObject result = (JSONObject) handler.parse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

}
