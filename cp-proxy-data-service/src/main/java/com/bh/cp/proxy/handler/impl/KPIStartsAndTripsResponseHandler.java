package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.WidgetConstants;

@Component
public class KPIStartsAndTripsResponseHandler {

	protected Object parse(HashMap<String, Object> response) {
		JSONObject result = new JSONObject();
		JSONObject output = new JSONObject();
		JSONArray resultArray = new JSONArray();
		try {
			JSONObject dataArray = new JSONObject(response);
			int totalResult = dataArray.optInt(WidgetConstants.TOTAL_RESULTS);
			result.put(WidgetConstants.VALUE, totalResult);
			resultArray.put(result);
			output.put(WidgetConstants.DATA, resultArray);
		} catch (Exception e) {
			return output.put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return output;
	}

}
