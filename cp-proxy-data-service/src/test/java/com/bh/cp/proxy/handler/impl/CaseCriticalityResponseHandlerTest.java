package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.constants.WidgetConstants;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CaseCriticalityResponseHandlerTest {

	@InjectMocks
	private CaseCriticalityResponseHandler<Object> handler;

	List<Map<String, Object>> filterHierarchyList = new ArrayList<>();
	Map<String, Object> request = new HashMap<>();
	Map<String, Object> mockResponseData = new HashMap<>();
	Map<String, Object> resource;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	String days;
	String weeks;
	String months;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		resource = new HashMap<>();
		resource.put(WidgetConstants.EVENT_DATE, "12-12-2023 12:00:00");
		resource.put(WidgetConstants.CRITICALITY, WidgetConstants.HIGH);
		mockResponseData.put(WidgetConstants.RESOURCES, Arrays.asList(resource));
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		days = LocalDateTime.now().plusDays(10).format(formatter);
		weeks = LocalDateTime.now().plusDays(60).format(formatter);
		months = LocalDateTime.now().plusDays(90).format(formatter);
		ReflectionTestUtils.setField(handler, "t", mockResponseData);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with 3 months daterange")
	void testParseWithDataRange3() throws Exception {
		List<Map<String, Object>> result = (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with 6 months daterange")
	void testParseWithDataRange6() throws Exception {
		resource.put(WidgetConstants.EVENT_DATE, "12-08-2023 12:00:00");
		resource.put(WidgetConstants.CRITICALITY, WidgetConstants.LOW);
		mockResponseData.put(WidgetConstants.RESOURCES, Arrays.asList(resource));
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE6);
		List<Map<String, Object>> result = (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with 1y  daterange")
	void testParseWithDataRange1Y() throws Exception {
		resource.put(WidgetConstants.EVENT_DATE, "12-11-2022 12:00:00");
		resource.put(WidgetConstants.CRITICALITY, WidgetConstants.MEDIUM);
		mockResponseData.put(WidgetConstants.RESOURCES, Arrays.asList(resource));
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE1Y);
		List<Map<String, Object>> result = (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
	}

	@SuppressWarnings("unchecked")
	@ParameterizedTest
	@MethodSource("provideDateArguments")
	@DisplayName("Parse the Response - Giving response with YTD  daterange")
	void testParseWithDataRangeYTD(String eventDate) throws Exception {
		resource.put(WidgetConstants.EVENT_DATE, eventDate);
		resource.put(WidgetConstants.CRITICALITY, WidgetConstants.MEDIUM);
		mockResponseData.put(WidgetConstants.RESOURCES, Arrays.asList(resource));
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGEYTD);
		List<Map<String, Object>> result = (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
	}

	private Stream<Arguments> provideDateArguments() {
		return Stream.of(Arguments.of(LocalDateTime.now().minusDays(15).format(formatter)),
				Arguments.of(LocalDateTime.now().minusDays(7).format(formatter)),
				Arguments.of(LocalDateTime.now().plusDays(97).format(formatter)));
	}

	@SuppressWarnings("unchecked")
	@ParameterizedTest
	@CsvSource({ "25-12-2023 12:00:00,2023-12-18 12:00:00,2023-12-26 12:00:00,WidgetConstants.MEDIUM",
			"25-12-2023 12:00:00,2023-11-18 12:00:00,2023-12-26 12:00:00,WidgetConstants.HIGH",
			"25-12-2023 12:00:00,2023-07-18 12:00:00,2023-12-26 12:00:00,WidgetConstants.MEDIUM",
			"25-12-2023 12:00:00,2022-11-18 12:00:00,2023-12-26 12:00:00,WidgetConstants.LOW",
			"25-12-2022 12:00:00,2018-05-18 12:00:00,2023-12-26 12:00:00,WidgetConstants.HIGH", })
	void testWithDifferentCustomDateRanges(String eventDate, String startDate, String endDate, String criticality) {
		resource.put(WidgetConstants.EVENT_DATE, eventDate);
		resource.put(WidgetConstants.CRITICALITY, criticality);
		mockResponseData.put(WidgetConstants.RESOURCES, Arrays.asList(resource));
		request.put(WidgetConstants.STARTDATE, startDate);
		request.put(WidgetConstants.ENDDATE, endDate);
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATECUSTOM);
		List<Map<String, Object>> result = (List<Map<String, Object>>) handler.parse(request);
		assertNotNull(result);
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Resources as Empty")
	void testParseWithResourcesEmpty() throws Exception {
		mockResponseData.put(WidgetConstants.RESOURCES, new ArrayList<>());
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATECUSTOM);
		JSONObject result = (JSONObject) handler.parse(request);
		assertNotNull(result);
	}

}