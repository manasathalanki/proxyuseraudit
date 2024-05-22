package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.helper.WidgetSubscriptionCheckHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

class LastIcenterResponseHandlerTest {

	@Mock
	private WidgetSubscriptionCheckHelper checkHelper;

	@Mock
	private HttpServletRequest httpServletRequest;

	@InjectMocks
	private LastIcenterResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	Map<String, Object> mockResponseData = new HashMap<>();
	JSONArray array;
	Map<String, Object> moHiResponse;
	List<String> privileagesList;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		array = new JSONArray().put(new JSONObject().put(WidgetConstants.CASEID, 123)
				.put(WidgetConstants.TYPE, "Sample Type").put(WidgetConstants.UNITNAME, "Sample Unit")
				.put(WidgetConstants.LASTNOTIFICATIONUTC, "2023-08-17T12:00:00Z")
				.put(WidgetConstants.CRITICALITY, "HIGH").put(WidgetConstants.TITLE, "title"));
		mockResponseData.put(WidgetConstants.RESOURCES, array);
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		request.put(ProxyConstants.HTTPSERVLETREQUEST, httpServletRequest);
		request.put(ProxyConstants.WIDGET_ID, 8);
		request.put(ProxyConstants.VID, "MC_GT0672");
		moHiResponse = new HashMap<>();
		moHiResponse.put(ProxyConstants.SHOWLIVEDATA, true);
		privileagesList = new ArrayList<>();
		privileagesList.add("Maintenance Optimizer");
		privileagesList.add("Health Index");
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParseWithProperOutput() throws JsonProcessingException {
		when(checkHelper.checkAdvanceServicePrivilegeAccess((HttpServletRequest)request.get(ProxyConstants.HTTPSERVLETREQUEST),
				(String)request.get(ProxyConstants.VID))).thenReturn(true);
		JSONObject result = (JSONObject) handler.parse(request);
		assertEquals("2023-08-17T12:00:00Z",
				result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.EVENTDATER));
		assertEquals("Sample Unit", result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.UNITNAMER));
		assertEquals('H', result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CRITICALITY));
		assertEquals("title", result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CASETITLER));
		assertEquals("Sample Type", result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.TYPE));
		assertEquals(ColorConstants.CRITICALITYHIGHNEW, result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.BGCOLOR));
	}

	@Test
	@DisplayName("Parse the Response - criticality as low")
	void testParseWithCriticalityLow() throws Exception {
		when(checkHelper.checkAdvanceServicePrivilegeAccess((HttpServletRequest)request.get(ProxyConstants.HTTPSERVLETREQUEST),
				(String)request.get(ProxyConstants.VID))).thenReturn(true);
		array = new JSONArray().put(new JSONObject().put(WidgetConstants.CASEID, 123)
				.put(WidgetConstants.TYPE, "Sample Type").put(WidgetConstants.UNITNAME, "Sample Unit")
				.put(WidgetConstants.EVENTDATEUTC, "2023-08-17T12:00:00Z").put(WidgetConstants.CRITICALITY, "LOW")
				.put(WidgetConstants.TITLE, "title"));

		mockResponseData.put(WidgetConstants.RESOURCES, array);
		JSONObject result = (JSONObject) handler.parse(request);
		assertEquals('L', result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CRITICALITY));
		assertEquals(ColorConstants.CRITICALITYLOWNEW,
				result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.BGCOLOR));
	}

	@Test
	@DisplayName("Parse the Response - criticality as medium")
	void testParseWithCriticalityMedium() throws Exception {
		when(checkHelper.checkAdvanceServicePrivilegeAccess((HttpServletRequest)request.get(ProxyConstants.HTTPSERVLETREQUEST),
				(String)request.get(ProxyConstants.VID))).thenReturn(true);
		array = new JSONArray().put(new JSONObject().put(WidgetConstants.CASEID, 123)
				.put(WidgetConstants.TYPE, "Sample Type").put(WidgetConstants.UNITNAME, "Sample Unit")
				.put(WidgetConstants.EVENTDATEUTC, "2023-08-17T12:00:00Z").put(WidgetConstants.CRITICALITY, "MEDIUM")
				.put(WidgetConstants.TITLE, "title"));
		mockResponseData.put(WidgetConstants.RESOURCES, array);
		JSONObject result = (JSONObject) handler.parse(request);
		assertEquals('M', result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CRITICALITY));
		assertEquals(ColorConstants.CRITICALITYMEDIUMNEW,
				result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.BGCOLOR));
	}

	@Test
	@DisplayName("Parse the Response - Criticality Not Found In Resources In the Input")
	void testParseCriticalityNotFoundInInput() throws Exception {
		when(checkHelper.checkAdvanceServicePrivilegeAccess((HttpServletRequest)request.get(ProxyConstants.HTTPSERVLETREQUEST),
				(String)request.get(ProxyConstants.VID))).thenReturn(false);
		array = new JSONArray().put(new JSONObject().put(WidgetConstants.CASEID, 123)
				.put(WidgetConstants.TYPE, "Sample Type").put(WidgetConstants.UNITNAME, "Sample Unit")
				.put(WidgetConstants.EVENTDATEUTC, "2023-08-17T12:00:00Z"));
		mockResponseData.put(WidgetConstants.RESOURCES, array);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.EMPTYSTRING,
				result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CRITICALITY));

	}

	@Test
	@DisplayName("Parse the Response - Criticality Found In the Input But It is null")
	void testParseLevel_CriticalityIsNull() throws Exception {
		when(checkHelper.checkAdvanceServicePrivilegeAccess((HttpServletRequest)request.get(ProxyConstants.HTTPSERVLETREQUEST),
				(String)request.get(ProxyConstants.VID))).thenReturn(false);
		array = new JSONArray().put(new JSONObject().put(WidgetConstants.CASEID, 123)
				.put(WidgetConstants.TYPE, JSONObject.NULL).put(WidgetConstants.UNITNAME, JSONObject.NULL)
				.put(WidgetConstants.EVENTDATEUTC, JSONObject.NULL));

		mockResponseData.put(WidgetConstants.RESOURCES, array);
		mockResponseData.put(WidgetConstants.CRITICALITY, null);
		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.EMPTYSTRING,
				result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CRITICALITY));

	}

	@Test
	@DisplayName("Parse the Response - Resources Found In the Input But Length is Zero")
	void testParseLevel_ResourcesLengthZero() {

		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray().put(new JSONObject()));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.EMPTYSTRING,
				result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CRITICALITY));

	}

	@Test
	@DisplayName("Parse the Response - Resources Not Found In the Input")
	void testParseLevel_ResourcesNotFound() {
		mockResponseData.put(WidgetConstants.DATA, new JSONArray().put(new JSONObject()));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.EMPTYSTRING,
				result.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CRITICALITY));

	}

}