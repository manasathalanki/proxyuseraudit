package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class DeviceNotUpdatedCounterResponseHandler<T> extends JsonResponseHandler<T> {

	private HttpServletRequest httpServletRequest;
	private ProxyService proxyService;
	private static final Logger logger = LoggerFactory.getLogger(DeviceNotUpdatedCounterResponseHandler.class);

	@Autowired
	@SuppressWarnings("unchecked")
	protected DeviceNotUpdatedCounterResponseHandler(HttpServletRequest httpServletRequest, ProxyService proxyService) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.proxyService = proxyService;
	}

	@Override
	protected Object parse(Map<String, Object> request) {

		@SuppressWarnings("unchecked")
		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		JSONObject totalDeviceCounts;
		JSONObject totalCount;
		Integer totalDeviceNumberCount;
		if (response == null || !(response.containsKey(WidgetConstants.DATA))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(WidgetConstants.DATA, JSONObject.NULL);
			return nullObject;
		}
		totalDeviceCounts = callTotalDeviceCount(request);
		Integer deviceNotUpdatedCount = (Integer) response.get(ProxyConstants.DATA);
		totalCount = totalDeviceCounts != null ? (JSONObject) totalDeviceCounts.get(ProxyConstants.DATA)
				: new JSONObject();
		totalDeviceNumberCount = (Integer) totalCount.get(ProxyConstants.DATA);
		Map<String, Integer> finalResponse = new HashMap<>();
		finalResponse.put(WidgetConstants.DEVICE_NOT_UPDATED_COUNT, deviceNotUpdatedCount);
		finalResponse.put(WidgetConstants.TOTAL_DEVICE_COUNT, totalDeviceNumberCount);
		return new JSONObject().put(WidgetConstants.DATA, finalResponse);
	}

	private JSONObject callTotalDeviceCount(Map<String, Object> request) {
		logger.info("Calling total devices handler......");
		String plantId = request.get(ProxyConstants.PLANTID).toString();
		request.put(ProxyConstants.INPUT_PARAM, ProxyConstants.PATH_PARAM);
		request.put("plantId", plantId);
		request.put(WidgetConstants.SERVICE_ID, 65);
		JSONObject resultResponse = null;
		try {
			resultResponse = (JSONObject) proxyService.execute(request, httpServletRequest);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return resultResponse;
	}
}
