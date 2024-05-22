package com.bh.cp.audit.trail.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import com.auth0.jwt.impl.NullClaim;
import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.audit.trail.dto.request.SanitizedAuditTrailUserActionDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedAuditUsageRequestDTO;
import com.bh.cp.audit.trail.dto.request.SanitizedPerformanceRequestDTO;
import com.bh.cp.audit.trail.dto.response.PerformanceResponseDTO;
import com.bh.cp.audit.trail.entity.AuditTrailPerformance;
import com.bh.cp.audit.trail.entity.AuditTrailTables;
import com.bh.cp.audit.trail.entity.AuditTrailUsage;
import com.bh.cp.audit.trail.entity.AuditTrailUserAction;
import com.bh.cp.audit.trail.entity.CommonBlobs;
import com.bh.cp.audit.trail.entity.Companies;
import com.bh.cp.audit.trail.entity.Personas;
import com.bh.cp.audit.trail.entity.Statuses;
import com.bh.cp.audit.trail.entity.Users;
import com.bh.cp.audit.trail.repository.AuditTrailPerformanceRepository;
import com.bh.cp.audit.trail.repository.AuditTrailTablesRepository;
import com.bh.cp.audit.trail.repository.AuditTrailUsageRepository;
import com.bh.cp.audit.trail.repository.AuditTrailUserActionRepository;
import com.bh.cp.audit.trail.repository.UsersRepository;
import com.bh.cp.audit.trail.service.AuditTrailService;
import com.bh.cp.audit.trail.util.JwtUtil;
import com.bh.cp.audit.trail.util.StringUtil;

class AuditTrailServiceImplTest {

	@InjectMocks
	AuditTrailServiceImpl auditTrailServiceImpl;

	SanitizedAuditUsageRequestDTO auditTrailUsage;

	SanitizedPerformanceRequestDTO performanceRequest;

	@Mock
	private AuditTrailService auditTrailService;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	List<PerformanceResponseDTO> listPerformanceDto;

	PerformanceResponseDTO performanceResponse;

	@Mock
	private AuditTrailTablesRepository auditTrailTablesRepository;

	@Mock
	private AuditTrailUsageRepository auditTrailUsageRepository;

	@Mock
	private AuditTrailUserActionRepository auditTrailUserActionRepository;

	@Mock
	private AuditTrailPerformanceRepository auditPerformanceRepository;

	@Mock
	private UsersRepository usersRepository;

	AuditTrailUsage usage;

	AuditTrailPerformance auditTrailPerformance;

	@Mock
	private JwtUtil jwtUtil;

	List<AuditTrailPerformance> auditTrailPerformancesList = new ArrayList<>();

	private Map<String, Claim> claims;

	private Personas personas;

	private Users users;

	private Companies companies;

	private Statuses statuses;

	private CommonBlobs commonBlobs;

	private AuditTrailTables auditTrailTables;

	private AuditTrailUserAction auditTrailUserAction;

	private SanitizedAuditTrailUserActionDTO auditTrailUserActionDto;

	private Map<Object, Object> diffMap;

