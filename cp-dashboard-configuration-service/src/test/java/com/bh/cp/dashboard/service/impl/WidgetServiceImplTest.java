package com.bh.cp.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.impl.NullClaim;
import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.dashboard.aop.AuditTrailAspect;
import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.dto.request.WidgetOrderRequestDTO;
import com.bh.cp.dashboard.dto.request.WidgetsRequestDTO;
import com.bh.cp.dashboard.dto.response.WidgetsResponseDTO;
import com.bh.cp.dashboard.entity.Cards;
import com.bh.cp.dashboard.entity.CommonBlobs;
import com.bh.cp.dashboard.entity.Companies;
import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.CustomizationsAssets;
import com.bh.cp.dashboard.entity.CustomizationsWidgets;
import com.bh.cp.dashboard.entity.Personas;
import com.bh.cp.dashboard.entity.ServicesDirectory;
import com.bh.cp.dashboard.entity.Settings;
import com.bh.cp.dashboard.entity.Statuses;
import com.bh.cp.dashboard.entity.Users;
import com.bh.cp.dashboard.entity.WidgetTypes;
import com.bh.cp.dashboard.entity.Widgets;
import com.bh.cp.dashboard.entity.WidgetsLevel;
import com.bh.cp.dashboard.exception.ServerUnavailableException;
import com.bh.cp.dashboard.repository.CustomizationsAssetsRepository;
import com.bh.cp.dashboard.repository.CustomizationsRepository;
import com.bh.cp.dashboard.repository.CustomizationsWidgetsRepository;
import com.bh.cp.dashboard.repository.UsersRepository;
import com.bh.cp.dashboard.repository.WidgetsLevelRepository;
import com.bh.cp.dashboard.repository.WidgetsRepository;
import com.bh.cp.dashboard.service.AssetHierarchyFilterService;
import com.bh.cp.dashboard.service.DashboardService;
import com.bh.cp.dashboard.service.UMSClientService;
import com.bh.cp.dashboard.service.UserSettingsService;
import com.bh.cp.dashboard.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.ws.rs.NotFoundException;

class WidgetServiceImplTest {

	@InjectMocks
	private WidgetServiceImpl widgetService;

	@MockBean
	private AuditTrailAspect auditTrailAspect;

	@Mock
	private CustomizationsRepository customizationsRepository;

	@Mock
	private CustomizationsWidgetsRepository customizationsWidgetsRepository;

	@Mock
	private WidgetsLevelRepository widgetsLevelRepository;

	@Mock
	private CustomizationsAssetsRepository customizationsAssetsRepository;

	@Mock
	private WidgetsRepository widgetsRepository;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private UsersRepository usersRepository;

	@Mock
	private RestTemplate restTemplate;

	private WidgetsRequestDTO widgetsDto;

	private WidgetOrderRequestDTO widgetDto;

	private WidgetsResponseDTO dto;

	private List<WidgetOrderRequestDTO> requestList;

	private List<WidgetsResponseDTO> responseList;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	@Mock
	AssetHierarchyFilterService assetHierarchyFilterService;

	private WidgetsLevel level;

	private Widgets widgets;

	private CommonBlobs commonBlobs;

	private WidgetTypes widgetTypes;

	private ServicesDirectory servicesDirectory;

	private Cards cards;

	private Statuses statuses;

	private List<WidgetsLevel> widgetsLevels;

	private CustomizationsWidgets customizationsWidgets;

	private Customizations customizations;

	private Personas personas;

	private Users users;

	private Companies companies;

	private List<CustomizationsWidgets> customizationsWidgetsList;

	private CustomizationsAssets customizationsAssets;

	private List<CustomizationsAssets> customizationsAssetsList;

	private Map<String, List<String>> attributes;

	private Map<String, Claim> claims;

	@Mock
	private DashboardService dashboardService;

	@Mock
	private UMSClientService umsClientService;

	@Mock
	private UserSettingsService userSettingsService;

	List<String> subcribedWidgets = new ArrayList<>();

	private Settings setting;

	List<Integer> enabledWidgets;

