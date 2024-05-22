package com.bh.cp.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import com.auth0.jwt.impl.NullClaim;
import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.dashboard.dto.request.UserSettingsRequestDTO;
import com.bh.cp.dashboard.dto.response.PersonaResponseDTO;
import com.bh.cp.dashboard.dto.response.UserSettingsResponseDTO;
import com.bh.cp.dashboard.entity.Companies;
import com.bh.cp.dashboard.entity.Personas;
import com.bh.cp.dashboard.entity.Settings;
import com.bh.cp.dashboard.entity.Statuses;
import com.bh.cp.dashboard.entity.Users;
import com.bh.cp.dashboard.repository.PersonasRepository;
import com.bh.cp.dashboard.repository.SettingsRepository;
import com.bh.cp.dashboard.repository.UsersRepository;
import com.bh.cp.dashboard.service.CacheService;
import com.bh.cp.dashboard.util.JwtUtil;

import jakarta.ws.rs.NotFoundException;

class UserSettingsServiceImplTest {

	@InjectMocks
	private UserSettingsServiceImpl userSettingsService;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private UsersRepository usersRepository;

	@Mock
	private PersonasRepository personasRepo;

	@Mock
	private SettingsRepository settingsRepository;

	@Mock
	private CacheService cacheService;

	private MockHttpServletRequest mockHttpServletRequest;

	private Map<String, Claim> claims;

	private Users user;

	private UserSettingsRequestDTO settingsRequestDTO;

	private UserSettingsResponseDTO settingsResponseDTO;

	private PersonaResponseDTO personaResponseDTO;

	private List<PersonaResponseDTO> personaResponseDTOList = new ArrayList<>();

	private Personas persona;

	private Statuses status;

	private List<Personas> personasList;

	private Settings settings;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		personasList = new ArrayList<>();
		status = new Statuses();
		status.setId(3000);
		persona = new Personas();
		persona.setId(4001);
		persona.setDescription("All services");
		persona.setStatuses(status);
		personasList.add(persona);
		user = new Users();
		user.setId(2001);
		user.setEmail("test@test.com");
		user.setSso("abc");
		user.setCompanies(new Companies());
		user.setPersonas(persona);
		user.setDisplayAssetName("0");
		user.setTimeZone("1");
		user.setRetiredAssets("0");
		user.setUom("0");

		settingsRequestDTO = new UserSettingsRequestDTO();
		settingsRequestDTO.setDisplayAssetName("1");
		settingsRequestDTO.setRetiredAssets("0");
		settingsRequestDTO.setTimeZone("0");
		settingsRequestDTO.setUom("1");
		settingsRequestDTO.setPersonaId(1);

		personaResponseDTO = new PersonaResponseDTO();
		personaResponseDTO.setDescription("All services");
		personaResponseDTO.setSelected(true);
		personaResponseDTO.setStatusId(3000);
		personaResponseDTO.setId(4001);
		personaResponseDTOList.add(personaResponseDTO);

		settingsResponseDTO = new UserSettingsResponseDTO();
		settingsResponseDTO.setDisplayAssetName("0");
		settingsResponseDTO.setTimeZone("1");
		settingsResponseDTO.setRetiredAssets("0");
		settingsResponseDTO.setUom("0");
		settingsResponseDTO.setPersonas(personaResponseDTOList);

		settings = new Settings();
		settings.setDefaultPersonas(persona);

