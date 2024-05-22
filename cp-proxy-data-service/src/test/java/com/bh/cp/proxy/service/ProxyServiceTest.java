package com.bh.cp.proxy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.adapter.ServicesAdapter;
import com.bh.cp.proxy.adapter.impl.RestServicesAdapter;
import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.entity.ServicesDirectory;
import com.bh.cp.proxy.entity.ServicesDynamicParameters;
import com.bh.cp.proxy.exception.ProxyException;
import com.bh.cp.proxy.factory.ResponseHandlerFactory;
import com.bh.cp.proxy.factory.ServicesFactory;
import com.bh.cp.proxy.handler.ResponseHandler;
import com.bh.cp.proxy.helper.HeadersFormatHelper;
import com.bh.cp.proxy.helper.InputFormatHelper;
import com.bh.cp.proxy.helper.ReplaceValueHelper;
import com.bh.cp.proxy.repository.ServicesDirectoryRepository;

class ProxyServiceTest {

	@InjectMocks
	private ProxyService proxyService;
	@Mock
	MockHttpServletRequest httpServletRequest;
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
	private ServicesDirectoryRepository servicesDirectoryRepository;
	@Mock
	private ReplaceValueHelper replaceStaticValuesHelper;
	@Mock
	private ReplaceValueHelper replaceDynamicValuesHelper;
	@Mock
	private ServicesAdapter servicesAdapter;
	@Mock
	private ResponseHandler<?> responseHandler;
	@Mock
	private RestServicesAdapter restServicesAdapter;
	@Mock
	private AuditTrailAspect auditTrailAspect;

	private Map<String, Object> widgetsDataRequest;
	private ServicesDirectory servicesDirectoryDB;
	private ServicesDynamicParameters dynamicParameters;
	private com.bh.cp.proxy.pojo.ServicesDirectory servicesDirectory;
	private List<Map<String, Object>> filterHierarchyList;
	private Map<String, Object> filterhierarchyMap;
	private Map<String, Boolean> umsResponseMap;
	private Map<String, String> replaceStaticValues;
	private Set<ServicesDynamicParameters> dynamicParametersSet;
	private Map<String, String> replaceDynamicValues;
	private List<String> projectsList;

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
		servicesDirectoryDB.setHeaders("<PROJECT>");
		servicesDirectoryDB.setInputData("test");
		servicesDirectoryDB.setOutputHandler("com.bh.cp.proxy.handler.impl.CaseStatusResponseHandler");
		servicesDirectory = new com.bh.cp.proxy.pojo.ServicesDirectory();
		servicesDirectory.setInputData(servicesDirectoryDB.getInputData());
		servicesDirectory.setMethod(servicesDirectoryDB.getMethod());
		servicesDirectory.setUri(servicesDirectoryDB.getUri());
		servicesDirectory.setHeaders(servicesDirectoryDB.getHeaders());
		servicesDirectory.setOutputHandler(servicesDirectoryDB.getOutputHandler());

