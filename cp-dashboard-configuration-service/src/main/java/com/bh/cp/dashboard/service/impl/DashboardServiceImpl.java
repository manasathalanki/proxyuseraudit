package com.bh.cp.dashboard.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.constants.ExceptionConstants;
import com.bh.cp.dashboard.constants.JSONUtilConstants;
import com.bh.cp.dashboard.dto.request.LayoutRequestDTO;
import com.bh.cp.dashboard.dto.response.AddAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.DeleteAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.HeaderResponseDTO;
import com.bh.cp.dashboard.dto.response.LayoutAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.LayoutResponseDTO;
import com.bh.cp.dashboard.dto.response.LayoutWidgetResponseDTO;
import com.bh.cp.dashboard.dto.response.UsersAssetsResponseDTO;
import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.CustomizationsAssets;
import com.bh.cp.dashboard.entity.CustomizationsWidgets;
import com.bh.cp.dashboard.entity.Settings;
import com.bh.cp.dashboard.entity.Users;
import com.bh.cp.dashboard.entity.WidgetsLevel;
import com.bh.cp.dashboard.repository.CustomizationsAssetsRepository;
import com.bh.cp.dashboard.repository.CustomizationsRepository;
import com.bh.cp.dashboard.repository.CustomizationsWidgetsRepository;
import com.bh.cp.dashboard.repository.WidgetsLevelRepository;
import com.bh.cp.dashboard.service.AssetHierarchyFilterService;
import com.bh.cp.dashboard.service.AssetService;
import com.bh.cp.dashboard.service.DashboardService;
import com.bh.cp.dashboard.service.UMSClientService;
import com.bh.cp.dashboard.service.UserSettingsService;
import com.bh.cp.dashboard.util.SortUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

@Service
public class DashboardServiceImpl implements DashboardService {

	private UMSClientService umsClientService;

	private UserSettingsService userSettingsService;

	private AssetHierarchyFilterService assetHierarchyFilterService;

	private CustomizationsRepository customizationsRepository;

	private CustomizationsWidgetsRepository customizationsWidgetsRepository;

	private CustomizationsAssetsRepository customizationsAssetsRepository;

	private WidgetsLevelRepository widgetsLevelRepository;

	private AssetService assetService;

