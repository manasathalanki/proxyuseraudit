package com.bh.cp.proxy.asset.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AssetHierarchyFilterService {

	public List<Map<String, Object>> getSubTree(List<Map<String, Object>> assetHierarchy, String vid);

	public Map<String, Object> getAssetsMap(List<Map<String, Object>> assetHierarchy, String vidToFind,
			boolean collectAsVid);

	public String getImmediateParentField(List<Map<String, Object>> assetHierarchy, String vid, String field);

	public List<String> getFieldValuesForLevel(List<Map<String, Object>> assetHierarchy, String vid,
			String levelToFetchFrom, String fieldName);

	public Map<String, Map<String, Set<String>>> getFieldsAndEnabledServicesToMap(
			List<Map<String, Object>> assetHierarchy);

	public List<Map<String, String>> getProjectsAndPlants(Map<String, Object> assetsMap);

	public List<String> getIdList(List<Map<String, Object>> assetHierarchy, List<String> vids);

	public boolean validateVid(List<Map<String, Object>> assetHierarchy, String vidToFind);

	 
	 /**
     * getSubTreeForWidget
     * @deprecated
     * This method is no longer acceptable to get the subtrees for the selected widget.
     * @param List of assetHierarchy and vid
     * @return List of subtrees
     */

	@Deprecated(since = "fix/TA908926-sonar-fixes-part-2")
	public List<Map<String, Object>> getSubTreeForWidget(List<Map<String, Object>> assetHierarchy, String vid);

}