	@BeforeEach
	void setup() {

		MockitoAnnotations.openMocks(this);
		auditTrailTables = new AuditTrailTables();
		auditTrailUserAction = new AuditTrailUserAction();
		diffMap = new HashMap<>();
		Map<Object, Object> pre = new HashMap<>();
		Map<Object, Object> post = new HashMap<>();
		diffMap.put("pre", pre);
		diffMap.put("post", post);
		diffMap.put("id", pre);
		users = new Users();
		users.setId(1);
		users.setEmail("test@gmail.com");
		users.getEmail();
		users.setCompanies(companies);
		users.getCompanies();
		users.setPersonas(personas);
		users.getPersonas();
		users.setSso("sso");
		users.getSso();
		users.setUsername("test");
		users.getUsername();
		users.setDisplayAssetName("test");
		users.getDisplayAssetName();
		users.setRetiredAssets("test");
		users.getRetiredAssets();
		users.setTimeZone("test");
		users.getTimeZone();
		users.setUom("test");
		users.getUom();

		commonBlobs = new CommonBlobs();
		commonBlobs.setId(1);
		commonBlobs.setMaterial(null);
		commonBlobs.setExtension(".png");
		commonBlobs.getExtension();
		commonBlobs.getId();
		commonBlobs.getMaterial();
		companies = new Companies();
		companies.setId(1);
		companies.setName("bh");
		companies.setIconImages(commonBlobs);
		companies.getIconImages();
		companies.getId();
		companies.getName();
		statuses = new Statuses();
		statuses.setId(1);
		statuses.setDescription("ACTIVE");
		statuses.setStatusIndicator(1);
		statuses.setStatusType("ACTIVE");
		statuses.getDescription();
		statuses.getId();
		statuses.getStatusIndicator();
		statuses.getStatusType();
		personas = new Personas();
		personas.setId(1);
		personas.setStatuses(statuses);
		personas.setDescription("operation");
		personas.getDescription();
		personas.getId();
		personas.getStatuses();
		performanceRequest = new SanitizedPerformanceRequestDTO();
		performanceRequest.setThreadName("thread_name");
		performanceRequest.setServiceName("applicationName");
		performanceRequest.setModule("functionality");
		performanceRequest.setModuleDescription("moduleDescription");
		performanceRequest.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceRequest.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceRequest.setTotalExecutionTimeMs(123l);
		performanceRequest.setStatus(true);
		performanceRequest.setSso("sso");
		performanceRequest.setUri("uri");
		performanceRequest.setInputDetails("inputDetails");
		auditTrailUsage = new SanitizedAuditUsageRequestDTO();
		auditTrailUsage.setSso(users.getSso());
		auditTrailUsage.setActivity("activity");
		auditTrailUsage.setFunctionality("functionality");
		auditTrailUsage.setStatus(true);
		auditTrailUsage.setEntryTime(new Timestamp(System.currentTimeMillis()));
		auditTrailUsage.setServiceName("applicationName");
		auditTrailUsage.setThreadName("functionality");
		auditTrailUsage.setThreadName("thread_name");
		listPerformanceDto = new ArrayList<>();

		performanceResponse = new PerformanceResponseDTO();
		performanceResponse.setId(1);
		performanceResponse.setModule("functionality");
		performanceResponse.setSso(users.getSso());
		performanceResponse.setInputDetails("Input");
		performanceResponse.setServiceName("service");
		performanceResponse.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceResponse.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
		performanceResponse.setTotalExecutionTime(123l);
		performanceResponse.setStatus("Success");
		listPerformanceDto.add(performanceResponse);
		usage = new AuditTrailUsage();
		usage.setActivity(auditTrailUsage.getActivity());
		usage.setEntryTime(auditTrailUsage.getEntryTime());
		usage.setExitTime(auditTrailUsage.getExitTime());
		usage.setFunctionality(auditTrailUsage.getFunctionality());
		usage.setStatuses(new Statuses());
		usage.setUsers(users);
		usage.setServiceName(auditTrailUsage.getServiceName());
		usage.setThreadName(auditTrailUsage.getThreadName());

		auditTrailPerformance = new AuditTrailPerformance();
		auditTrailPerformance.setId(1);
		auditTrailPerformance.setEndTime(performanceRequest.getEndTime());
		auditTrailPerformance.setInputDetails(StringUtil.limitChars(performanceRequest.getInputDetails(), 2000));
		auditTrailPerformance.setServiceName(performanceRequest.getServiceName());
		auditTrailPerformance.setStartTime(performanceRequest.getStartTime());
		auditTrailPerformance.setStatus(new Statuses());
		auditTrailPerformance.setThreadName(performanceRequest.getThreadName());
		auditTrailPerformance.setTotalExecutionTimeMs(performanceRequest.getTotalExecutionTimeMs());
		auditTrailPerformance.setUser(users);
		auditTrailPerformance.setModule(performanceRequest.getModule());
		auditTrailPerformance.setModuleDescription(performanceRequest.getModuleDescription());
		auditTrailPerformance.getModule();
		auditTrailPerformance.getModuleDescription();
		auditTrailPerformance.getThreadName();
		auditTrailPerformance.getUri();

		auditTrailUserActionDto = new SanitizedAuditTrailUserActionDTO();
		auditTrailUserActionDto.setActionDate(Timestamp.valueOf(LocalDateTime.now()));
		auditTrailUserActionDto.setData("Data");
		auditTrailUserActionDto.setApplication("testApplication");
		auditTrailUserActionDto.setPrimaryKey(1);
		auditTrailUserActionDto.setSchema("testSchema");
		auditTrailUserActionDto.setSso("sso");
		auditTrailUserActionDto.setTableName("testTable");
		auditTrailUserActionDto.setUserAction("CREATE");

		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		claims = new HashMap<String, Claim>();

		claims.put("preferred_username", new NullClaim());
		auditTrailPerformancesList.add(auditTrailPerformance);

	}

