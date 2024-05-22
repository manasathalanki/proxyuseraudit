package com.bh.cp.dashboard.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.constants.ExceptionConstants;
import com.bh.cp.dashboard.constants.JSONUtilConstants;
import com.bh.cp.dashboard.dto.request.AddAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.ChangeAssetNameRequestDTO;
import com.bh.cp.dashboard.dto.request.DeleteAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.GetAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.UsersDefaultAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.UsersFavoriteAssetsRequestDTO;
import com.bh.cp.dashboard.dto.response.AddAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.ChangeAssetNameResponseDTO;
import com.bh.cp.dashboard.dto.response.DeleteAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.UsersAssetsResponseDTO;
import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.CustomizationsAssets;
import com.bh.cp.dashboard.entity.CustomizationsWidgets;
import com.bh.cp.dashboard.entity.Users;
import com.bh.cp.dashboard.entity.UsersDefaultAssets;
import com.bh.cp.dashboard.entity.UsersFavoriteAssets;
import com.bh.cp.dashboard.repository.CustomizationsAssetsRepository;
import com.bh.cp.dashboard.repository.CustomizationsRepository;
import com.bh.cp.dashboard.repository.CustomizationsWidgetsRepository;
import com.bh.cp.dashboard.repository.UsersDefaultAssetsRepository;
import com.bh.cp.dashboard.repository.UsersFavoriteAssetsRepository;
import com.bh.cp.dashboard.service.AssetHierarchyFilterService;
import com.bh.cp.dashboard.service.AssetService;
import com.bh.cp.dashboard.service.DashboardService;
import com.bh.cp.dashboard.service.UMSClientService;
import com.bh.cp.dashboard.service.UserSettingsService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

@Service
public class AssetServiceImpl implements AssetService {

	private UMSClientService umsClientService;

	private UserSettingsService userSettingsService;

	private AssetHierarchyFilterService assetHierarchyFilterService;

	private DashboardService dashboardService;

	private CustomizationsRepository customizationsRepository;

	private CustomizationsWidgetsRepository customizationsWidgetsRepository;

	private CustomizationsAssetsRepository customizationsAssetsRepository;

	private UsersFavoriteAssetsRepository usersFavoriteAssetsRepository;

	private UsersDefaultAssetsRepository usersDefaultAssetsRepository;

	public AssetServiceImpl(@Autowired UMSClientService umsClientService,
			@Autowired UserSettingsService userSettingsService, @Autowired DashboardService dashboardService,
			@Autowired CustomizationsRepository customizationsRepository,
			@Autowired CustomizationsWidgetsRepository customizationsWidgetsRepository,
			@Autowired CustomizationsAssetsRepository customizationsAssetsRepository,
			@Autowired UsersFavoriteAssetsRepository usersFavoriteAssetsRepository,
			@Autowired UsersDefaultAssetsRepository usersDefaultAssetsRepository,
			@Autowired AssetHierarchyFilterService assetHierarchyFilterService) {
		super();
		this.umsClientService = umsClientService;
		this.userSettingsService = userSettingsService;
		this.dashboardService = dashboardService;
		this.assetHierarchyFilterService = assetHierarchyFilterService;
		this.customizationsRepository = customizationsRepository;
		this.customizationsWidgetsRepository = customizationsWidgetsRepository;
		this.customizationsAssetsRepository = customizationsAssetsRepository;
		this.usersFavoriteAssetsRepository = usersFavoriteAssetsRepository;
		this.usersDefaultAssetsRepository = usersDefaultAssetsRepository;
	}