	@BeforeEach
	void setup() {

		MockitoAnnotations.openMocks(this);
		enabledWidgets = new ArrayList<>();
		enabledWidgets.add(1);
		ReflectionTestUtils.setField(widgetService, "enabledWidgets", enabledWidgets);

		subcribedWidgets.add("sso");

		servicesDirectory = new ServicesDirectory();

		servicesDirectory.setId(1);

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

		widgetTypes.setDescription("KPI");

		widgetTypes.setStatuses(statuses);

		commonBlobs = new CommonBlobs();

		commonBlobs.setId(1);

		commonBlobs.setMaterial(null);

		widgets = new Widgets();

		widgets.setCards(cards);

		widgets.setServicesDirectory(servicesDirectory);

		widgets.setStatuses(statuses);

		widgets.setWidgetTypes(widgetTypes);

		widgets.setDescription("applicable");

		widgets.setDetailsUri("API");

		widgets.setId(1);

		widgets.setPaidService(true);

		widgets.setTitle("PR_EssoPng");
		widgets.setIdmPrivilege("PR_EssoPng");

		level = new WidgetsLevel();

		level.setId(1);

		level.setAssetLevel("plants");

		level.setIconImage(commonBlobs);

		level.setStaticImage(commonBlobs);
		level.setGreyedImage(commonBlobs);

		level.setWidgets(widgets);

		widgetsLevels = new ArrayList<>();

		widgetsLevels.add(level);

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

		users.setSso("emailid");

		users.setUsername("test");

		customizations = new Customizations();

		customizations.setId(1);
		customizations.setAssetLevel("plants");

		customizations.setDateRange("2023-05-01");

		customizations.setDefault(false);

		customizations.setPersonas(personas);

		customizations.setUsers(users);

		customizationsWidgets = new CustomizationsWidgets();

		customizationsWidgets.setId(1);

		customizationsWidgets.setOrderNumber(1);

		customizationsWidgets.setWidgets(widgets);

		customizationsWidgets.setCustomizations(customizations);

		customizationsWidgetsList = new ArrayList<>();

		customizationsWidgetsList.add(customizationsWidgets);

		widgetsDto = new WidgetsRequestDTO();

		widgetDto = new WidgetOrderRequestDTO();

		widgetsDto.setLevel("plants");

		widgetsDto.setType("kpi");

		requestList = new ArrayList<>();

		widgetDto.setOrderNumber(1);

		widgetDto.setWidgetId(1);
		widgetDto.setChecked(true);
		requestList.add(widgetDto);

		widgetsDto.setWidgets(requestList);

		customizationsAssets = new CustomizationsAssets();

		customizationsAssets.setId(1);

		customizationsAssets.setAssetName("plants");

		customizationsAssets.setCustomizations(customizations);

		customizationsAssets.setOrderNumber(1);

		customizationsAssets.setVid("PL_100");

		customizationsAssetsList = new ArrayList<>();

		customizationsAssetsList.add(customizationsAssets);

		dto = new WidgetsResponseDTO();

		dto.setHasAccess(true);

		dto.setCustomizationId(1);

		dto.setOrderNumber(1);

		dto.setStaticImageId(1);

		dto.setTitle("Test");

		dto.setWidgetId(1);
		dto.setChecked(true);
		dto.setHasAccess(true);

		responseList = new ArrayList<>();

		responseList.add(dto);

		List<String> attributesList = new ArrayList<>();

		attributesList.add("PR_EssoPng");

		attributesList.add("PR_EssoPng1");

		attributes = new HashMap<>();

		attributes.put("widget_title", attributesList);

		claims = new HashMap<String, Claim>();

		claims.put("preferred_username", new NullClaim());
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		setting = new Settings();
		setting.setCompanies(companies);
		setting.setDefaultPersonas(personas);
		setting.setDefaultRole("All Services");
		setting.setMyDashboardPersona(personas);
		setting.setId(1);
	}

