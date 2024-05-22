package com.bh.cp.user.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.bh.cp.user.constants.JSONUtilConstants;
import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.request.AssetHierarchyRequestDTO;
import com.bh.cp.user.dto.request.WidgetAccessRequestDTO;
import com.bh.cp.user.dto.request.WidgetApplicableRequestDTO;
import com.bh.cp.user.dto.response.AssetResponseDTO;
import com.bh.cp.user.dto.response.WidgetAccessResponseDTO;
import com.bh.cp.user.dto.response.WidgetApplicableResponseDTO;
import com.bh.cp.user.entity.Users;
import com.bh.cp.user.entity.Widgets;
import com.bh.cp.user.entity.WidgetsFieldsApplicability;
import com.bh.cp.user.exception.AttributeNotFoundException;
import com.bh.cp.user.respository.UsersRepository;
import com.bh.cp.user.respository.WidgetsAdvanceServicesApplicabilityRepository;
import com.bh.cp.user.respository.WidgetsFieldsApplicabilityRepository;
import com.bh.cp.user.respository.WidgetsRepository;
import com.bh.cp.user.service.AccessService;
import com.bh.cp.user.service.GenericAssetHierarchyFilterService;
import com.bh.cp.user.service.UserAssetHierarchyService;
import com.bh.cp.user.util.JSONUtil;
import com.bh.cp.user.util.JwtUtil;
import com.bh.cp.user.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

@Service
public class UserAssetHierarchyServiceImpl implements UserAssetHierarchyService {

	private static final Logger logger = LoggerFactory.getLogger(UserAssetHierarchyServiceImpl.class);

	private JwtUtil jwtUtil;

	private AccessService accessService;

	private GenericAssetHierarchyFilterService genericAssetHierarchyFilterService;

	private UsersRepository usersRepository;

	private WidgetsRepository widgetsRepository;

	private WidgetsFieldsApplicabilityRepository widgetsFieldsApplicabilityRepository;

	private WidgetsAdvanceServicesApplicabilityRepository widgetsAdvanceServicesApplicabilityRepository;

	public UserAssetHierarchyServiceImpl(@Autowired JwtUtil jwtUtil, @Autowired AccessService accessService,
			@Autowired GenericAssetHierarchyFilterService genericAssetHierarchyFilterService,
			@Autowired UsersRepository usersRepository, @Autowired WidgetsRepository widgetsRepository,
			@Autowired WidgetsFieldsApplicabilityRepository widgetsFieldsApplicabilityRepository,
			@Autowired WidgetsAdvanceServicesApplicabilityRepository widgetsAdvanceServicesApplicabilityRepository) {
		super();
		this.jwtUtil = jwtUtil;
		this.accessService = accessService;
		this.genericAssetHierarchyFilterService = genericAssetHierarchyFilterService;
		this.usersRepository = usersRepository;
		this.widgetsRepository = widgetsRepository;
		this.widgetsFieldsApplicabilityRepository = widgetsFieldsApplicabilityRepository;
		this.widgetsAdvanceServicesApplicabilityRepository = widgetsAdvanceServicesApplicabilityRepository;
	}

	@Override
	public WidgetAccessResponseDTO checkWidgetAccess(HttpServletRequest httpServletRequest,
			WidgetAccessRequestDTO requestDto) throws AttributeNotFoundException, NotFoundException,
			InterruptedException, ExecutionException, IOException {
		List<Map<String, Object>> userAssetHierarchy = accessService
				.getCurrentUserFilteredHierarchy(httpServletRequest);
		boolean matchFound = genericAssetHierarchyFilterService.validateVid(userAssetHierarchy, requestDto.getVid());
		if (!matchFound) {
			throw new AccessDeniedException("Asset of provided VID is not Accessible");
		}

		List<String> privileges = accessService.getCurrentUserPrivileges(httpServletRequest);
		String sso = SecurityUtil.getSSO(SecurityUtil.getClaims(httpServletRequest, jwtUtil));
		return validateWidgetAccess(userAssetHierarchy, privileges, requestDto.getVid(), requestDto.getWidgetId(), sso);
	}

	private WidgetAccessResponseDTO validateWidgetAccess(List<Map<String, Object>> userAssetHierarchy,
			List<String> privileges, String vid, Integer widgetId, String sso) throws NotFoundException {

		WidgetAccessResponseDTO response = new WidgetAccessResponseDTO();
		Widgets widget = widgetsRepository.findById(widgetId).orElse(null);

		if (widget == null || !widget.isPaidService()) {
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "Either Widget is not Found or widget is not paid -- {}",
					widgetId);
			return response;
		}

		Users user = usersRepository.findBySso(sso)
				.orElseThrow(() -> new NotFoundException("User not found in database"));
		if (user.getPersonas() != null && user.getPersonas().getDescription().equals("Show only active service")) {
			response.setActiveServicesPersona(true);
		}