	@Override
	@SuppressWarnings("unchecked")
	public AddAssetResponseDTO fetchAssets(HttpServletRequest httpServletRequest, GetAssetRequestDTO requestDto)
			throws JsonProcessingException, NotFoundException {

		Users user = userSettingsService.extractSsoAndReturnUser(httpServletRequest);
		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, Object> accessibleVidsAndLevel = dashboardService.getAccessibleVidsAndLevel(filteredHierarchy,
				requestDto.getParentVid(), false);
		String level = (String) accessibleVidsAndLevel.get(DashboardConstants.LEVEL);
		List<String> accessibleVids = (List<String>) accessibleVidsAndLevel.get(level);

		Customizations customization = dashboardService.getCustomizationsForView(user, level);
		List<CustomizationsAssets> customizedAssetsList = dashboardService.getCustomizedAssets(customization);

		AddAssetResponseDTO responseDTO = new AddAssetResponseDTO();
		responseDTO.setLevel(level);
		responseDTO.setCustomizationId(customization != null ? customization.getId() : null);
		dashboardService.setAssetResponse(responseDTO, filteredHierarchy, customization, customizedAssetsList,
				accessibleVids, true);
		return responseDTO;
	}

	@Override
	@SuppressWarnings("unchecked")
	public AddAssetResponseDTO addAssets(HttpServletRequest httpServletRequest, AddAssetRequestDTO requestDto)
			throws NotFoundException, JsonProcessingException {

		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, Object> accessibleVidsAndLevel = dashboardService.getAccessibleVidsAndLevel(filteredHierarchy,
				requestDto.getParentVid(), false);
		String level = (String) accessibleVidsAndLevel.get(DashboardConstants.LEVEL);
		List<String> accessibleVids = (List<String>) accessibleVidsAndLevel.get(level);

		List<CustomizationsAssets> newAssetCustomizations = requestDto.getAssets().stream()
				.filter(inputAsset -> inputAsset.getOrderNumber() != 0 && accessibleVids.contains(inputAsset.getVid()))
				.map(customizedAsset -> {
					CustomizationsAssets customizationAsset = new CustomizationsAssets();
					customizationAsset.setOrderNumber(customizedAsset.getOrderNumber());
					customizationAsset.setVid(customizedAsset.getVid());
					return customizationAsset;
				}).toList();

		if (newAssetCustomizations.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
					ExceptionConstants.MSG_CANT_DELETE_ALL_ASSETS);
		}

		Customizations customization = dashboardService.getCustomizationsForModify(httpServletRequest,
				requestDto.getCustomizationId(), level);

		List<CustomizationsAssets> existingAssetCustomization = customizationsAssetsRepository
				.findByCustomizations(customization);

		if (!existingAssetCustomization.isEmpty() && customization.getUsers() != null) {
			customizationsAssetsRepository.deleteAll(existingAssetCustomization.stream()
					.filter(existingCustomization -> accessibleVids.contains(existingCustomization.getVid())).toList());
		}

		for (CustomizationsAssets assetCustomization : newAssetCustomizations) {
			String assetName = existingAssetCustomization.stream()
					.filter(customizedAsset -> assetCustomization.getVid().equals(customizedAsset.getVid())
							&& customizedAsset.getAssetName() != null)
					.map(CustomizationsAssets::getAssetName).findFirst().orElse(null);
			assetCustomization.setAssetName(assetName);
			assetCustomization.setCustomizations(customization);
		}

