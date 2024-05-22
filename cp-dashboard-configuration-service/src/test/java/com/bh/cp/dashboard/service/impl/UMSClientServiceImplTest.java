package com.bh.cp.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.dashboard.constants.JSONUtilConstants;
import com.bh.cp.dashboard.service.RestClientWrapperService;
import com.fasterxml.jackson.databind.ObjectMapper;

class UMSClientServiceImplTest {

	@InjectMocks
	private UMSClientServiceImpl umsClientService;

	@Mock
	private RestClientWrapperService restClientWrapperService;

	@Mock
	private ObjectMapper mapper;

	private MockHttpServletRequest mockHttpServletRequest;

	private List<Map<String, Object>> assetHierarchy;
	private List<String> privileagesList;

	private class TestConstants {

		private static final String TEST1 = "TEST1";
		private static final String PL_TEST1 = "PL_TEST1";
		private static final String MC_TEST1 = "MC_TEST1";
		private static final String PR_TEST1 = "PR_TEST1";
		private static final String TR_TEST1 = "TR_TEST1";
		private static final String LN_TEST1 = "LN_TEST1";
		private static final String TESTPRFIELDKEY1 = "testPRFieldKey1";
		private static final String TESTPLFIELDKEY1 = "testPLFieldKey1";
		private static final String TESTRFIELDKEY1 = "testTRFieldKey1";
		private static final String TESTLNFIELDKEY1 = "testLNFieldKey1";
		private static final String TESTMCFIELDKEY1 = "testMCFieldKey1";
		private static final String PR_TEST1VALUE1 = "PR_TEST1Value1";
		private static final String PL_TEST1VALUE1 = "PL_TEST1Value1";
		private static final String TR_TEST1VALUE1 = "TR_TEST1Value1";
		private static final String LN_TEST1VALUE1 = "LN_TEST1Value1";
		private static final String MC_TEST1VALUE1 = "MC_TEST1Value1";
	}

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		privileagesList = new ArrayList<>();
		privileagesList.add("policy1");
		privileagesList.add("policy2");
		List<Map<String, Object>> projects1 = new ArrayList<>();
		Map<String, Object> data = new HashMap<>();
		data.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		data.put(JSONUtilConstants.DATA, projects1);
		List<Map<String, Object>> plants1 = new ArrayList<>();
		Map<String, Object> data1 = new HashMap<>();
		data1.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PLANTS);
		data1.put(JSONUtilConstants.DATA, plants1);
		List<Map<String, Object>> trains1 = new ArrayList<>();
		Map<String, Object> data2 = new HashMap<>();
		data2.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_TRAINS);
		data2.put(JSONUtilConstants.DATA, trains1);
		List<Map<String, Object>> lineups1 = new ArrayList<>();
		Map<String, Object> data3 = new HashMap<>();
		data3.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_LINEUPS);
		data3.put(JSONUtilConstants.DATA, lineups1);
		List<Map<String, Object>> machines1 = new ArrayList<>();
		Map<String, Object> data4 = new HashMap<>();
		data4.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_MACHINES);
		data4.put(JSONUtilConstants.DATA, machines1);

		Map<String, Object> additionalFields = new HashMap<>();
		additionalFields.put(JSONUtilConstants.EQUIPMENTCODES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.TECHNOLOGYCODES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.ENABLEDSERVICES, List.of("abc", "cde"));

		Map<String, Object> machine1 = new HashMap<>();
		machine1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		machine1.put(JSONUtilConstants.VID, TestConstants.MC_TEST1);
		machine1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.MC_TEST1,
				TestConstants.TESTMCFIELDKEY1, TestConstants.MC_TEST1VALUE1));
		machine1.putAll(additionalFields);
		machines1.add(machine1);

		Map<String, Object> lineup1 = new HashMap<>();
		lineup1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		lineup1.put(JSONUtilConstants.VID, TestConstants.LN_TEST1);
		lineup1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.LN_TEST1,
				TestConstants.TESTLNFIELDKEY1, TestConstants.LN_TEST1VALUE1));
		lineup1.put(JSONUtilConstants.CHILDREN, data4);
		lineup1.putAll(additionalFields);
		lineups1.add(lineup1);

		Map<String, Object> train1 = new HashMap<>();
		train1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		train1.put(JSONUtilConstants.VID, TestConstants.TR_TEST1);
		train1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.TR_TEST1,
				TestConstants.TESTRFIELDKEY1, TestConstants.TR_TEST1VALUE1));
		train1.put(JSONUtilConstants.CHILDREN, data3);
		train1.putAll(additionalFields);
		trains1.add(train1);

		Map<String, Object> plant1 = new HashMap<>();
		plant1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		plant1.put(JSONUtilConstants.VID, TestConstants.PL_TEST1);
		plant1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PL_TEST1,
				TestConstants.TESTPLFIELDKEY1, TestConstants.PL_TEST1VALUE1));
		plant1.put(JSONUtilConstants.CHILDREN, data2);
		plant1.putAll(additionalFields);
		plants1.add(plant1);

		Map<String, Object> project1 = new HashMap<>();
		project1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		project1.put(JSONUtilConstants.VID, TestConstants.PR_TEST1);
		project1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST1,
				TestConstants.TESTPRFIELDKEY1, TestConstants.PR_TEST1VALUE1));
		project1.put(JSONUtilConstants.CHILDREN, data1);
		project1.putAll(additionalFields);
		projects1.add(project1);

		assetHierarchy = new ArrayList<>();
		assetHierarchy.add(data);
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer abc");
		ReflectionTestUtils.setField(umsClientService, "userAssetHierarchyUri", "/v1/me/hierarchy");
		ReflectionTestUtils.setField(umsClientService, "userPrivilegesUri", "/v1/me/privileges");
		ReflectionTestUtils.setField(umsClientService, "mapper", mapper);
	}

	@Test
	@DisplayName("GetUserAssetHierarchy")
	void testGetUserAssetHierarchy() throws Exception {
		String response = "[{\"level\":\"projects\",\"data\":[{\"id\":\"TEST1\",\"vid\":\"PR_TEST1\",\"fields\":{},\"children\":{\"level\":\"plants\",\"data\":[{\"id\":\"TEST1\",\"vid\":\"PL_TEST1\",\"fields\":{\"equipmentCode\":\"ab\",\"technologyCodeOg\":\"bc\"},\"enabledServices\":[\"CBM2567\"]}]}}]}]";
		ResponseEntity<String> assetHierarchyResponse = new ResponseEntity<String>(response, HttpStatus.OK);
		when(restClientWrapperService.getResponseFromUrl(any(MockHttpServletRequest.class), anyString()))
				.thenReturn(assetHierarchyResponse);
		when(mapper.readValue(assetHierarchyResponse.getBody(), List.class)).thenReturn(assetHierarchy);
		List<Map<String, Object>> assetHierarchy = umsClientService.getUserAssetHierarchy(mockHttpServletRequest);
		assertNotNull(assetHierarchy);
		assertEquals(1, assetHierarchy.size());
	}

	@Test
	@DisplayName("GetUserPrivileges")
	void testGetUserPrivileges() throws Exception {
		String policyJson = "[\"policy1\",\"policy2\"]";
		ResponseEntity<String> privileageResponse = new ResponseEntity<String>(policyJson, HttpStatus.OK);
		when(restClientWrapperService.getResponseFromUrl(any(MockHttpServletRequest.class), anyString()))
				.thenReturn(privileageResponse);
		when(mapper.readValue(privileageResponse.getBody(), List.class)).thenReturn(privileagesList);
		List<String> widgetSubcriptionResponse = umsClientService.getUserPrivileges(mockHttpServletRequest);
		assertEquals("policy1", widgetSubcriptionResponse.get(0));
		assertEquals("policy2", widgetSubcriptionResponse.get(1));
	}
}
