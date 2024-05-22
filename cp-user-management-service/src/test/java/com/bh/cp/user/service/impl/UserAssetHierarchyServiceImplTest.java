package com.bh.cp.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.user.constants.JSONUtilConstants;
import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.request.AssetHierarchyRequestDTO;
import com.bh.cp.user.dto.request.WidgetAccessRequestDTO;
import com.bh.cp.user.dto.request.WidgetApplicableRequestDTO;
import com.bh.cp.user.dto.response.AssetResponseDTO;
import com.bh.cp.user.dto.response.WidgetAccessResponseDTO;
import com.bh.cp.user.dto.response.WidgetApplicableResponseDTO;
import com.bh.cp.user.entity.Personas;
import com.bh.cp.user.entity.Statuses;
import com.bh.cp.user.entity.Users;
import com.bh.cp.user.entity.WidgetTypes;
import com.bh.cp.user.entity.Widgets;
import com.bh.cp.user.entity.WidgetsFieldsApplicability;
import com.bh.cp.user.respository.UsersRepository;
import com.bh.cp.user.respository.WidgetsAdvanceServicesApplicabilityRepository;
import com.bh.cp.user.respository.WidgetsFieldsApplicabilityRepository;
import com.bh.cp.user.respository.WidgetsRepository;
import com.bh.cp.user.service.AccessService;
import com.bh.cp.user.service.FetchAssetHierarchyService;
import com.bh.cp.user.service.GenericAssetHierarchyFilterService;
import com.bh.cp.user.util.JwtUtil;
import com.bh.cp.user.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

class UserAssetHierarchyServiceImplTest {

	@InjectMocks
	private UserAssetHierarchyServiceImpl userAssetHierarchyServiceImpl;

	@Mock
	private AccessService accessService;

	@Mock
	private FetchAssetHierarchyService fetchAssetHierarchyService;

	@Mock
	private GenericAssetHierarchyFilterService genericAssetHierarchyFilterService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private UsersRepository usersRepository;

	@Mock
	private WidgetsRepository widgetsRepository;

	@Mock
	private WidgetsFieldsApplicabilityRepository widgetsFieldsApplicabilityRepository;

	@Mock
	private WidgetsAdvanceServicesApplicabilityRepository widgetsAdvanceServicesApplicabilityRepository;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private SecurityUtil securityUtil;

	private WidgetAccessRequestDTO widgetAccessRequestDTO;

	private WidgetAccessResponseDTO widgetAccessResponseDTO;

	private WidgetApplicableRequestDTO widgetApplicableRequestDTO;

	private List<Map<String, Object>> assetHierarchy;

	private Map<String, Claim> claims;

	private Widgets widgets;

	private WidgetTypes widgetTypes;

	private Statuses statuses;

	private Personas personas;

	private Users users;

	private Map<String, Map<String, Set<String>>> techEquipCodeAndEnabledServicesMap;

	private Set<String> enabledServices;

	private Set<String> technologyCodes;

	private Set<String> equipmentCodes;

	private List<String> machinesList;

	private List<WidgetsFieldsApplicability> widgetsFieldsApplicabilityList;

	private Map<String, Object> assetsMap;

	private WidgetsFieldsApplicability widgetsFieldsApplicability;

	private MockHttpServletRequest mockHttpServletRequest;

	private AssetHierarchyRequestDTO assetHierarchyRequestDTO;

	private Boolean matchFound;

	private class TestConstants {

