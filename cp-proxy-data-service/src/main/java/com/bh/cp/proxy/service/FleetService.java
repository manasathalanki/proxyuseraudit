package com.bh.cp.proxy.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.bh.cp.proxy.adapter.ServicesAdapter;
import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.factory.ResponseHandlerFactory;
import com.bh.cp.proxy.factory.ServicesFactory;
import com.bh.cp.proxy.helper.HeadersFormatHelper;
import com.bh.cp.proxy.helper.InputFormatHelper;
import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.bh.cp.proxy.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class FleetService {

	private final Logger logger = LoggerFactory.getLogger(FleetService.class);

	private Integer fleetDataWidgetId;

	private ProxyService proxyService;

	private UMSClientService umsClientService;

	private AssetHierarchyFilterService assetHierarchyFilterService;

	private HeadersFormatHelper headersFormatHelper;

	private InputFormatHelper inputFormatHelper;

	private ServicesFactory servicesFactory;

	private ResponseHandlerFactory responseHandlerFactory;

	@Autowired
	private FleetService(ProxyService proxyService, UMSClientService umsClientService,
			AssetHierarchyFilterService assetHierarchyFilterService, HeadersFormatHelper headersFormatHelper,
			InputFormatHelper inputFormatHelper, ServicesFactory servicesFactory,
			ResponseHandlerFactory responseHandlerFactory,
			@Value("${fleet.data.widget-id}") Integer fleetDataWidgetId) {
		super();
		this.proxyService = proxyService;
		this.umsClientService = umsClientService;
		this.assetHierarchyFilterService = assetHierarchyFilterService;
		this.headersFormatHelper = headersFormatHelper;
		this.inputFormatHelper = inputFormatHelper;
		this.servicesFactory = servicesFactory;
		this.responseHandlerFactory = responseHandlerFactory;
		this.fleetDataWidgetId = fleetDataWidgetId;
	}

	@SuppressWarnings("unchecked")
	public Object execute(Map<String, Object> request, HttpServletRequest httpServletRequest)
			throws JsonProcessingException, IllegalArgumentException, SecurityException, InterruptedException {
		List<Map<String, Object>> userAssetHierarchyList = umsClientService.getUserAssetHierarchy(httpServletRequest);
		Map<String, Object> assetsMap = assetHierarchyFilterService.getAssetsMap(userAssetHierarchyList,
				(String) request.get(ProxyConstants.VID), true);
		if (request.get(ProxyConstants.VID) != null
				&& !(boolean) assetsMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false)) {
			throw new AccessDeniedException("Asset is not Accessible");
		}

		String level = (String) assetsMap.getOrDefault(JSONUtilConstants.NEXTLEVEL, null);
		List<String> assets = (List<String>) assetsMap.getOrDefault(level, new ArrayList<>());

		List<Callable<JSONObject>> tasksList = assets.stream().map(vid -> createTask(userAssetHierarchyList, vid))
				.filter(x -> x != null).toList();

		ExecutorService executorService = Executors.newFixedThreadPool(20);
		List<JSONObject> output = executorService.invokeAll(tasksList).stream().map(task -> {
			try {
				return task.get();
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
			}
			return new JSONObject();
		}).filter(response -> response.has(ProxyConstants.DATA)).map(response -> {
			if (response.get(ProxyConstants.DATA) instanceof JSONObject) {
				return response.getJSONObject(ProxyConstants.DATA);
			}
			return new JSONObject();
		}).toList();

		executorService.shutdown();
		return new JSONObject().put(ProxyConstants.DATA, output);

	}

	@SuppressWarnings("unchecked")
	private Callable<JSONObject> createTask(List<Map<String, Object>> userAssetHierarchy, String vid) {
		return () -> {
			Map<String, Object> request = new HashMap<>();
			request.put(ProxyConstants.WIDGET_ID, fleetDataWidgetId);
			request.put(ProxyConstants.VID, vid);
			Map<String, Object> assetsMap = assetHierarchyFilterService.getAssetsMap(userAssetHierarchy, vid, false);
			List<String> lineupIdsUnderVids = (List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_LINEUPS,
					new ArrayList<>());
			String lineupIdsCsv = StringUtil.toCSV(lineupIdsUnderVids, ",", "");
			Map<String, String> output = new HashMap<>();
			output.put(ProxyConstants.KEY_LINEUP_IDS_CSV, lineupIdsCsv);
			request.put(ProxyConstants.REPLACE_VALUES, output);
			ServicesDirectory servicesDirectory = proxyService.getServicesDirectory(request);
			ServicesAdapter servicesAdapter = servicesFactory.getInstanceOf(servicesDirectory);
			servicesDirectory.setHeaders(headersFormatHelper.format(servicesDirectory, request));
			servicesDirectory.setInputData(inputFormatHelper.format(servicesDirectory, request));
			Object serviceResponse = servicesAdapter.execute(servicesDirectory);
			Object responseObject = responseHandlerFactory.getInstanceOf(servicesDirectory).format(serviceResponse,
					request);
			logger.info("Fleet Data Response Fetched for Vid {}", vid);
			return responseObject instanceof JSONObject jsonObject ? jsonObject
					: new JSONObject().put(WidgetConstants.DATA,
							((JSONObject) responseObject).get(WidgetConstants.DATA));
		};
	}

}
