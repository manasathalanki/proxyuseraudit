package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

class MaintenanceOptimizerResponseHandlerTest {

	@Mock
	private ProxyService proxyService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@InjectMocks
	private MaintenanceOptimizerResponseHandler<?> handler;

	Map<String, Object> request = new HashMap<>();
	Map<String, Object> request1 = new HashMap<>();
	String inputObject;
	String valuesApiResponse;
	String actualSuggestedNull;
	Map<String, Object> mockResponseData = new HashMap<>();
	Map<String, String> replaceValues = new HashMap<>();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		replaceValues=new HashMap<>();
		replaceValues.put(ProxyConstants.KEY_LINEUP_IDS_CSV, "LNTEST");
		request.put(ProxyConstants.REPLACE_VALUES, replaceValues);
		request.put(ProxyConstants.VID, "MC_MCTEST");
		request1.put(ProxyConstants.REPLACE_VALUES, replaceValues);
		mockResponseData.put(WidgetConstants.RESOURCES, createFirstApiResponse());
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
		ReflectionTestUtils.setField(handler, "maintenanceOptimizerWidgetId", -3);
		ReflectionTestUtils.setField(handler, "maintenanceOptimizerEventWidgetId", -31);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParseWithProperOutput() throws Exception {
		when(proxyService.execute(anyMap(),any(HttpServletRequest.class)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, createSecondApiResponse())).thenReturn(new JSONObject().put(WidgetConstants.DATA, createThirdApiResponse()));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

	@ParameterizedTest
	@CsvSource({ "-3", "-31" })
	@DisplayName("Parse the Response - widgetId matched")
	void testParseWithSameWidgetId(Integer widgetId) throws Exception {
		request.put(WidgetConstants.WIDGETID, widgetId);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with No Data Found")
	void testParseWithCatch() throws Exception {
		when(proxyService.execute(anyMap(),any(HttpServletRequest.class)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, new JSONObject())).thenReturn(new JSONObject().put(WidgetConstants.DATA,new JSONObject()));
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	private JSONArray createFirstApiResponse() {
		JSONArray resourcesArray = new JSONArray()
				.put(new JSONObject().put(WidgetConstants.CASEID, 12345).put(WidgetConstants.LINEUPID, "LNTEST"))
				.put(new JSONObject().put(WidgetConstants.CASEID, 12346).put(WidgetConstants.LINEUPID, "LNTEST1"))
				.put(new JSONObject().put(WidgetConstants.CASEID, 12347).put(WidgetConstants.LINEUPID, "LNTEST2"))
				.put(new JSONObject().put(WidgetConstants.CASEID, 12348).put(WidgetConstants.LINEUPID, "LNTEST3"))
				.put(new JSONObject().put(WidgetConstants.CASEID, 12349).put(WidgetConstants.LINEUPID, "LNTEST4"))
				.put(new JSONObject().put(WidgetConstants.CASEID, 12350).put(WidgetConstants.LINEUPID, "LNTEST5"));
		return resourcesArray;
	}

	private JSONObject createSecondApiResponse() {
		JSONArray resourcesArray = new JSONArray()
				.put(new JSONObject().put("parentCaseId", 12345).put("taskId", 3171)
						.put("suggestedDate", LocalDateTime.now().minusDays(15).format(formatter))
						.put("isUrgent", false))
				.put(new JSONObject().put("parentCaseId", 12346).put("taskId", 4676)
						.put("suggestedDate", LocalDateTime.now().plusDays(66).format(formatter))
						.put("isUrgent", false))
				.put(new JSONObject().put("parentCaseId", 12347).put("taskId", 4678)
						.put("suggestedDate", LocalDateTime.now().plusDays(19).format(formatter).toString())
						.put("isUrgent", false))
				.put(new JSONObject().put("parentCaseId", 12348).put("taskId", 4679)
						.put("suggestedDate", LocalDateTime.now().plusDays(9).format(formatter).toString())
						.put("isUrgent", false))
				.put(new JSONObject().put("parentCaseId", 12349).put("taskId", 4670)
						.put("suggestedDate", LocalDateTime.now().plusDays(4).format(formatter).toString())
						.put("isUrgent", false))
				.put(new JSONObject().put("parentCaseId", 12350).put("taskId", 4671)
						.put("suggestedDate", LocalDateTime.now().plusDays(15).format(formatter).toString())
						.put("isUrgent", true));
		JSONObject data = new JSONObject().put(WidgetConstants.TASKS, resourcesArray);
		return data;
	}

	private JSONObject createThirdApiResponse() {
		JSONArray resourcesArray = new JSONArray()
				.put(new JSONObject().put("rmdEventId", 12345).put("eventTypeDesc", "test")
						.put(WidgetConstants.LINEUPID, "LNTEST").put(WidgetConstants.EVENT_TYPE, "test1"))
				.put(new JSONObject().put("rmdEventId", 406).put("eventTypeDesc", "test1")
						.put(WidgetConstants.LINEUPID, "LNTEST1").put(WidgetConstants.EVENT_TYPE, "test2"))
				.put(new JSONObject().put("rmdEventId", 407).put("eventTypeDesc", "test2")
						.put(WidgetConstants.LINEUPID, "LNTEST2").put(WidgetConstants.EVENT_TYPE, "test3"))
				.put(new JSONObject().put("rmdEventId", 408).put("eventTypeDesc", "test3")
						.put(WidgetConstants.LINEUPID, "LNTEST3").put(WidgetConstants.EVENT_TYPE, "test4"))
				.put(new JSONObject().put("rmdEventId", 409).put("eventTypeDesc", "test4")
						.put(WidgetConstants.LINEUPID, "LNTEST4").put(WidgetConstants.EVENT_TYPE, "test5"))
				.put(new JSONObject().put("rmdEventId", 410).put("eventTypeDesc", "test5")
						.put(WidgetConstants.LINEUPID, "LNTEST5").put(WidgetConstants.EVENT_TYPE, "test6"));
		JSONObject data = new JSONObject().put(WidgetConstants.DATA, resourcesArray);
		JSONObject data1 = new JSONObject().put(WidgetConstants.DATA, data);
		return data1;
	}

}
