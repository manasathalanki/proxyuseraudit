package com.bh.cp.proxy.asset.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

public interface UMSClientService {

	public List<Map<String, Object>> getUserAssetHierarchy(HttpServletRequest httpServletRequest)
			throws JsonProcessingException;

	public Map<String,Object> getUserDetails(HttpServletRequest httpServletRequest) throws JsonProcessingException;
	
	public List<Map<String, Object>> getAllUserDetails(HttpServletRequest httpServletRequest) throws JsonProcessingException;

	public List<String> getUserPrivileges(HttpServletRequest httpServletRequest) throws JsonProcessingException;

	public Map<String, Boolean> getWidgetAccess(HttpServletRequest httpServletRequest, String vid, Integer widgetId)
			throws JsonProcessingException;

	public List<String> getApplicableMachinesForWidget(HttpServletRequest httpServletRequest, String vid,
			Integer widgetId, String field) throws JsonProcessingException;

	public List<Map<String, Object>> getOktaUserDetails(HttpServletRequest httpServletRequest, JSONObject json) throws JsonProcessingException;

	public Map<String, Boolean> getWidgetAdvanceServicesAccess(HttpServletRequest httpServletRequest, String vid, Integer widgetId)
			throws JsonProcessingException;
}