		customizationsAssetsRepository.saveAll(newAssetCustomizations);
		userSettingsService.updateToMyDashboard(customization.getUsers());
		AddAssetResponseDTO responseDTO = new AddAssetResponseDTO();
		responseDTO.setLevel(level);
		responseDTO.setCustomizationId(customization.getId());
		dashboardService.setAssetResponse(responseDTO, filteredHierarchy, customization, newAssetCustomizations,
				accessibleVids, false);
		return responseDTO;
	}

	@Override
	@SuppressWarnings("unchecked")
	public DeleteAssetResponseDTO deleteAssets(HttpServletRequest httpServletRequest, DeleteAssetRequestDTO requestDto)
			throws NotFoundException, JsonProcessingException {

		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, Object> accessibleVidsAndLevel = dashboardService.getAccessibleVidsAndLevel(filteredHierarchy,
				requestDto.getParentVid(), false);
		String level = (String) accessibleVidsAndLevel.get(DashboardConstants.LEVEL);
		List<String> accessibleVids = (List<String>) accessibleVidsAndLevel.get(level);

		List<String> vidsToDeleted = requestDto.getVids();
		vidsToDeleted.retainAll(accessibleVids);

		if (vidsToDeleted.containsAll(accessibleVids)) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
					ExceptionConstants.MSG_CANT_DELETE_ALL_ASSETS);
		}

		Customizations customization = dashboardService.getCustomizationsForModify(httpServletRequest,
				requestDto.getCustomizationId(), level);

		List<CustomizationsAssets> existingAssetCustomization = customizationsAssetsRepository
				.findByCustomizationsAndVidIn(customization, accessibleVids);

		List<CustomizationsAssets> remainingAssetCustomizations = new ArrayList<>();
		List<CustomizationsAssets> assetCustomizationToDelete = new ArrayList<>();
		if (existingAssetCustomization.isEmpty()) {
			Integer order = 1;
			for (String vid : accessibleVids) {
				if (!vidsToDeleted.contains(vid)) {
					CustomizationsAssets customizationAsset = new CustomizationsAssets();
					customizationAsset.setOrderNumber(order);
					customizationAsset.setVid(vid);
					customizationAsset.setCustomizations(customization);
					customizationsAssetsRepository.save(customizationAsset);
					remainingAssetCustomizations.add(customizationAsset);
					order++;
				}
			}
		} else {
			for (CustomizationsAssets assetCustomization : existingAssetCustomization) {
				if (vidsToDeleted.contains(assetCustomization.getVid())) {
					assetCustomizationToDelete.add(assetCustomization);
				} else {
					remainingAssetCustomizations.add(assetCustomization);
				}
			}
		}

		if (remainingAssetCustomizations.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
					ExceptionConstants.MSG_CANT_DELETE_ALL_ASSETS);
		}

		customizationsAssetsRepository.deleteAll(assetCustomizationToDelete);
		userSettingsService.updateToMyDashboard(customization.getUsers());
		DeleteAssetResponseDTO responseDTO = new DeleteAssetResponseDTO();
		responseDTO.setLevel(level);
		responseDTO.setCustomizationId(customization.getId());
		dashboardService.setAssetResponse(responseDTO, filteredHierarchy, customization, remainingAssetCustomizations,
				accessibleVids, false);
		return responseDTO;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ChangeAssetNameResponseDTO assetNameCustomization(ChangeAssetNameRequestDTO changeAssetNameRequestDTO,
			HttpServletRequest httpServletRequest) throws JsonProcessingException, NotFoundException {

		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, Object> accessibleVidsAndLevel = dashboardService.getAccessibleVidsAndLevel(filteredHierarchy,
				changeAssetNameRequestDTO.getParentVid(), false);
		String level = (String) accessibleVidsAndLevel.get(DashboardConstants.LEVEL);
		List<String> accessibleVids = (List<String>) accessibleVidsAndLevel.get(level);

		Customizations customization = customizationsRepository
				.findByIdAndAssetLevel(changeAssetNameRequestDTO.getCustomizationId(), level)
				.orElseThrow(() -> new NotFoundException(ExceptionConstants.CUSTOMIZATION_NOT_FOUND));

		if (customization != null) {
			Users users = userSettingsService.extractSsoAndReturnUser(httpServletRequest);
			Customizations findByUsersSso = customizationsRepository.findByUsersSsoAndAssetLevel(users.getSso(), level);
			if (findByUsersSso != null) {
				return updateAssetBasedOnSsoCustomization(findByUsersSso, changeAssetNameRequestDTO, accessibleVids);
			}
			Customizations personaCustomization = customizationsRepository
					.findByUsersSsoIsNullAndPersonasIdAndAssetLevel(users.getPersonas().getId(), level);
			if (personaCustomization == null) {
				throw new NoSuchElementException(ExceptionConstants.CUSTOMIZATION_NOT_VALID);
			}
			return createAssetBasedOnSsoCustomization(customization, changeAssetNameRequestDTO, users, accessibleVids);
		} else {
			throw new NoSuchElementException(ExceptionConstants.CUSTOMIZATION_NOT_VALID);
		}

	}

	private ChangeAssetNameResponseDTO createAssetBasedOnSsoCustomization(Customizations personaCustomizations,
			ChangeAssetNameRequestDTO assetRequestDTO, Users users, List<String> listAssetsVid) {
		Customizations saveCustomization = new Customizations();
		saveCustomization.setDateRange(personaCustomizations.getDateRange());
		saveCustomization.setDefault(personaCustomizations.isDefault());
		saveCustomization.setAssetLevel(personaCustomizations.getAssetLevel());
		saveCustomization.setUsers(users);
		Customizations savedCustomization = customizationsRepository.save(saveCustomization);

		List<CustomizationsWidgets> widgetsCustomizations = customizationsWidgetsRepository
				.findByCustomizationsId(personaCustomizations.getId());

		saveAssetAndUpdateAssetName(listAssetsVid, assetRequestDTO, savedCustomization);

		dashboardService.saveCustomizationsWidgets(widgetsCustomizations, savedCustomization);

		ChangeAssetNameResponseDTO response = new ChangeAssetNameResponseDTO();
		response.setAssetName(assetRequestDTO.getAssetName());
		response.setCustomizationId(savedCustomization.getId());
		response.setLevel(savedCustomization.getAssetLevel());
		response.setVid(assetRequestDTO.getVid());

		return response;
	}

	private void saveAssetAndUpdateAssetName(List<String> listAssetsVid, ChangeAssetNameRequestDTO assetRequestDTO,
			Customizations savedCustomization) {
		int count = 0;
		for (String vid : listAssetsVid) {
			CustomizationsAssets customizationAssets = new CustomizationsAssets();
			if (assetRequestDTO.getVid().equals(vid)) {
				customizationAssets.setAssetName(assetRequestDTO.getAssetName());
			} else {
				customizationAssets.setAssetName(null);
			}
			customizationAssets.setCustomizations(savedCustomization);
			customizationAssets.setOrderNumber(++count);
			customizationAssets.setVid(vid);
			customizationsAssetsRepository.save(customizationAssets);
		}

	}

	private ChangeAssetNameResponseDTO updateAssetBasedOnSsoCustomization(Customizations customization,
			ChangeAssetNameRequestDTO assetRequestDTO, List<String> listAssetsVid) {
		ChangeAssetNameResponseDTO response = new ChangeAssetNameResponseDTO();
		CustomizationsAssets customizationAsset = customizationsAssetsRepository
				.findByCustomizationsIdAndVid(customization.getId(), assetRequestDTO.getVid());

		if (customizationAsset != null) {
			customizationAsset.setAssetName(assetRequestDTO.getAssetName());
			CustomizationsAssets savedCustomizationAsset = customizationsAssetsRepository.save(customizationAsset);
			response.setAssetName(savedCustomizationAsset.getAssetName());
			response.setCustomizationId(savedCustomizationAsset.getCustomizations().getId());
			response.setLevel(assetRequestDTO.getLevel());
			response.setVid(savedCustomizationAsset.getVid());
		} else {
			saveAssetAndUpdateAssetName(listAssetsVid, assetRequestDTO, customization);
			response.setAssetName(assetRequestDTO.getAssetName());
			response.setCustomizationId(customization.getId());
			response.setLevel(assetRequestDTO.getLevel());
			response.setVid(assetRequestDTO.getVid());
		}
		return response;
	}

	@Override
	public UsersAssetsResponseDTO storeFavoriteAssets(HttpServletRequest httpServletRequest,
			UsersFavoriteAssetsRequestDTO requestDto) throws NotFoundException, JsonProcessingException {

		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, Object> assetsMap = assetHierarchyFilterService.getAssetsMap(filteredHierarchy, requestDto.getVid(),
				true);
		if (!(boolean) assetsMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false)) {
			throw new AccessDeniedException("Asset is not Accessible");
		}

		Users user = userSettingsService.extractSsoAndReturnUser(httpServletRequest);
		UsersAssetsResponseDTO response = new UsersAssetsResponseDTO();
		response.setVid(requestDto.getVid());
		UsersFavoriteAssets existingFavoriteAsset = usersFavoriteAssetsRepository.findByUsersAndVid(user,
				requestDto.getVid());
		response.setStatus("No Action Performed");
		if (requestDto.isMarkAsFavorite()) {
			if (existingFavoriteAsset != null) {
				response.setStatus("Already added vid in Favorite Assets");
				return response;
			}

			UsersFavoriteAssets newFavoriteAsset = new UsersFavoriteAssets();
			newFavoriteAsset.setUsers(user);
			newFavoriteAsset.setVid(requestDto.getVid());
			usersFavoriteAssetsRepository.save(newFavoriteAsset);
			response.setStatus("Added vid to Favorite Assets");
		} else if (existingFavoriteAsset != null) {
			usersFavoriteAssetsRepository.delete(existingFavoriteAsset);
			response.setStatus("Removed vid from Favorite Assets");
		}

		return response;
	}

	@Override
	public List<UsersAssetsResponseDTO> getAllFavoriteAssets(HttpServletRequest httpServletRequest)
			throws NotFoundException, JsonProcessingException {
		Users user = userSettingsService.extractSsoAndReturnUser(httpServletRequest);
		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, String> displayNameMap = assetHierarchyFilterService.getDisplayNameMap(filteredHierarchy);
		List<UsersFavoriteAssets> assets = usersFavoriteAssetsRepository.findAllByUsers(user);
		return assets.stream().filter(asset -> {
			if (displayNameMap.get(asset.getVid()) == null) {
				usersFavoriteAssetsRepository.delete(asset);
				return false;
			}
			return true;
		}).map(asset -> new UsersAssetsResponseDTO(asset.getVid(), displayNameMap.get(asset.getVid()),
				asset.getUsers().getSso())).toList();
	}

	@Override
	public UsersAssetsResponseDTO setDefaultAsset(HttpServletRequest httpServletRequest,
			UsersDefaultAssetRequestDTO requestDto) throws NotFoundException, JsonProcessingException {

		UsersAssetsResponseDTO response = new UsersAssetsResponseDTO();
		Users user = userSettingsService.extractSsoAndReturnUser(httpServletRequest);
		UsersDefaultAssets existingUserDefaultAsset = usersDefaultAssetsRepository.findByUsersSso(user.getSso())
				.orElse(null);
		if (existingUserDefaultAsset != null) {
			usersDefaultAssetsRepository.delete(existingUserDefaultAsset);
		}

		String defaultVid = requestDto.getDefaultVid();
		if (defaultVid == null) {
			response.setStatus("Default Asset removed");
			return response;
		}

		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, Object> assetsMap = assetHierarchyFilterService.getAssetsMap(filteredHierarchy, defaultVid, true);
		if (!(boolean) assetsMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false)) {
			throw new AccessDeniedException("Asset is not Accessible");
		}

		UsersDefaultAssets newUserDefaultAsset = new UsersDefaultAssets();
		newUserDefaultAsset.setUsers(user);
		newUserDefaultAsset.setDefaultVid(defaultVid);
		usersDefaultAssetsRepository.save(newUserDefaultAsset);
		response.setVid(defaultVid);
		response.setStatus("Default Asset added");
		return response;
	}

	@Override
	public UsersAssetsResponseDTO getDefaultAsset(HttpServletRequest httpServletRequest)
			throws NotFoundException, JsonProcessingException {
		List<Map<String, Object>> filteredHierarchy = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, String> displayNameMap = assetHierarchyFilterService.getDisplayNameMap(filteredHierarchy);
		Users user = userSettingsService.extractSsoAndReturnUser(httpServletRequest);
		UsersDefaultAssets userDefaultAsset = usersDefaultAssetsRepository.findByUsersSso(user.getSso()).orElse(null);
		if (userDefaultAsset != null) {
			if (displayNameMap.get(userDefaultAsset.getDefaultVid()) != null) {
				return new UsersAssetsResponseDTO(userDefaultAsset.getDefaultVid(),
						displayNameMap.get(userDefaultAsset.getDefaultVid()), user.getSso());
			} else {
				usersDefaultAssetsRepository.delete(userDefaultAsset);
			}
		}
		return new UsersAssetsResponseDTO();
	}
}