		private static final String TEST1 = "TEST1";
		private static final String TEST2 = "TEST2";
		private static final String TEST3 = "TEST3";
		private static final String PL_TEST1 = "MC_TEST1";
		private static final String PL_TEST2 = "MC_TEST2";
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
	}

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		machinesList = new ArrayList<>();
		machinesList.add("MC_TEST1");
		assetsMap = new HashMap<>();
		assetsMap.put(JSONUtilConstants.LEVEL_MACHINES, machinesList);
		assetsMap.put("searchVid", TestConstants.PL_TEST1);
		widgetAccessRequestDTO = new WidgetAccessRequestDTO();
		widgetAccessRequestDTO.setVid("PR_TEST1");
		widgetAccessRequestDTO.setWidgetId(1);
		widgetAccessResponseDTO = new WidgetAccessResponseDTO();
		widgetAccessResponseDTO.setActiveServicesPersona(true);
		widgetAccessResponseDTO.setApplicability(true);
		widgetAccessResponseDTO.setApplicability(true);

		widgetApplicableRequestDTO = new WidgetApplicableRequestDTO();
		widgetApplicableRequestDTO.setVid(TestConstants.PL_TEST1);
		widgetApplicableRequestDTO.setWidgetId(1);
		widgetApplicableRequestDTO.setField("machines");

		assetHierarchyRequestDTO = new AssetHierarchyRequestDTO();
		assetHierarchyRequestDTO.setField("vid");
		assetHierarchyRequestDTO.setVid(TestConstants.PR_TEST1);

		Claim userClaim = Mockito.mock(Claim.class);
		claims = new HashMap<String, Claim>();
		claims.put("preferred_username", userClaim);

		statuses = new Statuses();
		statuses.setId(1);
		statuses.setDescription("ACTIVE");
		statuses.setStatusIndicator(1);
		statuses.setStatusType("ACTIVE");

		widgetTypes = new WidgetTypes();
		widgetTypes.setId(1);
		widgetTypes.setDescription("KPI");
		widgetTypes.setStatuses(statuses);

		widgets = new Widgets();
		widgets.setStatuses(statuses);
		widgets.setWidgetTypes(widgetTypes);
		widgets.setId(1);
		widgets.setPaidService(true);
		widgets.setTitle("widget1");
		widgets.setIdmPrivilege("privileage1");

		personas = new Personas();
		personas.setId(1);
		personas.setStatuses(statuses);
		personas.setDescription("operation");

		users = new Users();
		users.setId(1);
		users.setEmail("user1@gmail.com");
		users.setPersonas(personas);
		users.setSso("user1");

		enabledServices = new HashSet<>();
		enabledServices.add("abc");
		enabledServices.add("cde");

		technologyCodes = new HashSet<>();
		technologyCodes.add("abc");
		technologyCodes.add("cde");

		equipmentCodes = new HashSet<>();
		equipmentCodes.add("abc");
		equipmentCodes.add("cde");

		Map<String, Set<String>> vidMap = new HashMap<>();
		vidMap.put(JSONUtilConstants.ENABLEDSERVICES, enabledServices);
		vidMap.put(JSONUtilConstants.EQUIPMENTCODES, equipmentCodes);
		vidMap.put(JSONUtilConstants.TECHNOLOGYCODES, technologyCodes);

		Map<String, Set<String>> vidMap1 = new HashMap<>();
		vidMap1.put(JSONUtilConstants.ENABLEDSERVICES, new HashSet<>());
		vidMap1.put(JSONUtilConstants.EQUIPMENTCODES, new HashSet<>());
		vidMap1.put(JSONUtilConstants.TECHNOLOGYCODES, new HashSet<>());

		techEquipCodeAndEnabledServicesMap = new HashMap<>();
		techEquipCodeAndEnabledServicesMap.put("PR_TEST1", vidMap);
		techEquipCodeAndEnabledServicesMap.put(TestConstants.PL_TEST1, vidMap1);

		widgetsFieldsApplicability = new WidgetsFieldsApplicability();
		widgetsFieldsApplicability.setId(1);
		widgetsFieldsApplicability.setWidgets(widgets);
		widgetsFieldsApplicability.setEquipmentCode("abc");
		widgetsFieldsApplicability.setTechnologyCode("abc");