		claims = new HashMap<String, Claim>();
		claims.put("email", new NullClaim());
		claims.put("preferred_username", new NullClaim());
		claims.put("given_name", new NullClaim());

		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		when(jwtUtil.getClaims(anyString())).thenReturn(claims);

	}

	@Test
	@DisplayName("GetUserSettings - Fetching User Settings")
	void getUserSettings() throws NotFoundException {
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(user);
		when(personasRepo.findAll()).thenReturn(personasList);
		UserSettingsResponseDTO expectedResponseDTO = userSettingsService.getUserSettings(mockHttpServletRequest);
		assertEquals(expectedResponseDTO.getDisplayAssetName(), settingsResponseDTO.getDisplayAssetName());
	}

	@Test
	@DisplayName("GetUserSettings - User Persona is Null")
	void getUserSettings_UserPersonsIsNull() throws Exception {
		user = new Users();
		user.setId(2001);
		user.setEmail("test@test.com");
		user.setSso("abc");
		user.setCompanies(new Companies());
		user.setDisplayAssetName("0");
		user.setTimeZone("1");
		user.setRetiredAssets("0");
		user.setUom("0");
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(user);
		when(personasRepo.findAll()).thenReturn(personasList);
		when(settingsRepository.findById(1)).thenReturn(Optional.of(settings));
		UserSettingsResponseDTO expectedResponseDTO = userSettingsService.getUserSettings(mockHttpServletRequest);
		assertEquals(expectedResponseDTO.getDisplayAssetName(), settingsResponseDTO.getDisplayAssetName());
	}

	@Test
	@DisplayName("GetUserSettings - User Not Found")
	void getUserSettings_UserNotFound() throws Exception {
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(anyString())).thenReturn(null);
		when(personasRepo.findAll()).thenReturn(personasList);
		when(settingsRepository.findById(1)).thenReturn(Optional.of(settings));
		user.setSso(sso);
		when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));
		UserSettingsResponseDTO expectedResponseDTO = userSettingsService.getUserSettings(mockHttpServletRequest);
		assertEquals(persona.getId(), expectedResponseDTO.getPersonas().get(0).getId());
		assertEquals(persona.getDescription(), expectedResponseDTO.getPersonas().get(0).getDescription());
		assertEquals(persona.getStatuses().getId(), expectedResponseDTO.getPersonas().get(0).getStatusId());
	}

	@Test
	@DisplayName("UpdateToMyDashboard")
	void testUpdateToMyDashboard() throws Exception {
		user = new Users();
		user.setId(2001);
		user.setEmail("test@test.com");
		user.setSso("abc");
		user.setCompanies(new Companies());
		user.setDisplayAssetName("0");
		user.setTimeZone("1");
		user.setRetiredAssets("0");
		user.setUom("0");
		user.setPersonas(persona);
		when(settingsRepository.findById(1)).thenReturn(Optional.of(settings));
		when(usersRepository.save(any(Users.class))).thenReturn(user);
		userSettingsService.updateToMyDashboard(user);
		verify(usersRepository).save(any(Users.class));
	}

	@Test
	@DisplayName("UpdateUserSettings - By Sending Empty Request Body")
	void UpdateUserSettings_Positive() throws NotFoundException {
		UserSettingsRequestDTO settingsRequestDTO = new UserSettingsRequestDTO();
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(user);
		when(personasRepo.findAll()).thenReturn(personasList);
		when(personasRepo.findById(any(Integer.class))).thenReturn(Optional.of(persona));
		doNothing().when(cacheService).clearCacheWithPattern(any(), any());
		when(usersRepository.save(any(Users.class))).thenReturn(user);
		UserSettingsResponseDTO expectedResponseDTO = userSettingsService.updateUserSettings(mockHttpServletRequest,
				settingsRequestDTO);
		assertEquals(expectedResponseDTO.getRetiredAssets(), settingsResponseDTO.getRetiredAssets());
		assertEquals(expectedResponseDTO.getDisplayAssetName(), settingsResponseDTO.getDisplayAssetName());
		assertEquals(expectedResponseDTO.getTimeZone(), settingsResponseDTO.getTimeZone());
		assertEquals(expectedResponseDTO.getUom(), settingsResponseDTO.getUom());
		assertEquals(expectedResponseDTO.getPersonas().get(0).getDescription(),
				settingsResponseDTO.getPersonas().get(0).getDescription());
	}

	@Test
	@DisplayName("UpdateUserSettings - By Updating All The Fields In The Input")
	void UpdateUserSettings_Negative() throws NotFoundException {
		settingsResponseDTO.setDisplayAssetName("1");
		settingsResponseDTO.setRetiredAssets("0");
		settingsResponseDTO.setTimeZone("0");
		settingsResponseDTO.setUom("1");
		personaResponseDTO.setId(1);
		personaResponseDTOList.add(personaResponseDTO);
		settingsResponseDTO.setPersonas(personaResponseDTOList);
		persona.setId(1);
		Map<String, Claim> claims = jwtUtil.getClaims(mockHttpServletRequest.getHeader("Authorization").substring(7));
		String sso = claims.get("preferred_username").toString().replace("\"", "");
		when(usersRepository.findBySso(sso)).thenReturn(user);
		when(personasRepo.findAll()).thenReturn(personasList);
		when(personasRepo.findById(any(Integer.class))).thenReturn(Optional.of(persona));
		when(usersRepository.save(any(Users.class))).thenReturn(user);
		doNothing().when(cacheService).clearCacheWithPattern(any(), any());
		UserSettingsResponseDTO expectedResponseDTO = userSettingsService.updateUserSettings(mockHttpServletRequest,
				settingsRequestDTO);
		assertEquals(expectedResponseDTO.getRetiredAssets(), settingsResponseDTO.getRetiredAssets());
		assertEquals(expectedResponseDTO.getDisplayAssetName(), settingsResponseDTO.getDisplayAssetName());
		assertEquals(expectedResponseDTO.getTimeZone(), settingsResponseDTO.getTimeZone());
		assertEquals(expectedResponseDTO.getUom(), settingsResponseDTO.getUom());
		assertEquals(expectedResponseDTO.getPersonas().get(0).getDescription(),
				settingsResponseDTO.getPersonas().get(0).getDescription());
	}
}
