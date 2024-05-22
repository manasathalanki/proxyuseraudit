package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class KpiTokenResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	protected KpiTokenResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		int count;
		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		if (!(response.containsKey(WidgetConstants.DATA))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(WidgetConstants.DATA, JSONObject.NULL);
			return nullObject;
		}
		JSONObject jsonObject = new JSONObject(response);
		JSONObject outputObject = new JSONObject();

		JSONArray array = jsonObject.getJSONArray(WidgetConstants.DATA);
		count = 0;
		if (array.length() != 0) {
			for (int i = 0; i < array.length(); i++) {
				String token = array.getJSONObject(i).optString((ProxyConstants.TOKEN_SMALL), "");
				count = count + Integer.parseInt(token);
			}
		}
		outputObject.put(ProxyConstants.TOKEN_COUNTS, count);
		outputObject.put(ProxyConstants.TOKEN_LISTS, array);
		return new JSONObject().put(WidgetConstants.DATA, outputObject);
	}

}
