package com.bh.cp.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.constants.ExceptionConstants;
import com.bh.cp.dashboard.constants.JSONUtilConstants;
import com.bh.cp.dashboard.dto.request.AddAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.AssetOrderRequestDTO;
import com.bh.cp.dashboard.dto.request.ChangeAssetNameRequestDTO;
import com.bh.cp.dashboard.dto.request.DeleteAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.GetAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.UsersDefaultAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.UsersFavoriteAssetsRequestDTO;
import com.bh.cp.dashboard.dto.response.AddAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.ChangeAssetNameResponseDTO;
import com.bh.cp.dashboard.dto.response.DeleteAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.UsersAssetsResponseDTO;
import com.bh.cp.dashboard.entity.CommonBlobs;
import com.bh.cp.dashboard.entity.Companies;
import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.CustomizationsAssets;
import com.bh.cp.dashboard.entity.CustomizationsWidgets;
import com.bh.cp.dashboard.entity.Personas;
import com.bh.cp.dashboard.entity.Statuses;
import com.bh.cp.dashboard.entity.Users;
import com.bh.cp.dashboard.entity.UsersDefaultAssets;
import com.bh.cp.dashboard.entity.UsersFavoriteAssets;
import com.bh.cp.dashboard.entity.Widgets;
import com.bh.cp.dashboard.repository.CustomizationsAssetsRepository;
import com.bh.cp.dashboard.repository.CustomizationsRepository;
import com.bh.cp.dashboard.repository.CustomizationsWidgetsRepository;
import com.bh.cp.dashboard.repository.UsersDefaultAssetsRepository;
import com.bh.cp.dashboard.repository.UsersFavoriteAssetsRepository;
import com.bh.cp.dashboard.repository.UsersRepository;
import com.bh.cp.dashboard.service.AssetHierarchyFilterService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.ws.rs.NotFoundException;

class AssetServiceImplTest {

	@InjectMocks
	private AssetServiceImpl assetService;

	@Value("${keycloak-wrapper-service-uri}")
	private String keycloakWrapperUri;

	@Value("${bh.asset.hierarchy.url}")
	private String assetHierarchyUrl;

	@Value("${bh.cert.filename}")
	private String bhCertFileName;

	@Mock
	private DashboardServiceImpl dashboardServiceImpl;

	@Mock
	private AssetHierarchyFilterService assetHierarchyFilterService;

	@Mock
	private UMSClientServiceImpl umsClientService;

	@Mock
	private UserSettingsServiceImpl userSettingsService;

	@Mock
	private CustomizationsWidgetsRepository customizationWidgetsRepo;

	@Mock
	private CustomizationsAssetsRepository customizationsAssetsRepository;

	@Mock
	private CustomizationsRepository customizationsRepository;

	@Mock
	private CustomizationsWidgetsRepository customizationsWidgetsRepository;

	@Mock
	private UsersRepository usersRepository;

	@Mock
	private UsersFavoriteAssetsRepository usersFavoriteAssetsRepository;

	@Mock
	private UsersDefaultAssetsRepository usersDefaultAssetsRepository;

	private MockHttpServletRequest mockHttpServletRequest;

	private Map<String, Claim> claims;

	private Companies company;

	private Users user;

	private Personas persona;

	private Customizations customization;

	private CustomizationsAssets customizationsAssets1;

	private ChangeAssetNameResponseDTO changeAssetResponse;

	private Widgets widgets;

	private CommonBlobs commonBlobs;

	private Statuses statuses;

	private CustomizationsWidgets customizationsWidgets;

	private Customizations customizations;

	private Personas personas;

	private Users users;

	private Companies companies;

	private List<CustomizationsWidgets> customizationsWidgetsList;

	private CustomizationsAssets customizationsAssets;