	public DashboardServiceImpl(@Autowired UMSClientService umsClientService,
			@Autowired UserSettingsService userSettingsService,
			@Autowired AssetHierarchyFilterService assetHierarchyFilterService,
			@Autowired CustomizationsRepository customizationsRepository,
			@Autowired CustomizationsWidgetsRepository customizationsWidgetsRepository,
			@Autowired CustomizationsAssetsRepository customizationsAssetsRepository,
			@Autowired WidgetsLevelRepository widgetsLevelRepository, @Lazy @Autowired AssetService assetService) {
		super();
		this.umsClientService = umsClientService;
		this.userSettingsService = userSettingsService;
		this.assetHierarchyFilterService = assetHierarchyFilterService;
		this.customizationsRepository = customizationsRepository;
		this.customizationsWidgetsRepository = customizationsWidgetsRepository;
		this.customizationsAssetsRepository = customizationsAssetsRepository;
		this.widgetsLevelRepository = widgetsLevelRepository;
		this.assetService = assetService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public LayoutResponseDTO getUserDashboardLayout(HttpServletRequest httpServletRequest, LayoutRequestDTO requestDto)
			throws NotFoundException, IOException {

		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Users user = userSettingsService.extractSsoAndReturnUser(httpServletRequest);
		String vid = requestDto.getVid();
		boolean showSibilings = requestDto.isShowSiblings();

		if (requestDto.getVid() == null) {
			UsersAssetsResponseDTO userAssetsResponseDto = assetService.getDefaultAsset(httpServletRequest);
			if (userAssetsResponseDto.getVid() != null) {
				vid = userAssetsResponseDto.getVid();
				showSibilings = true;
			}
		}

		LayoutResponseDTO responseDTO = new LayoutResponseDTO();
		Map<String, Object> accessibleVidsAndLevel = getAccessibleVidsAndLevel(filteredHierarchy, vid, showSibilings);
		String level = (String) accessibleVidsAndLevel.get(DashboardConstants.LEVEL);
		List<String> accessibleVids = (List<String>) accessibleVidsAndLevel.get(level);
		responseDTO.setSelectedVid(showSibilings ? vid : null);
		responseDTO.setParentVid(
				showSibilings ? (String) accessibleVidsAndLevel.getOrDefault(DashboardConstants.PARENTVID, null) : vid);

		Customizations customization = getCustomizationsForView(user, level);
		List<CustomizationsAssets> customizedAssetsList = getCustomizedAssets(customization);

		setHeaderResponse(responseDTO, user);
		responseDTO.setMyDashboard(user.getPersonas() != null
				&& user.getPersonas().getId().equals(DashboardConstants.MY_DASHBOARD_PERSONA_ID));
		responseDTO.setLevel(level);
		responseDTO.setCustomizationId(customization.getId() != null ? customization.getId() : null);
		setAssetResponse(responseDTO, filteredHierarchy, customization, customizedAssetsList, accessibleVids, false);
		setWidgetResponse(responseDTO, customization, level, DashboardConstants.KPI);
		setWidgetResponse(responseDTO, customization, level, DashboardConstants.SUMMARY);
		responseDTO.setDateRange(customization.getDateRange() != null ? customization.getDateRange() : null);
		return responseDTO;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAccessibleVidsAndLevel(List<Map<String, Object>> filteredHierarchy, String vid,
			boolean showSibilings) throws NotFoundException {
		Map<String, Object> assetsMap = assetHierarchyFilterService.getAssetsMap(filteredHierarchy, vid, true);
		if (vid != null && !(boolean) assetsMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false)) {
			throw new AccessDeniedException("Asset is not Accessible");
		}

		if (showSibilings) {
			String parentLevel = (String) assetsMap.getOrDefault(JSONUtilConstants.PREVIOUSLEVEL, null);
			String parentVid = ((List<String>) assetsMap.getOrDefault(parentLevel, new ArrayList<>())).stream()
					.findFirst().orElse(null);
			assetsMap = assetHierarchyFilterService.getAssetsMap(filteredHierarchy, parentVid, true);
			assetsMap.put(DashboardConstants.PARENTVID, parentVid);
		}

		String level = (String) assetsMap.getOrDefault(JSONUtilConstants.NEXTLEVEL, null);
		if (level == null) {
			throw new NotFoundException(ExceptionConstants.CHILDREN_NOT_FOUND);
		}
		assetsMap.put(DashboardConstants.LEVEL, level);

		return assetsMap;
	}

	@Override
	public Customizations getCustomizationsForView(Users user, String level) throws NotFoundException {

		Settings defaultSettings = userSettingsService.getDefaultSettings();
		if (defaultSettings.getMyDashboardPersona().equals(user.getPersonas())) {
			Customizations userCustomizations = customizationsRepository
					.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(false, user.getSso(), level).orElse(null);
			if (userCustomizations != null) {
				return userCustomizations;
			}
		}

		return Optional
				.ofNullable(customizationsRepository.findByUsersSsoIsNullAndPersonasAndAssetLevel(user.getPersonas(),
						level))
				.orElse(customizationsRepository
						.findByUsersSsoIsNullAndPersonasAndAssetLevel(defaultSettings.getDefaultPersonas(), level));

	}

	@Override
	public Customizations getCustomizationsForModify(HttpServletRequest httpServletRequest, Integer customizationId,
			String level) throws NotFoundException {

		Users user = userSettingsService.extractSsoAndReturnUser(httpServletRequest);
		Customizations defaultCustomizations = customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(true, user.getSso(), level).orElse(null);
		if (defaultCustomizations != null) {
			return defaultCustomizations;
		}

		Customizations userCustomizations = customizationsRepository
				.findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(false, user.getSso(), level).orElse(null);
		if (userCustomizations != null) {
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

		List<CustomizationsWidgets> widgetCustomizations = customizationsWidgetsRepository
				.findAllByCustomizations(personaCustomizations);
		Customizations newCustomizations = new Customizations();
		newCustomizations.setUsers(user);
		newCustomizations.setAssetLevel(level);
		newCustomizations.setDefault(false);
		newCustomizations.setDateRange(DashboardConstants.DATE_RANGE_3M);
		customizationsRepository.save(newCustomizations);
		saveCustomizationsWidgets(widgetCustomizations, newCustomizations);

		return newCustomizations;
	}

	@Override
	public List<CustomizationsAssets> getCustomizedAssets(Customizations customization) {
		return Optional.ofNullable(customizationsAssetsRepository.findByCustomizations(customization))
				.orElse(new ArrayList<>());
	}

	@Override
	public void saveCustomizationsWidgets(List<CustomizationsWidgets> widgetsCustomizations,
			Customizations savedCustomization) {
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

	@Override
	public void saveCustomizationsAssets(List<CustomizationsAssets> existingCustomizations,
			Customizations savedCustomization) {
		List<CustomizationsAssets> newAssetCustomizations = new ArrayList<>();
		existingCustomizations.stream().forEach(data -> {
			CustomizationsAssets newCustomization = new CustomizationsAssets();
			newCustomization.setOrderNumber(data.getOrderNumber());
			newCustomization.setVid(data.getVid());
			newCustomization.setAssetName(data.getAssetName());
			newCustomization.setCustomizations(savedCustomization);
			newAssetCustomizations.add(newCustomization);
		});
		customizationsAssetsRepository.saveAll(newAssetCustomizations);
	}

	private void setHeaderResponse(LayoutResponseDTO responseDTO, Users user) {

		HeaderResponseDTO headerDto = new HeaderResponseDTO();
		if (user.getCompanies() != null) {
			headerDto.setCustomerName(user.getCompanies().getName());
			headerDto.setCompanyLogo(user.getCompanies().getIconImages().getId());
		}
		headerDto.setDateTime(Instant.now().toString());
		responseDTO.setHeader(headerDto);
	}

	@Override
	public void setAssetResponse(Object responseDTO, List<Map<String, Object>> filteredHierarchy,
			Customizations customization, List<CustomizationsAssets> customizedAssetsList, List<String> accessibleVids,
			boolean showAllAssets) throws JsonProcessingException {

		String level = customization != null ? customization.getAssetLevel() : JSONUtilConstants.LEVEL_PROJECTS;
		Map<String, String> displayNameMap = assetHierarchyFilterService.getDisplayNameMap(filteredHierarchy);
		Map<String, String> imageSrcMap = assetHierarchyFilterService.getImageSrcMap(filteredHierarchy);
		Map<String, String> idMap = assetHierarchyFilterService.getIdMap(filteredHierarchy);
		Map<String, String> customerNameMap = assetHierarchyFilterService.getCustomerNameMap(filteredHierarchy,
				accessibleVids);
		Map<String, CustomizationsAssets> customizedAssetsMap = customizedAssetsList.stream()
				.collect(Collectors.toMap(CustomizationsAssets::getVid, asset -> asset));

		if (accessibleVids.isEmpty()) {
			return;
		}

		List<LayoutAssetResponseDTO> assetList = new ArrayList<>();
		List<String> addedAssets = new ArrayList<>();
		AtomicInteger notCustomizedAssetIndex = new AtomicInteger(0);
		accessibleVids.stream().forEach(vid -> {
			if (customizedAssetsMap.containsKey(vid)) {
				CustomizationsAssets customizedAsset = customizedAssetsMap.get(vid);
				addedAssets.add(vid);
				assetList.add(new LayoutAssetResponseDTO(customizedAsset,
						customizedAsset.getAssetName() != null ? customizedAsset.getAssetName()
								: displayNameMap.get(vid),
						customerNameMap.get(vid), imageSrcMap.get(vid), level, true));
			} else {
				LayoutAssetResponseDTO assetResponseDTO = new LayoutAssetResponseDTO();
				assetResponseDTO.setVid(vid);
				assetResponseDTO.setOrderNumber(showAllAssets ? 0 : notCustomizedAssetIndex.incrementAndGet());
				assetResponseDTO.setIsSelected(false);
				assetResponseDTO.setTitle(displayNameMap.get(vid));
				assetResponseDTO.setImageId(LayoutAssetResponseDTO.assetImage(vid));
				assetResponseDTO.setCustomerName(customerNameMap.get(vid));
				assetResponseDTO.setImageSrc(imageSrcMap.get(vid));
				assetResponseDTO.setDefaultImageSrc(LayoutAssetResponseDTO.assetDefaultImage(level));
				assetList.add(assetResponseDTO);
			}
		});

		if (addedAssets.isEmpty() && showAllAssets) {
			assetList.forEach(assetResponseDTO -> assetResponseDTO.setIsSelected(true));
		} else if (!addedAssets.isEmpty() && !showAllAssets) {
			assetList.removeIf(assetResponseDTO -> assetResponseDTO.getIsSelected().equals(false));
		}

		Collections.sort(assetList, Comparator.comparing(LayoutAssetResponseDTO::getTitle));

		addAssetsToDTO(responseDTO, assetList, displayNameMap, idMap);

	}

	private void addAssetsToDTO(Object responseDTO, List<LayoutAssetResponseDTO> assetList,
			Map<String, String> displayNameMap, Map<String, String> idMap) {
		if (responseDTO instanceof LayoutResponseDTO layoutResponseDto) {
			layoutResponseDto.setAssets(assetList);
			String selectedVid = layoutResponseDto.getSelectedVid();
			if (selectedVid == null || assetList.stream().noneMatch(asset -> asset.getVid().equals(selectedVid))) {
				layoutResponseDto.setSelectedVid(!assetList.isEmpty() ? assetList.get(0).getVid() : null);
			}

			layoutResponseDto.setSelectedId(idMap.get(layoutResponseDto.getSelectedVid()));
			layoutResponseDto.setSelectedTitle(displayNameMap.get(layoutResponseDto.getSelectedVid()));
		} else if (responseDTO instanceof AddAssetResponseDTO addAssetResponseDTO) {
			addAssetResponseDTO.setAssets(assetList);
		} else if (responseDTO instanceof DeleteAssetResponseDTO deleteAssetResponseDTO) {
			deleteAssetResponseDTO.setAssets(assetList);
		}
	}

	private void setWidgetResponse(LayoutResponseDTO responseDTO, Customizations customization, String level,
			String widgetType) {

		List<LayoutWidgetResponseDTO> widgetResponseList = new ArrayList<>();
		List<CustomizationsWidgets> customizedWidgetsList = customizationsWidgetsRepository
				.findAllByCustomizationsAndAndWidgetsWidgetTypesDescription(customization, widgetType);
		if (!customizedWidgetsList.isEmpty()) {
			customizedWidgetsList.stream().forEach(customizedWidget -> {
				WidgetsLevel widgetLevel = widgetsLevelRepository
						.findByAssetLevelAndWidgetsIdAndWidgetsStatusesStatusIndicator(level,
								customizedWidget.getWidgets().getId(), DashboardConstants.ACTIVE_STATUS_INDICATOR)
						.orElse(null);
				if (widgetLevel == null) {
					return;
				}
				widgetResponseList.add(new LayoutWidgetResponseDTO(customizedWidget, widgetLevel));
			});
		} else {
			AtomicInteger nonCustomizedWidget = new AtomicInteger(0);
			widgetsLevelRepository
					.findByAssetLevelAndWidgetsWidgetTypesDescriptionAndWidgetsStatusesStatusIndicator(level,
							widgetType, DashboardConstants.ACTIVE_STATUS_INDICATOR)
					.stream().forEach(currWidgetLevel -> {
						LayoutWidgetResponseDTO widgetResponse = new LayoutWidgetResponseDTO(
								currWidgetLevel.getWidgets(), currWidgetLevel);
						widgetResponse.setOrderNumber(nonCustomizedWidget.incrementAndGet());
						widgetResponseList.add(widgetResponse);
					});
		}

		List<LayoutWidgetResponseDTO> widgetResponseListOrdered = customization.isReordered() ? widgetResponseList
				: SortUtil.sortBySpanWidth(widgetResponseList);

		if (widgetType.equals(DashboardConstants.KPI)) {
			responseDTO.setKpis(widgetResponseListOrdered);
		} else if (widgetType.equals(DashboardConstants.SUMMARY)) {
			responseDTO.setSummary(widgetResponseListOrdered);
		}
	}

}