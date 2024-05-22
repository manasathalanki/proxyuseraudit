package com.bh.cp.dashboard.service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

public interface UMSClientService {

	public List<Map<String, Object>> getUserAssetHierarchy(HttpServletRequest httpServletRequest)
			throws JsonProcessingException;

	public List<String> getUserPrivileges(HttpServletRequest httpServletRequest) throws JsonProcessingException;

	public Map<String, Object> getWidgetSubscription(HttpServletRequest httpServletRequest, Integer widgetId)
			throws JsonProcessingException;

}
