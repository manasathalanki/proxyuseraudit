package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class KPIAvailabilityResponseHandler<T> extends JsonResponseHandler<T> {

	private KPIReliabilityAndAvailabilityResponseHandler reliabilityAndAvailabilityResponseHandler;

	@SuppressWarnings("unchecked")
	public KPIAvailabilityResponseHandler(
			@Autowired KPIReliabilityAndAvailabilityResponseHandler reliabilityAndAvailabilityResponseHandler) {
		super((T) new HashMap<String, Object>());
		this.reliabilityAndAvailabilityResponseHandler = reliabilityAndAvailabilityResponseHandler;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		return reliabilityAndAvailabilityResponseHandler.parse(response,request);
	}
}