	@Test
	void testGetAllWidgetsUserPersonaNull() throws Exception {
		users.setPersonas(null);
		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
		when(userSettingsService.getDefaultSettings()).thenReturn(setting);

		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(setting.getDefaultPersonas(),
				"plants")).thenReturn(customizations);
		when(widgetsLevelRepository.findByAssetLevelAndWidgetsWidgetTypesDescriptionAndWidgetsStatusesStatusIndicator(
				"plants", "KPI", DashboardConstants.ACTIVE_STATUS_INDICATOR)).thenReturn(widgetsLevels);
		when(customizationsWidgetsRepository.findByCustomizationsId(1)).thenReturn(customizationsWidgetsList);
		responseList = widgetService.getAllWidgets("plants", "KPI", 1, mockHttpServletRequest);
		assertNotNull(responseList);
	}

	@Test
	void testGetAllWidgetsUserPersonaNotNullAndEqualToDefaultPersona() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
		when(userSettingsService.getDefaultSettings()).thenReturn(setting);

		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(setting.getDefaultPersonas(),
				"plants")).thenReturn(customizations);
		when(widgetsLevelRepository.findByAssetLevelAndWidgetsWidgetTypesDescriptionAndWidgetsStatusesStatusIndicator(
				"plants", "KPI", DashboardConstants.ACTIVE_STATUS_INDICATOR)).thenReturn(widgetsLevels);
		when(customizationsWidgetsRepository.findByCustomizationsId(1)).thenReturn(customizationsWidgetsList);
		responseList = widgetService.getAllWidgets("plants", "KPI", 1, mockHttpServletRequest);
		assertNotNull(responseList);
	}

	@Test
	void testGetAllWidgetsUserPersonaNotNullAndNotEqualToDefaultPersona() throws Exception {
		personas.setId(3);
		setting.setDefaultPersonas(personas);
		Personas persona = new Personas();
		persona.setId(2);
		users.setPersonas(persona);
		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
		when(userSettingsService.getDefaultSettings()).thenReturn(setting);

		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(setting.getDefaultPersonas(),
				"plants")).thenReturn(customizations);
		when(widgetsLevelRepository.findByAssetLevelAndWidgetsWidgetTypesDescriptionAndWidgetsStatusesStatusIndicator(
				"plants", "KPI", DashboardConstants.ACTIVE_STATUS_INDICATOR)).thenReturn(widgetsLevels);
		when(customizationsWidgetsRepository.findByCustomizationsId(1)).thenReturn(customizationsWidgetsList);
		responseList = widgetService.getAllWidgets("plants", "KPI", 1, mockHttpServletRequest);
		assertNotNull(responseList);
	}
//
//	@Test
//	void widgets_positive() throws JsonProcessingException, NoSuchElementException {
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(customizations);
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//		when(customizationsWidgetsRepository.findByCustomizationsIdAndWidgetsWidgetTypesDescription(
//				customizations.getId(), widgetsDto.getType().toUpperCase())).thenReturn(customizationsWidgetsList);
//		when(widgetsRepository.findById(widgetDto.getWidgetId())).thenReturn(Optional.of(widgets));
//		when(customizationsWidgetsRepository.save(customizationsWidgets)).thenReturn(customizationsWidgets);
//		when(widgetsLevelRepository.findByAssetLevelAndWidgetsId(widgetsDto.getLevel(), widgets.getId()))
//				.thenReturn(level);
//		List<WidgetsResponseDTO> result = widgetService.widgets(widgetsDto, mockHttpServletRequest);
//		assertNotNull(result);
//
//	}

	@Test
	void widgetsCustomizationNotNullAndUserNullAndSsoNullPersonaNullWidgetRequestEqualsSummary()
			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
		widgetsDto.setType("KPI");
		widgetTypes.setDescription("KPI");
		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
		when(customizationsRepository.findByIdAndAssetLevel(any(), any())).thenReturn(Optional.of(customizations));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
				.thenReturn(null);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				widgetsDto.getLevel())).thenReturn(null);
		when(userSettingsService.getDefaultSettings()).thenReturn(setting);

		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				widgetsDto.getLevel())).thenReturn(customizations);

		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
				1, widgetsDto.getType().toUpperCase())).thenReturn(level);

		when(customizationsRepository.save(any(Customizations.class))).thenReturn(customizations);
		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
		when(customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "SUMMARY"))
				.thenReturn(customizationsWidgetsList);

		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);

		when(usersRepository.save(users)).thenReturn(users);
		List<WidgetsResponseDTO> result = widgetService.addWidgets(widgetsDto, mockHttpServletRequest, true);
		assertNotNull(result);
	}

	@Test
	void widgetsCustomizationNotNullAndUserNullAndSsoNullPersonaNullWidgetRequestEqualsKPI()
			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
		widgetsDto.setType("SUMMARY");
		widgetTypes.setDescription("SUMMARY");
		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
		when(customizationsRepository.findByIdAndAssetLevel(any(), any())).thenReturn(Optional.of(customizations));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
				.thenReturn(null);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				widgetsDto.getLevel())).thenReturn(null);
		when(userSettingsService.getDefaultSettings()).thenReturn(setting);

		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				widgetsDto.getLevel())).thenReturn(customizations);

		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
				1, widgetsDto.getType().toUpperCase())).thenReturn(level);

		when(customizationsRepository.save(any(Customizations.class))).thenReturn(customizations);
		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
		when(customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "SUMMARY"))
				.thenReturn(customizationsWidgetsList);

		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);

		when(usersRepository.save(users)).thenReturn(users);
		List<WidgetsResponseDTO> result = widgetService.addWidgets(widgetsDto, mockHttpServletRequest, true);
		assertNotNull(result);
	}