		Map<String, Map<String, Set<String>>> techEquipCodeAndEnabledServicesMap = genericAssetHierarchyFilterService
				.getFieldsAndEnabledServicesToMap(userAssetHierarchy);
		Map<String, Set<String>> vidMap = techEquipCodeAndEnabledServicesMap.get(vid);
		Set<String> enabledServices = vidMap.getOrDefault(JSONUtilConstants.ENABLEDSERVICES, new HashSet<>());
		Set<String> technologyCodes = vidMap.getOrDefault(JSONUtilConstants.TECHNOLOGYCODES, new HashSet<>());
		Set<String> equipmentCodes = vidMap.getOrDefault(JSONUtilConstants.EQUIPMENTCODES, new HashSet<>());
		Set<String> lowNoxs = vidMap.getOrDefault(JSONUtilConstants.LOWNOXS, new HashSet<>());

		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Checking Applicability for Widget {} with provided vid {}",
				widgetId, vid);
		List<WidgetsFieldsApplicability> widgetsFieldsApplicability = widgetsFieldsApplicabilityRepository
				.findByWidgetsId(widget.getId());

		response.setApplicability(
				checkFieldApplicability(technologyCodes, equipmentCodes, lowNoxs, widgetsFieldsApplicability));
		if (!response.isApplicability()) {
			response.setEnabled(false);
			response.setHasAccess(false);
			return response;
		}

		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Checking Enabled Services for Widget {} with provided vid {}",
				widgetId, vid);
		response.setEnabled(checkAdvanceServiceApplicability(widget.getId(), enabledServices));
		if (!response.isEnabled()) {
			response.setEnabled(false);
			response.setHasAccess(false);
			return response;
		}

		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Checking Keycloak Access for Widget {} with provided vid {}",
				widgetId, vid);
		response.setHasAccess(checkWidgetSubscribed(privileges, widget));

		return response;
	}

	@Override
	public Map<String, Boolean> checkWidgetAdvanceServicesAccess(HttpServletRequest httpServletRequest, WidgetAccessRequestDTO requestDTO)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		List<Map<String, Object>> userAssetHierarchy = accessService
				.getCurrentUserFilteredHierarchy(httpServletRequest);
		Map<String, Map<String, Set<String>>> techEquipCodeAndEnabledServicesMap = genericAssetHierarchyFilterService
				.getFieldsAndEnabledServicesToMap(userAssetHierarchy);
		Map<String, Set<String>> vidMap = techEquipCodeAndEnabledServicesMap.get(requestDTO.getVid());
		Set<String> enabledServices = vidMap.getOrDefault(JSONUtilConstants.ENABLEDSERVICES, new HashSet<>());
		return Map.of(JSONUtilConstants.ENABLEDSERVICES,checkAdvanceServiceApplicability(requestDTO.getWidgetId(), enabledServices));
	}

	private boolean checkFieldApplicability(Set<String> technologyCodes, Set<String> equipmentCodes,
			Set<String> lowNoxs, List<WidgetsFieldsApplicability> widgetsFieldsApplicability) {
		if (!widgetsFieldsApplicability.isEmpty()) {
			for (WidgetsFieldsApplicability row : widgetsFieldsApplicability) {
				String technologyCode = row.getTechnologyCode();
				String equipmentCode = row.getEquipmentCode();
				String lowNox = row.getLowNox();

				boolean technologyCodeMatched = technologyCode == null
						|| technologyCodes.stream().anyMatch(field -> field.equals(technologyCode));
				boolean equipmentCodeMatched = equipmentCode == null
						|| equipmentCodes.stream().anyMatch(field -> field.startsWith(equipmentCode));
				boolean lowNoxMatched = lowNox == null || lowNoxs.stream().anyMatch(field -> field.contains(lowNox));

				if (technologyCodeMatched && equipmentCodeMatched && lowNoxMatched) {
					return true;
				}
			}
		} else {
			return true;
		}

		return false;
	}

	private boolean checkAdvanceServiceApplicability(Integer widgetId, Set<String> enabledServices) {
		return widgetsAdvanceServicesApplicabilityRepository.matchEnabledServicesForWidget(widgetId, enabledServices);
	}

	public boolean checkFieldMatch(Set<String> hierarchyFields, String fieldRegex) {
		return fieldRegex == null || hierarchyFields.stream().anyMatch(field -> field.matches(fieldRegex));
	}

	@Override
	public Map<String, Boolean> checkWidgetKeycloakAccess(HttpServletRequest httpServletRequest, Integer widgetId)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException {
		List<String> privileges = accessService.getCurrentUserPrivileges(httpServletRequest);
		Map<String, Boolean> outputMap = new HashMap<>();
		outputMap.put(UMSConstants.SUBSCRIBED, false);

		Widgets widget = widgetsRepository.findById(widgetId).orElse(null);
		if (widget == null) {
			return outputMap;
		}

		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Checking Keycloak Access for Widget {}", widgetId);
		outputMap.put(UMSConstants.SUBSCRIBED, checkWidgetSubscribed(privileges, widget));
		return outputMap;

	}

	private boolean checkWidgetSubscribed(List<String> privileges, Widgets widget) {
		if (!widget.isPaidService()) {
			return true;
		}

		if (widget.getIdmPrivilege().contains("||")) {
			return Stream.of(widget.getIdmPrivilege().split("\\|\\|")).anyMatch(privileges::contains);
		} else if (widget.getIdmPrivilege().contains("&&")) {
			return privileges.containsAll(Arrays.asList(widget.getIdmPrivilege().split("&&")));
		}

		return privileges.contains(widget.getIdmPrivilege());
	}

	@Override
	public List<Map<String, Object>> getUserAssetHierarchyChildren(HttpServletRequest httpServletRequest,
			AssetHierarchyRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		List<Map<String, Object>> userAssetHierarchy = accessService
				.getCurrentUserFilteredHierarchy(httpServletRequest);
		return genericAssetHierarchyFilterService.getSubTree(userAssetHierarchy, requestDto.getVid());
	}

	@Override
	public Map<String, String> getUserAssetHierarchyField(HttpServletRequest httpServletRequest,
			AssetHierarchyRequestDTO requestDto, boolean fetchFromParent)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		List<Map<String, Object>> userAssetHierarchy = accessService
				.getCurrentUserFilteredHierarchy(httpServletRequest);
		Map<String, String> outputMap = new HashMap<>();
		if (requestDto.getVid() == null || requestDto.getField() == null) {
			return outputMap;
		}

		outputMap.put(requestDto.getField(), genericAssetHierarchyFilterService
				.getImmediateParentField(userAssetHierarchy, requestDto.getVid(), requestDto.getField()));
		return outputMap;
	}

	@Override
	public AssetResponseDTO getUserAssetHierarchyAssets(HttpServletRequest httpServletRequest,
			AssetHierarchyRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {
		List<Map<String, Object>> userAssetHierarchy = accessService
				.getCurrentUserFilteredHierarchy(httpServletRequest);
		return new AssetResponseDTO(genericAssetHierarchyFilterService.getAssetsMap(userAssetHierarchy,
				requestDto.getVid(), requestDto.getField()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public WidgetApplicableResponseDTO getUserApplicableMachinesForWidget(HttpServletRequest httpServletRequest,
			WidgetApplicableRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {

		Integer widgetId = requestDto.getWidgetId();
		List<Map<String, Object>> userAssetHierarchy = accessService
				.getCurrentUserFilteredHierarchy(httpServletRequest);

		Map<String, Object> assetsMap = genericAssetHierarchyFilterService.getAssetsMap(userAssetHierarchy,
				requestDto.getVid(), JSONUtilConstants.VID);

		Widgets widget = widgetsRepository.findById(widgetId).orElse(null);
		if (widget == null) {
			return null;
		}

		Map<String, Map<String, Set<String>>> techEquipCodeAndEnabledServicesMap = genericAssetHierarchyFilterService
				.getFieldsAndEnabledServicesToMap(userAssetHierarchy);
		List<WidgetsFieldsApplicability> widgetsFieldsApplicability = widgetsFieldsApplicabilityRepository
				.findByWidgetsId(widgetId);
		List<String> machinesVidList = ((List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_MACHINES,
				new ArrayList<>()));
		if (widget.isPaidService()) {
			machinesVidList.removeIf(vid -> {
				Map<String, Set<String>> vidMap = techEquipCodeAndEnabledServicesMap.get(vid);
				Set<String> enabledServices = vidMap.getOrDefault(JSONUtilConstants.ENABLEDSERVICES, new HashSet<>());
				Set<String> technologyCodes = vidMap.getOrDefault(JSONUtilConstants.TECHNOLOGYCODES, new HashSet<>());
				Set<String> equipmentCodes = vidMap.getOrDefault(JSONUtilConstants.EQUIPMENTCODES, new HashSet<>());
				Set<String> lowNoxs = vidMap.getOrDefault(JSONUtilConstants.LOWNOXS, new HashSet<>());
				boolean applicability = checkFieldApplicability(technologyCodes, equipmentCodes, lowNoxs,
						widgetsFieldsApplicability);
				boolean enabled = checkAdvanceServiceApplicability(widget.getId(), enabledServices);
				return !applicability || !enabled;
			});
		}

		if (requestDto.getField().equals(JSONUtilConstants.ID)) {
			List<String> outputList = new ArrayList<>();
			JSONUtil.getIdsForVid(userAssetHierarchy, machinesVidList, outputList);
			return new WidgetApplicableResponseDTO(outputList);
		}

		return new WidgetApplicableResponseDTO(machinesVidList);
	}

}
