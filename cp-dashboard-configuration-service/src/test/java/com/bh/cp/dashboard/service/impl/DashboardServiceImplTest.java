package com.bh.cp.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.constants.ExceptionConstants;
import com.bh.cp.dashboard.constants.JSONUtilConstants;
import com.bh.cp.dashboard.dto.request.LayoutRequestDTO;
import com.bh.cp.dashboard.dto.response.LayoutResponseDTO;
import com.bh.cp.dashboard.dto.response.UsersAssetsResponseDTO;
import com.bh.cp.dashboard.entity.Cards;
import com.bh.cp.dashboard.entity.CommonBlobs;
import com.bh.cp.dashboard.entity.Companies;
import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.CustomizationsAssets;
import com.bh.cp.dashboard.entity.CustomizationsWidgets;
import com.bh.cp.dashboard.entity.Personas;
import com.bh.cp.dashboard.entity.Settings;
import com.bh.cp.dashboard.entity.Statuses;
import com.bh.cp.dashboard.entity.Users;
import com.bh.cp.dashboard.entity.UsersDefaultAssets;
import com.bh.cp.dashboard.entity.WidgetTypes;
import com.bh.cp.dashboard.entity.Widgets;
import com.bh.cp.dashboard.entity.WidgetsLevel;
import com.bh.cp.dashboard.repository.CustomizationsAssetsRepository;
import com.bh.cp.dashboard.repository.CustomizationsRepository;
import com.bh.cp.dashboard.repository.CustomizationsWidgetsRepository;
import com.bh.cp.dashboard.repository.UsersDefaultAssetsRepository;
import com.bh.cp.dashboard.repository.WidgetsLevelRepository;
import com.bh.cp.dashboard.service.AssetHierarchyFilterService;
import com.bh.cp.dashboard.service.AssetService;
import com.bh.cp.dashboard.service.UMSClientService;
import com.bh.cp.dashboard.service.UserSettingsService;

import jakarta.ws.rs.NotFoundException;

class DashboardServiceImplTest {

	@InjectMocks
	private DashboardServiceImpl dashboardService;

	@Mock
	private UMSClientService umsClientService;

	@Mock
	private UserSettingsService userSettingsService;

	@Mock
	private AssetHierarchyFilterService assetHierarchyFilterService;

	@Mock
	private CustomizationsRepository customizationsRepository;

	@Mock
	private CustomizationsWidgetsRepository customizationsWidgetsRepository;

	@Mock
	private CustomizationsAssetsRepository customizationsAssetsRepository;

	@Mock
	private WidgetsLevelRepository widgetsLevelRepository;

	@Mock
	private UsersDefaultAssetsRepository usersDefaultAssetsRepository;

	@Mock
	private AssetService assetService;

	private MockHttpServletRequest mockHttpServletRequest;
	private Map<String, Claim> claims;

	private CommonBlobs commonBlobs;
	private Personas personas;
	private Users users;
	private Companies companies;
	private Widgets widgets;
	private WidgetsLevel widgetsLevel;
	private UsersDefaultAssets userDefaultAssets;
	private Customizations customizations;
	private CustomizationsAssets customizationsAssets;
	private WidgetTypes widgetTypes;
	private Cards cards;
	private Statuses statuses;
	private Settings settings;
	private List<CustomizationsAssets> customizationsAssetsList;
	private CustomizationsWidgets customizationsWidgets;
	private List<CustomizationsWidgets> customizationsWidgetsList;
	private List<WidgetsLevel> widgetsLevelList;

	private Map<String, Object> accessibleVidsAndLevel;
	private List<Map<String, Object>> assetHierarchy;
	private List<String> plantsList;
	private List<String> projectsList;
	private Map<String, String> displayNameMap;
	private Map<String, String> imageSrcMap;
	private Map<String, String> idMap;
	private Map<String, String> customerNameMap;
	private Map<String, Object> assetsMap;

	private LayoutRequestDTO layoutRequestDto;

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

		commonBlobs = new CommonBlobs();
		commonBlobs.setId(1);
		commonBlobs.setMaterial(null);

		cards = new Cards();
		cards.setId(1);
		cards.setCardType("Graph");
		cards.setDescription("Graph type");

