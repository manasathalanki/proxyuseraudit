package com.bh.cp.dashboard.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.constants.ExceptionConstants;
import com.bh.cp.dashboard.dto.request.UserSettingsRequestDTO;
import com.bh.cp.dashboard.dto.response.PersonaResponseDTO;
import com.bh.cp.dashboard.dto.response.UserSettingsResponseDTO;
import com.bh.cp.dashboard.entity.Personas;
import com.bh.cp.dashboard.entity.Settings;
import com.bh.cp.dashboard.entity.Users;
import com.bh.cp.dashboard.repository.PersonasRepository;
import com.bh.cp.dashboard.repository.SettingsRepository;
import com.bh.cp.dashboard.repository.UsersRepository;
import com.bh.cp.dashboard.service.CacheService;
import com.bh.cp.dashboard.service.UserSettingsService;
import com.bh.cp.dashboard.util.JwtUtil;
import com.bh.cp.dashboard.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsServiceImpl.class);

	private JwtUtil jwtUtil;

	private CacheService cacheService;

	private UsersRepository usersRepository;

	private PersonasRepository personasRepository;

	private SettingsRepository settingsRepository;

	public UserSettingsServiceImpl(@Autowired JwtUtil jwtUtil, @Autowired CacheService cacheService,
			@Autowired UsersRepository usersRepository, @Autowired PersonasRepository personasRepository,
			@Autowired SettingsRepository settingsRepository) {
		super();
		this.jwtUtil = jwtUtil;
		this.cacheService = cacheService;
		this.usersRepository = usersRepository;
		this.personasRepository = personasRepository;
		this.settingsRepository = settingsRepository;
	}

	@Override
	public Settings getDefaultSettings() throws NotFoundException {
		LOGGER.info("Getting default settings...");
		return settingsRepository.findById(1)
				.orElseThrow(() -> new NotFoundException(ExceptionConstants.DEFAULT_SETTINGS_NOT_FOUND));
	}

	@Override
	public UserSettingsResponseDTO getUserSettings(HttpServletRequest httpServletRequest) throws NotFoundException {
		return filterUserSettingsResponse(extractSsoAndReturnUser(httpServletRequest));
	}

	@Override
	public Users extractSsoAndReturnUser(HttpServletRequest httpServletRequest) throws NotFoundException {
		Map<String, Claim> claims = SecurityUtil.getClaims(httpServletRequest, jwtUtil);
		String preferredUsername = SecurityUtil.getFieldFromClaims(claims, DashboardConstants.KEY_PREFERRED_USERNAME,
				ExceptionConstants.PREFERRED_USERNAME_INVALID);
		try {
			return retrieveUserBySso(preferredUsername);
		} catch (NotFoundException e) {
			String email = SecurityUtil.getFieldFromClaims(claims, DashboardConstants.KEY_EMAIL,
					ExceptionConstants.EMAIL_NOT_FOUND);
			String givenName = SecurityUtil.getFieldFromClaims(claims, DashboardConstants.KEY_GIVEN_NAME,
					ExceptionConstants.GIVEN_NAME_INVALID);
			Settings defaultSettings = getDefaultSettings();

			Users user = new Users();
			user.setEmail(email);
			user.setUsername(givenName);
			user.setSso(preferredUsername);
			user.setCompanies(defaultSettings.getCompanies());
			return usersRepository.save(user);
		}
	}

	private Users retrieveUserBySso(String preferredUsername) throws NotFoundException {
		return Optional.ofNullable(usersRepository.findBySso(preferredUsername))
				.orElseThrow(() -> new NotFoundException(ExceptionConstants.USER_NOT_FOUND));
	}

	private UserSettingsResponseDTO filterUserSettingsResponse(Users user) throws NotFoundException {
		UserSettingsResponseDTO responseDTO = new UserSettingsResponseDTO();
		responseDTO.setSso(user.getSso());
		responseDTO.setTimeZone(user.getTimeZone());
		responseDTO.setDisplayAssetName(user.getDisplayAssetName());
		responseDTO.setRetiredAssets(user.getRetiredAssets());
		responseDTO.setUom(user.getUom());
		Integer selectedpersonaId = user.getPersonas() != null ? user.getPersonas().getId()
				: getDefaultSettings().getDefaultPersonas().getId();
		List<Personas> personas = personasRepository.findAll();
		PersonaResponseDTO personaResponseDTO;
		List<PersonaResponseDTO> personasList = new ArrayList<>();
		for (Personas persona : personas) {
			personaResponseDTO = new PersonaResponseDTO();
			personaResponseDTO.setSelected((persona.getId().equals(selectedpersonaId)));
			personaResponseDTO.setDescription(persona.getDescription());
			personaResponseDTO.setId(persona.getId());
			personaResponseDTO.setStatusId(persona.getStatuses().getId());
			personasList.add(personaResponseDTO);
		}
		responseDTO.setPersonas(personasList);
		return responseDTO;
	}

	@Override
	public UserSettingsResponseDTO updateUserSettings(HttpServletRequest httpServletRequest,
			UserSettingsRequestDTO settingsRequestDTO) throws NotFoundException {
		Users user = extractSsoAndReturnUser(httpServletRequest);
		if (settingsRequestDTO.getDisplayAssetName() != null)
			user.setDisplayAssetName(settingsRequestDTO.getDisplayAssetName());
		if (settingsRequestDTO.getRetiredAssets() != null)
			user.setRetiredAssets(settingsRequestDTO.getRetiredAssets());
		if (settingsRequestDTO.getUom() != null)
			user.setUom(settingsRequestDTO.getUom());
		if (settingsRequestDTO.getTimeZone() != null)
			user.setTimeZone(settingsRequestDTO.getTimeZone());
		if (settingsRequestDTO.getPersonaId() != null) {
			cacheService.clearCacheWithPattern("widgetaccess",
					"*widgetaccess::" + httpServletRequest.getHeader("Authorization") + "*");
			Personas personas = personasRepository.findById(settingsRequestDTO.getPersonaId())
					.orElseThrow(() -> new NotFoundException(ExceptionConstants.PERSONA_NOT_FOUND));
			user.setPersonas(personas);
		}

		Users savedUser = usersRepository.save(user);
		return filterUserSettingsResponse(savedUser);
	}

	@Override
	public void updateToMyDashboard(Users user) throws NotFoundException {
		Settings defaultSettings = getDefaultSettings();
		if (user.getPersonas() == null || !user.getPersonas().equals(defaultSettings.getMyDashboardPersona())) {
			SecurityUtil.sanitizeLogging(LOGGER, Level.INFO, "Switching to My Dashboard for sso {}", user.getSso());
			Personas personas = defaultSettings.getMyDashboardPersona();
			user.setPersonas(personas);
			usersRepository.save(user);
		}
	}

}
