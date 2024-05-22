package com.bh.cp.dashboard.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.service.RestClientWrapperService;
import com.bh.cp.dashboard.service.UMSClientService;
import com.bh.cp.dashboard.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UMSClientServiceImpl implements UMSClientService {

	private final String userAssetHierarchyUri;

	private final String userPrivilegesUri;

	private final String widgetSubCheckUri;

	private RestClientWrapperService restClientWrapperService;

	private ObjectMapper mapper;

	public UMSClientServiceImpl(@Autowired RestClientWrapperService restClientWrapperService,
			@Autowired ObjectMapper mapper, @Value("${cp.ums.user.asset.hierarchy.uri}") String userAssetHierarchyUri,
			@Value("${cp.ums.user.privileges.uri}") String userPrivilegesUri,
			@Value("${cp.ums.user.widget.subscription.check.uri}") String widgetSubCheckUri) {
		super();
		this.restClientWrapperService = restClientWrapperService;
		this.mapper = mapper;
		this.userAssetHierarchyUri = userAssetHierarchyUri;
		this.userPrivilegesUri = userPrivilegesUri;
		this.widgetSubCheckUri = widgetSubCheckUri;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "userassethierarchy", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public List<Map<String, Object>> getUserAssetHierarchy(HttpServletRequest httpServletRequest)
			throws JsonProcessingException {
		ResponseEntity<String> assetHierarchyResponse = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				userAssetHierarchyUri);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.readValue(assetHierarchyResponse.getBody(), List.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "userprivileges", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public List<String> getUserPrivileges(HttpServletRequest httpServletRequest) throws JsonProcessingException {
		ResponseEntity<String> widgetSubcriptionResponse = restClientWrapperService
				.getResponseFromUrl(httpServletRequest, userPrivilegesUri);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.readValue(widgetSubcriptionResponse.getBody(), List.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "widgetsubscription", key = "#httpServletRequest.getHeader(\"Authorization\").concat(#widgetId.toString())")
	public Map<String, Object> getWidgetSubscription(HttpServletRequest httpServletRequest, Integer widgetId)
			throws JsonProcessingException {
		ResponseEntity<String> widgetSubcriptionResponse = restClientWrapperService
				.getResponseFromUrl(httpServletRequest, SecurityUtil.sanitizeUrl(widgetSubCheckUri,
						Map.of(DashboardConstants.WIDGET_ID_PATHVARIABLE, widgetId)));
		return mapper.readValue(widgetSubcriptionResponse.getBody(), Map.class);
	}

}