		widgetsFieldsApplicabilityList = new ArrayList<>();
		widgetsFieldsApplicabilityList.add(widgetsFieldsApplicability);

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
	}

	@Test
	@DisplayName("CheckWidgetAccess - Vid Found in Hierarchy")
	void testCheckWidgetAccess() throws JsonProcessingException, Exception {
		matchFound = true;
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.validateVid(assetHierarchy, widgetAccessRequestDTO.getVid()))
				.thenReturn(matchFound);
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("privileage1", "privileage2"));
		when(SecurityUtil.getClaims(mockHttpServletRequest, jwtUtil)).thenReturn(claims);
		when(SecurityUtil.getSSO(claims)).thenReturn("user1");
		when(widgetsRepository.findById(widgetAccessRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		when(usersRepository.findBySso("user1")).thenReturn(Optional.of(users));
		when(genericAssetHierarchyFilterService.getFieldsAndEnabledServicesToMap(assetHierarchy))
				.thenReturn(techEquipCodeAndEnabledServicesMap);
		when(widgetsFieldsApplicabilityRepository.findByWidgetsId(widgetAccessRequestDTO.getWidgetId()))
				.thenReturn(widgetsFieldsApplicabilityList);
		when(widgetsAdvanceServicesApplicabilityRepository
				.matchEnabledServicesForWidget(widgetAccessRequestDTO.getWidgetId(), enabledServices)).thenReturn(true);
		WidgetAccessResponseDTO response = userAssetHierarchyServiceImpl.checkWidgetAccess(mockHttpServletRequest,
				widgetAccessRequestDTO);
		assertNotNull(response);
		assertEquals(true, response.isEnabled());
		assertEquals(false, response.isActiveServicesPersona());
		assertEquals(true, response.isApplicability());
		assertEquals(true, response.isHasAccess());
	}

	@Test
	@DisplayName("CheckWidgetAccess - Widget Not Found")
	void testCheckWidgetAccessWidgetNotFound() throws JsonProcessingException, Exception {
		matchFound = true;
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.validateVid(assetHierarchy, widgetAccessRequestDTO.getVid()))
				.thenReturn(matchFound);
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("privileage1", "privileage2"));
		when(SecurityUtil.getClaims(mockHttpServletRequest, jwtUtil)).thenReturn(claims);
		when(SecurityUtil.getSSO(claims)).thenReturn("user1");
		when(widgetsRepository.findById(widgetAccessRequestDTO.getWidgetId())).thenReturn(Optional.empty());
		WidgetAccessResponseDTO response = userAssetHierarchyServiceImpl.checkWidgetAccess(mockHttpServletRequest,
				widgetAccessRequestDTO);
		assertNotNull(response);
		assertEquals(true, response.isEnabled());
		assertEquals(false, response.isActiveServicesPersona());
		assertEquals(true, response.isApplicability());
		assertEquals(true, response.isHasAccess());
	}

	@Test
	@DisplayName("CheckWidgetAccess - User Not Found")
	void testCheckWidgetAccessUserNotFound() throws JsonProcessingException, Exception {
		matchFound = true;
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.validateVid(assetHierarchy, widgetAccessRequestDTO.getVid()))
				.thenReturn(matchFound);
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("privileage1", "privileage2"));
		when(SecurityUtil.getClaims(mockHttpServletRequest, jwtUtil)).thenReturn(claims);
		when(SecurityUtil.getSSO(claims)).thenReturn("user1");
		when(widgetsRepository.findById(widgetAccessRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		when(usersRepository.findBySso("user1")).thenReturn(Optional.empty());
		when(genericAssetHierarchyFilterService.getFieldsAndEnabledServicesToMap(assetHierarchy))
				.thenReturn(techEquipCodeAndEnabledServicesMap);
		when(widgetsFieldsApplicabilityRepository.findByWidgetsId(widgetAccessRequestDTO.getWidgetId()))
				.thenReturn(widgetsFieldsApplicabilityList);
		when(widgetsAdvanceServicesApplicabilityRepository
				.matchEnabledServicesForWidget(widgetAccessRequestDTO.getWidgetId(), enabledServices)).thenReturn(true);
		assertThrows(NotFoundException.class, () -> {
			userAssetHierarchyServiceImpl.checkWidgetAccess(mockHttpServletRequest, widgetAccessRequestDTO);
		});
	}

	@Test
	@DisplayName("CheckWidgetAccess - Vid Not Found in Hierarchy")
	void testCheckWidgetAccessMatchNotFound() throws JsonProcessingException, Exception {
		matchFound = false;
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.validateVid(assetHierarchy, widgetAccessRequestDTO.getVid()))
				.thenReturn(matchFound);
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("privileage1", "privileage2"));
		when(SecurityUtil.getClaims(mockHttpServletRequest, jwtUtil)).thenReturn(claims);
		when(SecurityUtil.getSSO(claims)).thenReturn("user1");
		when(widgetsRepository.findById(widgetAccessRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		when(usersRepository.findBySso("user1")).thenReturn(Optional.of(users));
		when(genericAssetHierarchyFilterService.getFieldsAndEnabledServicesToMap(assetHierarchy))
				.thenReturn(techEquipCodeAndEnabledServicesMap);
		when(widgetsFieldsApplicabilityRepository.findByWidgetsId(widgetAccessRequestDTO.getWidgetId()))
				.thenReturn(widgetsFieldsApplicabilityList);
		when(widgetsAdvanceServicesApplicabilityRepository
				.matchEnabledServicesForWidget(widgetAccessRequestDTO.getWidgetId(), enabledServices)).thenReturn(true);
		assertThrows(AccessDeniedException.class, () -> {
			userAssetHierarchyServiceImpl.checkWidgetAccess(mockHttpServletRequest, widgetAccessRequestDTO);
		});
	}

	@Test
	@DisplayName("CheckWidgetAccess - User Has Active Persona")
	void testCheckWidgetAccessActivePersona() throws JsonProcessingException, Exception {
		matchFound = true;
		personas.setDescription("Show only active service");
		users.setPersonas(personas);
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.validateVid(assetHierarchy, widgetAccessRequestDTO.getVid()))
				.thenReturn(matchFound);
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("privileage1", "privileage2"));
		when(SecurityUtil.getClaims(mockHttpServletRequest, jwtUtil)).thenReturn(claims);
		when(SecurityUtil.getSSO(claims)).thenReturn("user1");
		when(widgetsRepository.findById(widgetAccessRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		when(usersRepository.findBySso("user1")).thenReturn(Optional.of(users));
		when(genericAssetHierarchyFilterService.getFieldsAndEnabledServicesToMap(assetHierarchy))
				.thenReturn(techEquipCodeAndEnabledServicesMap);
		when(widgetsFieldsApplicabilityRepository.findByWidgetsId(widgetAccessRequestDTO.getWidgetId()))
				.thenReturn(widgetsFieldsApplicabilityList);
		when(widgetsAdvanceServicesApplicabilityRepository
				.matchEnabledServicesForWidget(widgetAccessRequestDTO.getWidgetId(), enabledServices)).thenReturn(true);
		WidgetAccessResponseDTO response = userAssetHierarchyServiceImpl.checkWidgetAccess(mockHttpServletRequest,
				widgetAccessRequestDTO);
		assertNotNull(response);
		assertEquals(true, response.isEnabled());
		assertEquals(true, response.isActiveServicesPersona());
		assertEquals(true, response.isApplicability());
		assertEquals(true, response.isHasAccess());
	}

	@Test
	@DisplayName("CheckWidgetAccess - Field Applicability As True")
	void testCheckWidgetAccessFieldApplicability() throws JsonProcessingException, Exception {
		matchFound = true;
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.validateVid(assetHierarchy, widgetAccessRequestDTO.getVid()))
				.thenReturn(matchFound);
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("privileage1", "privileage2"));
		when(SecurityUtil.getClaims(mockHttpServletRequest, jwtUtil)).thenReturn(claims);
		when(SecurityUtil.getSSO(claims)).thenReturn("user1");
		when(widgetsRepository.findById(widgetAccessRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		when(usersRepository.findBySso("user1")).thenReturn(Optional.of(users));
		when(genericAssetHierarchyFilterService.getFieldsAndEnabledServicesToMap(assetHierarchy))
				.thenReturn(techEquipCodeAndEnabledServicesMap);
		when(widgetsFieldsApplicabilityRepository.findByWidgetsId(widgetAccessRequestDTO.getWidgetId()))
				.thenReturn(new ArrayList<>());
		when(widgetsAdvanceServicesApplicabilityRepository
				.matchEnabledServicesForWidget(widgetAccessRequestDTO.getWidgetId(), enabledServices))
				.thenReturn(false);
		WidgetAccessResponseDTO response = userAssetHierarchyServiceImpl.checkWidgetAccess(mockHttpServletRequest,
				widgetAccessRequestDTO);
		assertNotNull(response);
		assertEquals(false, response.isEnabled());
		assertEquals(false, response.isActiveServicesPersona());
		assertEquals(true, response.isApplicability());
		assertEquals(false, response.isHasAccess());
	}

	@Test
	@DisplayName("getUserApplicableMachinesForWidget - Field as VID")
	void testGetUserApplicableMachinesForWidget() throws JsonProcessingException, Exception {
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.getAssetsMap(assetHierarchy, widgetApplicableRequestDTO.getVid(),
				JSONUtilConstants.VID)).thenReturn(assetsMap);
		when(widgetsRepository.findById(widgetApplicableRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		when(genericAssetHierarchyFilterService.getFieldsAndEnabledServicesToMap(assetHierarchy))
				.thenReturn(techEquipCodeAndEnabledServicesMap);
		when(widgetsFieldsApplicabilityRepository.findByWidgetsId(widgetAccessRequestDTO.getWidgetId()))
				.thenReturn(widgetsFieldsApplicabilityList);
		WidgetApplicableResponseDTO response = userAssetHierarchyServiceImpl
				.getUserApplicableMachinesForWidget(mockHttpServletRequest, widgetApplicableRequestDTO);
		assertNotNull(response);
		assertEquals(0, response.getMachines().size());
	}

	@Test
	@DisplayName("getUserApplicableMachinesForWidget - Field as ID")
	void testGetUserApplicableMachinesForWidgetFieldAsID() throws JsonProcessingException, Exception {
		widgetApplicableRequestDTO.setField(JSONUtilConstants.ID);
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.getAssetsMap(assetHierarchy, widgetApplicableRequestDTO.getVid(),
				JSONUtilConstants.VID)).thenReturn(assetsMap);
		when(widgetsRepository.findById(widgetApplicableRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		when(genericAssetHierarchyFilterService.getFieldsAndEnabledServicesToMap(assetHierarchy))
				.thenReturn(techEquipCodeAndEnabledServicesMap);
		when(widgetsFieldsApplicabilityRepository.findByWidgetsId(widgetAccessRequestDTO.getWidgetId()))
				.thenReturn(widgetsFieldsApplicabilityList);
		WidgetApplicableResponseDTO response = userAssetHierarchyServiceImpl
				.getUserApplicableMachinesForWidget(mockHttpServletRequest, widgetApplicableRequestDTO);
		assertNotNull(response);
		assertEquals(0, response.getMachines().size());
	}

	@Test
	@DisplayName("GetUserApplicableMachinesForWidget - Widget Not Found")
	void testGetUserApplicableMachinesForWidgetFieldWidgetNotFound() throws JsonProcessingException, Exception {
		widgetApplicableRequestDTO.setField(JSONUtilConstants.ID);
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.getAssetsMap(assetHierarchy, widgetApplicableRequestDTO.getVid(),
				JSONUtilConstants.VID)).thenReturn(assetsMap);
		when(widgetsRepository.findById(widgetApplicableRequestDTO.getWidgetId())).thenReturn(Optional.empty());
		when(genericAssetHierarchyFilterService.getFieldsAndEnabledServicesToMap(assetHierarchy))
				.thenReturn(techEquipCodeAndEnabledServicesMap);
		when(widgetsFieldsApplicabilityRepository.findByWidgetsId(widgetAccessRequestDTO.getWidgetId()))
				.thenReturn(widgetsFieldsApplicabilityList);
		WidgetApplicableResponseDTO response = userAssetHierarchyServiceImpl
				.getUserApplicableMachinesForWidget(mockHttpServletRequest, widgetApplicableRequestDTO);
		assertNull(response);
	}

	@Test
	@DisplayName("CheckWidgetKeycloakAccess - subscribed true")
	void checkWidgetKeycloakAccess() throws JsonProcessingException,Exception {
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privileage1"));
		when(widgetsRepository.findById(widgetApplicableRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		Map<String, Boolean> outputMap=userAssetHierarchyServiceImpl.checkWidgetKeycloakAccess(mockHttpServletRequest, 1);
		assertNotNull(outputMap);
		assertEquals(true, outputMap.get(UMSConstants.SUBSCRIBED));
	}

	@Test
	@DisplayName("CheckWidgetKeycloakAccess - subscribed false")
	void checkWidgetKeycloakAccessFalse() throws JsonProcessingException,Exception {
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privileage2"));
		when(widgetsRepository.findById(widgetApplicableRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		Map<String, Boolean> outputMap=userAssetHierarchyServiceImpl.checkWidgetKeycloakAccess(mockHttpServletRequest, 1);
		assertNotNull(outputMap);
		assertEquals(false, outputMap.get(UMSConstants.SUBSCRIBED));
	}

	@Test
	@DisplayName("CheckWidgetKeycloakAccess - Widget Not a Paid Service")
	void checkWidgetKeycloakAccessNotAPaidService() throws JsonProcessingException, Exception {
		widgets.setPaidService(false);
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privileage2"));
		when(widgetsRepository.findById(widgetApplicableRequestDTO.getWidgetId())).thenReturn(Optional.of(widgets));
		Map<String, Boolean> outputMap = userAssetHierarchyServiceImpl.checkWidgetKeycloakAccess(mockHttpServletRequest,
				1);
		assertNotNull(outputMap);
		assertEquals(true, outputMap.get(UMSConstants.SUBSCRIBED));
	}

	@Test
	@DisplayName("CheckWidgetKeycloakAccess - subscribed false and widgets as null")
	void checkWidgetKeycloakAccessFalseWidgetsNull() throws JsonProcessingException,Exception {
		when(accessService.getCurrentUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privileage2"));
		when(widgetsRepository.findById(widgetApplicableRequestDTO.getWidgetId())).thenReturn(Optional.of(new Widgets()));
		Map<String, Boolean> outputMap=userAssetHierarchyServiceImpl.checkWidgetKeycloakAccess(mockHttpServletRequest, 2);
		assertNotNull(outputMap);
		assertEquals(false, outputMap.get(UMSConstants.SUBSCRIBED));
	}

	@Test
	@DisplayName("GetUserAssetHierarchyChildren")
	void testGetUserAssetHierarchyChildren() throws JsonProcessingException,Exception {
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.getSubTree(assetHierarchy,assetHierarchyRequestDTO.getVid())).thenReturn(assetHierarchy);
		List<Map<String, Object>> userHierarchy=userAssetHierarchyServiceImpl.getUserAssetHierarchyChildren(mockHttpServletRequest, assetHierarchyRequestDTO);
		assertNotNull(userHierarchy);
	}

	@Test
	@DisplayName("GetUserAssetHierarchyField - Vid Found In the requestDTO")
	void testGetUserAssetHierarchyField() throws JsonProcessingException,Exception {
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.getImmediateParentField(assetHierarchy,assetHierarchyRequestDTO.getVid(),assetHierarchyRequestDTO.getField())).thenReturn("[{\"level\":\"projects\"}]");
		Map<String, String> outputMap=userAssetHierarchyServiceImpl.getUserAssetHierarchyField(mockHttpServletRequest, assetHierarchyRequestDTO,true);
		assertNotNull(outputMap);
		assertEquals("[{\"level\":\"projects\"}]", outputMap.get(assetHierarchyRequestDTO.getField()));
	}

	@Test
	@DisplayName("GetUserAssetHierarchyField - Vid Not Found In the requestDTO")
	void testGetUserAssetHierarchyFieldVidNotFound() throws JsonProcessingException, Exception {
		assetHierarchyRequestDTO = new AssetHierarchyRequestDTO();
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.getImmediateParentField(assetHierarchy,
				assetHierarchyRequestDTO.getVid(), assetHierarchyRequestDTO.getField()))
				.thenReturn("[{\"level\":\"projects\"}]");
		Map<String, String> outputMap = userAssetHierarchyServiceImpl.getUserAssetHierarchyField(mockHttpServletRequest,
				assetHierarchyRequestDTO, true);
		assertEquals(0, outputMap.size());
	}

	@Test
	@DisplayName("getUserAssetHierarchyAssets")
	void testGetUserAssetHierarchyAssets() throws JsonProcessingException, Exception {
		assetHierarchyRequestDTO.setVid(TestConstants.PL_TEST1);
		List<String> machinesList = new ArrayList<>();
		machinesList.add(TestConstants.PL_TEST1);
		Map<String, Object> assetsMap = new HashMap<>();
		assetsMap.put(JSONUtilConstants.LEVEL_MACHINES, machinesList);
		assetsMap.put(JSONUtilConstants.SEARCHVID, TestConstants.PL_TEST1);
		assetsMap.put(JSONUtilConstants.PREVIOUSLEVEL, "lineups");
		assetsMap.put(JSONUtilConstants.CURRENTLEVEL, "machines");
		assetsMap.put(JSONUtilConstants.MATCHFOUND, true);
		assetsMap.put(JSONUtilConstants.LEVEL_LINEUPS, Arrays.asList("LN_TEST1"));
		assetsMap.put(JSONUtilConstants.LEVEL_PROJECTS, Arrays.asList("PR_TEST1"));
		when(accessService.getCurrentUserFilteredHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.getAssetsMap(assetHierarchy, assetHierarchyRequestDTO.getVid(),
				assetHierarchyRequestDTO.getField())).thenReturn(assetsMap);
		AssetResponseDTO assetResponseDTO = userAssetHierarchyServiceImpl
				.getUserAssetHierarchyAssets(mockHttpServletRequest, assetHierarchyRequestDTO);
		assertNotNull(assetResponseDTO);
		assertEquals("lineups", assetResponseDTO.getPreviousLevel());
		assertEquals(TestConstants.PR_TEST1, assetResponseDTO.getProjects().get(0));
		assertEquals("LN_TEST1", assetResponseDTO.getLineups().get(0));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("CheckWidgetAdvanceServicesAccess")
	void testCheckWidgetAdvanceServicesAccess() throws Exception {
		when(accessService.getCurrentUserFilteredHierarchy(any(HttpServletRequest.class))).thenReturn(assetHierarchy);
		when(genericAssetHierarchyFilterService.getFieldsAndEnabledServicesToMap(anyList()))
		.thenReturn(techEquipCodeAndEnabledServicesMap);
		when(widgetsAdvanceServicesApplicabilityRepository
				.matchEnabledServicesForWidget(anyInt(), any(Set.class))).thenReturn(true);
		Map<String,Boolean> actualResponse=userAssetHierarchyServiceImpl.checkWidgetAdvanceServicesAccess(httpServletRequest, widgetAccessRequestDTO);
		assertNotNull(actualResponse);
		assertTrue(actualResponse.get(JSONUtilConstants.ENABLEDSERVICES));
	}

}
