package com.bh.cp.proxy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.adapter.ServicesAdapter;
import com.bh.cp.proxy.adapter.impl.RestServicesAdapter;
import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.entity.ServicesDirectory;
import com.bh.cp.proxy.factory.ResponseHandlerFactory;
import com.bh.cp.proxy.factory.ServicesFactory;
import com.bh.cp.proxy.handler.ResponseHandler;
import com.bh.cp.proxy.helper.HeadersFormatHelper;
import com.bh.cp.proxy.helper.InputFormatHelper;

class FleetServiceTest {

	@InjectMocks
	private FleetService fleetService;

	@Mock
	private MockHttpServletRequest httpServletRequest;
	@Mock
	private ProxyService proxyService;
	@Mock
	private UMSClientService umsClientService;
	@Mock
	private AssetHierarchyFilterService assetHierarchyFilterService;
	@Mock
	private HeadersFormatHelper headersFormatHelper;
	@Mock
	private InputFormatHelper inputFormatHelper;
	@Mock
	private ServicesFactory servicesFactory;
	@Mock
	private ResponseHandlerFactory responseHandlerFactory;
	@Mock
	private ServicesAdapter servicesAdapter;
	@Mock
	private ResponseHandler<?> responseHandler;
	@Mock
	private RestServicesAdapter restServicesAdapter;

	private Map<String, Object> widgetsDataRequest;
	private ServicesDirectory servicesDirectoryDB;
	private com.bh.cp.proxy.pojo.ServicesDirectory servicesDirectory;
	private List<Map<String, Object>> filterHierarchyList;
	private Map<String, String> replaceStaticValues;
	private List<String> projectsList;
	private Map<String, Object> assetsMap;
	private List<String> lineupsList;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		widgetsDataRequest = new HashMap<>();
		widgetsDataRequest.put(ProxyConstants.WIDGET_ID, 1);
		widgetsDataRequest.put(ProxyConstants.DATE_RANGE, WidgetConstants.DATERANGE3);
		widgetsDataRequest.put(ProxyConstants.VID, "PR_TEST");
		widgetsDataRequest.put(ProxyConstants.LEVEL, ProxyConstants.LEVEL_PROJECTS);
		servicesDirectoryDB = new ServicesDirectory();
		servicesDirectoryDB.setId(1);
		servicesDirectoryDB.setInputData("filter");
		servicesDirectoryDB.setCommunicationFormat("test");
		servicesDirectoryDB.setServicetype("REST");
		servicesDirectoryDB.setUri("http://test.com");
		servicesDirectoryDB.setMethod("GET");
		servicesDirectoryDB.setInputData(ProxyConstants.KEY_LINEUP_IDS_CSV);
		servicesDirectoryDB.setOutputHandler("com.bh.cp.proxy.handler.impl.FleetResponseHandler");
		servicesDirectory = new com.bh.cp.proxy.pojo.ServicesDirectory();
		servicesDirectory.setInputData(servicesDirectoryDB.getInputData());
		servicesDirectory.setMethod(servicesDirectoryDB.getMethod());
		servicesDirectory.setUri(servicesDirectoryDB.getUri());
		servicesDirectory.setHeaders(servicesDirectoryDB.getHeaders());
		servicesDirectory.setOutputHandler(servicesDirectoryDB.getOutputHandler());

		projectsList = new ArrayList<>();
		projectsList.add("PR_TEST");
		lineupsList = new ArrayList<>();
		lineupsList.add("LN_TEST1");
		lineupsList.add("LN_TEST2");
		assetsMap = new HashMap<>();
		assetsMap.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);
		assetsMap.put(JSONUtilConstants.LEVEL_LINEUPS, lineupsList);
		assetsMap.put(JSONUtilConstants.MATCHFOUND, true);
		assetsMap.put(JSONUtilConstants.NEXTLEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		replaceStaticValues = new HashMap<>();
		replaceStaticValues.put(ProxyConstants.KEY_ASSET_ID_TEXT, "PR_TEST");
		widgetsDataRequest.put(ProxyConstants.FILTEREDASSETHIERARCHY, filterHierarchyList);
		widgetsDataRequest.put(ProxyConstants.HTTPSERVLETREQUEST, httpServletRequest);
		widgetsDataRequest.put(ProxyConstants.REPLACE_VALUES, replaceStaticValues);

		ReflectionTestUtils.setField(proxyService, "fleetDataWidgetId", -1);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("TestExecute - Request Contains FleetWidgetId And MatchFound is TRUE")
	void testExecuteWithWidgetId() throws Exception {
		JSONObject data = new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(assetHierarchyFilterService.getAssetsMap(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID), true)).thenReturn(assetsMap);
		when(proxyService.getServicesDirectory(any(Map.class))).thenReturn(servicesDirectory);
		when(headersFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("PR_TEST");
		when(inputFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("TEST");
		when(servicesFactory.getInstanceOf(any())).thenReturn(restServicesAdapter);
		when(restServicesAdapter.execute(any())).thenReturn(new JSONObject());
		when(responseHandlerFactory.getInstanceOf(any())).thenReturn((ResponseHandler<Object>) responseHandler);
		when(responseHandlerFactory.getInstanceOf(any()).format(any(Object.class), any(Map.class)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, data));
		JSONObject expectedResponse = (JSONObject) fleetService.execute(widgetsDataRequest, httpServletRequest);
		assertEquals(data, expectedResponse.getJSONArray(WidgetConstants.DATA).get(0));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("TestExecute - Request Contains FleetWidgetId And Response Is Not JSONObject")
	void testExecuteWithResponseAsNotJSONObject() throws Exception {
		Map<String,Object> dataMap=new HashMap<>();
		dataMap.put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(assetHierarchyFilterService.getAssetsMap(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID), true)).thenReturn(assetsMap);
		when(proxyService.getServicesDirectory(any(Map.class))).thenReturn(servicesDirectory);
		when(headersFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("PR_TEST");
		when(inputFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("TEST");
		when(servicesFactory.getInstanceOf(any())).thenReturn(restServicesAdapter);
		when(restServicesAdapter.execute(any())).thenReturn(new JSONObject());
		when(responseHandlerFactory.getInstanceOf(any())).thenReturn((ResponseHandler<Object>) responseHandler);
		when(responseHandlerFactory.getInstanceOf(any()).format(any(Object.class), any(Map.class)))
				.thenReturn(dataMap);
		JSONObject expectedResponse = (JSONObject) fleetService.execute(widgetsDataRequest, httpServletRequest);
		assertEquals(0, expectedResponse.getJSONArray(WidgetConstants.DATA).length());
	}
	
	@Test
	@DisplayName("TestExecute - Access Denied Exception(Match Found Is False)")
	void testExecuteAccessDeniedException() throws Exception {
		assetsMap.put(JSONUtilConstants.MATCHFOUND, false);
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(assetHierarchyFilterService.getAssetsMap(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID),true)).thenReturn(assetsMap);
		AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
			fleetService.execute(widgetsDataRequest, httpServletRequest);
		});
		assertEquals("Asset is not Accessible", exception.getMessage());
	}

}
