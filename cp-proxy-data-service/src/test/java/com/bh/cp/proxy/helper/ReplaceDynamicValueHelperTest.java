//package com.bh.cp.proxy.helper;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
//import com.bh.cp.proxy.asset.service.UMSClientService;
//import com.bh.cp.proxy.constants.ProxyConstants;
//import com.bh.cp.proxy.entity.ServicesDirectory;
//import com.bh.cp.proxy.pojo.ServicesDynamicParameters;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//
//class ReplaceDynamicValueHelperTest {
//	
//	@InjectMocks
//	private ReplaceDynamicValueHelper replaceDynamicValueHelper;
//	
//	HttpServletRequest httpServletRequest;
//	@Mock
//	private UMSClientService umsClientService;
//	@Mock
//	private AssetHierarchyFilterService assetHierarchyFilterService;
//	Map<String, Object> request = new HashMap<>();
//	ServicesDirectory servicesDirectoryDB;
//	com.bh.cp.proxy.pojo.ServicesDirectory servicesDirectory;
//	List<Map<String, Object>> filterHierarchyList;
//	Map<String, Object> filterhierarchyMap;
//	Map<String, Boolean> umsResponseMap;
//	Map<String, String> replaceStaticValues;
//	Map<String, Object> assetsMap;
//	Map<String, Map<String, Set<String>>> fieldsMap;
//	Map<String, Set<String>> vidFieldMap;
//	
//	
//	@Test
//	@DisplayName("Parse the Response - Giving response with Proper Output  for CommentList Edit")
//	void replaceDynamic() throws Exception {
//
//		Map<String, Object> request1  = new HashMap<>();
//		request1.put("<TO_DT_UTC>", "05-03-2024 07:04:27");
//		request1.put("<TO_DT_UTC_IN_MS>", "1709596800000");
//		request1.put("<FROM_DT_UTC>", "05-03-2024 07:04:27");
//		request1.put("<ASSET_LEVEL>", "");
//		List<String> lineUp = new ArrayList<>();
//		lineUp.add("L0672");
//		lineUp.add("L0673");
//		lineUp.add("L0674");
//		request1.put("<LINEUP_IDS_TEXT>", lineUp);
//		request1.put("<FROM_DT_UTC_IN_MS>", "1701734400000");
//		request1.put("<ASSET_LEVEL_SINGULAR>", "05-03-2024 07:04:27");
//		List<String> lineUpCsv = new ArrayList<>();
//		lineUpCsv.add("L0672");
//		lineUpCsv.add("L0673");
//		lineUpCsv.add("L0674");
//		request1.put("<LINEUP_IDS_CSV>", lineUpCsv);
//		request1.put("<FROM_DT_UTC_IN_MS>", "1701734400000");
//		request1.put("<ASSET_LEVEL_SINGULAR>", "");
//		request1.put("<PROJECT_ID_TEXT>", "NOBLE");
//		request1.put("<TRAIN_ID_TEXT>", "1701734400000");
//		request1.put("<FROM_DT_YYYY_MM_DD_HHMMSS_UTC>", "2023-12-05 07:04:27");
//		request1.put("<TO_DT_DD_MM_YYYY_HHMMSS_UTC>", "");
//		request.put("replace_values", request1);
//		request.put("attachment","Y");
//		
//		
//		servicesDirectoryDB = new ServicesDirectory();
//		servicesDirectoryDB.setId(1);
//		servicesDirectoryDB.setInputData("filter");
//		servicesDirectoryDB.setCommunicationFormat("test");
//		servicesDirectoryDB.setServicetype("REST");
//		servicesDirectoryDB.setUri("http://test.com");
//		servicesDirectoryDB.setMethod("GET");
//		servicesDirectoryDB.setHeaders("<PROJECT>");
//		servicesDirectoryDB.setInputData("test");
//		servicesDirectoryDB.setOutputHandler("com.bh.cp.proxy.handler.impl.CaseStatusResponseHandler");
//		ServicesDynamicParameters servicesDynamicParameters = new ServicesDynamicParameters();
//		servicesDynamicParameters.setField("attachment");
//		servicesDynamicParameters.setId(12);
//		servicesDynamicParameters.setInputData("attachment");
//		Set<ServicesDynamicParameters> servicesDynamicParameters2 = new HashSet<>();
//		servicesDynamicParameters2.add(servicesDynamicParameters);
//		servicesDirectory = new com.bh.cp.proxy.pojo.ServicesDirectory();
//		servicesDirectory.setInputData(servicesDirectoryDB.getInputData());
//		servicesDirectory.setMethod(servicesDirectoryDB.getMethod());
//		servicesDirectory.setUri(servicesDirectoryDB.getUri());
//		servicesDirectory.setHeaders(servicesDirectoryDB.getHeaders());
//		servicesDirectory.setOutputHandler(servicesDirectoryDB.getOutputHandler());
//		servicesDirectory.setDynamicParameters(servicesDynamicParameters2);
//		Map<String,Object> appendInputParam = new HashMap<>();
//		appendInputParam.put(ProxyConstants.INPUT_PARAM, ProxyConstants.PATH_PARAM);
//		replaceDynamicValueHelper = new ReplaceDynamicValueHelper(assetHierarchyFilterService);
//		when(replaceDynamicValueHelper.validateInputAndReturn("attachment","[Y]")).thenReturn("attachment");
//		replaceDynamicValueHelper.appendInputParams("query", "path","", new StringBuilder(), "", appendInputParam);
//		Map<String, String> replaceValuesResult = replaceDynamicValueHelper.getReplaceValues(request,
//				filterHierarchyList, servicesDirectory);
//		assertNotNull(replaceValuesResult);
//	}
//}