		statuses = new Statuses();
		statuses.setId(1);
		statuses.setDescription("ACTIVE");
		statuses.setStatusIndicator(1);
		statuses.setStatusType("ACTIVE");

		widgetTypes = new WidgetTypes();
		widgetTypes.setId(1);
		widgetTypes.setDescription(DashboardConstants.KPI);
		widgetTypes.setStatuses(statuses);

		personas = new Personas();
		personas.setId(1);
		personas.setDescription("Show All services");

		companies = new Companies();
		companies.setId(1);
		companies.setName("bh");
		companies.setIconImages(commonBlobs);

		users = new Users();
		users.setId(1);
		users.setEmail("test@gmail.com");
		users.setCompanies(companies);
		users.setSso("testUser");
		users.setUsername("test");

		customizations = new Customizations();
		customizations.setId(1);
		customizations.setUsers(users);
		customizations.setDateRange("3M");
		customizations.setDefault(false);
		customizations.setAssetLevel(JSONUtilConstants.LEVEL_PROJECTS);
		customizations.setPersonas(personas);
		customizations.setUsers(users);
		customizations.setReordered(true);

		customizationsAssets = new CustomizationsAssets();
		customizationsAssets.setId(1);
		customizationsAssets.setAssetName("Project Test1");
		customizationsAssets.setCustomizations(customizations);
		customizationsAssets.setOrderNumber(1);
		customizationsAssets.setVid(TestConstants.PR_TEST1);
		customizationsAssetsList = new ArrayList<>();
		customizationsAssetsList.add(customizationsAssets);

		widgets = new Widgets();
		widgets.setCards(cards);
		widgets.setStatuses(statuses);
		widgets.setWidgetTypes(widgetTypes);
		widgets.setDescription("applicable");
		widgets.setDetailsUri("API");
		widgets.setId(1);
		widgets.setPaidService(true);
		widgets.setTitle("Case Management");

		widgetsLevel = new WidgetsLevel();
		widgetsLevel.setId(1);
		widgetsLevel.setAssetLevel(JSONUtilConstants.LEVEL_PROJECTS);
		widgetsLevel.setIconImage(commonBlobs);
		widgetsLevel.setStaticImage(commonBlobs);
		widgetsLevel.setWidgets(widgets);

		widgetsLevelList = new ArrayList<>();
		widgetsLevelList.add(widgetsLevel);

		customizationsWidgets = new CustomizationsWidgets();
		customizationsWidgets.setId(1);
		customizationsWidgets.setOrderNumber(1);
		customizationsWidgets.setWidgets(widgets);
		customizationsWidgets.setCustomizations(customizations);
		customizationsWidgetsList = new ArrayList<>();
		customizationsWidgetsList.add(customizationsWidgets);

		userDefaultAssets = new UsersDefaultAssets();
		userDefaultAssets.setDefaultVid(TestConstants.PR_TEST1);

		settings = new Settings();
		settings.setCompanies(companies);
		settings.setDefaultRole("Show All services");
		settings.setId(1);
		settings.setMyDashboardPersona(new Personas());

		projectsList = new ArrayList<>();
		projectsList.add(TestConstants.PR_TEST1);

		plantsList = new ArrayList<>();
		plantsList.add(TestConstants.PL_TEST1);

