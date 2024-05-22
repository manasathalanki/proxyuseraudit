package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class KPIOpenCasesResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public KPIOpenCasesResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject responseObject = new JSONObject();
		JSONArray resourcesArray = new JSONArray();
		List<Map<String, String>> outputList = new ArrayList<>();
		outputList.add(createMap(WidgetConstants.DATA, WidgetConstants.NODATAFOUND));
		try {
			JSONObject inputObject = new JSONObject(response);
			resourcesArray = inputObject.getJSONArray(WidgetConstants.RESOURCES);
			if (resourcesArray.length() != 0) {
				List<String> statusList = new ArrayList<>();
				Map<String, Integer> countStatusMap = null;
				for (int i = 0; i < resourcesArray.length(); i++) {
					statusList.add(resourcesArray.getJSONObject(i).optString(WidgetConstants.STATUS, ""));
				}
				countStatusMap = new HashMap<>();
				for (String str : statusList) {
					countStatusMap.put(str, countStatusMap.getOrDefault(str, 0) + 1);
				}
				outputList = new ArrayList<>();
				outputList
						.add(createMap("Open Cases", countStatusMap.getOrDefault(WidgetConstants.OPENC, 0).toString()));
				responseObject.put(WidgetConstants.DATA, outputList);
			} else {
				throw new JSONException(WidgetConstants.RESOURCESAREEMPTY);
			}

		} catch (Exception e) {
			responseObject.put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return responseObject;
	}

	private Map<String, String> createMap(String title, String value) {

		Map<String, String> map = new HashMap<>();
		map.put(WidgetConstants.TITLE, title);
		map.put(WidgetConstants.VALUE, value);

		return map;
	}
}