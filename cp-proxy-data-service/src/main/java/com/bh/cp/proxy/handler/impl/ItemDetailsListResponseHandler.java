package com.bh.cp.proxy.handler.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ItemDetailsListResponseHandler<T> extends JsonResponseHandler<T> {

	JSONObject map;
	JSONArray list;

	@Autowired
	@SuppressWarnings("unchecked")
	protected ItemDetailsListResponseHandler(HttpServletRequest httpServletRequest) {
		super((T) new HashMap<String, Object>());
	}

	@Override
	protected Object parse(Map<String, Object> request) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		if (!(response.containsKey(WidgetConstants.DATA))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(WidgetConstants.DATA, JSONObject.NULL);
			return nullObject;
		}
		JSONObject jsonObject = new JSONObject(response);
		JSONObject outputObject = new JSONObject();

		JSONArray array = jsonObject.getJSONArray(WidgetConstants.DATA);
		list = new JSONArray();
		for (int i = 0; i < array.length(); i++) {

			map = new JSONObject();

			Long firstEventDate = Long
					.parseLong(array.getJSONObject(i).optString((JSONUtilConstants.LAST_UPDATE_DATE), ""));

			String dateUpdate = new SimpleDateFormat(ProxyConstants.DATE_FORMAT_TO_UTC_MM)
					.format(new Date(firstEventDate));
			map.put(JSONUtilConstants.LAST_UPDATE_DATE, dateUpdate);

			String status = array.getJSONObject(i).optString((JSONUtilConstants.STATUS), "");
			if (status != null && status.equalsIgnoreCase("OK")) {
				map.put(JSONUtilConstants.STATUS, "Updated");
			} else {
				map.put(JSONUtilConstants.STATUS, "Not Updated");
			}

			map.put(JSONUtilConstants.ITEM,
					array.getJSONObject(i).optString((JSONUtilConstants.GROUP_DESCRIPTION), ""));
			map.put(JSONUtilConstants.DESCRIPTION, array.getJSONObject(i).optString((JSONUtilConstants.NAME_ITEM), ""));

			list.put(map);
		}

		outputObject.put("list", list);

		return new JSONObject().put(WidgetConstants.DATA, outputObject);
	}

}
