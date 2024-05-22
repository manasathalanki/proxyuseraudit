package com.bh.cp.proxy.asset.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.util.JSONUtil;

@Service
public class AssetHierarchyFilterServiceImpl implements AssetHierarchyFilterService {

	Logger logger = LoggerFactory.getLogger(AssetHierarchyFilterServiceImpl.class);

	@Override
	public List<Map<String, Object>> getSubTree(List<Map<String, Object>> assetHierarchy, String vid) {
		return JSONUtil.getSubTree(assetHierarchy, vid);
	}

	@Override
	public Map<String, Object> getAssetsMap(List<Map<String, Object>> assetHierarchy, String vidToFind,
			boolean collectAsVid) {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.addAssetsToMap(assetHierarchy, vidToFind, collectAsVid, outputMap);
		return outputMap;
	}

	@Override
	public String getImmediateParentField(List<Map<String, Object>> assetHierarchy, String vid, String field) {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.addFieldsOfVidToMap(assetHierarchy, vid, Arrays.asList(field), outputMap, true, null);
		return (String) outputMap.getOrDefault(field, null);
	}

	@Override
	public List<String> getFieldValuesForLevel(List<Map<String, Object>> assetHierarchy, String vid,
			String levelToFetchFrom, String fieldName) {
		List<Map<String, Object>> outputList = new ArrayList<>();
		List<String> fieldNames = List.of(fieldName);
		JSONUtil.addMultipleFieldsOfLevelUnderVid(assetHierarchy, vid, levelToFetchFrom, fieldNames, new ArrayList<>(),
				outputList, false);
		return outputList.stream().map(fieldMap -> (String) fieldMap.get(fieldName)).toList();
	}

	@Override
	public Map<String, Map<String, Set<String>>> getFieldsAndEnabledServicesToMap(
			List<Map<String, Object>> assetHierarchy) {
		Map<String, Map<String, Set<String>>> enableServicesMap = new HashMap<>();
		JSONUtil.addFieldsAndEnabledServicesToMap(assetHierarchy, enableServicesMap);
		enableServicesMap.remove(null);
		return enableServicesMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getProjectsAndPlants(Map<String, Object> assetsMap) {

		List<String> projectsList = (List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_PROJECTS,
				new ArrayList<>());
		List<String> plantsList = (List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_PLANTS,
				new ArrayList<>());
		List<Map<String, String>> outputList = new ArrayList<>();

		projectsList.stream().forEach(project -> plantsList.forEach(plant -> {
			Map<String, String> assetMap = new LinkedHashMap<>();
			assetMap.put(JSONUtilConstants.PLANTS_PREFIX, plant);
			assetMap.put(JSONUtilConstants.PROJECTS_PREFIX, project);
			outputList.add(assetMap);
		}));

		return outputList;
	}

	@Override
	public List<String> getIdList(List<Map<String, Object>> assetHierarchy, List<String> vids) {
		List<String> outputList = new ArrayList<>();
		JSONUtil.getIdsForVid(assetHierarchy, vids, outputList);
		return outputList;
	}

	@Override
	public boolean validateVid(List<Map<String, Object>> assetHierarchy, String vidToFind) {
		Map<String, Boolean> outputMap = new HashMap<>();
		JSONUtil.validateVid(assetHierarchy, vidToFind, outputMap);
		return outputMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false);
	}

	/** Deprecated **/
	@Override
	public List<Map<String, Object>> getSubTreeForWidget(List<Map<String, Object>> assetHierarchy, String vid) {
		List<Map<String, Object>> assets = new ArrayList<>();
		this.getSubTreeForChildren(assetHierarchy, vid, assets);
		return assets;
	}

	/** Deprecated **/
	@SuppressWarnings("unchecked")
	private void getSubTreeForChildren(List<Map<String, Object>> assetHierarchy, String vid,
			List<Map<String, Object>> assets) {
		for (Map<String, Object> data : assetHierarchy) {
			if (data.get(JSONUtilConstants.DATA) == null) {
				logger.info("this method is deprecated and removed as soon as possible");
			} else {
				for (Map<String, Object> entity : (List<Map<String, Object>>) data.get(JSONUtilConstants.DATA)) {
					List<Map<String, Object>> dataList = new ArrayList<>();
					dataList.add((Map<String, Object>) entity.get(JSONUtilConstants.CHILDREN));
					if (vid.equals(entity.get(JSONUtilConstants.VID))) {
						assets.add(entity);
						break;
					} else if (entity.get(JSONUtilConstants.CHILDREN) != null) {
						getSubTreeForChildren(dataList, vid, assets);
					}
				}
			}
		}
	}

}