//	@Test
	// Default
//	void widgetsCustomizationNotNullAndUserNullAndSsoNullPersonaAssumeAllServiceWidgetRequest()
//			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
////		widgetsDto.setType("SUMMARY");
////		widgetTypes.setDescription("SUMMARY");
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
//				.thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
//		
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
//				.thenReturn(null);
//	
////		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
////				widgetsDto.getLevel())).thenReturn(null);
//		
//		when(userSettingsService.getDefaultSettings()).thenReturn(setting);
//		
//		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(setting.getDefaultPersonas(),
//				widgetsDto.getLevel())).thenReturn(customizations);
//		
////		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
////				1, widgetsDto.getType().toUpperCase())).thenReturn(level);
////		
////		when(customizationsRepository.save(any(Customizations.class))).thenReturn(customizations);
////		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
////		when(customizationsWidgetsRepository
////				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "SUMMARY"))
////				.thenReturn(customizationsWidgetsList);
////
////		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
////
////		when(usersRepository.save(users)).thenReturn(users);
//		List<WidgetsResponseDTO> result = widgetService.widgets(widgetsDto, mockHttpServletRequest);
//		assertNotNull(result);
//	}

	@Test
	void widgetsCustomizationNotNullAndUserNullAndSsoNullPersonaNullWidgetRequestEqualsSummaryInvalidType()
			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
		widgetsDto.setType("KPI");
		widgetTypes.setDescription("KPI");
		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("privilege1", "privilege2"));
		when(customizationsRepository.findByIdAndAssetLevel(any(), any())).thenReturn(Optional.of(customizations));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
				.thenReturn(null);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				widgetsDto.getLevel())).thenReturn(null);
		when(userSettingsService.getDefaultSettings()).thenReturn(setting);

		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				widgetsDto.getLevel())).thenReturn(customizations);

		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
				1, widgetsDto.getType().toUpperCase())).thenReturn(null);
		assertThrows(InvalidDataAccessApiUsageException.class,
				() -> widgetService.addWidgets(widgetsDto, mockHttpServletRequest, true));
	}

	@Test
	void widgetsCustomizationNotNullAndUserNullAndSsoNullPersonaNullInvalidType()
			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
		widgetsDto.setType("SUMMARY");
		widgetTypes.setDescription("KPI");
		widgets.setDescription("KPI");

		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
		when(customizationsRepository.findByIdAndAssetLevel(any(), any())).thenReturn(Optional.of(customizations));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
				.thenReturn(null);
		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				widgetsDto.getLevel())).thenReturn(null);
		when(userSettingsService.getDefaultSettings()).thenReturn(setting);

		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
				widgetsDto.getLevel())).thenReturn(customizations);

		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
				1, widgetsDto.getType().toUpperCase())).thenReturn(level);

		when(customizationsRepository.save(any(Customizations.class))).thenReturn(customizations);

		customizationsRepository.delete(any(Customizations.class));
		assertDoesNotThrow(() -> widgetService.addWidgets(widgetsDto, mockHttpServletRequest, true));
	}

