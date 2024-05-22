package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class PeriodFireTokenCountResponse<T> extends JsonResponseHandler<T> {

	@Autowired
	@SuppressWarnings("unchecked")
	protected PeriodFireTokenCountResponse(HttpServletRequest httpServletRequest) {
		super((T) new HashMap<String, Object>());
	}

	@Override
	protected Object parse(Map<String, Object> request) {

		@SuppressWarnings("unchecked")
		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		if (response==null || !(response.containsKey(WidgetConstants.DATA))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
			return nullObject;
		}
		return new JSONObject().put(WidgetConstants.DATA, response);
	}

}