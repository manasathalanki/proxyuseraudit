package com.bh.cp.dashboard.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bh.cp.dashboard.constants.JSONUtilConstants;
import com.bh.cp.dashboard.service.AssetHierarchyFilterService;
import com.bh.cp.dashboard.util.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class AssetHierarchyFilterServiceImpl implements AssetHierarchyFilterService {

	@Override
	public Map<String, Object> getAssetsMap(List<Map<String, Object>> assetHierarchy, String vidToFind,
			boolean collectAsVid) {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.addAssetsToMap(assetHierarchy, vidToFind, collectAsVid, outputMap);
		return outputMap;
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
	public Map<String, String> getImageSrcMap(List<Map<String, Object>> assetHierarchy) throws JsonProcessingException {
		Map<String, String> imageSrcMap = new HashMap<>();
		JSONUtil.addExtFieldToOutputMap(assetHierarchy, imageSrcMap, JSONUtilConstants.IMAGESRC);
		imageSrcMap.remove(null);
		return imageSrcMap;
	}

	@Override
	public Map<String, String> getIdMap(List<Map<String, Object>> assetHierarchy) throws JsonProcessingException {
		Map<String, String> idMap = new HashMap<>();
		JSONUtil.addExtFieldToOutputMap(assetHierarchy, idMap, JSONUtilConstants.ID);
		idMap.remove(null);
		return idMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getCustomerNameMap(List<Map<String, Object>> assetHierarchy, List<String> vids)
			throws JsonProcessingException {
		Map<String, String> customerNameMap = new HashMap<>();
		Map<String, String> displayNameMap = getDisplayNameMap(assetHierarchy);
		for (String vid : vids) {
			Map<String, Object> assetsMap = new HashMap<>();
			JSONUtil.addAssetsToMap(assetHierarchy, vid, true, assetsMap);
			String projectVid = ((List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_PROJECTS,
					new ArrayList<>())).get(0);
			customerNameMap.put(vid, displayNameMap.get(projectVid));
		}

		return customerNameMap;
	}

}