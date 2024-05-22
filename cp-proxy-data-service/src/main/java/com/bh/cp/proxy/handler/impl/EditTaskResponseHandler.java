package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class EditTaskResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public EditTaskResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {

		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		JSONObject jsonObject = new JSONObject(response);
		JSONObject responseObj = new JSONObject();

		try {

			JSONObject map;

			if (jsonObject.length() != 0) {

				map = new JSONObject();

				map.put(ProxyConstants.TASK_ID,
						jsonObject.optString(ProxyConstants.TASK_ID) != null
								? jsonObject.optString(ProxyConstants.TASK_ID)
								: "");

				map.put(ProxyConstants.TASK_STATUS,
						jsonObject.optString(ProxyConstants.TASK_STATUS) != null
								? jsonObject.optString(ProxyConstants.TASK_STATUS)
								: "");

				map.put(ProxyConstants.ROOT_CAUSE,
						jsonObject.optString(ProxyConstants.ROOT_CAUSE) != null
								? jsonObject.optString(ProxyConstants.ROOT_CAUSE)
								: "");

				responseObj.put("data", map);

			}
		} catch (JSONException e) {
			responseObj.put(WidgetConstants.DATA, JSONObject.NULL);
		}

		return responseObj;

	}

}
