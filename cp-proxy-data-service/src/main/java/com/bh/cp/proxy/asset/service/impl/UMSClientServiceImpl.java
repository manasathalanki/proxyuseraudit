package com.bh.cp.proxy.asset.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.asset.service.RestClientWrapperService;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.dto.request.WidgetAccessRequestDTO;
import com.bh.cp.proxy.dto.request.WidgetApplicableRequestDTO;
import com.bh.cp.proxy.pojo.AuditDate;
import com.bh.cp.proxy.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UMSClientServiceImpl implements UMSClientService {

	private static final Logger logger = LoggerFactory.getLogger(UMSClientServiceImpl.class);

	private String userAssetHierarchyUri;

	private String widgetAccessCheckUri;

	private String widgetApplicableMachinesUri;

	private String userPrivilegesUri;

	private String userDetailsUri;

	private String allUserDetailsUri;

	private String oktaUserDetailsUri;

	private RestClientWrapperService restClientWrapperService;

	private ObjectMapper mapper;

	private AuditTrailAspect auditTrailAspect;

	private String widgetAdvanceServicesAccessCheckUri;

	public UMSClientServiceImpl(@Autowired RestClientWrapperService restClientWrapperService,
			@Autowired ObjectMapper mapper, @Value("${cp.ums.all.user.details.uri}") String allUserDetailsUri,
			@Value("${cp.ums.widget.access.check.uri}") String widgetAccessCheckUri,
			@Value("${cp.ums.user.asset.hierarchy.uri}") String userAssetHierarchyUri,
			@Value("${cp.ums.user.details.uri}") String userDetailsUri,
			@Value("${cp.ums.widget.applicable.machines.uri}") String widgetApplicableMachinesUri,
			@Value("${cp.ums.user.privileges.uri}") String userPrivilegesUri,
			@Value("${cp.ums.okta.user.details.uri}") String oktaUserDetailsUri,
			@Value("${cp.ums.widget.advance.service.access.check.uri}") String widgetAdvanceServicesAccessCheckUri,
			@Autowired AuditTrailAspect auditTrailAspect) {
		super();
		this.restClientWrapperService = restClientWrapperService;
		this.mapper = mapper;
		this.allUserDetailsUri = allUserDetailsUri;
		this.oktaUserDetailsUri = oktaUserDetailsUri;
		this.widgetAccessCheckUri = widgetAccessCheckUri;
		this.userAssetHierarchyUri = userAssetHierarchyUri;
		this.userDetailsUri = userDetailsUri;
		this.widgetApplicableMachinesUri = widgetApplicableMachinesUri;
		this.userPrivilegesUri = userPrivilegesUri;
		this.auditTrailAspect = auditTrailAspect;
		this.widgetAdvanceServicesAccessCheckUri = widgetAdvanceServicesAccessCheckUri;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "userassethierarchy", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public List<Map<String, Object>> getUserAssetHierarchy(HttpServletRequest httpServletRequest)
			throws JsonProcessingException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		try {
			ResponseEntity<String> assetHierarchyResponse = restClientWrapperService
					.getResponseFromUrl(httpServletRequest, userAssetHierarchyUri);
			return mapper.readValue(assetHierarchyResponse.getBody(), List.class);
		} finally {
			if (httpServletRequest.getAttribute(ProxyConstants.FLEET_DATA_WIDGETID) == null) {
				JSONObject json = new JSONObject();
				json.put("uri", userAssetHierarchyUri);
				Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
				long executionTime = endTime.getTime() - startTime.getTime();
				auditTrailAspect.saveAuditTrailPerformance(
						(new StringBuilder(this.getClass().getCanonicalName()).append(".")
								.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
						json, new AuditDate(startTime, endTime, executionTime), true);
			}
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "proxyuserdetails", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public Map<String, Object> getUserDetails(HttpServletRequest httpServletRequest) throws JsonProcessingException {
		ResponseEntity<String> assetHierarchyResponse = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				userDetailsUri);
		return mapper.readValue(assetHierarchyResponse.getBody(), Map.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "alluserdetails", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public List<Map<String, Object>> getAllUserDetails(HttpServletRequest httpServletRequest)
			throws JsonProcessingException {
		ResponseEntity<String> assetHierarchyResponse = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				allUserDetailsUri);
		return mapper.readValue(assetHierarchyResponse.getBody(), List.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "oktauserdetails", key = "#httpServletRequest.getHeader(\"Authorization\").concat(#mailObject.toString)")
	public List<Map<String, Object>> getOktaUserDetails(HttpServletRequest httpServletRequest, JSONObject mailObject)
			throws JsonProcessingException {
		ResponseEntity<String> assetHierarchyResponse = restClientWrapperService.postBodyToUrl(httpServletRequest,
				oktaUserDetailsUri, mailObject.toString());
		return mapper.readValue(assetHierarchyResponse.getBody(), List.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "userprivileges", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public List<String> getUserPrivileges(HttpServletRequest httpServletRequest) throws JsonProcessingException {
		ResponseEntity<String> widgetSubcriptionResponse = restClientWrapperService
				.getResponseFromUrl(httpServletRequest, userPrivilegesUri);
		return mapper.readValue(widgetSubcriptionResponse.getBody(), List.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "widgetaccess", key = "#httpServletRequest.getHeader(\"Authorization\").concat(#vid).concat(#widgetId)")
	public Map<String, Boolean> getWidgetAccess(HttpServletRequest httpServletRequest, String vid, Integer widgetId)
			throws JsonProcessingException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		try {
			WidgetAccessRequestDTO requestDto = new WidgetAccessRequestDTO(vid, widgetId);
			String jsonBody = mapper.writeValueAsString(requestDto);
			ResponseEntity<String> checkWidgetAccessResponse = restClientWrapperService
					.postBodyToUrl(httpServletRequest, widgetAccessCheckUri, jsonBody);
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "UMS Response => for Widget id {} ,VID {} is {}", widgetId,
					vid, checkWidgetAccessResponse.getBody());
			return mapper.readValue(checkWidgetAccessResponse.getBody(), Map.class);
		} finally {
			if (httpServletRequest.getAttribute("fleetDataWidgetId") == null) {
				JSONObject json = new JSONObject();
				json.put("uri", widgetAccessCheckUri);
				Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
				long executionTime = endTime.getTime() - startTime.getTime();
				auditTrailAspect.saveAuditTrailPerformance(
						(new StringBuilder(this.getClass().getCanonicalName()).append(".")
								.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
						json, new AuditDate(startTime, endTime, executionTime), true);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "applicablemachines", key = "#httpServletRequest.getHeader(\"Authorization\").concat('#').concat(#vid).concat('#').concat(#widgetId.toString()).concat('#').concat(#field)")
	public List<String> getApplicableMachinesForWidget(HttpServletRequest httpServletRequest, String vid,
			Integer widgetId, String field) throws JsonProcessingException {
		WidgetApplicableRequestDTO requestDto = new WidgetApplicableRequestDTO(vid, widgetId, field);
		String jsonBody = mapper.writeValueAsString(requestDto);
		ResponseEntity<String> widgetAppMachinesResponse = restClientWrapperService.postBodyToUrl(httpServletRequest,
				widgetApplicableMachinesUri, jsonBody);
		JSONObject widgetAppMachinesJson = new JSONObject(widgetAppMachinesResponse.getBody());
		if (widgetAppMachinesJson.has(ProxyConstants.MACHINES)) {
			SecurityUtil.sanitizeLogging(logger, Level.INFO,
					"UMS Response => Applicable Machines for Widget id {} ,VID {} is {}", widgetId, vid,
					widgetAppMachinesJson.get(ProxyConstants.MACHINES).toString());
			return mapper.readValue(widgetAppMachinesJson.get(ProxyConstants.MACHINES).toString(), List.class);
		}
		return new ArrayList<>();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "widgetaccess", key = "#httpServletRequest.getHeader(\"Authorization\").concat(\"AdvSvc\").concat(#vid).concat(#widgetId)")
	public Map<String, Boolean> getWidgetAdvanceServicesAccess(HttpServletRequest httpServletRequest, String vid,
			Integer widgetId) throws JsonProcessingException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		try {
			WidgetAccessRequestDTO requestDto = new WidgetAccessRequestDTO(vid, widgetId);
			String jsonBody = mapper.writeValueAsString(requestDto);
			ResponseEntity<String> checkWidgetAdvanceServicesAccessResponse = restClientWrapperService
					.postBodyToUrl(httpServletRequest, widgetAdvanceServicesAccessCheckUri, jsonBody);
			SecurityUtil.sanitizeLogging(logger, Level.INFO,
					"UMS Advance Services Access Response => for Widget id {} ,VID {} is {}", widgetId, vid,
					checkWidgetAdvanceServicesAccessResponse.getBody());
			return mapper.readValue(checkWidgetAdvanceServicesAccessResponse.getBody(), Map.class);
		} finally {
			if (httpServletRequest.getAttribute("fleetDataWidgetId") == null) {
				JSONObject json = new JSONObject();
				json.put("uri", widgetAdvanceServicesAccessCheckUri);
				Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
				long executionTime = endTime.getTime() - startTime.getTime();
				auditTrailAspect.saveAuditTrailPerformance(
						(new StringBuilder(this.getClass().getCanonicalName()).append(".")
								.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
						json, new AuditDate(startTime, endTime, executionTime), true);
			}
		}
	}

}
