package com.bh.cp.proxy.helper;

import java.util.List;
import java.util.Map;

import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ReplaceValueHelper {

	public Map<String, String> getReplaceValues(Map<String, Object> request,
			List<Map<String, Object>> userAssetHierarchyList, ServicesDirectory servicesDirectory)
			throws JsonProcessingException;
}
