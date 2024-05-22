package com.bh.cp.proxy.util;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.bh.cp.proxy.dto.request.DeviceDataRequestDTO;

import jakarta.validation.Valid;

@Component
public class DeviceDataAppender {

	public Map<String, Object> appendRequestData(@Valid DeviceDataRequestDTO dataRequest,
			Map<String, Object> widgetsDataRequest) {

		if (!(StringUtil.isEmptyCaseString(dataRequest.getPlantId()))) {
			widgetsDataRequest.put("plantId", dataRequest.getPlantId());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getDeviceId()))) {
			widgetsDataRequest.put("deviceId", Integer.parseInt(dataRequest.getDeviceId()));
		}
		return widgetsDataRequest;
	}

}
