package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.FleetConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class FleetResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public FleetResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {

		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject result = new JSONObject();
		try {
			Map<String, Integer> statusCounts = new HashMap<>();
			Map<String, List<String>> statusLineupIds = new HashMap<>();
			JSONObject inputObject = new JSONObject(response);
			JSONArray jsonArray = inputObject.getJSONArray(WidgetConstants.DATA);
			statusCounts.put(FleetConstants.METRIC_WORKING, 0);
			statusCounts.put(FleetConstants.METRIC_STOPPED, 0);

			for (Object obj : jsonArray) {
				if (obj instanceof JSONObject jsonObject) {
					statusCounts.computeIfPresent(jsonObject.optString(FleetConstants.METRIC_MR_STATUS),
							(key, value) -> value + 1);
					statusLineupIds.computeIfAbsent(jsonObject.optString(FleetConstants.METRIC_MR_STATUS),
							k -> new ArrayList<>()).add(jsonObject.optString(FleetConstants.METRIC_LINEUP_ID));
				}
			}
			JSONObject statusData = null;
			for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
				statusData = new JSONObject();
				statusData.put(FleetConstants.METRIC_VALUE, entry.getValue());
				statusData.put(FleetConstants.METRIC_DATA, new JSONArray(statusLineupIds.get(entry.getKey())));
				result.put(FleetConstants.METRIC_VID, request.get(FleetConstants.METRIC_VID).toString());
				result.put(getStatusDescription(entry.getKey()), statusData);
			}

		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return new JSONObject().put(WidgetConstants.DATA, result);
	}

	static String getStatusDescription(String status) {
		switch (status) {
		case FleetConstants.METRIC_WORKING:
			return FleetConstants.METRIC_RUNNING;
		case FleetConstants.METRIC_STOPPED:
			return FleetConstants.METRIC_NOT_RUNNING;
		default:
			return "Unknown";
		}

	}
}
