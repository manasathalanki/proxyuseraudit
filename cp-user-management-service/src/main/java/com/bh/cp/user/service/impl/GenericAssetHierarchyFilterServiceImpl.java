package com.bh.cp.user.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bh.cp.user.constants.JSONUtilConstants;
import com.bh.cp.user.service.FetchAssetHierarchyService;
import com.bh.cp.user.service.GenericAssetHierarchyFilterService;
import com.bh.cp.user.util.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class GenericAssetHierarchyFilterServiceImpl implements GenericAssetHierarchyFilterService {

	private static final Logger logger = LoggerFactory.getLogger(GenericAssetHierarchyFilterServiceImpl.class);

	private final ObjectMapper mapper = new ObjectMapper();

	private FetchAssetHierarchyService fetchAssetHierarchyService;

	public GenericAssetHierarchyFilterServiceImpl(@Autowired FetchAssetHierarchyService fetchAssetHierarchyService) {
		super();
		this.fetchAssetHierarchyService = fetchAssetHierarchyService;
	}

	@Override
	public List<Map<String, Object>> getFilteredHierarchy(List<String> vids, boolean compact)
			throws IOException {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		List<Map<String, Object>> fullHierarchy = fetchAssetHierarchyService.callAssetHierarchyAPIv2();
		List<Map<String, Object>> filtered = JSONUtil.filterList(fullHierarchy, vids);
		setAdditionalFieldsToHierarchy(filtered, compact);
		return filtered;
	}

	@Override
	public void setAdditionalFieldsToHierarchy(List<Map<String, Object>> assetHierarchy, boolean compact) {
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addTechEquipCodeAndEnabledService(assetHierarchy, null);
		JSONUtil.addDisplayName(assetHierarchy, compact);
	}

	@Override
	public List<Map<String, Object>> getSubTree(List<Map<String, Object>> assetHierarchy, String vid) {
		return JSONUtil.getSubTree(assetHierarchy, vid);
	}

	@Override
	public String getImmediateParentField(List<Map<String, Object>> assetHierarchy, String vid, String field) {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.addFieldsOfVidToMap(assetHierarchy, vid, Arrays.asList(field), outputMap, true, null);
		return (String) outputMap.getOrDefault(field, null);
	}

	@Override
	public String getAssetHierarchyByLevel(String level) throws IOException {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		List<Map<String, Object>> fullHierarchy = fetchAssetHierarchyService.callAssetHierarchyAPIv2();
		List<String> fieldNames = new ArrayList<>();
		fieldNames.add(JSONUtilConstants.ID);
		fieldNames.add(JSONUtilConstants.DESCRIPTION);
		fieldNames.add(JSONUtilConstants.SERIALNO);

		List<String> outerFieldNames = new ArrayList<>();
		outerFieldNames.add(JSONUtilConstants.DISPLAYNAME);
		outerFieldNames.add(JSONUtilConstants.VID);

		setAdditionalFieldsToHierarchy(fullHierarchy, false);
		List<Map<String, Object>> outputList = new ArrayList<>();

		JSONUtil.addMultipleFieldsOfLevelUnderVid(fullHierarchy, null, level, fieldNames, outerFieldNames, outputList,
				false);
		outputList.forEach(entry -> entry.put(JSONUtilConstants.DISPLAYID, entry.get(JSONUtilConstants.ID)));
		if (level.equals(JSONUtilConstants.LEVEL_MACHINES)) {
			outputList.removeIf(entry -> entry.get(JSONUtilConstants.SERIALNO) == null);
		}
		return mapper.writeValueAsString(outputList);
	}

	@Override
	public Map<String, String> getDisplayNameMap(List<Map<String, Object>> assetHierarchy)
			throws JsonProcessingException {
		Map<String, String> displayNameMap = new HashMap<>();
		JSONUtil.addExtFieldToOutputMap(assetHierarchy, displayNameMap, JSONUtilConstants.DISPLAYNAME);
		displayNameMap.remove(null);
		return displayNameMap;
	}

	@Override
	public Map<String, Map<String, Set<String>>> getFieldsAndEnabledServicesToMap(
			List<Map<String, Object>> assetHierarchy) {
		logger.info("Preparing Fields and Enabled Services Map...");
		Map<String, Map<String, Set<String>>> enableServicesMap = new HashMap<>();
		JSONUtil.addFieldsAndEnabledServicesToMap(assetHierarchy, enableServicesMap);
		enableServicesMap.remove(null);
		logger.info("Returning Fields and Enabled Services Map...");
		return enableServicesMap;
	}

	@Override
	public Map<String, Object> getAssetsMap(List<Map<String, Object>> userAssetHierarchy, String vid, String field) {
		Map<String, Object> assetsMap = new HashMap<>();
		JSONUtil.addAssetsToMap(userAssetHierarchy, vid, JSONUtilConstants.VID.equals(field), assetsMap);
		assetsMap.remove(null);
		return assetsMap;
	}

	@Override
	public boolean validateVid(List<Map<String, Object>> assetHierarchy, String vidToFind) {
		Map<String, Boolean> outputMap = new HashMap<>();
		JSONUtil.validateVid(assetHierarchy, vidToFind, outputMap);
		return outputMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false);
	}

}