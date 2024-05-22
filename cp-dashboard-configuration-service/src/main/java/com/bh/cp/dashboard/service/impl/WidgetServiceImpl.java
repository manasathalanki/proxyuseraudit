package com.bh.cp.dashboard.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.constants.ExceptionConstants;
import com.bh.cp.dashboard.dto.request.DeleteWidgetRequestDTO;
import com.bh.cp.dashboard.dto.request.WidgetOrderRequestDTO;
import com.bh.cp.dashboard.dto.request.WidgetsRequestDTO;
import com.bh.cp.dashboard.dto.response.WidgetInfoResponseDTO;
import com.bh.cp.dashboard.dto.response.WidgetsResponseDTO;
import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.CustomizationsWidgets;
import com.bh.cp.dashboard.entity.Settings;
import com.bh.cp.dashboard.entity.Users;
import com.bh.cp.dashboard.entity.Widgets;
import com.bh.cp.dashboard.entity.WidgetsLevel;
import com.bh.cp.dashboard.repository.CustomizationsRepository;
import com.bh.cp.dashboard.repository.CustomizationsWidgetsRepository;
import com.bh.cp.dashboard.repository.UsersRepository;
import com.bh.cp.dashboard.repository.WidgetsLevelRepository;
import com.bh.cp.dashboard.repository.WidgetsRepository;
import com.bh.cp.dashboard.service.DashboardService;
import com.bh.cp.dashboard.service.UMSClientService;
import com.bh.cp.dashboard.service.UserSettingsService;
import com.bh.cp.dashboard.service.WidgetService;
import com.bh.cp.dashboard.util.JwtUtil;
import com.bh.cp.dashboard.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

@Service
public class WidgetServiceImpl implements WidgetService {

	private static final Logger logger = LoggerFactory.getLogger(WidgetServiceImpl.class);

	private List<Integer> enabledWidgets;

	private JwtUtil jwtUtil;

	private CustomizationsRepository customizationsRepository;

	private CustomizationsWidgetsRepository customizationsWidgetsRepository;

	private WidgetsLevelRepository widgetsLevelRepository;

	private UsersRepository usersRepository;

	private WidgetsRepository widgetsRepository;

	private DashboardService dashboardService;

	private UMSClientService umsClientService;

	private UserSettingsService userSettingsService;

	public WidgetServiceImpl(@Autowired JwtUtil jwtUtil, @Autowired CustomizationsRepository customizationsRepository,
			@Autowired CustomizationsWidgetsRepository customizationsWidgetsRepository,
			@Autowired WidgetsLevelRepository widgetsLevelRepository, @Autowired UsersRepository usersRepository,
			@Autowired WidgetsRepository widgetsRepository, @Autowired DashboardService dashboardService,
			@Autowired UMSClientService umsClientService, @Autowired UserSettingsService userSettingsService,
			@Value("${enabled-widgets-list}") List<Integer> enabledWidgets) {
		super();
		this.jwtUtil = jwtUtil;
		this.customizationsRepository = customizationsRepository;
		this.customizationsWidgetsRepository = customizationsWidgetsRepository;
		this.widgetsLevelRepository = widgetsLevelRepository;
		this.usersRepository = usersRepository;
		this.widgetsRepository = widgetsRepository;
		this.dashboardService = dashboardService;
		this.umsClientService = umsClientService;
		this.userSettingsService = userSettingsService;
		this.enabledWidgets = enabledWidgets;
	}