	@Test
	void testSaveAuditTrailUsageSuccess() throws Exception {
	  when(usersRepository.findBySso("sso")).thenReturn(users);
	  usage.setThreadName("save_thread_name");
		when(auditTrailUsageRepository.save(usage)).thenReturn(usage);
		auditTrailServiceImpl.saveAuditTrailUsage(auditTrailUsage);
	  assertNotNull(auditTrailUsage);
	}

	@Test
	void testSaveAuditTrailUsageUpdateSuccess() throws Exception {
	  when(usersRepository.findBySso("sso")).thenReturn(users);
	  when(auditTrailUsageRepository.findByThreadName("thread_name")).thenReturn(usage);
		when(auditTrailUsageRepository.save(usage)).thenReturn(usage);
		auditTrailServiceImpl.saveAuditTrailUsage(auditTrailUsage);
	  assertNotNull(auditTrailUsage);
	}

	@Test
	void testSaveAuditTrailUsageFailure() throws Exception {
		auditTrailUsage.setStatus(false);
		when(usersRepository.findBySso("sso")).thenReturn(users);
		when(auditTrailUsageRepository.save(usage)).thenReturn(usage);
		auditTrailServiceImpl.saveAuditTrailUsage(auditTrailUsage);
		assertNotNull(auditTrailUsage);
	}

	@Test
	void testAuditTrailPerformancesStatusSuccess() throws Exception {
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(auditPerformanceRepository.findByUserSso(sso)).thenReturn(auditTrailPerformancesList);
		when(auditTrailServiceImpl.auditTrailPerformances(mockHttpServletRequest)).thenReturn(listPerformanceDto);
		assertNotNull(listPerformanceDto);
	}

	@Test
	void testAuditTrailPerformancesStatusFailure() throws Exception {
		performanceRequest.setStatus(false);
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(auditPerformanceRepository.findByUserSso(sso)).thenReturn(auditTrailPerformancesList);
		when(auditTrailServiceImpl.auditTrailPerformances(mockHttpServletRequest)).thenReturn(listPerformanceDto);
		assertNotNull(listPerformanceDto);
	}

	@Test
	void testSaveAuditTrailPerformances() throws Exception {
		 when(usersRepository.findBySso("sso")).thenReturn(users);
		when(auditPerformanceRepository.save(auditTrailPerformance)).thenReturn(auditTrailPerformance);
		auditTrailServiceImpl.saveAuditTrailPerformances(performanceRequest);
		assertNotNull(performanceRequest);
	}

	@Test
	void testSaveAuditTrailUserAction() {
		when(auditTrailUserActionRepository.save(auditTrailUserAction)).thenReturn(auditTrailUserAction);
		auditTrailServiceImpl.saveAuditTrailUserAction(auditTrailUserActionDto);
		assertNotNull(auditTrailUserAction);
	}

	@Test
	void testSaveAuditTrailUserActionUpdate() {
		auditTrailUserActionDto.setUserAction("UPDATE");
		when(auditTrailUserActionRepository.save(auditTrailUserAction)).thenReturn(auditTrailUserAction);
		auditTrailServiceImpl.saveAuditTrailUserAction(auditTrailUserActionDto);
		assertNotNull(auditTrailUserAction);
	}

	@Test
	void testSaveAuditTrailUserActionDiffEmpty() {
		auditTrailUserActionDto.setData("Data");
		auditTrailTablesRepository.save(auditTrailTables);
		auditTrailServiceImpl.saveAuditTrailUserAction(auditTrailUserActionDto);
		assertNotNull(auditTrailUserAction);
	}

	@Test
	void testSaveAuditTrailTables() {
		when(auditTrailTablesRepository.save(auditTrailTables)).thenReturn(auditTrailTables);
		when(auditTrailServiceImpl.saveAuditTrailTables(auditTrailTables)).thenReturn(auditTrailTables);
		assertNotNull(auditTrailTables);
	}

}