		dynamicParameters = new ServicesDynamicParameters();
		dynamicParameters.setId(12);
		dynamicParameters.setField(ProxyConstants.KEY_LINEUP_ID_TEXT);
		dynamicParameters.setInputData(
				"{\"filterOperand1\": {\"field\": \"issue.lineup_id\"},\"filterValues\": [<FILTER_VALUE>],\"operation\": \"eq\"}");
		dynamicParametersSet = new HashSet<>();
		dynamicParametersSet.add(dynamicParameters);
		replaceStaticValues = new HashMap<>();
		replaceStaticValues.put("<MACHINE_ID_CSV>", "MC_TEST");
		replaceStaticValues.put("<PROJECT>", "PR_TEST");
		replaceDynamicValues = new HashMap<>();
		replaceDynamicValues.put("<FILTERVALUE>", "LN_TEST1");
		projectsList = new ArrayList<>();
		projectsList.add("PR_TEST");
		filterHierarchyList = new ArrayList<>();
		filterhierarchyMap = new HashMap<>();
		filterhierarchyMap.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);
		filterHierarchyList.add(filterhierarchyMap);
		umsResponseMap = new HashMap<>();
		umsResponseMap.put(ProxyConstants.APPLICABILITY, true);
		umsResponseMap.put(ProxyConstants.ENABLED, true);
		umsResponseMap.put(ProxyConstants.HASACCESS, true);
		umsResponseMap.put(ProxyConstants.ACTIVESERVICESPERSONA, true);
		widgetsDataRequest.put(ProxyConstants.FILTEREDASSETHIERARCHY, filterHierarchyList);
		widgetsDataRequest.put(ProxyConstants.HTTPSERVLETREQUEST, httpServletRequest);
		widgetsDataRequest.put(ProxyConstants.ASSETSIDMAP, filterhierarchyMap);
		widgetsDataRequest.put(ProxyConstants.REPLACE_VALUES, replaceStaticValues);

		ReflectionTestUtils.setField(proxyService, "isServiceReponseLogsEnabled", true);
		ReflectionTestUtils.setField(proxyService, "fleetDataWidgetId", -1);
	}

	@SuppressWarnings("unchecked")
	@ParameterizedTest
	@CsvSource({"1","-1"})
	@DisplayName("TestExecute - Request Contains WidgetId with VID Not Null")
	void testExecuteWithWidgetIdAndVidNotNull(Integer widgetId) throws Exception {
		widgetsDataRequest.put(ProxyConstants.WIDGET_ID, widgetId);
		when(servicesDirectoryRepository.findByWidgetId((Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID)))
				.thenReturn(Optional.of(servicesDirectoryDB));
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(umsClientService.getWidgetAccess(httpServletRequest, (String) widgetsDataRequest.get(ProxyConstants.VID),
				(Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID))).thenReturn(umsResponseMap);
		when(assetHierarchyFilterService.validateVid(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID))).thenReturn(true);
		when(assetHierarchyFilterService.getAssetsMap(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID), true)).thenReturn(filterhierarchyMap);
		when(replaceStaticValuesHelper.getReplaceValues(widgetsDataRequest, filterHierarchyList, servicesDirectory))
				.thenReturn(replaceStaticValues);
		when(replaceDynamicValuesHelper.getReplaceValues(widgetsDataRequest, filterHierarchyList, servicesDirectory))
				.thenReturn(replaceStaticValues);
		when(headersFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("PR_TEST");
		when(inputFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("TEST");
		when(servicesFactory.getInstanceOf(any())).thenReturn(restServicesAdapter);
		when(restServicesAdapter.execute(any())).thenReturn(new JSONObject());
		when(responseHandlerFactory.getInstanceOf(any()))
				.thenReturn((ResponseHandler<Object>) responseHandler);
		when(responseHandlerFactory.getInstanceOf(any()).format(any(Object.class), any(Map.class)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND));
		JSONObject expectedResponse=(JSONObject)proxyService.execute(widgetsDataRequest, httpServletRequest);
		assertEquals(WidgetConstants.NODATAFOUND, expectedResponse.get(WidgetConstants.DATA));
		assertEquals(false,expectedResponse.get( ProxyConstants.SHOWGREYIMAGE));
		assertEquals(true,expectedResponse.get(ProxyConstants.SHOWLIVEDATA));
		assertEquals(false,expectedResponse.get(ProxyConstants.HIDEWIDGET));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("TestExecute - Request Contains WidgetId,VID Is Null And Response Is Not JSONObject")
	void testExecuteWithWidgetIdVidNull() throws Exception {
		HashMap<String, Object> response = new HashMap<>();
		response.put(ProxyConstants.LEVEL_PROJECTS, "PR_TEST");
		widgetsDataRequest.put(ProxyConstants.VID, null);
		when(servicesDirectoryRepository.findByWidgetId((Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID)))
				.thenReturn(Optional.of(servicesDirectoryDB));
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(umsClientService.getWidgetAccess(httpServletRequest, (String) widgetsDataRequest.get(ProxyConstants.VID),
				(Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID))).thenReturn(umsResponseMap);
		when(assetHierarchyFilterService.validateVid(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID))).thenReturn(true);
		when(assetHierarchyFilterService.getAssetsMap(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID), true)).thenReturn(filterhierarchyMap);
		when(replaceStaticValuesHelper.getReplaceValues(widgetsDataRequest, filterHierarchyList, servicesDirectory))
				.thenReturn(replaceStaticValues);
		when(replaceDynamicValuesHelper.getReplaceValues(widgetsDataRequest, filterHierarchyList, servicesDirectory))
				.thenReturn(replaceStaticValues);
		when(headersFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("PR_test");
		when(inputFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("test");
		when(servicesFactory.getInstanceOf(any())).thenReturn(restServicesAdapter);
		when(restServicesAdapter.execute(any())).thenReturn(new JSONObject());
		when(responseHandlerFactory.getInstanceOf(any())).thenReturn((ResponseHandler<Object>) responseHandler);
		when(responseHandlerFactory.getInstanceOf(any()).format(any(Object.class), any(Map.class)))
				.thenReturn(response);
		JSONObject expectedResponse = (JSONObject) proxyService.execute(widgetsDataRequest,
				httpServletRequest);
		assertEquals("PR_TEST", expectedResponse.getJSONObject(WidgetConstants.DATA).get(ProxyConstants.LEVEL_PROJECTS));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("TestExecute - Request Contains serviceId")
	void testExecuteWithServiceId() throws Exception {
		HashMap<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.CASE_CRITICALITY, WidgetConstants.HIGH);
		widgetsDataRequest.put(ProxyConstants.WIDGET_ID, null);
		widgetsDataRequest.put(ProxyConstants.SERVICE_ID, 16);
		widgetsDataRequest.put(ProxyConstants.REPLACE_VALUES, replaceDynamicValues);
		servicesDirectoryDB.setId((Integer) widgetsDataRequest.get(ProxyConstants.SERVICE_ID));
		servicesDirectoryDB.setInputData("{\"filterConditions\":[<DYNAMIC_PARAMETERS>]");
		dynamicParameters.setServicesDirectory(servicesDirectoryDB);
		servicesDirectoryDB.setDynamicParameters(dynamicParametersSet);
		when(servicesDirectoryRepository.findById((Integer) widgetsDataRequest.get(ProxyConstants.SERVICE_ID)))
				.thenReturn(Optional.of(servicesDirectoryDB));
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(umsClientService.getWidgetAccess(httpServletRequest, (String) widgetsDataRequest.get(ProxyConstants.VID),
				(Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID))).thenReturn(umsResponseMap);
		when(assetHierarchyFilterService.validateVid(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID))).thenReturn(true);
		when(assetHierarchyFilterService.getAssetsMap(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID), true)).thenReturn(filterhierarchyMap);
		when(replaceStaticValuesHelper.getReplaceValues(widgetsDataRequest, filterHierarchyList, servicesDirectory))
				.thenReturn(replaceStaticValues);
		when(replaceDynamicValuesHelper.getReplaceValues(widgetsDataRequest, filterHierarchyList, servicesDirectory))
				.thenReturn(replaceDynamicValues);
		when(headersFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("PR_TEST");
		when(inputFormatHelper.format(servicesDirectory, widgetsDataRequest)).thenReturn("TEST");
		when(servicesFactory.getInstanceOf(any())).thenReturn(restServicesAdapter);
		when(restServicesAdapter.execute(any())).thenReturn(new JSONObject());
		when(responseHandlerFactory.getInstanceOf(any())).thenReturn((ResponseHandler<Object>) responseHandler);
		when(responseHandlerFactory.getInstanceOf(any()).format(any(Object.class), any(Map.class)))
				.thenReturn(new JSONObject().put(WidgetConstants.DATA, response));
		JSONObject expectedResponse = (JSONObject) proxyService.execute(widgetsDataRequest, httpServletRequest);
		assertEquals(WidgetConstants.HIGH,
				expectedResponse.getJSONObject(WidgetConstants.DATA).get(WidgetConstants.CASE_CRITICALITY));
		assertEquals(servicesDirectoryDB.getId(), expectedResponse.getJSONObject("service_logs")
				.getJSONObject("proxy_payload").get(ProxyConstants.SERVICE_ID));
		assertEquals(false, expectedResponse.get(ProxyConstants.SHOWGREYIMAGE));
		assertEquals(true, expectedResponse.get(ProxyConstants.SHOWLIVEDATA));
		assertEquals(false, expectedResponse.get(ProxyConstants.HIDEWIDGET));
	}

	@Test
	@DisplayName("TestExecute - Access Denied Exception")
	void testExecuteAccessDeniedException() throws Exception {
		when(servicesDirectoryRepository.findByWidgetId((Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID)))
				.thenReturn(Optional.of(servicesDirectoryDB));
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(umsClientService.getWidgetAccess(httpServletRequest, (String) widgetsDataRequest.get(ProxyConstants.VID),
				(Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID))).thenReturn(umsResponseMap);
		when(assetHierarchyFilterService.validateVid(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID))).thenReturn(false);
		AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
			proxyService.execute(widgetsDataRequest, httpServletRequest);
		});
		assertEquals("Asset is not Accessible", exception.getMessage());
	}

	@Test
	@DisplayName("TestExecute - Proxy Exception")
	void testExecuteProxyException() throws Exception {
		when(servicesDirectoryRepository.findByWidgetId((Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID)))
				.thenReturn(Optional.empty());
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(umsClientService.getWidgetAccess(httpServletRequest, (String) widgetsDataRequest.get(ProxyConstants.VID),
				(Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID))).thenReturn(umsResponseMap);
		when(assetHierarchyFilterService.validateVid(filterHierarchyList,
				(String) widgetsDataRequest.get(ProxyConstants.VID))).thenReturn(false);
		ProxyException exception = assertThrows(ProxyException.class, () -> {
			proxyService.execute(widgetsDataRequest, httpServletRequest);
		});
		assertEquals("Could not find data for given input.", exception.getMessage());
	}
	@Test
	@DisplayName("TestExecute - Widget Is static")
	void testExecuteWithStaticWidget() throws Exception {
		servicesDirectoryDB.setUri(null);
		servicesDirectory.setUri(null);
		umsResponseMap.put(ProxyConstants.ACTIVESERVICESPERSONA, false);
		when(servicesDirectoryRepository.findByWidgetId((Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID)))
				.thenReturn(Optional.of(servicesDirectoryDB));
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(umsClientService.getWidgetAccess(httpServletRequest, (String) widgetsDataRequest.get(ProxyConstants.VID),
				(Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID))).thenReturn(umsResponseMap);
		JSONObject expectedResponse = (JSONObject) proxyService.execute(widgetsDataRequest, httpServletRequest);
		assertEquals(false, expectedResponse.get(ProxyConstants.SHOWGREYIMAGE));
		assertEquals(true, expectedResponse.get(ProxyConstants.SHOWLIVEDATA));
		assertEquals(false, expectedResponse.get(ProxyConstants.HIDEWIDGET));
	}

	@ParameterizedTest
	@CsvSource({ "false,true", "false,false" })
	@DisplayName("TestExecute - Widget Is Not static")
	void testExecuteWithNonStaticWidget(Boolean enabled, Boolean appliability) throws Exception {
		umsResponseMap.put(ProxyConstants.ENABLED, enabled);
		umsResponseMap.put(ProxyConstants.APPLICABILITY, appliability);
		when(servicesDirectoryRepository.findByWidgetId((Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID)))
				.thenReturn(Optional.of(servicesDirectoryDB));
		when(umsClientService.getUserAssetHierarchy(httpServletRequest)).thenReturn(filterHierarchyList);
		when(umsClientService.getWidgetAccess(httpServletRequest, (String) widgetsDataRequest.get(ProxyConstants.VID),
				(Integer) widgetsDataRequest.get(ProxyConstants.WIDGET_ID))).thenReturn(umsResponseMap);
		JSONObject expectedResponse = (JSONObject) proxyService.execute(widgetsDataRequest, httpServletRequest);
		assertEquals(false, expectedResponse.get(ProxyConstants.SHOWGREYIMAGE));
		assertEquals(false, expectedResponse.get(ProxyConstants.SHOWLIVEDATA));
		assertEquals(true, expectedResponse.get(ProxyConstants.HIDEWIDGET));
	}
}