	@Override
	public List<WidgetsResponseDTO> addWidgets(WidgetsRequestDTO widgetsRequest, HttpServletRequest httpServletRequest,
			boolean isReordered) throws NoSuchElementException, JsonProcessingException, NotFoundException {
		Map<String, Claim> claims = SecurityUtil.getClaims(httpServletRequest, jwtUtil);
		String sso = SecurityUtil.getSSO(claims);
		List<WidgetOrderRequestDTO> checkedWidgets = widgetsRequest.getWidgets().stream().filter(i -> i.isChecked())
				.toList();
		if (checkedWidgets.isEmpty()) {
			logger.error("WidgetServiceImpl: widgets()=> Error:Allowed only checked widgets");
			throw new NoSuchElementException("Allowed only checked widgets");
		}
		widgetsRequest.setWidgets(checkedWidgets);

		Users users = usersRepository.findBySso(sso);
		Customizations findByUsersSso = customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(),
				widgetsRequest.getLevel());
		if (findByUsersSso != null) {
			return updateWidgetsBasedOnSsoCustomization(httpServletRequest, findByUsersSso, widgetsRequest,
					isReordered);
		}
		Customizations personaCustomization = customizationsRepository
				.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(), widgetsRequest.getLevel());
		if (personaCustomization == null) {
			logger.info("WidgetServiceImpl: widgets()=> Defalut Persona customization");
			Settings defaultSettings = userSettingsService.getDefaultSettings();
			personaCustomization = customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(
					defaultSettings.getDefaultPersonas(), widgetsRequest.getLevel());
		}
		return createWidgetsBasedOnSsoCustomization(personaCustomization, widgetsRequest, httpServletRequest,
				isReordered);
	}

	private CustomizationsWidgets getWidgetCustomization(List<CustomizationsWidgets> findByCustomizationsId,
			Integer widgetId) {
		CustomizationsWidgets widgetsCustomization = null;
		for (CustomizationsWidgets widget : findByCustomizationsId) {

			if (widget.getWidgets().getId().equals(widgetId)) {
				widgetsCustomization = widget;
				break;
			}
		}

		return widgetsCustomization;
	}

	private List<WidgetsResponseDTO> updateWidgetsBasedOnSsoCustomization(HttpServletRequest httpServletRequest,
			Customizations customizations, WidgetsRequestDTO widgetsRequest, boolean isReordered)
			throws NotFoundException {

		logger.info("widgets(): updateWidgetsBasedOnSsoCustomization()");

		List<CustomizationsWidgets> findByCustomizationsId = customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(),
						widgetsRequest.getType().toUpperCase());
		List<WidgetsResponseDTO> responseList = new ArrayList<>();

		List<CustomizationsWidgets> list = new ArrayList<>();

		widgetsRequest.getWidgets().stream().forEach(data -> {

			CustomizationsWidgets widgetsCustomization = getWidgetCustomization(findByCustomizationsId,
					data.getWidgetId());
			WidgetsResponseDTO widgetsResponse = new WidgetsResponseDTO();

			if (widgetsCustomization == null) {
				Widgets widgets = widgetsRepository.findByIdAndWidgetTypesDescription(data.getWidgetId(),
						widgetsRequest.getType().toUpperCase());
				if (widgets == null) {
					logger.error("WidgetServiceImpl: widgets()=> Error:Invalid Type");
					throw new InvalidDataAccessApiUsageException(ExceptionConstants.INVALID_TYPE);
				}
				if (widgets.isPaidService()) {
					if (widgetSubscribed(httpServletRequest, widgets.getId())) {
						CustomizationsWidgets customizationWidgets = new CustomizationsWidgets();
						customizationWidgets.setCustomizations(customizations);

						customizationWidgets.setWidgets(widgets);
						customizationWidgets.setOrderNumber(data.getOrderNumber());
						CustomizationsWidgets savedCustomizationWidgets = customizationsWidgetsRepository
								.save(customizationWidgets);
						widgetsResponse.setWidgetId(savedCustomizationWidgets.getWidgets().getId());
						widgetsResponse.setTitle(savedCustomizationWidgets.getWidgets().getTitle());
						widgetsResponse.setOrderNumber(savedCustomizationWidgets.getOrderNumber());
						widgetsResponse.setHasAccess(true);
						widgetsResponse.setPaidService(widgets.isPaidService());
						widgetsResponse.setChecked(true);
						WidgetsLevel widgetsLevel = widgetsLevelRepository
								.findByAssetLevelAndWidgetsId(widgetsRequest.getLevel(), widgets.getId()).orElse(null);
						widgetsResponse.setCustomizationId(customizations.getId());
						widgetsResponse.setStaticImageId(widgetsLevel.getStaticImage().getId());
						responseList.add(widgetsResponse);
						list.add(savedCustomizationWidgets);
					}
				} else {
					CustomizationsWidgets customizationWidgets = new CustomizationsWidgets();
					customizationWidgets.setCustomizations(customizations);

					customizationWidgets.setWidgets(widgets);
					customizationWidgets.setOrderNumber(data.getOrderNumber());
					CustomizationsWidgets savedCustomizationWidgets = customizationsWidgetsRepository
							.save(customizationWidgets);
					widgetsResponse.setWidgetId(savedCustomizationWidgets.getWidgets().getId());
					widgetsResponse.setTitle(savedCustomizationWidgets.getWidgets().getTitle());
					widgetsResponse.setOrderNumber(savedCustomizationWidgets.getOrderNumber());
					widgetsResponse.setHasAccess(true);
					widgetsResponse.setPaidService(widgets.isPaidService());
					widgetsResponse.setChecked(true);
					WidgetsLevel widgetsLevel = widgetsLevelRepository
							.findByAssetLevelAndWidgetsId(widgetsRequest.getLevel(), widgets.getId()).orElse(null);
					widgetsResponse.setCustomizationId(customizations.getId());
					widgetsResponse.setStaticImageId(widgetsLevel.getStaticImage().getId());
					responseList.add(widgetsResponse);
					list.add(savedCustomizationWidgets);
				}
			} else {
				widgetsCustomization.setOrderNumber(data.getOrderNumber());
				CustomizationsWidgets updatedCustomizationWidgets = customizationsWidgetsRepository
						.save(widgetsCustomization);
				widgetsResponse.setWidgetId(updatedCustomizationWidgets.getWidgets().getId());
				widgetsResponse.setTitle(updatedCustomizationWidgets.getWidgets().getTitle());
				widgetsResponse.setOrderNumber(updatedCustomizationWidgets.getOrderNumber());
				widgetsResponse.setPaidService(updatedCustomizationWidgets.getWidgets().isPaidService());
				widgetsResponse.setHasAccess(true);
				widgetsResponse.setChecked(true);
				WidgetsLevel widgetsLevel = widgetsLevelRepository.findByAssetLevelAndWidgetsId(
						widgetsRequest.getLevel(), updatedCustomizationWidgets.getWidgets().getId()).orElse(null);
				widgetsResponse.setCustomizationId(customizations.getId());
				widgetsResponse.setStaticImageId(widgetsLevel.getStaticImage().getId());
				responseList.add(widgetsResponse);
				list.add(updatedCustomizationWidgets);
			}
		});

		findByCustomizationsId.stream().filter(data -> !list.contains(data))
				.forEach(i -> customizationsWidgetsRepository.deleteById(i.getId()));

		customizations.setReordered(isReordered);
		customizationsRepository.save(customizations);
		userSettingsService.updateToMyDashboard(customizations.getUsers());
		return responseList;
	}

	private List<WidgetsResponseDTO> createWidgetsBasedOnSsoCustomization(Customizations personaCustomizations,
			WidgetsRequestDTO widgetsRequest, HttpServletRequest httpServletRequest, boolean isReordered)
			throws NotFoundException {

		logger.info("widgets(): createWidgetsBasedOnSsoCustomization()");

		Map<String, Claim> claims = SecurityUtil.getClaims(httpServletRequest, jwtUtil);
		String sso = SecurityUtil.getSSO(claims);
		Users users = usersRepository.findBySso(sso);

		List<WidgetsLevel> subscribedWidgetsLevel = subscribedWidgetsFromWidgetsRequest(widgetsRequest,
				httpServletRequest);

		Customizations saveCustomization = new Customizations();
		saveCustomization.setDateRange(personaCustomizations.getDateRange());
		saveCustomization.setDefault(personaCustomizations.isDefault());
		saveCustomization.setAssetLevel(personaCustomizations.getAssetLevel());
		saveCustomization.setUsers(users);
		saveCustomization.setReordered(isReordered);
		Customizations savedCustomization = customizationsRepository.save(saveCustomization);

		List<WidgetsResponseDTO> widgetsResponseDTO = saveCustomizationsWidets(savedCustomization, widgetsRequest,
				subscribedWidgetsLevel);
		if (widgetsRequest.getType().equalsIgnoreCase(DashboardConstants.KPI)) {
			saveCustomizationsWidetsKpiOrSummary(savedCustomization, personaCustomizations, DashboardConstants.SUMMARY);
		} else if (widgetsRequest.getType().equalsIgnoreCase(DashboardConstants.SUMMARY)) {
			saveCustomizationsWidetsKpiOrSummary(savedCustomization, personaCustomizations, DashboardConstants.KPI);
		}

		userSettingsService.updateToMyDashboard(savedCustomization.getUsers());
		return widgetsResponseDTO;
	}

	private List<WidgetsResponseDTO> saveCustomizationsWidets(Customizations savedCustomization,
			WidgetsRequestDTO widgetsRequest, List<WidgetsLevel> subscribedWidgets) {

		List<WidgetsResponseDTO> responseList = new ArrayList<>();

		subscribedWidgets.stream().forEach(widgetLevel -> {
			for (WidgetOrderRequestDTO data : widgetsRequest.getWidgets()) {
				if (!widgetLevel.getWidgets().getWidgetTypes().getDescription()
						.equalsIgnoreCase(widgetsRequest.getType())) {
					customizationsRepository.delete(savedCustomization);
					logger.error("widgets(): saveCustomizationsWidets()=> Error:Invalid Type");
					throw new InvalidDataAccessApiUsageException(ExceptionConstants.INVALID_TYPE);
				}
				if (widgetLevel.getWidgets().getId().equals(data.getWidgetId())) {
					CustomizationsWidgets customizationWidgets = new CustomizationsWidgets();
					customizationWidgets.setCustomizations(savedCustomization);
					customizationWidgets.setWidgets(widgetLevel.getWidgets());
					customizationWidgets.setOrderNumber(data.getOrderNumber());
					customizationsWidgetsRepository.save(customizationWidgets);
					WidgetsResponseDTO widgetsResponse = new WidgetsResponseDTO();
					widgetsResponse.setPaidService(widgetLevel.getWidgets().isPaidService());
					widgetsResponse.setCustomizationId(savedCustomization.getId());
					widgetsResponse.setWidgetId(data.getWidgetId());
					widgetsResponse.setTitle(widgetLevel.getWidgets().getTitle());
					widgetsResponse.setOrderNumber(data.getOrderNumber());
					widgetsResponse.setStaticImageId(widgetLevel.getStaticImage().getId());
					widgetsResponse.setChecked(true);
					widgetsResponse.setHasAccess(true);
					responseList.add(widgetsResponse);
					break;
				}
			}
		});

		return responseList;
	}

	private void saveCustomizationsWidetsKpiOrSummary(Customizations savedCustomization,
			Customizations personaCustomization, String type) {

		List<CustomizationsWidgets> customizationsKpiWidgets = customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(personaCustomization.getId(), type);

		customizationsKpiWidgets.stream().forEach(data -> {
			CustomizationsWidgets customizationWidgets = new CustomizationsWidgets();
			customizationWidgets.setCustomizations(savedCustomization);
			customizationWidgets.setWidgets(data.getWidgets());
			customizationWidgets.setOrderNumber(data.getOrderNumber());
			customizationWidgets.setCustomizations(savedCustomization);
			customizationsWidgetsRepository.save(customizationWidgets);
		});
	}

	private List<WidgetsLevel> subscribedWidgetsFromWidgetsRequest(WidgetsRequestDTO widgetsRequest,
			HttpServletRequest httpServletRequest) {
		List<Integer> list = widgetsRequest.getWidgets().stream().map(data -> data.getWidgetId()).toList();

		List<WidgetsLevel> listWidgetLevel = list.stream()
				.map(data -> widgetsLevelRepository.findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(
						widgetsRequest.getLevel(), data, widgetsRequest.getType().toUpperCase()))
				.toList();

		if (listWidgetLevel.get(0) == null) {
			logger.error("widgets(): subscribedWidgetsFromWidgetsRequest()=> Error:Invalid Type");
			throw new InvalidDataAccessApiUsageException(ExceptionConstants.INVALID_TYPE);
		}

		return listWidgetLevel.stream().filter(widgetLevel -> {
			if (widgetLevel.getWidgets().isPaidService()) {
				return widgetSubscribed(httpServletRequest, widgetLevel.getWidgets().getId());
			}
			return true;
		}).toList();
	}

	private boolean widgetSubscribed(HttpServletRequest httpServletRequest, Integer widgetId) {
		try {
			Map<String, Object> widgetSubResponse = umsClientService.getWidgetSubscription(httpServletRequest,
					widgetId);
			return (boolean) widgetSubResponse.getOrDefault(DashboardConstants.SUBSCRIBED, false);
		} catch (Exception e) {
			logger.info("Exception error while fetching subscription information for widget {}...", widgetId);
			return false;
		}
	}

	private Customizations getPersonaOrSsoCustomization(Settings defaultSettings, Users users, String level) {
		Customizations findByIdCustomization = null;

		if (users.getPersonas() == null) {
			findByIdCustomization = customizationsRepository
					.findByUsersSsoIsNullAndPersonasAndAssetLevel(defaultSettings.getDefaultPersonas(), level);
		} else if (!users.getPersonas().getId().equals(defaultSettings.getMyDashboardPersona().getId())) {
			findByIdCustomization = customizationsRepository
					.findByUsersSsoIsNullAndPersonasAndAssetLevel(users.getPersonas(), level);
		} else if (users.getPersonas().getId().equals(defaultSettings.getMyDashboardPersona().getId())) {
			findByIdCustomization = customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), level);
			if (findByIdCustomization == null) {
				findByIdCustomization = customizationsRepository
						.findByUsersSsoIsNullAndPersonasAndAssetLevel(defaultSettings.getDefaultPersonas(), level);
			}
		}
		return findByIdCustomization;
	}

	@Override
	public List<WidgetsResponseDTO> getAllWidgets(String level, String type, Integer customizationId,
			HttpServletRequest httpServletRequest) throws JsonProcessingException, NotFoundException {

		SecurityUtil.sanitizeLogging(logger, Level.INFO, "WidgetServiceImpl: getAllWidgets()=> [{}, {}, {}]", level,
				type, customizationId);

		Map<String, Claim> claims = SecurityUtil.getClaims(httpServletRequest, jwtUtil);
		List<WidgetsResponseDTO> responseList = new ArrayList<>();
		String sso = SecurityUtil.getSSO(claims);
		Users users = usersRepository.findBySso(sso);
		Settings defaultSettings = userSettingsService.getDefaultSettings();
		Customizations findByIdCustomization = getPersonaOrSsoCustomization(defaultSettings, users, level);
		Integer customizationIdNew = findByIdCustomization != null ? findByIdCustomization.getId() : null;
		List<WidgetsLevel> listWidgetsLevel = widgetsLevelRepository
				.findByAssetLevelAndWidgetsWidgetTypesDescriptionAndWidgetsStatusesStatusIndicator(level,
						type.toUpperCase(), DashboardConstants.ACTIVE_STATUS_INDICATOR);

		List<WidgetsLevel> sortWidgetLevel = listWidgetsLevel.stream()
				.filter(i -> enabledWidgets.contains(i.getWidgets().getId())).map(l -> l).toList();

		List<CustomizationsWidgets> listCustomizationsWidgets = customizationsWidgetsRepository
				.findByCustomizationsId(findByIdCustomization != null ? findByIdCustomization.getId() : null);

		List<CustomizationsWidgets> sortCustomizationWidgets = listCustomizationsWidgets.stream()
				.filter(i -> enabledWidgets.contains(i.getWidgets().getId())).map(l -> l).toList();

		sortWidgetLevel.stream().forEach(widget -> {
			WidgetsResponseDTO widgetResponse = new WidgetsResponseDTO();
			CustomizationsWidgets checkWidgets = getWidgetCustomization(sortCustomizationWidgets,
					widget.getWidgets().getId());
			if (widget.getWidgets().isPaidService()) {
				widgetResponse.setPaidService(true);
				widgetResponse.setHasAccess(widgetSubscribed(httpServletRequest, widget.getWidgets().getId()));
			} else {
				widgetResponse.setPaidService(false);
				widgetResponse.setHasAccess(true);
			}
			if (checkWidgets == null) {
				widgetResponse.setOrderNumber(null);
				widgetResponse.setChecked(false);
			} else {
				if (widgetResponse.isHasAccess()) {
					widgetResponse.setOrderNumber(checkWidgets.getOrderNumber());
					widgetResponse.setChecked(true);
				} else {
					widgetResponse.setOrderNumber(checkWidgets.getOrderNumber());
					widgetResponse.setChecked(false);
				}
			}
			widgetResponse.setCustomizationId(customizationIdNew);
			widgetResponse.setStaticImageId(widget.getStaticImage().getId());
			widgetResponse.setTitle(widget.getWidgets().getTitle());
			widgetResponse.setWidgetId(widget.getWidgets().getId());
			widgetResponse.setGreyedImageId(widget.getGreyedImage().getId());
			widgetResponse.setFooter(widget.getWidgets().getFooter());
			widgetResponse.setDescription(widget.getWidgets().getDescription());
			widgetResponse.setLockIconMessage(widget.getWidgets().getLockIconMessage());
			if (widget.getWidgets().getInfo() != null) {
				widgetResponse.setInfo(new WidgetInfoResponseDTO(widget.getWidgets().getInfo()));
			}
			responseList.add(widgetResponse);
		});

		return responseList;
	}

	@Override
	public List<WidgetsResponseDTO> deleteWidgets(DeleteWidgetRequestDTO widgetsRequestDTO,
			HttpServletRequest httpServletRequest) throws JsonProcessingException, NotFoundException {
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "WidgetServiceImpl: deleteWidgets()=> {}", widgetsRequestDTO);
		Customizations customizations = retrieveCustomizationsForModify(httpServletRequest,
				widgetsRequestDTO.getLevel(), widgetsRequestDTO);
		customizationsWidgetsRepository.deleteByCustomizationsIdAndWidgetsWidgetTypesDescriptionAndWidgetsIdIn(
				customizations.getId(), widgetsRequestDTO.getType().toUpperCase(), widgetsRequestDTO.getWidgetIds());
		List<CustomizationsWidgets> customizationWidgetsList = customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescription(customizations.getId(),
						widgetsRequestDTO.getType().toUpperCase());
		Map<Integer, Integer> widgetsLevelList = widgetsLevelRepository
				.findAllByAssetLevel(widgetsRequestDTO.getLevel()).stream()
				.collect(Collectors.toMap(widgetLevel -> widgetLevel.getWidgets().getId(),
						widgetLevel -> widgetLevel.getStaticImage().getId()));

		userSettingsService.updateToMyDashboard(customizations.getUsers());
		return customizationWidgetsList.stream()
				.map(customizedWidget -> new WidgetsResponseDTO(customizedWidget.getWidgets().getId(),
						customizations.getId(), customizedWidget.getWidgets().getTitle(),
						customizedWidget.getOrderNumber(), true, true, customizedWidget.getWidgets().isPaidService(),
						widgetsLevelList.get(customizedWidget.getWidgets().getId()), null, null, null, null, null))
				.toList();
	}

	private Customizations retrieveCustomizationsForModify(HttpServletRequest httpServletRequest, String level,
			DeleteWidgetRequestDTO widgetsRequestDTO) throws NotFoundException {

		Users user = userSettingsService.extractSsoAndReturnUser(httpServletRequest);
		Customizations userCustomizations = customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(false, user.getSso(), level).orElse(null);
		if (userCustomizations != null) {
			validateIsDeleteAllAssets(widgetsRequestDTO, userCustomizations);
			return userCustomizations;
		}

		Settings defaultSettings = userSettingsService.getDefaultSettings();
		Customizations personaCustomizations = Optional
				.ofNullable(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(user.getPersonas(),
						level))
				.orElse(customizationsRepository
						.findByUsersSsoIsNullAndPersonasAndAssetLevel(defaultSettings.getDefaultPersonas(), level));

		if (personaCustomizations == null) {
			throw new NotFoundException(ExceptionConstants.CUSTOMIZATION_NOT_FOUND);
		}

		validateIsDeleteAllAssets(widgetsRequestDTO, personaCustomizations);

		List<CustomizationsWidgets> widgetCustomizations = customizationsWidgetsRepository
				.findAllByCustomizations(personaCustomizations);
		Customizations newCustomizations = new Customizations();
		newCustomizations.setUsers(user);
		newCustomizations.setAssetLevel(level);
		newCustomizations.setDefault(false);
		newCustomizations.setDateRange(DashboardConstants.DATE_RANGE_3M);
		customizationsRepository.save(newCustomizations);
		saveWidgets(widgetCustomizations, newCustomizations);

		return newCustomizations;
	}

	private void validateIsDeleteAllAssets(DeleteWidgetRequestDTO widgetsRequestDTO,
			Customizations personaCustomizations) {
		List<CustomizationsWidgets> customizationWidgetsList = customizationsWidgetsRepository
				.findByCustomizationsIdAndWidgetsWidgetTypesDescriptionAndWidgetsStatusesStatusIndicator(
						personaCustomizations.getId(), widgetsRequestDTO.getType().toUpperCase(),
						DashboardConstants.ACTIVE_STATUS_INDICATOR);
		List<Integer> existingWidgetIds = customizationWidgetsList.stream().map(CustomizationsWidgets::getWidgets)
				.map(Widgets::getId).collect(Collectors.toList());
		existingWidgetIds.removeAll(widgetsRequestDTO.getWidgetIds());
		if (existingWidgetIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
					ExceptionConstants.MSG_CANT_DELETE_ALL_WIDGET);
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getAccessibleVids(HttpServletRequest httpServletRequest, String level)
			throws JsonProcessingException, NotFoundException {
		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, Object> accessibleVidsAndLevel = dashboardService.getAccessibleVidsAndLevel(filteredHierarchy, null,
				false);
		List<String> accessibleVids = (List<String>) accessibleVidsAndLevel.get(level);

		if (accessibleVids == null) {
			throw new NoSuchElementException("Invalid VID");
		}
		return accessibleVids;
	}

	private void saveWidgets(List<CustomizationsWidgets> widgetsCustomizations, Customizations savedCustomization) {
		List<CustomizationsWidgets> newWidgetsCustomizations = new ArrayList<>();
		widgetsCustomizations.stream().forEach(data -> {
			CustomizationsWidgets widgetsCustomization = new CustomizationsWidgets();
			widgetsCustomization.setCustomizations(savedCustomization);
			widgetsCustomization.setOrderNumber(data.getOrderNumber());
			widgetsCustomization.setWidgets(data.getWidgets());
			newWidgetsCustomizations.add(widgetsCustomization);
		});
		customizationsWidgetsRepository.saveAll(newWidgetsCustomizations);
	}

}
