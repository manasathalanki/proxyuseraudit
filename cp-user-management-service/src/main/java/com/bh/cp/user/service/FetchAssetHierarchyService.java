package com.bh.cp.user.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface FetchAssetHierarchyService {

	public List<Map<String, Object>> callAssetHierarchyAPI() throws JsonProcessingException;

	public List<Map<String, Object>> callAssetHierarchyAPIv2() throws IOException;

}
