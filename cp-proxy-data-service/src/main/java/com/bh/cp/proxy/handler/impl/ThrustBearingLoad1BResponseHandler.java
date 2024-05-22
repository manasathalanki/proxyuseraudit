package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class ThrustBearingLoad1BResponseHandler<T> extends JsonResponseHandler<T> {

	private ThrustBearingLoadResponseHandler thrustBearingLoadResponseHandler;

	@SuppressWarnings("unchecked")
	public ThrustBearingLoad1BResponseHandler(
			@Autowired ThrustBearingLoadResponseHandler thrustBearingLoadResponseHandler) {
		super((T) new HashMap<String, Object>());
		this.thrustBearingLoadResponseHandler = thrustBearingLoadResponseHandler;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		return thrustBearingLoadResponseHandler.parse(response);
	}
}