//	@Test
//	void widgetsCustomizationNotNullAndUserNullAndSsoNullWidgetRequestEqualsSummary()
//			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
//		widgetsDto.setType("KPI");
//		widgetTypes.setDescription("KPI");
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
//				.thenReturn(Arrays.asList("privilege1", "privilege2"));
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
//				.thenReturn(null);
//		when(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(),
//				widgetsDto.getLevel())).thenReturn(customizations);
////		when(assetHierarchyFilterService.getFilteredHierarchyFieldValues(
////				assetHierarchyFilterService.getUserFilteredHierarchy(mockHttpServletRequest, false),
////				widgetsDto.getLevel(), "vid", true, null))
////				.thenReturn(Arrays.asList("testAsset1", "testAsset2", "testAsset3"));
//
//		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
//				1, widgetsDto.getType().toUpperCase())).thenReturn(level);
//		when(customizationsRepository.save(any(Customizations.class))).thenReturn(customizations);
//		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
//
//		when(customizationsWidgetsRepository
//				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "SUMMARY"))
//				.thenReturn(customizationsWidgetsList);
//
//		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
//		when(customizationsAssetsRepository.save(any(CustomizationsAssets.class))).thenReturn(customizationsAssets);
//		List<WidgetsResponseDTO> result = widgetService.widgets(widgetsDto, mockHttpServletRequest);
//		assertNotNull(result);
//	}
//
//	@Test
//	void widgetsCustomizationNotNullAndUserNullAndSsoNullWidgetRequestEqualsKPI()
//			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
//		widgetsDto.setType("SUMMARY");
//		widgetTypes.setDescription("SUMMARY");
////		String groupsUrl = keycloakWrapperServiceUri + "/groups";
////		when(restTemplate.exchange(eq(groupsUrl), eq(HttpMethod.POST), any(HttpEntity.class),
////				any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(groupsList));
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
//				.thenReturn(Arrays.asList("privilege1", "privilege2"));
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
//				.thenReturn(null);
//		when(customizationsRepository.findByUsersSsoIsNullAndPersonasIdAndAssetLevel(users.getPersonas().getId(),
//				widgetsDto.getLevel())).thenReturn(customizations);
//
////		when(assetHierarchyFilterService.getFilteredHierarchyFieldValues(
////				assetHierarchyFilterService.getUserFilteredHierarchy(mockHttpServletRequest, false),
////				widgetsDto.getLevel(), "vid", true, null))
////				.thenReturn(Arrays.asList("testAsset1", "testAsset2", "testAsset3"));
//
//		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
//				1, widgetsDto.getType().toUpperCase())).thenReturn(level);
//		when(customizationsRepository.save(any(Customizations.class))).thenReturn(customizations);
//		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
//
//		when(customizationsWidgetsRepository
//				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "KPI"))
//				.thenReturn(customizationsWidgetsList);
//		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
//		when(customizationsAssetsRepository.save(any(CustomizationsAssets.class))).thenReturn(customizationsAssets);
//		List<WidgetsResponseDTO> result = widgetService.widgets(widgetsDto, mockHttpServletRequest);
//		assertNotNull(result);
//	}
//
	@Test
	void widgetsCustomizationNotNullAndUserNotNull()
			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privilege1", "privilege2"));
		
		when(customizationsRepository.findByIdAndAssetLevel(any(),any()))
				.thenReturn(Optional.of(customizations));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
				.thenReturn(customizations);
		when(customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "KPI"))
				.thenReturn(customizationsWidgetsList);
		when(widgetsRepository.findByIdAndWidgetTypesDescription(widgetDto.getWidgetId(),
				widgetsDto.getType().toUpperCase())).thenReturn(widgets);
		when(customizationsWidgetsRepository.save(customizationsWidgets)).thenReturn(customizationsWidgets);
		when(widgetsLevelRepository.findByAssetLevelAndWidgetsId(widgetsDto.getLevel(), widgetDto.getWidgetId()))
				.thenReturn(Optional.of(level));
		List<WidgetsResponseDTO> result = widgetService.addWidgets(widgetsDto, mockHttpServletRequest,true);
		assertNotNull(result);
	}

	@Test
	void widgetsCustomizationNotNullAndUserNotNullCustomizatiosWidgetsNull()
			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
		when(customizationsRepository.findByIdAndAssetLevel(any(), any()))
				.thenReturn(Optional.of(customizations));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
				.thenReturn(customizations);
		widgetDto.setWidgetId(1000);
		when(customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "KPI"))
				.thenReturn(customizationsWidgetsList);
		when(widgetsRepository.findByIdAndWidgetTypesDescription(widgetDto.getWidgetId(),
				widgetsDto.getType().toUpperCase())).thenReturn(widgets);
		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
		when(widgetsLevelRepository.findByAssetLevelAndWidgetsId(widgetsDto.getLevel(), widgets.getId()))
				.thenReturn(Optional.of(level));
		List<WidgetsResponseDTO> result = widgetService.addWidgets(widgetsDto, mockHttpServletRequest,true);
		assertNotNull(result);
	}

	@Test
	void widgetsCustomizationNotNullAndUserNotNullCustomizatiosWidgetsNotNullNotPaidService()
			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
		widgets.setPaidService(false);
		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
				.thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
		when(customizationsRepository.findByIdAndAssetLevel(any(), any())).thenReturn(Optional.of(customizations));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
				.thenReturn(customizations);
		widgetDto.setWidgetId(1000);
		when(customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "KPI"))
				.thenReturn(customizationsWidgetsList);
		when(widgetsRepository.findByIdAndWidgetTypesDescription(widgetDto.getWidgetId(),
				widgetsDto.getType().toUpperCase())).thenReturn(widgets);
		when(customizationsWidgetsRepository.save(any(CustomizationsWidgets.class))).thenReturn(customizationsWidgets);
		when(widgetsLevelRepository.findByAssetLevelAndWidgetsId(widgetsDto.getLevel(), widgets.getId()))
				.thenReturn(Optional.of(level));
		List<WidgetsResponseDTO> result = widgetService.addWidgets(widgetsDto, mockHttpServletRequest, true);
		assertNotNull(result);
	}

	@Test
	void widgetsCustomizationNotNullAndUserNotNullCustomizatiosWidgetsNullInvalidType()
			throws JsonProcessingException, NoSuchElementException, ServerUnavailableException, NotFoundException {
		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("PR_EssoPng", "PR_EssoPng"));
		when(customizationsRepository.findByIdAndAssetLevel(any(), any()))
				.thenReturn(Optional.of(customizations));
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(users);
		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
				.thenReturn(customizations);
		widgetDto.setWidgetId(1000);
		when(customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "KPI"))
				.thenReturn(customizationsWidgetsList);
		when(widgetsRepository.findByIdAndWidgetTypesDescription(widgetDto.getWidgetId(),
				widgetsDto.getType().toUpperCase())).thenReturn(null);
		assertThrows(InvalidDataAccessApiUsageException.class,
				() -> widgetService.addWidgets(widgetsDto, mockHttpServletRequest,true));
	}

