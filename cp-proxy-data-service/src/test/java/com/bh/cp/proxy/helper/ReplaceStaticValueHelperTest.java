package com.bh.cp.proxy.helper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.entity.ServicesDirectory;

import jakarta.servlet.http.HttpServletRequest;

class ReplaceStaticValueHelperTest {

	HttpServletRequest httpServletRequest;
	@InjectMocks
	private ReplaceStaticValueHelper replaceStaticValueHelper;
	@Mock
	private UMSClientService umsClientService;
	@Mock
	private AssetHierarchyFilterService assetHierarchyFilterService;
	Map<String, Object> request;
	ServicesDirectory servicesDirectoryDB;
	com.bh.cp.proxy.pojo.ServicesDirectory servicesDirectory;
	List<Map<String, Object>> filterHierarchyList;
	Map<String, Object> filterhierarchyMap;
	Map<String, Boolean> umsResponseMap;
	Map<String, String> replaceStaticValues;
	Map<String, Object> assetsMap;
	Map<String, Map<String, Set<String>>> fieldsMap;
	Map<String, Set<String>> vidFieldMap;
	String vid = null;
	String dateRange = null;
	String level = null;
	Integer widgetId = null;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		request = new HashMap<>();
		replaceStaticValues = new HashMap<>();
		assetsMap = new HashMap<>();
		assetsMap.put("currentLevel", "machines");
		assetsMap.put("previousLevel", "lineups");
		assetsMap.put("projects", Arrays.asList("PRTest01"));
		assetsMap.put("searchVid", "MC_MCTest01");
		assetsMap.put("plants", Arrays.asList("PLTest01"));
		assetsMap.put("machines", Arrays.asList("MCTest01"));
		assetsMap.put("trains", Arrays.asList("TRTest01"));
		assetsMap.put("lineups", Arrays.asList("LNTest01"));
		request.put(ProxyConstants.REPLACE_VALUES, replaceStaticValues);
		request.put(ProxyConstants.DATE_RANGE, WidgetConstants.DATERANGE3);
		request.put(ProxyConstants.VID, "MC_MCTest01");
		request.put(ProxyConstants.LEVEL, "machines");
		request.put(ProxyConstants.ASSETSIDMAP, assetsMap);
		request.put(ProxyConstants.WIDGET_ID, 5);
		vid = (String) request.get(ProxyConstants.VID);
		level = (String) request.get(ProxyConstants.LEVEL);
		dateRange = (String) request.get(ProxyConstants.DATE_RANGE);
		widgetId = (Integer) request.get(ProxyConstants.WIDGET_ID);

		vidFieldMap = new HashMap<>();
		vidFieldMap.put("gibSerialNos", new HashSet<>(Arrays.asList("MCTest20", "MCTest21")));
		vidFieldMap.put("enabledServices", new HashSet<>(Arrays.asList("TESTP", "TESTM")));
		vidFieldMap.put("technologyCodes", new HashSet<>());
		vidFieldMap.put("equipmentCodes", new HashSet<>());
		vidFieldMap.put("serialNos", new HashSet<>(Arrays.asList("MCTest01")));
		fieldsMap = new HashMap<>();
		fieldsMap.put((String) request.get(ProxyConstants.VID), vidFieldMap);
		servicesDirectoryDB = new ServicesDirectory();
		servicesDirectoryDB.setId(1);
		servicesDirectoryDB.setInputData("filter");
		servicesDirectoryDB.setCommunicationFormat("test");
		servicesDirectoryDB.setServicetype("REST");
		servicesDirectoryDB.setUri("http://test.com");
		servicesDirectoryDB.setMethod("GET");
		servicesDirectoryDB.setHeaders("<PROJECT>");
		servicesDirectoryDB.setInputData("test");
		servicesDirectoryDB.setOutputHandler("com.bh.cp.proxy.handler.impl.CaseStatusResponseHandler");
		servicesDirectory = new com.bh.cp.proxy.pojo.ServicesDirectory();
		servicesDirectory.setInputData(servicesDirectoryDB.getInputData());
		servicesDirectory.setMethod(servicesDirectoryDB.getMethod());
		servicesDirectory.setUri(servicesDirectoryDB.getUri());
		servicesDirectory.setHeaders(servicesDirectoryDB.getHeaders());
		servicesDirectory.setOutputHandler(servicesDirectoryDB.getOutputHandler());

		filterHierarchyList = new ArrayList<>();
		filterhierarchyMap = new HashMap<>();
		filterhierarchyMap.put("vid", Arrays.asList("MC_MCTest01"));
		filterHierarchyList.add(filterhierarchyMap);
		ReflectionTestUtils.setField(replaceStaticValueHelper, "fleetDataWidgetId", -1);
		ReflectionTestUtils.setField(replaceStaticValueHelper, "umsClientService", umsClientService);
		ReflectionTestUtils.setField(replaceStaticValueHelper, "assetHierarchyFilterService",
				assetHierarchyFilterService);
	}

	@ParameterizedTest
	@CsvSource({"5","-1"})
	@DisplayName("Getting Output-With widget Id not equal to fleets data")
	void testReplaceValues(Integer widgetID) throws Exception {
		request.put(ProxyConstants.WIDGET_ID, widgetID);
		Map<String, String> carbonMap = new HashMap<>();
		carbonMap.put("PR", "PRTest01");
		carbonMap.put("PL", "PLTest01");
		when(assetHierarchyFilterService.getFieldsAndEnabledServicesToMap(filterHierarchyList)).thenReturn(fieldsMap);
		when(umsClientService.getApplicableMachinesForWidget(httpServletRequest, vid, widgetID, ProxyConstants.VID))
				.thenReturn(Arrays.asList("MC_MCTest01"));
		when(umsClientService.getApplicableMachinesForWidget(httpServletRequest, vid, widgetID, ProxyConstants.ID))
				.thenReturn(Arrays.asList("MCTest01"));
	    List<Map<String,String>> list = new ArrayList<>();
	    list.add(carbonMap);
		when(assetHierarchyFilterService.getProjectsAndPlants(assetsMap)).thenReturn(list);
		Map<String, String> replaceValuesResult = replaceStaticValueHelper.getReplaceValues(request,
				filterHierarchyList, servicesDirectory);
		assertNotNull(replaceValuesResult);
	}

}
