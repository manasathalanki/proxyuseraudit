package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class ThrustBearingLoad7BSummaryResponseHandler<T> extends JsonResponseHandler<T> {

	private ThrustBearingLoadSummaryResponseHandler thrustBearingLoadSummaryResponseHandler;

	@SuppressWarnings("unchecked")
	public ThrustBearingLoad7BSummaryResponseHandler(
			@Autowired ThrustBearingLoadSummaryResponseHandler thrustBearingLoadSummaryResponseHandler) {
		super((T) new HashMap<String, Object>());
		this.thrustBearingLoadSummaryResponseHandler = thrustBearingLoadSummaryResponseHandler;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		return thrustBearingLoadSummaryResponseHandler.parse(response, request, ProxyConstants.THRUST_7B);
	}
}