		accessibleVidsAndLevel = new HashMap<>();
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL_PLANTS, plantsList);
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);

		assetsMap = new HashMap<>();
		assetsMap.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);
		assetsMap.put(JSONUtilConstants.LEVEL_PLANTS, plantsList);
		assetsMap.put(JSONUtilConstants.NEXTLEVEL, JSONUtilConstants.LEVEL_PROJECTS);

		displayNameMap = new HashMap<>();
		displayNameMap.put(TestConstants.PR_TEST1, TestConstants.TEST1);
		displayNameMap.put(TestConstants.PL_TEST1, TestConstants.TEST1);
		displayNameMap.put(TestConstants.TR_TEST1, TestConstants.TEST1);
		displayNameMap.put(TestConstants.LN_TEST1, TestConstants.TEST1);
		displayNameMap.put(TestConstants.MC_TEST1, TestConstants.TEST1);

		imageSrcMap = new HashMap<>();
		imageSrcMap.put(TestConstants.PR_TEST1, "/resources/project1.png");

		idMap = new HashMap<>();
		idMap.put(TestConstants.PR_TEST1, TestConstants.TEST1);

		customerNameMap = new HashMap<>();
		customerNameMap.put(TestConstants.PR_TEST1, "Project1 for Testing");

		layoutRequestDto = new LayoutRequestDTO();
		layoutRequestDto.setShowSiblings(false);
		layoutRequestDto.setVid(null);

		Claim userClaim = Mockito.mock(Claim.class);
		claims = new HashMap<String, Claim>();
		claims.put("preferred_username", userClaim);

		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
	}

	@Test
	@DisplayName("GetLayouts - Vid-Null,Sibilings-false and default Customization")
	void testGetLayouts() throws Exception {
		List<CustomizationsAssets> customizationsAssets = new ArrayList<>();
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(users);
		when(assetService.getDefaultAsset(mockHttpServletRequest)).thenReturn(new UsersAssetsResponseDTO());
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, layoutRequestDto.getVid(), true))
				.thenReturn(assetsMap);
		when(userSettingsService.getDefaultSettings()).thenReturn(settings);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(null);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(settings.getDefaultPersonas(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(customizations);
		when(customizationsAssetsRepository.findByCustomizations(customizations)).thenReturn(customizationsAssets);
		when(assetHierarchyFilterService.getDisplayNameMap(assetHierarchy)).thenReturn(displayNameMap);
		when(assetHierarchyFilterService.getImageSrcMap(assetHierarchy)).thenReturn(imageSrcMap);
		when(assetHierarchyFilterService.getIdMap(assetHierarchy)).thenReturn(idMap);
		when(assetHierarchyFilterService.getCustomerNameMap(assetHierarchy, projectsList)).thenReturn(customerNameMap);
		when(customizationsWidgetsRepository.findAllByCustomizationsAndAndWidgetsWidgetTypesDescription(customizations,
				DashboardConstants.KPI)).thenReturn(customizationsWidgetsList);
		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsStatusesStatusIndicator(
				JSONUtilConstants.LEVEL_PROJECTS, customizationsWidgets.getWidgets().getId(),
				DashboardConstants.ACTIVE_STATUS_INDICATOR)).thenReturn(Optional.of(widgetsLevel));
		LayoutResponseDTO actualResponseDTO = dashboardService.getUserDashboardLayout(mockHttpServletRequest,
				layoutRequestDto);
		assertNotNull(actualResponseDTO);
		assertEquals(users.getCompanies().getName(), actualResponseDTO.getHeader().getCustomerName());
		assertEquals(users.getCompanies().getIconImages().getId(), actualResponseDTO.getHeader().getCompanyLogo());
		assertEquals(TestConstants.PR_TEST1, actualResponseDTO.getSelectedVid());
		assertEquals(idMap.get(TestConstants.PR_TEST1), actualResponseDTO.getSelectedId());
		assertEquals(displayNameMap.get(TestConstants.PR_TEST1), actualResponseDTO.getSelectedTitle());
		assertEquals(null, actualResponseDTO.getParentVid());
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, actualResponseDTO.getLevel());
		assertEquals(1, actualResponseDTO.getAssets().size());
		assertEquals("/resources/project1.png", actualResponseDTO.getAssets().get(0).getImageSrc());
		assertEquals("Project1 for Testing", actualResponseDTO.getAssets().get(0).getCustomerName());
		assertEquals(1, actualResponseDTO.getKpis().size());
		assertEquals(1, actualResponseDTO.getKpis().get(0).getStaticImageId());
		assertEquals("Case Management", actualResponseDTO.getKpis().get(0).getTitle());
		assertEquals(0, actualResponseDTO.getSummary().size());
		assertEquals(1, actualResponseDTO.getCustomizationId());
	}

	@Test
	@DisplayName("GetLayouts - AccessibleVids Is empty And WidgetsLevel Is Null")
	void testGetLayoutsAccessibleVidsIsEmpty() throws Exception {
		customizations.setId(null);
		assetsMap.put(JSONUtilConstants.LEVEL_PROJECTS, new ArrayList<>());
		List<CustomizationsAssets> customizationsAssets = new ArrayList<>();
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(users);
		when(assetService.getDefaultAsset(mockHttpServletRequest)).thenReturn(new UsersAssetsResponseDTO());
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, layoutRequestDto.getVid(), true))
				.thenReturn(assetsMap);
		when(userSettingsService.getDefaultSettings()).thenReturn(settings);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(null);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(settings.getDefaultPersonas(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(customizations);
		when(customizationsAssetsRepository.findByCustomizations(customizations)).thenReturn(customizationsAssets);
		when(assetHierarchyFilterService.getDisplayNameMap(assetHierarchy)).thenReturn(displayNameMap);
		when(assetHierarchyFilterService.getImageSrcMap(assetHierarchy)).thenReturn(imageSrcMap);
		when(assetHierarchyFilterService.getIdMap(assetHierarchy)).thenReturn(idMap);
		when(assetHierarchyFilterService.getCustomerNameMap(assetHierarchy, projectsList)).thenReturn(customerNameMap);
		when(customizationsWidgetsRepository.findAllByCustomizationsAndAndWidgetsWidgetTypesDescription(customizations,
				DashboardConstants.KPI)).thenReturn(customizationsWidgetsList);
		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsStatusesStatusIndicator(
				JSONUtilConstants.LEVEL_PROJECTS, customizationsWidgets.getWidgets().getId(),
				DashboardConstants.ACTIVE_STATUS_INDICATOR)).thenReturn(Optional.empty());
		LayoutResponseDTO actualResponseDTO = dashboardService.getUserDashboardLayout(mockHttpServletRequest,
				layoutRequestDto);
		assertNotNull(actualResponseDTO);
		assertEquals(users.getCompanies().getName(), actualResponseDTO.getHeader().getCustomerName());
		assertEquals(users.getCompanies().getIconImages().getId(), actualResponseDTO.getHeader().getCompanyLogo());
		assertEquals(null, actualResponseDTO.getSelectedVid());
		assertEquals(null, actualResponseDTO.getSelectedId());
		assertEquals(null, actualResponseDTO.getSelectedTitle());
		assertEquals(null, actualResponseDTO.getParentVid());
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, actualResponseDTO.getLevel());
		assertEquals(null, actualResponseDTO.getAssets());
		assertEquals(0, actualResponseDTO.getKpis().size());
		assertEquals(0, actualResponseDTO.getSummary().size());
		assertEquals(null, actualResponseDTO.getCustomizationId());
	}

	@Test
	@DisplayName("GetLayouts - Vid-Null,Sibilings-false and User Did Customization")
	void testGetLayoutsUserHasCustomized() throws Exception {
		personas.setDescription("My Dashboard");
		personas.setId(6);
		users.setPersonas(personas);
		settings.setMyDashboardPersona(personas);
		customizations.setId(1003);
		customizationsAssets.setCustomizations(customizations);
		customizationsAssets.setAssetName(null);
		customizationsAssetsList = new ArrayList<>();
		customizationsAssetsList.add(customizationsAssets);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(users);
		when(assetService.getDefaultAsset(mockHttpServletRequest)).thenReturn(new UsersAssetsResponseDTO());
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, layoutRequestDto.getVid(), true))
				.thenReturn(assetsMap);
		when(userSettingsService.getDefaultSettings()).thenReturn(settings);
		when(customizationsRepository.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(false, users.getSso(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(Optional.of(customizations));
		when(customizationsAssetsRepository.findByCustomizations(customizations)).thenReturn(customizationsAssetsList);
		when(assetHierarchyFilterService.getDisplayNameMap(assetHierarchy)).thenReturn(displayNameMap);
		when(assetHierarchyFilterService.getImageSrcMap(assetHierarchy)).thenReturn(imageSrcMap);
		when(assetHierarchyFilterService.getIdMap(assetHierarchy)).thenReturn(idMap);
		when(assetHierarchyFilterService.getCustomerNameMap(assetHierarchy, projectsList)).thenReturn(customerNameMap);
		when(customizationsWidgetsRepository.findAllByCustomizationsAndAndWidgetsWidgetTypesDescription(customizations,
				DashboardConstants.KPI)).thenReturn(customizationsWidgetsList);
		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsStatusesStatusIndicator(
				JSONUtilConstants.LEVEL_PROJECTS, customizationsWidgets.getWidgets().getId(),
				DashboardConstants.ACTIVE_STATUS_INDICATOR)).thenReturn(Optional.of(widgetsLevel));
		LayoutResponseDTO actualResponseDTO = dashboardService.getUserDashboardLayout(mockHttpServletRequest,
				layoutRequestDto);
		assertNotNull(actualResponseDTO);
		assertEquals(users.getCompanies().getName(), actualResponseDTO.getHeader().getCustomerName());
		assertEquals(users.getCompanies().getIconImages().getId(), actualResponseDTO.getHeader().getCompanyLogo());
		assertEquals(TestConstants.PR_TEST1, actualResponseDTO.getSelectedVid());
		assertEquals(idMap.get(TestConstants.PR_TEST1), actualResponseDTO.getSelectedId());
		assertEquals(displayNameMap.get(TestConstants.PR_TEST1), actualResponseDTO.getSelectedTitle());
		assertEquals(null, actualResponseDTO.getParentVid());
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, actualResponseDTO.getLevel());
		assertEquals(1, actualResponseDTO.getAssets().size());
		assertEquals("/resources/project1.png", actualResponseDTO.getAssets().get(0).getImageSrc());
		assertEquals("Project1 for Testing", actualResponseDTO.getAssets().get(0).getCustomerName());
		assertEquals(1, actualResponseDTO.getKpis().size());
		assertEquals(1, actualResponseDTO.getKpis().get(0).getStaticImageId());
		assertEquals("Case Management", actualResponseDTO.getKpis().get(0).getTitle());
		assertEquals(0, actualResponseDTO.getSummary().size());
		assertEquals(customizations.getId(), actualResponseDTO.getCustomizationId());
	}

	@Test
	@DisplayName("GetUserLayout - Throw Asset is not Accessible")
	void testUserLayoutThrowsAcessDeniedException() throws Exception {
		assetsMap.put(JSONUtilConstants.MATCHFOUND, false);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(users);
		when(assetService.getDefaultAsset(mockHttpServletRequest))
				.thenReturn(new UsersAssetsResponseDTO(userDefaultAssets.getDefaultVid(), null, null));
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, layoutRequestDto.getVid(), true))
				.thenReturn(assetsMap);
		AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
			dashboardService.getUserDashboardLayout(mockHttpServletRequest, layoutRequestDto);
		});
		assertEquals("Asset is not Accessible", exception.getMessage());
	}

	@Test
	@DisplayName("GetUserLayout - Throw Not Found Exception")
	void testUserLayoutThrowsNotFoundException() throws Exception {
		layoutRequestDto.setShowSiblings(true);
		List<String> lineupList = new ArrayList<>();
		lineupList.add(TestConstants.LN_TEST1);
		Map<String, Object> assetsMap1 = new HashMap<>();
		assetsMap1.put(JSONUtilConstants.LEVEL_LINEUPS, lineupList);
		assetsMap1.put(JSONUtilConstants.MATCHFOUND, false);
		assetsMap1.put(JSONUtilConstants.PREVIOUSLEVEL, JSONUtilConstants.LEVEL_LINEUPS);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(users);
		when(assetService.getDefaultAsset(mockHttpServletRequest)).thenReturn(new UsersAssetsResponseDTO());
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, layoutRequestDto.getVid(), true))
				.thenReturn(assetsMap1);
		NotFoundException exception = assertThrows(NotFoundException.class, () -> {
			dashboardService.getUserDashboardLayout(mockHttpServletRequest, layoutRequestDto);
		});
		assertEquals(ExceptionConstants.CHILDREN_NOT_FOUND, exception.getMessage());
	}

	@Test
	@DisplayName("GetCustomizationsForModify - defaultCustomizations Not Null")
	void testGetCustomizationsForModify() {
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(users);
		when(customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(true, users.getSso(), JSONUtilConstants.LEVEL_PROJECTS))
		.thenReturn(Optional.of(customizations));
		Customizations actualCustomizations=dashboardService.getCustomizationsForModify(mockHttpServletRequest, null, JSONUtilConstants.LEVEL_PROJECTS);
		assertEquals(customizations.getAssetLevel(), actualCustomizations.getAssetLevel());
		assertEquals(customizations.getDateRange(), actualCustomizations.getDateRange());
		assertEquals(customizations.getDefaultVid(), actualCustomizations.getDefaultVid());
		assertEquals(customizations.getId(), actualCustomizations.getId());
		assertEquals(customizations.getPersonas().getId(), actualCustomizations.getPersonas().getId());
		assertEquals(customizations.getUsers().getSso(), actualCustomizations.getUsers().getSso());
	}

	@Test
	@DisplayName("GetCustomizationsForModify - defaultCustomizations Null And UserCustomizations Null")
	void testGetCustomizationsForModifyUserCustomizationsNotNull() {
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(users);
		when(customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(true, users.getSso(), JSONUtilConstants.LEVEL_PROJECTS))
		.thenReturn(Optional.empty()).thenReturn(Optional.of(customizations));
		when(customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(false, users.getSso(), JSONUtilConstants.LEVEL_PROJECTS))
		.thenReturn(Optional.of(customizations));
		Customizations actualCustomizations=dashboardService.getCustomizationsForModify(mockHttpServletRequest, null, JSONUtilConstants.LEVEL_PROJECTS);
		assertEquals(customizations.getAssetLevel(), actualCustomizations.getAssetLevel());
		assertEquals(customizations.getDateRange(), actualCustomizations.getDateRange());
		assertEquals(customizations.getDefaultVid(), actualCustomizations.getDefaultVid());
		assertEquals(customizations.getId(), actualCustomizations.getId());
		assertEquals(customizations.getPersonas().getId(), actualCustomizations.getPersonas().getId());
		assertEquals(customizations.getUsers().getSso(), actualCustomizations.getUsers().getSso());
	}

	@Test
	@DisplayName("GetCustomizationsForModify - Default,User and Persona Customizations Are Null")
	void testGetCustomizationsForModifyPersonaCustomizationsAreNull() {
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(users);
		when(customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(true, users.getSso(), JSONUtilConstants.LEVEL_PROJECTS))
		.thenReturn(Optional.empty());
		when(customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(false, users.getSso(), JSONUtilConstants.LEVEL_PROJECTS))
		.thenReturn(Optional.empty());
		when(userSettingsService.getDefaultSettings()).thenReturn(settings);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
						JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(null);
		NotFoundException exception = assertThrows(NotFoundException.class, () -> {
			dashboardService.getCustomizationsForModify(mockHttpServletRequest, 16, JSONUtilConstants.LEVEL_PROJECTS);
		});
		assertEquals(ExceptionConstants.CUSTOMIZATION_NOT_FOUND, exception.getMessage());
	}

	@Test
	@DisplayName("GetCustomizationsForModify - Default,User Are Null and Persona Customizations Not Null")
	void testGetCustomizationsForModifyPersonaCustomizationsNotNull() {
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(users);
		when(customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(true, users.getSso(), JSONUtilConstants.LEVEL_PROJECTS))
		.thenReturn(Optional.empty());
		when(customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(false, users.getSso(), JSONUtilConstants.LEVEL_PROJECTS))
		.thenReturn(Optional.empty());
		when(userSettingsService.getDefaultSettings()).thenReturn(settings);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
						JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(customizations);
		when(customizationsWidgetsRepository.findAllByCustomizations(customizations)).thenReturn(customizationsWidgetsList);
		when(customizationsRepository.save(any(Customizations.class))).thenReturn(customizations);
		when(customizationsWidgetsRepository.saveAll(customizationsWidgetsList)).thenReturn(customizationsWidgetsList);
		Customizations actualCustomizations=	dashboardService.getCustomizationsForModify(mockHttpServletRequest, 16, JSONUtilConstants.LEVEL_PROJECTS);
		assertEquals(customizations.getAssetLevel(),actualCustomizations.getAssetLevel());
	}

	@Test
	@DisplayName("SaveCustomizationAssets")
	void testSaveCustomizationAssets() {
		when(customizationsAssetsRepository.saveAll(customizationsAssetsList)).thenReturn(customizationsAssetsList);
		dashboardService.saveCustomizationsAssets(customizationsAssetsList, customizations);
		verify(customizationsAssetsRepository, times(1)).saveAll(anyList());
	}
}