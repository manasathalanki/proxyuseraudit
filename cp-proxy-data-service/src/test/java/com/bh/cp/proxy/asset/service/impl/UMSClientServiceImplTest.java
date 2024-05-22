package com.bh.cp.proxy.asset.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.asset.service.RestClientWrapperService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.dto.request.WidgetAccessRequestDTO;
import com.bh.cp.proxy.dto.request.WidgetApplicableRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

class UMSClientServiceImplTest {

	@InjectMocks
	private UMSClientServiceImpl clientServiceImpl;

	@Mock
	private RestClientWrapperService restClientWrapperService;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private AuditTrailAspect auditTrailAspect;
	private MockHttpServletRequest mockHttpServletRequest;
	private List<Map<String, Object>> assetHierarchy;
	private List<String> privileagesList;
	private Map<String, Object> userDetails;
	private List<Map<String, Object>> userDetailsList;
	private Map<String, Object> oktaUserMap;
	private Map<String, Object> profileMap;
	String json;

	private class TestConstants {

		private static final String TEST1 = "TEST1";
		private static final String TEST2 = "TEST2";
		private static final String TEST3 = "TEST3";
		private static final String PL_TEST1 = "PL_TEST1";
		private static final String PL_TEST2 = "PL_TEST2";
		private static final String PR_TEST1 = "PR_TEST1";
		private static final String PR_TEST2 = "PR_TEST2";
		private static final String PR_TEST3 = "PR_TEST3";
		private static final String TESTPRFIELDKEY1 = "testPRFieldKey1";
		private static final String TESTPLFIELDKEY1 = "testPLFieldKey1";
		private static final String PL_TEST1VALUE1 = "PL_TEST1Value1";
		private static final String PL_TEST2VALUE1 = "PL_TEST1Value1";
		private static final String PR_TEST1VALUE1 = "PR_TEST1Value1";
		private static final String PR_TEST2VALUE1 = "PR_TEST2Value1";
		private static final String PR_TEST3VALUE1 = "PR_TEST2Value1";
		private static final String ID = "id";
		private static final String TITLE = "title";
		private static final String NAME = "name";
		private static final String SURNAME = "surName";
		private static final String EMAIL = "email";
		private static final String ENABLED = "enabled";
		private static final String USERNAME = "userName";
		public static final String STATUS = "status";
		public static final String PROFILE = "profile";
		public static final String AUTH = "auth";
	}

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		List<Map<String, Object>> projects1 = new ArrayList<>();
		Map<String, Object> data = new HashMap<>();
		data.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		data.put(JSONUtilConstants.DATA, projects1);
		List<Map<String, Object>> plants1 = new ArrayList<>();
		Map<String, Object> data1 = new HashMap<>();
		data1.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PLANTS);
		data1.put(JSONUtilConstants.DATA, plants1);

		Map<String, Object> additionalFields = new HashMap<>();
		additionalFields.put(JSONUtilConstants.EQUIPMENTCODES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.TECHNOLOGYCODES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.ENABLEDSERVICES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.GIBSERIALNOS, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.SERIALNOS, List.of("abc", "cde"));

		Map<String, Object> plant1 = new HashMap<>();
		plant1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		plant1.put(JSONUtilConstants.VID, TestConstants.PL_TEST1);
		plant1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PL_TEST1,
				TestConstants.TESTPLFIELDKEY1, TestConstants.PL_TEST1VALUE1));
		plant1.putAll(additionalFields);
		plants1.add(plant1);

		Map<String, Object> plant2 = new HashMap<>();
		plant2.put(JSONUtilConstants.ID, TestConstants.TEST2);
		plant2.put(JSONUtilConstants.VID, TestConstants.PL_TEST2);
		plant2.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PL_TEST2,
				TestConstants.TESTPLFIELDKEY1, TestConstants.PL_TEST2VALUE1));
		plant2.putAll(additionalFields);
		plants1.add(plant2);

		Map<String, Object> project1 = new HashMap<>();
		project1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		project1.put(JSONUtilConstants.VID, TestConstants.PR_TEST1);
		project1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST1,
				TestConstants.TESTPRFIELDKEY1, TestConstants.PR_TEST1VALUE1));
		project1.put(JSONUtilConstants.CHILDREN, data1);
		project1.putAll(additionalFields);
		projects1.add(project1);

		Map<String, Object> project2 = new HashMap<>();
		project2.put(JSONUtilConstants.ID, TestConstants.TEST2);
		project2.put(JSONUtilConstants.VID, TestConstants.PR_TEST2);
		project2.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST2,
				TestConstants.TESTPRFIELDKEY1, TestConstants.PR_TEST2VALUE1));
		project2.putAll(additionalFields);
		projects1.add(project2);

		Map<String, Object> project3 = new HashMap<>();
		project3.put(JSONUtilConstants.ID, TestConstants.TEST3);
		project3.put(JSONUtilConstants.VID, TestConstants.PR_TEST3);
		project3.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST3,
				TestConstants.TESTPRFIELDKEY1, TestConstants.PR_TEST3VALUE1));
		project3.putAll(additionalFields);
		projects1.add(project3);

		assetHierarchy = new ArrayList<>();
		assetHierarchy.add(data);

		privileagesList = new ArrayList<>();
		privileagesList.add("policy1");
		privileagesList.add("policy2");

		userDetails = new HashMap<>();
		userDetails.put(TestConstants.ID, "c8d");
		userDetails.put(TestConstants.NAME, "test");
		userDetails.put(TestConstants.EMAIL, "test@bakerhughes.com");
		userDetails.put(TestConstants.USERNAME, "tes_user");
		userDetails.put(TestConstants.SURNAME, "test");
		userDetails.put(TestConstants.ENABLED, "Y");
		userDetails.put(TestConstants.TITLE, "EXTERNAL");
		userDetailsList = new ArrayList<>();
		userDetailsList.add(userDetails);
		profileMap = new HashMap<>();
		profileMap.put("firstName", "test");
		profileMap.put("lastName", "user");
		profileMap.put("login", "test.user@bakerhughes.com");
		profileMap.put("email", "test.user@bakerhughes.com");
		profileMap.put("samAccountName", "tesuse");
		oktaUserMap = new HashMap<>();
		oktaUserMap.put(TestConstants.ID, "00y");
		oktaUserMap.put(TestConstants.STATUS, "ACTIVE");
		oktaUserMap.put(TestConstants.PROFILE, profileMap);
		oktaUserMap.put(TestConstants.AUTH, userDetails);
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer abc");
		ReflectionTestUtils.setField(clientServiceImpl, "userAssetHierarchyUri", "v1/assets/hierarchy");
		ReflectionTestUtils.setField(clientServiceImpl, "widgetAccessCheckUri", "v1/assets/widgetAccess");
		ReflectionTestUtils.setField(clientServiceImpl, "widgetApplicableMachinesUri", "v1/assets/applicableMachines");
		ReflectionTestUtils.setField(clientServiceImpl, "userPrivilegesUri", "v1/assets/privileges");
		ReflectionTestUtils.setField(clientServiceImpl, "userDetailsUri", "v1/assets/userDetails");
		ReflectionTestUtils.setField(clientServiceImpl, "allUserDetailsUri", "v1/assets/allUserDetails");
		ReflectionTestUtils.setField(clientServiceImpl, "oktaUserDetailsUri", "v1/assets/oktaUserDeatils");
		ReflectionTestUtils.setField(clientServiceImpl, "widgetAdvanceServicesAccessCheckUri", "v1/assets/access");
	}

	@Test
	@DisplayName("GetUserAssetHierarchy")
	void testGetUserAssetHierarchy() throws Exception {
		ResponseEntity<String> response = ResponseEntity.ok().body("[{\"level\": \"projects\"}]");
		when(restClientWrapperService.getResponseFromUrl(mockHttpServletRequest, "v1/assets/hierarchy"))
				.thenReturn(response);
		when(mapper.readValue(response.getBody(), List.class)).thenReturn(assetHierarchy);
		List<Map<String, Object>> actualResponse = clientServiceImpl.getUserAssetHierarchy(mockHttpServletRequest);
		assertEquals(1, actualResponse.size());
	}

	@Test
	@DisplayName("GetUserPrivileges")
	void testGetUserPrivileges() throws Exception {
		json = "[\"policy1\",\"policy2\"]";
		ResponseEntity<String> privileageResponse = new ResponseEntity<String>(json, HttpStatus.OK);
		when(restClientWrapperService.getResponseFromUrl(any(MockHttpServletRequest.class), anyString()))
				.thenReturn(privileageResponse);
		when(mapper.readValue(privileageResponse.getBody(), List.class)).thenReturn(privileagesList);
		List<String> widgetSubcriptionResponse = clientServiceImpl.getUserPrivileges(mockHttpServletRequest);
		assertEquals("policy1", widgetSubcriptionResponse.get(0));
		assertEquals("policy2", widgetSubcriptionResponse.get(1));
	}

	@Test
	@DisplayName("GetUserDetails")
	void testGetUserDetails() throws Exception {
		json = "{\"id\":\"c8d\",\"name\":\"test\",\"email\":\"test@bakerhughes.com\",\"userName\":\"tes_user\",\"surName\":\"test\",\"enabled\":\"Y\",\"title\":\"EXTERNAL\"}";
		ResponseEntity<String> userDetailsResponse = new ResponseEntity<String>(json, HttpStatus.OK);
		when(restClientWrapperService.getResponseFromUrl(any(MockHttpServletRequest.class), anyString()))
				.thenReturn(userDetailsResponse);
		when(mapper.readValue(userDetailsResponse.getBody(), Map.class)).thenReturn(userDetails);
		Map<String, Object> actualResponse = clientServiceImpl.getUserDetails(mockHttpServletRequest);
		assertEquals(userDetails.get(TestConstants.ID), actualResponse.get(TestConstants.ID));
		assertEquals(userDetails.get(TestConstants.TITLE), actualResponse.get(TestConstants.TITLE));
	}

	@Test
	@DisplayName("GetAllUserDetails")
	void testGetAllUserDetails() throws Exception {
		json = "[{\"id\":\"c8d\",\"name\":\"test\",\"email\":\"test@bakerhughes.com\",\"userName\":\"tes_user\",\"surName\":\"test\",\"enabled\":\"Y\",\"title\":\"EXTERNAL\"}]";
		ResponseEntity<String> userDetailsResponse = new ResponseEntity<String>(json, HttpStatus.OK);
		when(restClientWrapperService.getResponseFromUrl(any(MockHttpServletRequest.class), anyString()))
				.thenReturn(userDetailsResponse);
		when(mapper.readValue(userDetailsResponse.getBody(), List.class)).thenReturn(userDetailsList);
		List<Map<String, Object>> actualResponse = clientServiceImpl.getAllUserDetails(mockHttpServletRequest);
		assertEquals(1, actualResponse.size());
		assertEquals(userDetails.get(TestConstants.ID), actualResponse.get(0).get(TestConstants.ID));
		assertEquals(userDetails.get(TestConstants.TITLE), actualResponse.get(0).get(TestConstants.TITLE));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("GetOktaUserDetails")
	void testGetOktaUserDetails() throws Exception {
		userDetailsList = new ArrayList<>();
		userDetailsList.add(oktaUserMap);
		json = "[{\"id\":\"00u\",\"status\":\"ACTIVE\",\"profile\":{\"firstName\":\"test\",\"lastName\":\"user\",\"login\":\"test.user@bakerhughes.com\",\"email\":\"test.user@bakerhughes.com\",\"samAccountName\":\"tesuse\",\"uid\":null},\"auth\":{\"id\":\"c8d\",\"name\":\"test\",\"email\":\"test.user@bakerhughes.com\",\"userName\":\"test_user\",\"surName\":\"test\",\"enabled\":\"Y\",\"title\":\"EXTERNAL\",\"status\":\"ACTIVE\"}}]";
		ResponseEntity<String> userDetailsResponse = new ResponseEntity<String>(json, HttpStatus.OK);
		when(restClientWrapperService.postBodyToUrl(any(MockHttpServletRequest.class), anyString(), anyString()))
				.thenReturn(userDetailsResponse);
		when(mapper.readValue(userDetailsResponse.getBody(), List.class)).thenReturn(userDetailsList);
		List<Map<String, Object>> actualResponse = clientServiceImpl.getOktaUserDetails(mockHttpServletRequest,
				new JSONObject());
		assertEquals(1, actualResponse.size());
		Map<String, Object> profile = (Map<String, Object>) actualResponse.get(0).get(TestConstants.PROFILE);
		Map<String, Object> auth = (Map<String, Object>) actualResponse.get(0).get(TestConstants.AUTH);
		assertEquals(oktaUserMap.get(TestConstants.ID), actualResponse.get(0).get(TestConstants.ID));
		assertEquals(oktaUserMap.get(TestConstants.STATUS), actualResponse.get(0).get(TestConstants.STATUS));
		assertEquals(profileMap.get("firstName"), profile.get("firstName"));
		assertEquals(profileMap.get("lastName"), profile.get("lastName"));
		assertEquals(profileMap.get("login"), profile.get("login"));
		assertEquals(profileMap.get("samAccountName"), profile.get("samAccountName"));
		assertEquals(userDetails.get(TestConstants.ID), auth.get(TestConstants.ID));
		assertEquals(userDetails.get(TestConstants.TITLE), auth.get(TestConstants.TITLE));
	}

	@Test
	@DisplayName("GetValidateWidgetAccess")
	void testGetValidateWidgetAccess() throws Exception {
		Map<String, Object> accessMap = new HashMap<>();
		accessMap.put(ProxyConstants.ENABLED, true);
		accessMap.put(ProxyConstants.APPLICABILITY, true);
		accessMap.put(ProxyConstants.ACTIVESERVICESPERSONA, true);
		accessMap.put(ProxyConstants.HASACCESS, true);
		json = "{\"applicability\":true,\"enabled\":true,\"hasAccess\":true,\"activeServicesPersona\":true}";
		ResponseEntity<String> userDetailsResponse = new ResponseEntity<String>(json, HttpStatus.OK);
		when(mapper.writeValueAsString(any(WidgetAccessRequestDTO.class)))
				.thenReturn("{\"vid\":\"PR_TEST1\",\"widgetId\":12}");
		when(restClientWrapperService.postBodyToUrl(any(MockHttpServletRequest.class), anyString(), anyString()))
				.thenReturn(userDetailsResponse);
		when(mapper.readValue(userDetailsResponse.getBody(), Map.class)).thenReturn(accessMap);
		Map<String, Boolean> actualResponse = clientServiceImpl.getWidgetAccess(mockHttpServletRequest,
				TestConstants.PR_TEST1, 12);
		assertTrue(actualResponse.get(ProxyConstants.APPLICABILITY));
		assertTrue(actualResponse.get(ProxyConstants.ENABLED));
		assertTrue(actualResponse.get(ProxyConstants.ACTIVESERVICESPERSONA));
	}

	@Test
	@DisplayName("GetApplicablesMachinesForWidget")
	void testGetApplicableMachinesForWidget() throws Exception {
		List<String> machinesList = new ArrayList<>();
		machinesList.add("MC_TEST1");
		machinesList.add("MC_TEST2");
		json="{\"machines\":[\"MC_TEST1\",\"MC_TEST2\"]}";
		ResponseEntity<String> userDetailsResponse = new ResponseEntity<String>(json, HttpStatus.OK);
		when(mapper.writeValueAsString(any(WidgetApplicableRequestDTO.class)))
				.thenReturn("{\"vid\":\"PR_TEST1\",\"widgetId\":12,\"field\":\"vid\"}");
		when(restClientWrapperService.postBodyToUrl(any(MockHttpServletRequest.class), anyString(), anyString()))
				.thenReturn(userDetailsResponse);
		JSONObject widgetJson=new JSONObject(userDetailsResponse.getBody());
		when(mapper.readValue(widgetJson.get(ProxyConstants.MACHINES).toString(), List.class)).thenReturn(machinesList);
		List<String> actualResponse = clientServiceImpl.getApplicableMachinesForWidget(mockHttpServletRequest,
				TestConstants.PR_TEST1, 12, ProxyConstants.VID);
		assertEquals(2, actualResponse.size());
		assertEquals("MC_TEST1", actualResponse.get(0));
		assertEquals("MC_TEST2", actualResponse.get(1));
	}

	@Test
	@DisplayName("GetWidgetAdvanceServicesAccess")
	void testGetWidgetAdvanceServicesAccess() throws Exception {
		Map<String, Object> accessMap = new HashMap<>();
		accessMap.put(JSONUtilConstants.ENABLEDSERVICES, true);
		json = "{\"enabledServices\":true}";
		ResponseEntity<String> userDetailsResponse = new ResponseEntity<String>(json, HttpStatus.OK);
		when(mapper.writeValueAsString(any(WidgetAccessRequestDTO.class)))
				.thenReturn("{\"vid\":\"PR_TEST1\",\"widgetId\":12}");
		when(restClientWrapperService.postBodyToUrl(any(MockHttpServletRequest.class), anyString(), anyString()))
				.thenReturn(userDetailsResponse);
		when(mapper.readValue(userDetailsResponse.getBody(), Map.class)).thenReturn(accessMap);
		Map<String, Boolean> actualResponse = clientServiceImpl.getWidgetAdvanceServicesAccess(mockHttpServletRequest,
				TestConstants.PR_TEST1, 12);
		assertTrue(actualResponse.get(JSONUtilConstants.ENABLEDSERVICES));
	}
}
