package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class KPIStartsResponseHandler<T> extends JsonResponseHandler<T> {

	private KPIStartsAndTripsResponseHandler kpiStartsAndTripsResponseHandler;

	@SuppressWarnings("unchecked")
	public KPIStartsResponseHandler(@Autowired KPIStartsAndTripsResponseHandler kpiStartsAndTripsResponseHandler) {
		super((T) new HashMap<String, Object>());
		this.kpiStartsAndTripsResponseHandler = kpiStartsAndTripsResponseHandler;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		return kpiStartsAndTripsResponseHandler.parse(response);
	}

}