	private List<CustomizationsAssets> customizationsAssetsList;
	private Map<String, Object> accessibleVidsAndLevel;
	private List<Map<String, Object>> assetHierarchy;
	private List<String> plantsList;
	private List<String> projectsList;
	private ChangeAssetNameRequestDTO changeRequestDto;
	private DeleteAssetRequestDTO deleteRequestDto;

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
		projectsList = new ArrayList<>();
		projectsList.add("PR_TEST2");
		plantsList = new ArrayList<>();
		plantsList.add(TestConstants.PL_TEST1);
		accessibleVidsAndLevel = new HashMap<>();
		accessibleVidsAndLevel.put(DashboardConstants.LEVEL, JSONUtilConstants.LEVEL_PLANTS);
		accessibleVidsAndLevel.put(JSONUtilConstants.CURRENTLEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		accessibleVidsAndLevel.put(JSONUtilConstants.NEXTLEVEL, JSONUtilConstants.LEVEL_PLANTS);
		accessibleVidsAndLevel.put(JSONUtilConstants.SEARCHVID, TestConstants.PR_TEST1);
		accessibleVidsAndLevel.put(JSONUtilConstants.MATCHFOUND, true);
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL_PLANTS, plantsList);
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);

		changeRequestDto = new ChangeAssetNameRequestDTO();
		changeRequestDto.setAssetName("Test Project1");
		changeRequestDto.setLevel(JSONUtilConstants.LEVEL_PROJECTS);
		changeRequestDto.setCustomizationId(1);
		changeRequestDto.setVid(TestConstants.PR_TEST1);

		changeAssetResponse = new ChangeAssetNameResponseDTO();
		changeAssetResponse.setAssetName("Test Project1");
		changeAssetResponse.setCustomizationId(1);
		changeAssetResponse.setLevel(JSONUtilConstants.LEVEL_PROJECTS);
		changeAssetResponse.setVid(TestConstants.PR_TEST1);

		deleteRequestDto = new DeleteAssetRequestDTO();
		deleteRequestDto.setVids(projectsList);

		personas = new Personas();
		personas.setId(1);
		personas.setStatuses(statuses);
		personas.setDescription("operation");

		companies = new Companies();
		companies.setId(1);
		companies.setName("bh");
		companies.setIconImages(commonBlobs);

		users = new Users();
		users.setId(1);
		users.setEmail("test@gmail.com");
		users.setCompanies(companies);
		users.setPersonas(personas);
		users.setSso("testUser");
		users.setUsername("test");

		customizations = new Customizations();
		customizations.setId(1);
		customizations.setUsers(users);
		customizations.setDateRange("2023-05-01");
		customizations.setDefault(false);
		customizations.setAssetLevel(JSONUtilConstants.LEVEL_PROJECTS);
		customizations.setPersonas(personas);
		customizations.setUsers(users);

		customizationsWidgets = new CustomizationsWidgets();
		customizationsWidgets.setId(1);
		customizationsWidgets.setOrderNumber(1);
		customizationsWidgets.setWidgets(widgets);
		customizationsWidgets.setCustomizations(customizations);
		customizationsWidgetsList = new ArrayList<>();
		customizationsWidgetsList.add(customizationsWidgets);
		customizationsAssets = new CustomizationsAssets();
		customizationsAssets.setId(1);
		customizationsAssets.setAssetName("Project Test2");
		customizationsAssets.setCustomizations(customizations);
		customizationsAssets.setOrderNumber(1);
		customizationsAssets.setVid("PR_TEST1");
		customizationsAssetsList = new ArrayList<>();
		customizationsAssetsList.add(customizationsAssets);

		company = new Companies();
		company.setId(1001);
		company.setName("test company");
		company.setIconImages(commonBlobs);

		persona = new Personas();
		persona.setId(4001);
		persona.setDescription("test engineer");

		user = new Users();
		user.setId(2001);
		user.setEmail("test@test.com");
		user.setSso("user1");
		user.setCompanies(company);
		user.setPersonas(persona);

		customization = new Customizations();
		customization.setId(1);
		customization.setPersonas(persona);
		customization.setDefault(false);
		customization.setDateRange("3M");
		customization.setAssetLevel(JSONUtilConstants.LEVEL_PROJECTS);

		customizationsAssets1 = new CustomizationsAssets();
		customizationsAssets1.setCustomizations(customization);
		customizationsAssets1.setVid("PR_TEST2");
		customizationsAssets1.setOrderNumber(1);
		customizationsAssetsList.add(customizationsAssets1);

		Claim userClaim = Mockito.mock(Claim.class);
		claims = new HashMap<String, Claim>();
		claims.put("preferred_username", userClaim);

		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
	}

	@Test
	@DisplayName("FetchAssets- Get All Assets with Level in Request Body")
	void testFetchAssets_Scenario1() throws NotFoundException, IOException {
		GetAssetRequestDTO requestDto = new GetAssetRequestDTO();
		requestDto.setParentVid(TestConstants.PR_TEST1);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, requestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(dashboardServiceImpl.getCustomizationsForView(user, JSONUtilConstants.LEVEL_PLANTS))
				.thenReturn(customization);
		when(dashboardServiceImpl.getCustomizedAssets(customization)).thenReturn(customizationsAssetsList);

		AddAssetResponseDTO actualResponse = assetService.fetchAssets(mockHttpServletRequest, requestDto);
		assertEquals("plants", actualResponse.getLevel());
		assertEquals(customization.getId(), actualResponse.getCustomizationId());
		verify(dashboardServiceImpl, times(1)).setAssetResponse(actualResponse, assetHierarchy, customization,
				customizationsAssetsList, plantsList, true);
	}

	@Test
	@DisplayName("AddAssets")
	void testAddAssets() throws Exception {
		AssetOrderRequestDTO assets = new AssetOrderRequestDTO();
		assets.setVid("PR_TEST2");
		assets.setOrderNumber(2);
		List<AssetOrderRequestDTO> assetsList = new ArrayList<>();
		assetsList.add(assets);
		AddAssetRequestDTO requestDto = new AddAssetRequestDTO();
		requestDto.setParentVid(TestConstants.PR_TEST1);
		requestDto.setCustomizationId(1);
		requestDto.setAssets(assetsList);
		accessibleVidsAndLevel.put(DashboardConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, requestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(dashboardServiceImpl.getCustomizationsForModify(mockHttpServletRequest, requestDto.getCustomizationId(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(customization);
		when(customizationsAssetsRepository.findByCustomizations(any(Customizations.class)))
				.thenReturn(customizationsAssetsList);
		when(customizationsAssetsRepository.saveAll(customizationsAssetsList)).thenReturn(customizationsAssetsList);
		AddAssetResponseDTO responseDto = assetService.addAssets(mockHttpServletRequest, requestDto);
		assertNotNull(responseDto);
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, responseDto.getLevel());
		assertEquals(customization.getId(), responseDto.getCustomizationId());
		verify(dashboardServiceImpl, times(1)).setAssetResponse(any(AddAssetResponseDTO.class), anyList(),
				any(Customizations.class), anyList(), anyList(), any(Boolean.class));
	}

	@Test
	@DisplayName("AddAssets - CannoDeleteAllAssetsException")
	void testAddAssets_CannotDeleteException() throws Exception {
		AssetOrderRequestDTO assets = new AssetOrderRequestDTO();
		assets.setVid("PR_TEST3");
		assets.setOrderNumber(2);
		List<AssetOrderRequestDTO> assetsList = new ArrayList<>();
		assetsList.add(assets);
		AddAssetRequestDTO requestDto = new AddAssetRequestDTO();
		requestDto.setParentVid(TestConstants.PR_TEST1);
		requestDto.setCustomizationId(1);
		requestDto.setAssets(assetsList);
		List<String> projectsList = new ArrayList<>();
		projectsList.add("PR_TEST2");

		accessibleVidsAndLevel.put(DashboardConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, requestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			assetService.addAssets(mockHttpServletRequest, requestDto);
		});
		assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getStatusCode());
	}

	@Test
	@DisplayName("GetAllFavoriteAssets")
	void testGetAllfavoriteAssets() throws Exception {
		UsersFavoriteAssets favoriteAssets = new UsersFavoriteAssets();
		favoriteAssets.setVid(TestConstants.PR_TEST1);
		favoriteAssets.setUsers(users);
		List<UsersFavoriteAssets> favoriteAssetsList = new ArrayList<>();
		favoriteAssetsList.add(favoriteAssets);
		Map<String, String> displayNameMap = new HashMap<>();
		displayNameMap.put(TestConstants.PR_TEST1, TestConstants.TEST1);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(assetHierarchyFilterService.getDisplayNameMap(assetHierarchy)).thenReturn(displayNameMap);
		when(usersFavoriteAssetsRepository.findAllByUsers(any(Users.class))).thenReturn(favoriteAssetsList);
		List<UsersAssetsResponseDTO> responseDtoList = assetService.getAllFavoriteAssets(mockHttpServletRequest);
		assertEquals(1, responseDtoList.size());
		assertEquals(TestConstants.PR_TEST1, responseDtoList.get(0).getVid());
		assertEquals(TestConstants.TEST1, responseDtoList.get(0).getDisplayName());
		assertEquals("testUser", responseDtoList.get(0).getSso());
	}

	@Test
	@DisplayName("StoreFavoriteAssets - Asset Is not Accessible")
	void testStoreFavoriteAssets() throws AccessDeniedException, JsonProcessingException {
		UsersFavoriteAssetsRequestDTO requestDto = new UsersFavoriteAssetsRequestDTO();
		accessibleVidsAndLevel.put(JSONUtilConstants.MATCHFOUND, false);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, null, true)).thenReturn(accessibleVidsAndLevel);
		AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
			assetService.storeFavoriteAssets(mockHttpServletRequest, requestDto);
		});
		assertEquals("Asset is not Accessible", exception.getMessage());
	}

	@Test
	@DisplayName("StoreFavoriteAssets - MarkFavorite As True And Vid is present in favorite Asset")
	void testStoreFavoriteAssetsVidExistsAsFavoriteAsset() throws Exception {
		UsersFavoriteAssets favoriteAssets = new UsersFavoriteAssets();
		favoriteAssets.setVid(TestConstants.PR_TEST1);
		favoriteAssets.setUsers(users);
		UsersFavoriteAssetsRequestDTO requestDto = new UsersFavoriteAssetsRequestDTO();
		requestDto.setMarkAsFavorite(true);
		requestDto.setVid(TestConstants.PR_TEST1);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, requestDto.getVid(), true))
				.thenReturn(accessibleVidsAndLevel);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(usersFavoriteAssetsRepository.findByUsersAndVid(any(Users.class), anyString())).thenReturn(favoriteAssets);
		UsersAssetsResponseDTO responseDTO = assetService.storeFavoriteAssets(mockHttpServletRequest, requestDto);
		assertEquals("Already added vid in Favorite Assets", responseDTO.getStatus());
		assertEquals(TestConstants.PR_TEST1, responseDTO.getVid());
	}

	@Test
	@DisplayName("StoreFavoriteAssets - Mark Favorite Is True And Adding Vid to Favorite Asset")
	void testStoreFavoriteAssetsVidNotExistsAsFavoriteAsset() throws Exception {
		UsersFavoriteAssets favoriteAssets = new UsersFavoriteAssets();
		favoriteAssets.setVid(TestConstants.PR_TEST1);
		favoriteAssets.setUsers(users);
		UsersFavoriteAssetsRequestDTO requestDto = new UsersFavoriteAssetsRequestDTO();
		requestDto.setMarkAsFavorite(true);
		requestDto.setVid(TestConstants.PR_TEST1);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, requestDto.getVid(), true))
				.thenReturn(accessibleVidsAndLevel);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(usersFavoriteAssetsRepository.findByUsersAndVid(any(Users.class), anyString())).thenReturn(null);
		when(usersFavoriteAssetsRepository.save(any(UsersFavoriteAssets.class))).thenReturn(favoriteAssets);
		UsersAssetsResponseDTO responseDTO = assetService.storeFavoriteAssets(mockHttpServletRequest, requestDto);
		assertEquals("Added vid to Favorite Assets", responseDTO.getStatus());
		assertEquals(TestConstants.PR_TEST1, responseDTO.getVid());
	}

	@Test
	@DisplayName("StoreFavoriteAssets - MarkFavorite As False")
	void testStoreFavoriteAssetsMarkFavoriteAsFalse() throws Exception {
		UsersFavoriteAssets favoriteAssets = new UsersFavoriteAssets();
		favoriteAssets.setVid(TestConstants.PR_TEST1);
		favoriteAssets.setUsers(users);
		UsersFavoriteAssetsRequestDTO requestDto = new UsersFavoriteAssetsRequestDTO();
		requestDto.setMarkAsFavorite(false);
		requestDto.setVid(TestConstants.PR_TEST1);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, requestDto.getVid(), true))
				.thenReturn(accessibleVidsAndLevel);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(usersFavoriteAssetsRepository.findByUsersAndVid(any(Users.class), anyString())).thenReturn(favoriteAssets);
		UsersAssetsResponseDTO responseDTO = assetService.storeFavoriteAssets(mockHttpServletRequest, requestDto);
		assertEquals("Removed vid from Favorite Assets", responseDTO.getStatus());
		assertEquals(TestConstants.PR_TEST1, responseDTO.getVid());
	}

	@Test
	@DisplayName("GetDefaultAsset - UserDefaultAsset Is Null")
	void testGetDefaultAsset() throws Exception{
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(assetHierarchyFilterService.getDisplayNameMap(assetHierarchy)).thenReturn(null);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(usersDefaultAssetsRepository.findByUsersSso(user.getSso())).thenReturn(Optional.empty());
		UsersAssetsResponseDTO responseDto=assetService.getDefaultAsset(mockHttpServletRequest);
		assertEquals(null, responseDto.getVid());
	}

	@Test
	@DisplayName("GetDefaultAsset - UserDefaultAsset Is Not Null")
	void testGetDefaultAssetUserDefaultAssetFound() throws Exception {
		Map<String, String> displayNameMap = new HashMap<>();
		displayNameMap.put(TestConstants.PR_TEST1, TestConstants.TEST1);
		UsersDefaultAssets userDefaultAssets = new UsersDefaultAssets();
		userDefaultAssets.setDefaultVid(TestConstants.PR_TEST1);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(assetHierarchyFilterService.getDisplayNameMap(assetHierarchy)).thenReturn(displayNameMap);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(usersDefaultAssetsRepository.findByUsersSso(user.getSso())).thenReturn(Optional.of(userDefaultAssets));
		UsersAssetsResponseDTO responseDto = assetService.getDefaultAsset(mockHttpServletRequest);
		assertEquals(TestConstants.PR_TEST1, responseDto.getVid());
	}

	@Test
	@DisplayName("SetDefaultAsset - DefaultAsset Found in DB")
	void testDefaultAsset() throws Exception {
		UsersDefaultAssetRequestDTO requestDto = new UsersDefaultAssetRequestDTO();
		requestDto.setDefaultVid(TestConstants.PR_TEST1);
		UsersDefaultAssets userDefaultAssets = new UsersDefaultAssets();
		userDefaultAssets.setDefaultVid(TestConstants.PR_TEST1);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(usersDefaultAssetsRepository.findByUsersSso(user.getSso())).thenReturn(Optional.of(userDefaultAssets));
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, requestDto.getDefaultVid(), true))
				.thenReturn(accessibleVidsAndLevel);
		userDefaultAssets.setUsers(users);
		when(usersDefaultAssetsRepository.save(userDefaultAssets)).thenReturn(userDefaultAssets);
		UsersAssetsResponseDTO responseDto = assetService.setDefaultAsset(mockHttpServletRequest, requestDto);
		assertNotNull(responseDto);
		assertEquals(TestConstants.PR_TEST1, responseDto.getVid());
		assertEquals("Default Asset added", responseDto.getStatus());
	}

	@Test
	@DisplayName("SetDefaultAsset - Match Found Is False")
	void testDefaultAssetMatchFoundIsFalse() throws Exception {
		UsersDefaultAssetRequestDTO requestDto = new UsersDefaultAssetRequestDTO();
		requestDto.setDefaultVid(TestConstants.PR_TEST1);
		UsersDefaultAssets userDefaultAssets = new UsersDefaultAssets();
		userDefaultAssets.setDefaultVid(TestConstants.PR_TEST1);
		accessibleVidsAndLevel.put(JSONUtilConstants.MATCHFOUND, false);
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(usersDefaultAssetsRepository.findByUsersSso(user.getSso())).thenReturn(Optional.of(userDefaultAssets));
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(assetHierarchyFilterService.getAssetsMap(assetHierarchy, requestDto.getDefaultVid(), true))
				.thenReturn(accessibleVidsAndLevel);
		userDefaultAssets.setUsers(users);
		when(usersDefaultAssetsRepository.save(userDefaultAssets)).thenReturn(userDefaultAssets);
		AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
			assetService.setDefaultAsset(mockHttpServletRequest, requestDto);
		});
		assertEquals("Asset is not Accessible", exception.getMessage());
	}

	@Test
	@DisplayName("SetDefaultAsset - DefaultVid Is Null")
	void testDefaultAssetIsNull() throws Exception {
		UsersDefaultAssetRequestDTO requestDto = new UsersDefaultAssetRequestDTO();
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(usersDefaultAssetsRepository.findByUsersSso(user.getSso()))
				.thenReturn(Optional.of(new UsersDefaultAssets()));
		UsersAssetsResponseDTO responseDto = assetService.setDefaultAsset(mockHttpServletRequest, requestDto);
		assertEquals("Default Asset removed", responseDto.getStatus());
	}

	@Test
	@DisplayName("AssetNameCustomization - Customization is NotFound")
	void testChangeAssetNameCustomizationNotFound() throws Exception {
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, changeRequestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(customizationsRepository.findByIdAndAssetLevel(changeRequestDto.getCustomizationId(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(Optional.empty());
		NotFoundException exception = assertThrows(NotFoundException.class, () -> {
			assetService.assetNameCustomization(changeRequestDto, mockHttpServletRequest);
		});
		assertEquals(ExceptionConstants.CUSTOMIZATION_NOT_FOUND, exception.getMessage());
	}

	@Test
	@DisplayName("AssetNameCustomization - FindByUserSso and PersonaCustomization Is Null")
	void testChangeAssetNameFindByUserSsoIsNull() throws Exception {
		customizations.setId(changeRequestDto.getCustomizationId());
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, changeRequestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(customizationsRepository.findByIdAndAssetLevel(anyInt(), anyString()))
				.thenReturn(Optional.of(customizations));
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), JSONUtilConstants.LEVEL_PROJECTS))
				.thenReturn(null);
		NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
			assetService.assetNameCustomization(changeRequestDto, mockHttpServletRequest);
		});
		assertEquals(ExceptionConstants.CUSTOMIZATION_NOT_VALID, exception.getMessage());
	}

	@Test
	@DisplayName("AssetNameCustomization - FindByUserSso Is Null And PersonaCustomization Null")
	void testChangeAssetNamePersonaCustomizationNotNull() throws Exception {
		projectsList = new ArrayList<>();
		projectsList.add(TestConstants.PR_TEST1);
		customizations.setId(changeRequestDto.getCustomizationId());
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, changeRequestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(customizationsRepository.findByIdAndAssetLevel(changeRequestDto.getCustomizationId(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(Optional.of(customizations));
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), JSONUtilConstants.LEVEL_PROJECTS))
				.thenReturn(null);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasIdAndAssetLevel(anyInt(), anyString()))
				.thenReturn(customizations);
		when(customizationsRepository.save(any(Customizations.class))).thenReturn(customization);
		when(customizationsWidgetsRepository.findByCustomizationsId(customization.getId()))
				.thenReturn(customizationsWidgetsList);
		when(customizationsAssetsRepository.save(any(CustomizationsAssets.class))).thenReturn(customizationsAssets);
		ChangeAssetNameResponseDTO actualResponseDto = assetService.assetNameCustomization(changeRequestDto,
				mockHttpServletRequest);
		assertEquals(changeAssetResponse.getAssetName(), actualResponseDto.getAssetName());
		assertEquals(changeAssetResponse.getCustomizationId(), actualResponseDto.getCustomizationId());
		assertEquals(changeAssetResponse.getLevel(), actualResponseDto.getLevel());
		assertEquals(changeAssetResponse.getVid(), actualResponseDto.getVid());
	}

	@Test
	@DisplayName("AssetNameCustomization - FindByUserSso Is Not Null And PersonaCustomization Null")
	void testChangeAssetNameFindByUserSsoIsNotNull() throws Exception {
		projectsList = new ArrayList<>();
		projectsList.add(TestConstants.PR_TEST1);
		customizations.setId(changeRequestDto.getCustomizationId());
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);
		customizationsAssets.setVid(TestConstants.PR_TEST1);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, changeRequestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(customizationsRepository.findByIdAndAssetLevel(changeRequestDto.getCustomizationId(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(Optional.of(customizations));
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(anyString(), anyString())).thenReturn(customizations);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasIdAndAssetLevel(anyInt(), anyString()))
				.thenReturn(customizations);
		when(customizationsAssetsRepository.findByCustomizationsIdAndVid(customizations.getId(),
				changeRequestDto.getVid())).thenReturn(customizationsAssets);
		when(customizationsAssetsRepository.save(customizationsAssets)).thenReturn(customizationsAssets);
		ChangeAssetNameResponseDTO actualResponseDto = assetService.assetNameCustomization(changeRequestDto,
				mockHttpServletRequest);
		assertEquals(changeAssetResponse.getAssetName(), actualResponseDto.getAssetName());
		assertEquals(changeAssetResponse.getCustomizationId(), actualResponseDto.getCustomizationId());
		assertEquals(changeAssetResponse.getLevel(), actualResponseDto.getLevel());
		assertEquals(changeAssetResponse.getVid(), actualResponseDto.getVid());
	}

	@Test
	@DisplayName("AssetNameCustomization - FindByUserSso Is Not Null,PersonaCustomization  And CustomizationAsset Is Null")
	void testChangeAssetNameCustomizationAssetIsNull() throws Exception {
		customizations.setId(changeRequestDto.getCustomizationId());
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, changeRequestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(customizationsRepository.findByIdAndAssetLevel(changeRequestDto.getCustomizationId(),
				JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(Optional.of(customizations));
		when(userSettingsService.extractSsoAndReturnUser(mockHttpServletRequest)).thenReturn(user);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(anyString(), anyString())).thenReturn(customizations);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasIdAndAssetLevel(anyInt(), anyString()))
				.thenReturn(customizations);
		when(customizationsAssetsRepository.findByCustomizationsIdAndVid(customizations.getId(),
				changeRequestDto.getVid())).thenReturn(null);
		when(customizationsAssetsRepository.save(customizationsAssets)).thenReturn(customizationsAssets);
		ChangeAssetNameResponseDTO actualResponseDto = assetService.assetNameCustomization(changeRequestDto,
				mockHttpServletRequest);
		assertEquals(changeAssetResponse.getAssetName(), actualResponseDto.getAssetName());
		assertEquals(changeAssetResponse.getCustomizationId(), actualResponseDto.getCustomizationId());
		assertEquals(changeAssetResponse.getLevel(), actualResponseDto.getLevel());
		assertEquals(changeAssetResponse.getVid(), actualResponseDto.getVid());
	}

	@Test
	@DisplayName("DeleteAssets - User Trying to Delete All Assets")
	void testDeleteAssetsAll() throws Exception {
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, deleteRequestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			assetService.deleteAssets(mockHttpServletRequest, deleteRequestDto);
		});
		assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getStatusCode());
	}

	@Test
	@DisplayName("DeleteAssets")
	void testDeleteAssets() throws Exception {
		projectsList = new ArrayList<>();
		projectsList.add("PR_TEST2");
		projectsList.add("PR_TEST3");
		projectsList.add("PR_TEST1");
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, deleteRequestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(dashboardServiceImpl.getCustomizationsForModify(mockHttpServletRequest,
				deleteRequestDto.getCustomizationId(), JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(customization);
		when(customizationsAssetsRepository.findByCustomizationsAndVidIn(customization, projectsList))
				.thenReturn(customizationsAssetsList);
		DeleteAssetResponseDTO actualResponseDto = assetService.deleteAssets(mockHttpServletRequest, deleteRequestDto);
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, actualResponseDto.getLevel());
		assertEquals(1, actualResponseDto.getCustomizationId());
		verify(dashboardServiceImpl, times(1)).setAssetResponse(any(DeleteAssetResponseDTO.class), anyList(),
				any(Customizations.class), anyList(), anyList(), any(Boolean.class));
	}

	@Test
	@DisplayName("DeleteAssets - Existing Customizations Is Empty")
	void testDeleteAssetsExistingCustomizationsEmpty() throws Exception {
		projectsList = new ArrayList<>();
		projectsList.add("PR_TEST2");
		projectsList.add("PR_TEST3");
		projectsList.add("PR_TEST1");
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, deleteRequestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(dashboardServiceImpl.getCustomizationsForModify(mockHttpServletRequest,
				deleteRequestDto.getCustomizationId(), JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(customization);
		when(customizationsAssetsRepository.findByCustomizationsAndVidIn(customization, projectsList))
				.thenReturn(new ArrayList<>());
		when(customizationsAssetsRepository.save(any(CustomizationsAssets.class))).thenReturn(customizationsAssets);
		DeleteAssetResponseDTO actualResponseDto = assetService.deleteAssets(mockHttpServletRequest, deleteRequestDto);
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, actualResponseDto.getLevel());
		assertEquals(1, actualResponseDto.getCustomizationId());
		verify(dashboardServiceImpl, times(1)).setAssetResponse(any(DeleteAssetResponseDTO.class), anyList(),
				any(Customizations.class), anyList(), anyList(), any(Boolean.class));
	}

	@Test
	@DisplayName("DeleteAssets - RemainingCustomizations Is Empty")
	void testDeleteAssetsRemainingCustomizationsIsEmpty() throws Exception {
		customizationsAssets = new CustomizationsAssets();
		customizationsAssets.setCustomizations(customizations);
		customizationsAssets.setVid("PR_TEST2");
		customizationsAssetsList = new ArrayList<>();
		customizationsAssetsList.add(customizationsAssets);
		projectsList = new ArrayList<>();
		projectsList.add("PR_TEST2");
		projectsList.add("PR_TEST3");
		projectsList.add("PR_TEST1");
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		accessibleVidsAndLevel.put(JSONUtilConstants.LEVEL_PROJECTS, projectsList);
		when(umsClientService.getUserAssetHierarchy(mockHttpServletRequest)).thenReturn(assetHierarchy);
		when(dashboardServiceImpl.getAccessibleVidsAndLevel(assetHierarchy, deleteRequestDto.getParentVid(), false))
				.thenReturn(accessibleVidsAndLevel);
		when(dashboardServiceImpl.getCustomizationsForModify(mockHttpServletRequest,
				deleteRequestDto.getCustomizationId(), JSONUtilConstants.LEVEL_PROJECTS)).thenReturn(customization);
		when(customizationsAssetsRepository.findByCustomizationsAndVidIn(customization, projectsList))
				.thenReturn(customizationsAssetsList);
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			assetService.deleteAssets(mockHttpServletRequest, deleteRequestDto);
		});
		assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getStatusCode());
	}

}