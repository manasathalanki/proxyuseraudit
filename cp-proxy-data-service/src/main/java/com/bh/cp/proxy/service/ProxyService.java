package com.bh.cp.proxy.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.bh.cp.proxy.adapter.ServicesAdapter;
import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.exception.ProxyException;
import com.bh.cp.proxy.factory.ResponseHandlerFactory;
import com.bh.cp.proxy.factory.ServicesFactory;
import com.bh.cp.proxy.helper.HeadersFormatHelper;
import com.bh.cp.proxy.helper.InputFormatHelper;
import com.bh.cp.proxy.helper.ReplaceValueHelper;
import com.bh.cp.proxy.pojo.AuditDate;
import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.bh.cp.proxy.pojo.ServicesDynamicParameters;
import com.bh.cp.proxy.repository.ServicesDirectoryRepository;
import com.bh.cp.proxy.util.SecurityUtil;
import com.bh.cp.proxy.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ProxyService {

	private static final Logger logger = LoggerFactory.getLogger(ProxyService.class);

	@Value("${app.service.response.logs.enabled:false}")
	private boolean isServiceReponseLogsEnabled;

	private Integer fleetDataWidgetId;

	private UMSClientService umsClientService;

	private AssetHierarchyFilterService assetHierarchyFilterService;

	private HeadersFormatHelper headersFormatHelper;

	private InputFormatHelper inputFormatHelper;

	private ServicesFactory servicesFactory;

	private ResponseHandlerFactory responseHandlerFactory;

	private ServicesDirectoryRepository servicesDirectoryRepository;

	private ReplaceValueHelper replaceStaticValuesHelper;

	private ReplaceValueHelper replaceDynamicValuesHelper;

	private AuditTrailAspect auditTrailAspect;

	public ProxyService(@Autowired UMSClientService umsClientService,
			@Autowired AssetHierarchyFilterService assetHierarchyFilterService,
			@Autowired HeadersFormatHelper headersFormatHelper, @Autowired InputFormatHelper inputFormatHelper,
			@Autowired ServicesFactory servicesFactory, @Autowired ResponseHandlerFactory responseHandlerFactory,
			@Autowired ServicesDirectoryRepository servicesDirectoryRepository,
			@Autowired @Qualifier("static") ReplaceValueHelper replaceStaticValuesHelper,
			@Autowired @Qualifier("dynamic") ReplaceValueHelper replaceDynamicValuesHelper,
			@Value("${fleet.data.widget-id}") Integer fleetDataWidgetId, @Autowired AuditTrailAspect auditTrailAspect) {
		super();
		this.umsClientService = umsClientService;
		this.assetHierarchyFilterService = assetHierarchyFilterService;
		this.headersFormatHelper = headersFormatHelper;
		this.inputFormatHelper = inputFormatHelper;
		this.servicesFactory = servicesFactory;
		this.responseHandlerFactory = responseHandlerFactory;
		this.servicesDirectoryRepository = servicesDirectoryRepository;
		this.replaceStaticValuesHelper = replaceStaticValuesHelper;
		this.replaceDynamicValuesHelper = replaceDynamicValuesHelper;
		this.fleetDataWidgetId = fleetDataWidgetId;
		this.auditTrailAspect = auditTrailAspect;
	}

	@SuppressWarnings("unchecked")
	public Object execute(Map<String, Object> request, HttpServletRequest httpServletRequest)
			throws ClassNotFoundException, IllegalArgumentException, SecurityException, JsonProcessingException,
			ProxyException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		try {
			String proxyCall = !request.containsKey(ProxyConstants.FILTEREDASSETHIERARCHY) ? request.toString()
					: "Recursive Call for Widget " + request.get(WidgetConstants.WIDGETID);
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "ProxyService: execute()=> {}", proxyCall);

			ServicesDirectory servicesDirectory = getServicesDirectory(request);
			boolean isStaticWidget = servicesDirectory.getUri() == null;
			List<Map<String, Object>> userAssetHierarchyList = umsClientService
					.getUserAssetHierarchy(httpServletRequest);

			Map<String, Object> outputMap = new HashMap<>();
			String vid = (String) request.getOrDefault(ProxyConstants.VID, null);

			if (!StringUtil.isEmptyCaseString(vid)) {
				outputMap = validateWidgetSubscription(httpServletRequest, request);
				if (Boolean.FALSE.equals(outputMap.get(ProxyConstants.SHOWLIVEDATA)) || isStaticWidget) {
					logger.info("Widget Validation not successful or Static Widget...");
					return new JSONObject(outputMap);
				}
			}

			if (vid != null && !assetHierarchyFilterService.validateVid(userAssetHierarchyList, vid)) {
				throw new AccessDeniedException("Asset is not Accessible");
			} else if (vid == null) {
				Map<String, Object> assetsVidMap = assetHierarchyFilterService.getAssetsMap(userAssetHierarchyList, vid,
						true);
				vid = ((List<String>) assetsVidMap.getOrDefault(JSONUtilConstants.LEVEL_PROJECTS, new ArrayList<>()))
						.get(0);
				request.put(ProxyConstants.VID, vid);
			}

			request.putIfAbsent(ProxyConstants.FILTEREDASSETHIERARCHY, userAssetHierarchyList);
			request.putIfAbsent(ProxyConstants.HTTPSERVLETREQUEST, httpServletRequest);
			request.putIfAbsent(ProxyConstants.ASSETSIDMAP,
					assetHierarchyFilterService.getAssetsMap(userAssetHierarchyList, vid, false));
			request.putIfAbsent(ProxyConstants.REPLACE_VALUES,
					replaceStaticValuesHelper.getReplaceValues(request, userAssetHierarchyList, servicesDirectory));
			request.putIfAbsent(ProxyConstants.REPLACE_VALUES,
					replaceDynamicValuesHelper.getReplaceValues(request, userAssetHierarchyList, servicesDirectory));

			servicesDirectory.setHeaders(headersFormatHelper.format(servicesDirectory, request));
			servicesDirectory.setInputData(inputFormatHelper.format(servicesDirectory, request));

			ServicesAdapter servicesAdapter = servicesFactory.getInstanceOf(servicesDirectory);
			Object serviceResponse = servicesAdapter.execute(servicesDirectory);
			Object responseObject = responseHandlerFactory.getInstanceOf(servicesDirectory).format(serviceResponse,
					request);
			if (isServiceReponseLogsEnabled) {
				Map<String, Object> serviceLogs = new HashMap<>();
				request.remove(ProxyConstants.ASSETSIDMAP);
				request.remove(ProxyConstants.FILTEREDASSETHIERARCHY);
				request.remove(ProxyConstants.HTTPSERVLETREQUEST);
				serviceLogs.put("proxy_payload", request);
				serviceLogs.put("ext_service_url", servicesDirectory.getUri());
				serviceLogs.put("ext_service_input", servicesDirectory.getInputData());
				serviceLogs.put("ext_service_response", String.valueOf(serviceResponse));
				outputMap.put("service_logs", serviceLogs);
			}

			if (responseObject instanceof JSONObject jsonObject) {
				logger.info("Response is JSONObject.");
				outputMap.put(WidgetConstants.DATA, jsonObject.get(WidgetConstants.DATA));
			} else {
				String responseObj = responseObject != null ? responseObject.getClass().getName() : null;
				SecurityUtil.sanitizeLogging(logger, Level.INFO, "Response {} is not JSONObject.", responseObj);
				outputMap.put(WidgetConstants.DATA, responseObject);
			}

			return new JSONObject(outputMap);
		} finally {
			request.remove(ProxyConstants.HTTPSERVLETREQUEST);
			if (!request.containsValue(-1)) {
				Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
				long executionTime = endTime.getTime() - startTime.getTime();
				auditTrailAspect.saveAuditTrailPerformance(
						(new StringBuilder(this.getClass().getCanonicalName()).append(".")
								.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
						new JSONObject(request), new AuditDate(startTime, endTime, executionTime), true);
			}
		}
	}

	public Map<String, Object> validateWidgetSubscription(HttpServletRequest httpServletRequest,
			Map<String, Object> request) throws JsonProcessingException {
		Map<String, Object> accessMap = new HashMap<>();
		accessMap.put(ProxyConstants.SHOWGREYIMAGE, false);
		accessMap.put(ProxyConstants.HIDEWIDGET, false);
		if (Objects.equals(request.getOrDefault(ProxyConstants.WIDGET_ID, -1), fleetDataWidgetId)) {
			accessMap.put(ProxyConstants.SHOWLIVEDATA, true);
			return accessMap;
		}
		accessMap.put(ProxyConstants.SHOWLIVEDATA, false);

		Map<String, Boolean> umsResponseMap = umsClientService.getWidgetAccess(httpServletRequest,
				(String) request.get(ProxyConstants.VID), (Integer) request.get(ProxyConstants.WIDGET_ID));
		boolean applicability = umsResponseMap.getOrDefault(ProxyConstants.APPLICABILITY, false);
		boolean enabled = umsResponseMap.getOrDefault(ProxyConstants.ENABLED, false);
		boolean hasAccess = umsResponseMap.getOrDefault(ProxyConstants.HASACCESS, false);
		boolean activeServicesPersona = umsResponseMap.getOrDefault(ProxyConstants.ACTIVESERVICESPERSONA, false);

		if (!applicability) {
			accessMap.put(ProxyConstants.HIDEWIDGET, true);
		} else if (enabled && hasAccess) {
			accessMap.put(ProxyConstants.SHOWLIVEDATA, true);
		} else {
			accessMap.put(ProxyConstants.SHOWGREYIMAGE, true);
		}

		if (activeServicesPersona && (boolean) accessMap.get(ProxyConstants.SHOWGREYIMAGE)) {
			accessMap.put(ProxyConstants.SHOWGREYIMAGE, false);
			accessMap.put(ProxyConstants.HIDEWIDGET, true);
		}
		return accessMap;
	}

	public ServicesDirectory getServicesDirectory(Map<String, Object> request) throws ProxyException {
		com.bh.cp.proxy.entity.ServicesDirectory servicesDirectoryDB = getServiceDirectoryDBObject(request);
		ServicesDirectory servicesDirectory = new ServicesDirectory();
		servicesDirectory.setId(servicesDirectoryDB.getId());
		servicesDirectory.setServiceType(servicesDirectoryDB.getServicetype());
		servicesDirectory.setCommunicationFormat(servicesDirectoryDB.getCommunicationFormat());
		servicesDirectory.setUri(servicesDirectoryDB.getUri());
		servicesDirectory.setMethod(servicesDirectoryDB.getMethod());
		servicesDirectory.setHeaders(servicesDirectoryDB.getHeaders());
		servicesDirectory.setInputData(servicesDirectoryDB.getInputData());
		servicesDirectory.setOutputHandler(servicesDirectoryDB.getOutputHandler());
		servicesDirectory.setWidgetId(servicesDirectoryDB.getWidgetId());
		Set<com.bh.cp.proxy.entity.ServicesDynamicParameters> servicesDynamicParametersDB = servicesDirectoryDB
				.getDynamicParameters();
		if (servicesDynamicParametersDB != null) {
			Set<ServicesDynamicParameters> servicesDynamicParameters = new HashSet<>();
			for (com.bh.cp.proxy.entity.ServicesDynamicParameters dynamicParametersDB : servicesDynamicParametersDB) {
				ServicesDynamicParameters dynamicParameters = new ServicesDynamicParameters(dynamicParametersDB.getId(),
						dynamicParametersDB.getField(), dynamicParametersDB.getInputData());
				servicesDynamicParameters.add(dynamicParameters);
			}
			servicesDirectory.setDynamicParameters(servicesDynamicParameters);
		}
		return servicesDirectory;
	}

	private com.bh.cp.proxy.entity.ServicesDirectory getServiceDirectoryDBObject(Map<String, Object> request)
			throws ProxyException {
		Integer widgetId = (Integer) request.get(ProxyConstants.WIDGET_ID);
		if (widgetId != null) {
			Optional<com.bh.cp.proxy.entity.ServicesDirectory> servicesDirectoryDB = servicesDirectoryRepository
					.findByWidgetId(widgetId);
			if (servicesDirectoryDB.isPresent())
				return servicesDirectoryDB.get();
		}
		Integer serviceId = (Integer) request.get(ProxyConstants.SERVICE_ID);
		if (serviceId != null) {
			Optional<com.bh.cp.proxy.entity.ServicesDirectory> servicesDirectoryDB = servicesDirectoryRepository
					.findById(serviceId);
			if (servicesDirectoryDB.isPresent())
				return servicesDirectoryDB.get();
		}
		throw new ProxyException("Could not find data for given input.");
	}
}
