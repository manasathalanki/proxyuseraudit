package com.bh.cp.user.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface GenericAssetHierarchyFilterService {

	public List<Map<String, Object>> getFilteredHierarchy(List<String> vids, boolean compact) throws IOException;

	public void setAdditionalFieldsToHierarchy(List<Map<String, Object>> actualHierarchy, boolean compact);

	public List<Map<String, Object>> getSubTree(List<Map<String, Object>> assetHierarchy, String vid);

	public String getImmediateParentField(List<Map<String, Object>> assetHierarchy, String vid, String field);

	public Map<String, Map<String, Set<String>>> getFieldsAndEnabledServicesToMap(
			List<Map<String, Object>> assetHierarchy);

	public String getAssetHierarchyByLevel(String level) throws IOException;

	public Map<String, String> getDisplayNameMap(List<Map<String, Object>> assetHierarchy)
			throws JsonProcessingException;

	public Map<String, Object> getAssetsMap(List<Map<String, Object>> userAssetHierarchy, String vid, String field);

	public boolean validateVid(List<Map<String, Object>> assetHierarchy, String vidToFind);

}
