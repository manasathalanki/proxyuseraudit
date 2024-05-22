package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.helper.WidgetSubscriptionCheckHelper;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

class EventsTimelineResponseHandlerTest {

	@Mock
	private ProxyService proxyService;

	@Mock
	private WidgetSubscriptionCheckHelper checkHelper;

	@Mock
	private HttpServletRequest httpServletRequest;

	@InjectMocks
	private EventsTimelineResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	String inputObject;
	String eventsResponse;
	Map<String, Object> eventsResponseSecond;
	Map<String, Object> mockResponseData = new HashMap<>();
	List<String> privileagesList;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		inputObject = "[{\"caseId\":166761225,\"title\":\"titleissue\",\"status\":\"OPEN\",\"customerPriority\":null,\"criticality\":\"HIGH\",\"type\":\"Early Warning\",\"anomalyCategory\":\"Rotordyn./Vib.\",\"eventDate\":\"06-02-204000:00:00\",\"eventDateUTC\":\"05-02-204023:00:00\",\"eventLogIds\":[\"58590\"],\"openDateUTC\":\"06-07-2023 08:37:38\",\"lineupId\":\"LNTEST\"}]";
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray(inputObject));
		request.put(WidgetConstants.WIDGETID, 4);
		request.put(ProxyConstants.HTTPSERVLETREQUEST, httpServletRequest);
		request.put(ProxyConstants.VID, "MC_MCTEST");
		eventsResponse = "[{\"id\":58590,\"asset_id\":\"BP_ANGO\",\"asset_level\":\"lineups\",\"d_event\":\"2023-12-13T00:00:00.000Z\",\"type_id\":\"AXCOWW\",\"type_desc\":\"AxialCompressorWaterWashing(Off-Line)\",\"lineup_id\":\"LNTEST\",\"serialno\":\"MCTEST\",\"n_failure_mode\":1,\"failure_mode_desc\":\"Rotordyn./Vib.\"}]";
		eventsResponseSecond = new HashMap<>();
		eventsResponseSecond.put(WidgetConstants.RESOURCES, new JSONArray(eventsResponse));
		privileagesList = new ArrayList<>();
		privileagesList.add("Maintenance Optimizer");
		privileagesList.add("Health Index");
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		ReflectionTestUtils.setField(handler, "eventTimelineWidgetId", -4);
		ReflectionTestUtils.setField(handler, "proxyService", proxyService);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with criticality as High and event-type as Early Warning")
	void testParseWithExpectedOutput() throws Exception {
		when(checkHelper.checkAdvanceServicePrivilegeAccess((HttpServletRequest)request.get(ProxyConstants.HTTPSERVLETREQUEST),
				(String)request.get(ProxyConstants.VID))).thenReturn(true);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, eventsResponseSecond));
		List<Map<String,Object>> result =  (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
		assertEquals("LNTEST", result.get(0).get((WidgetConstants.LINEUP_NAME)));
		assertEquals('H', result.get(0).get((WidgetConstants.CASE_CRITICALITY)));
	}

	@SuppressWarnings("unchecked")
	@ParameterizedTest
	@CsvSource({"Trip,LOW","Normal Shutdown,MEDIUM","Start,HIGH","Maintenance,LOW","Maintenance (Unplanned),LOW",
		"Maintenance - Water wash,MEDIUM","others,MEDIUM"})
	@DisplayName("Parse the Response - Giving response with event-type as Trip")
	void testParseWithEventTypeAsTrip(String type,String criticality) throws Exception {
		when(checkHelper.checkAdvanceServicePrivilegeAccess((HttpServletRequest)request.get(ProxyConstants.HTTPSERVLETREQUEST),
				(String)request.get(ProxyConstants.VID))).thenReturn(true);
		List<Map<String,Object>> newList=prepareMap();
		newList.get(0).put(WidgetConstants.TYPE,type);
		newList.get(0).put(WidgetConstants.CASE_CRITICALITY,criticality);
		mockResponseData.put(WidgetConstants.RESOURCES,newList);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, eventsResponseSecond));
		List<Map<String,Object>> result = (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
	}

	private List<Map<String, Object>> prepareMap() {
		List<String> eventLogIds = new ArrayList<>();
		eventLogIds.add("58590");
		Map<String, Object> resourcesMap = new HashMap<>();
		resourcesMap.put(WidgetConstants.CASEID, 166761225);
		resourcesMap.put(WidgetConstants.TITLE, "titleissue");
		resourcesMap.put(WidgetConstants.STATUS, "OPEN");
		resourcesMap.put(WidgetConstants.CASE_CRITICALITY, "LOW");
		resourcesMap.put(WidgetConstants.TYPE, "Trip");
		resourcesMap.put(WidgetConstants.ANOMALYCATEGORY, "Rotordyn./Vib.");
		resourcesMap.put(WidgetConstants.EVENT_DATE, "06-02-204000:00:00");
		resourcesMap.put(WidgetConstants.EVENTDATEUTC, "05-02-204023:00:00");
		resourcesMap.put(WidgetConstants.EVENTLOGIDS, eventLogIds);
		resourcesMap.put("openDateUTC", "06-07-2023 08:37:38");
		resourcesMap.put(WidgetConstants.LINEUPID, "LNTEST1");
		List<Map<String, Object>> resourcesList = new ArrayList<>();
		resourcesList.add(resourcesMap);
		return resourcesList;
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with event-type as Maintenance (Planned)")
	void testParseWithEventTypeAsDefaultColor() throws Exception {
		when(checkHelper.checkAdvanceServicePrivilegeAccess((HttpServletRequest)request.get(ProxyConstants.HTTPSERVLETREQUEST),
				(String)request.get(ProxyConstants.VID))).thenReturn(true);
		inputObject = "[{\"caseId\":166761225,\"title\":\"titleissue\",\"status\":\"OPEN\",\"criticality\":\"MEDIUM\",\"type\":\"Maintenance (Planned)\",\"anomalyCategory\":\"Rotordyn./Vib.\",\"eventDate\":\"06-02-204000:00:00\",\"eventDateUTC\":\"05-02-204023:00:00\",\"eventLogIds\":[\"58590\"],\"openDateUTC\":\"06-07-2023 08:37:38\",\"lineupId\":\"LNTEST\"}]";
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray(inputObject));
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, eventsResponseSecond));
		List<Map<String,Object>> result = (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
		assertEquals(ColorConstants.EVENT_TIMELINE_TRIP, result.get(0).get(WidgetConstants.COLOR));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with criticality as Null and event-type as Trip")
	void testParseWithCriticalityNull() throws Exception {
		inputObject = "[{\"caseId\":166761225,\"title\":\"titleissue\",\"status\":\"OPEN\",\"criticality\":null,\"type\":\"Trip\",\"anomalyCategory\":\"Rotordyn./Vib.\",\"eventDate\":\"06-02-204000:00:00\",\"eventDateUTC\":\"05-02-204023:00:00\",\"eventLogIds\":[\"58590\"],\"openDateUTC\":\"06-07-2023 08:37:38\",\"lineupId\":\"LNTEST\"}]";
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray(inputObject));
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, eventsResponseSecond));
		List<Map<String,Object>> result = (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
		assertEquals(JSONObject.NULL, result.get(0).get(WidgetConstants.CASE_CRITICALITY));
		assertEquals(ColorConstants.EVENT_TIMELINE_TRIP, result.get(0).get(WidgetConstants.COLOR));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - widgetIds are same")
	void testParseWithSameWidgetIds() throws Exception {
		request.put(WidgetConstants.WIDGETID, -4);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, eventsResponseSecond));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - No Data Found")
	void testParseWithExpection() throws Exception {
		mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.DATA, new ArrayList<>());
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		when(checkHelper.checkAdvanceServicePrivilegeAccess(
				(HttpServletRequest) request.get(ProxyConstants.HTTPSERVLETREQUEST),
				(String) request.get(ProxyConstants.VID))).thenReturn(false);
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, eventsResponseSecond));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}
}