//
//	@Test
//	void getAllWidgets() throws Exception {
////		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
////		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
////		String groupsUrl = keycloakWrapperServiceUri + "/groups";
////		when(restTemplate.exchange(eq(groupsUrl), eq(HttpMethod.POST), any(HttpEntity.class),
////				any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(groupsList));
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privilege1", "privilege2"));
//		when(customizationsRepository.findByIdAndAssetLevel(1, "plants")).thenReturn(Optional.of(customizations));
//		when(widgetsLevelRepository.findByAssetLevelAndWidgetsWidgetTypesDescriptionAndWidgetsStatusesStatusIndicator(
//				"plants", "PLANTS", 100)).thenReturn(widgetsLevels);
//		when(customizationsWidgetsRepository.findByCustomizationsId(1)).thenReturn(customizationsWidgetsList);
//		responseList = widgetService.getAllWidgets("plants", "PLANTS", 1, mockHttpServletRequest);
//		assertNotNull(responseList);
//	}
//
//	@Test
//	void getAllWidgetsFindByIdAndAssetLevelNull() throws Exception {
////		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
////		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
////		String groupsUrl = keycloakWrapperServiceUri + "/groups";
////		when(restTemplate.exchange(eq(groupsUrl), eq(HttpMethod.POST), any(HttpEntity.class),
////				any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(groupsList));
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privilege1", "privilege2"));
//		when(customizationsRepository.findByIdAndAssetLevel(1, "plants")).thenReturn(null);
//		assertThrows(InvalidDataAccessApiUsageException.class,
//				() -> widgetService.getAllWidgets("plants", "PLANTS", 1, mockHttpServletRequest));
//
//	}
//
//	@Test
//	void extractSubscribedWidgets() throws Exception {
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privilege1", "privilege2"));
//		assertEquals(2, 1 + 1);
//	}
//
//	@Test
//	void widgetsInvalidId() throws JsonProcessingException {
////		String groupsUrl = keycloakWrapperServiceUri + "/groups";
////		when(restTemplate.exchange(eq(groupsUrl), eq(HttpMethod.POST), any(HttpEntity.class),
////				any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(groupsList));
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privilege1", "privilege2"));
//		widgetDto.setChecked(true);
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(null);
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		when(usersRepository.findBySso(users.getSso())).thenReturn(users);
//		assertThrows(NoSuchElementException.class, () -> widgetService.widgets(widgetsDto, mockHttpServletRequest));
//	}
//
//	@Test
//	void widgetsPersonaCustomizationNull() throws JsonProcessingException {
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privilege1", "privilege2"));
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//				when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//		.thenReturn(null);
//		assertThrows(NoSuchElementException.class, () -> widgetService.widgets(widgetsDto, mockHttpServletRequest));
//	}
//
//	@Test
//	void widgetsUserManagementDown() {
//
//		widgetDto.setChecked(true);
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		assertThrows(ServerUnavailableException.class, () -> widgetService.widgets(widgetsDto, mockHttpServletRequest));
//	}
//
//	@Test
//	void getAllWidgetsUserManagementDown() {
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		assertThrows(ServerUnavailableException.class, () -> widgetService.getAllWidgets("plants", "PLANTS", 1, mockHttpServletRequest));
//	}
//
//	@Test
//	void widgetsCustomizationNotNullAndUserNotNullInvalidWidgetType()
//			throws JsonProcessingException, NoSuchElementException {
////		String groupsUrl = keycloakWrapperServiceUri + "/groups";
////		when(restTemplate.exchange(eq(groupsUrl), eq(HttpMethod.POST), any(HttpEntity.class),
////				any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(groupsList));
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privilege1", "privilege2"));
//		
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
//				.thenReturn(customizations);
//		widgetDto.setWidgetId(1000);
//		when(customizationsWidgetsRepository
//				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(), "KPI"))
//				.thenReturn(customizationsWidgetsList);
//		when(widgetsRepository.findByIdAndWidgetTypesDescription(widgetDto.getWidgetId(),
//				widgetsDto.getType().toUpperCase())).thenReturn(null);
//		assertThrows(InvalidDataAccessApiUsageException.class,
//				() -> widgetService.widgets(widgetsDto, mockHttpServletRequest));
//
//	}
//
	@Test
	void widgetsUnchecked() throws JsonProcessingException {
		when(umsClientService.getUserPrivileges(mockHttpServletRequest)).thenReturn(Arrays.asList("privilege1", "privilege2"));
		widgetDto.setChecked(false);
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		@SuppressWarnings("unused")
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		assertThrows(NoSuchElementException.class, () -> widgetService.addWidgets(widgetsDto, mockHttpServletRequest,true));
	}
