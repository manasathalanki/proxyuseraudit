package com.bh.cp.dashboard.service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface AssetHierarchyFilterService {

	public Map<String, Object> getAssetsMap(List<Map<String, Object>> assetHierarchy, String vidToFind,
			boolean collectAsVid);

	public Map<String, String> getDisplayNameMap(List<Map<String, Object>> assetHierarchy)
			throws JsonProcessingException;

	public Map<String, String> getImageSrcMap(List<Map<String, Object>> assetHierarchy) throws JsonProcessingException;

	public Map<String, String> getIdMap(List<Map<String, Object>> assetHierarchy) throws JsonProcessingException;

	public Map<String, String> getCustomerNameMap(List<Map<String, Object>> assetHierarchy, List<String> vids)
			throws JsonProcessingException;

}