//
//	@Test
//	void widgetsCustomizationNotNullAndUserNullAndSsoNullAssetHierarchyFilterNull()
//			throws JsonProcessingException, NoSuchElementException {
//		widgetsDto.setType("KPI");
//		widgetTypes.setDescription("KPI");
////		String groupsUrl = keycloakWrapperServiceUri + "/groups";
////		when(restTemplate.exchange(eq(groupsUrl), eq(HttpMethod.POST), any(HttpEntity.class),
////				any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(groupsList));
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
//				.thenReturn(Arrays.asList("privilege1", "privilege2"));
//
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
//				.thenReturn(null);
//		when(customizationsRepository.findByUsersSsoIsNullAndPersonasIdAndAssetLevel(users.getPersonas().getId(),
//				widgetsDto.getLevel())).thenReturn(customizations);
////		when(assetHierarchyFilterService.getFilteredHierarchyFieldValues(
////				assetHierarchyFilterService.getUserFilteredHierarchy(mockHttpServletRequest, false),
////				widgetsDto.getLevel(), "vid", true, null)).thenReturn(null);
//
//		assertThrows(NoSuchElementException.class, () -> widgetService.widgets(widgetsDto, mockHttpServletRequest));
//		assertEquals(2, 1 + 1);
//
//	}
//
//	@Test
//	void widgetsCustomizationNotNullAndUserNullAndSsoNullSaveCustomizationWidgetsInvalidType()
//			throws JsonProcessingException, NoSuchElementException {
//		widgetsDto.setType("SUMMARY");
//		widgetTypes.setDescription("KPI");
////		String groupsUrl = keycloakWrapperServiceUri + "/groups";
////		when(restTemplate.exchange(eq(groupsUrl), eq(HttpMethod.POST), any(HttpEntity.class),
////				any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(groupsList));
//
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
//				.thenReturn(Arrays.asList("privilege1", "privilege2"));
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
//				.thenReturn(null);
//		when(customizationsRepository.findByUsersSsoIsNullAndPersonasIdAndAssetLevel(users.getPersonas().getId(),
//				widgetsDto.getLevel())).thenReturn(customizations);
////		when(assetHierarchyFilterService.getFilteredHierarchyFieldValues(
////				assetHierarchyFilterService.getUserFilteredHierarchy(mockHttpServletRequest, false),
////				widgetsDto.getLevel(), "vid", true, null))
////				.thenReturn(Arrays.asList("testAsset1", "testAsset2", "testAsset3"));
//
//		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
//				1, widgetsDto.getType().toUpperCase())).thenReturn(level);
//		when(customizationsRepository.save(any(Customizations.class))).thenReturn(customizations);
//		customizationsRepository.delete(customizations);
//		assertThrows(InvalidDataAccessApiUsageException.class,
//				() -> widgetService.widgets(widgetsDto, mockHttpServletRequest));
//		assertEquals(2, 1 + 1);
//
//	}
//
//	@Test
//	@DisplayName("invalidType")
//	void widgetsCustomizationNotNullAndUserNullAndSsoNullSubscribedWidgetsFromWidgetsRequest()
//			throws JsonProcessingException, NoSuchElementException {
//		widgetsDto.setType("SUMMARY");
//		widgetTypes.setDescription("KPI");
////		when(restTemplate.exchange(eq(groupsUrl), eq(HttpMethod.POST), any(HttpEntity.class),
////				any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(groupsList));
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
//				.thenReturn(Arrays.asList("privilege1", "privilege2"));
//
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
//				.thenReturn(null);
//		when(customizationsRepository.findByUsersSsoIsNullAndPersonasIdAndAssetLevel(users.getPersonas().getId(),
//				widgetsDto.getLevel())).thenReturn(customizations);
////		when(assetHierarchyFilterService.getFilteredHierarchyFieldValues(
////				assetHierarchyFilterService.getUserFilteredHierarchy(mockHttpServletRequest, false),
////				widgetsDto.getLevel(), "vid", true, null))
////				.thenReturn(Arrays.asList("testAsset1", "testAsset2", "testAsset3"));
//
//		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
//				1, widgetsDto.getType().toUpperCase())).thenReturn(null);
//
//		assertThrows(InvalidDataAccessApiUsageException.class,
//				() -> widgetService.widgets(widgetsDto, mockHttpServletRequest));
//		assertEquals(2, 1 + 1);
//
//	}
//
//	@Test
//	void widgetsCustomizationNotNullAndUserNullAndSsoNullSubscribedWidgetsFromWidgetsRequestNotSubcribed()
//			throws JsonProcessingException, NoSuchElementException {
//		widgetsDto.setType("SUMMARY");
//		widgetTypes.setDescription("KPI");
////		String groupsUrl = keycloakWrapperServiceUri + "/groups";
////		when(restTemplate.exchange(eq(groupsUrl), eq(HttpMethod.POST), any(HttpEntity.class),
////				any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(groupsList));
//		when(umsClientService.getUserPrivileges(mockHttpServletRequest))
//				.thenReturn(Arrays.asList("privilege1", "privilege2"));
//
//		when(customizationsRepository.findByIdAndAssetLevel(widgetsDto.getCustomizationId(), widgetsDto.getLevel()))
//				.thenReturn(Optional.of(customizations));
//		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
//		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
//		String sso = claims.get("preferred_username").toString().replace("\"", "");
//		when(usersRepository.findBySso(sso)).thenReturn(users);
//		when(customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), widgetsDto.getLevel()))
//				.thenReturn(null);
//		when(customizationsRepository.findByUsersSsoIsNullAndPersonasIdAndAssetLevel(users.getPersonas().getId(),
//				widgetsDto.getLevel())).thenReturn(customizations);
////		when(assetHierarchyFilterService.getFilteredHierarchyFieldValues(
////				assetHierarchyFilterService.getUserFilteredHierarchy(mockHttpServletRequest, false),
////				widgetsDto.getLevel(), "vid", true, null))
////				.thenReturn(Arrays.asList("testAsset1", "testAsset2", "testAsset3"));
//		widgets.setTitle("test");
//		when(widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(widgetsDto.getLevel(),
//				1, widgetsDto.getType().toUpperCase())).thenReturn(level);
//
//		assertThrows(InvalidDataAccessApiUsageException.class,
//				() -> widgetService.widgets(widgetsDto, mockHttpServletRequest));
//		assertEquals(2, 1 + 1);
//
//	}

}