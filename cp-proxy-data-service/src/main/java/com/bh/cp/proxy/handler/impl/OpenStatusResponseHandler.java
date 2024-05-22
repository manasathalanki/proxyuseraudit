package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class OpenStatusResponseHandler<T> extends JsonResponseHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(OpenStatusResponseHandler.class);

	@SuppressWarnings("unchecked")
	public OpenStatusResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject requestObject = new JSONObject(response);
		JSONObject responseObject = new JSONObject();
		Map<String, String> colorMap = new HashMap<>();
		colorMap.put(WidgetConstants.PERFORMANCE, ColorConstants.PERFORMANCEC);
		colorMap.put(WidgetConstants.FILTERS, ColorConstants.FILTERSC);
		colorMap.put(WidgetConstants.ACTUATION, ColorConstants.ACTUATIONC);
		colorMap.put(WidgetConstants.INSTR, ColorConstants.INSTRC);
		colorMap.put(WidgetConstants.LUBRICATION, ColorConstants.LUBRICATIONC);
		colorMap.put(WidgetConstants.COMBUSTION, ColorConstants.COMBUSTIONC);
		colorMap.put(WidgetConstants.LEAKAGES, ColorConstants.LEAKAGESC);
		colorMap.put(WidgetConstants.DQUALITY, ColorConstants.DQUALITYC);
		colorMap.put(WidgetConstants.DRYGAS, ColorConstants.DRYGASC);
		List<String> anomalyList = new ArrayList<>();
		List<Map<String, String>> outputList = new ArrayList<>();
		try {
			JSONArray resourcesArray = requestObject.getJSONArray(WidgetConstants.RESOURCES);
			JSONObject filteredObject = new JSONObject();
			if (resourcesArray.length() != 0) {
				for (int i = 0; i < resourcesArray.length(); i++) {
					anomalyList.add(resourcesArray.getJSONObject(i).optString(WidgetConstants.ANOMALYCATEGORY,
							WidgetConstants.OTHER));
				}
				Map<String, Integer> countMap = new HashMap<>();
				for (String str : anomalyList) {
					countMap.put(str, countMap.getOrDefault(str, 0) + 1);
				}
				outputList = new ArrayList<>();
				for (Map.Entry<String, Integer> map : countMap.entrySet()) {
					if (colorMap.containsKey(map.getKey())) {
						outputList.add(createMap(map.getKey(), map.getValue().toString(), colorMap.get(map.getKey())));
					} else {
						outputList.add(createMap(map.getKey(), map.getValue().toString(), ColorConstants.COMBUSTIONC));
					}
				}
				filteredObject.put(WidgetConstants.TOTAL, anomalyList.size());
				filteredObject.put(WidgetConstants.OPENCASES, outputList);
				responseObject.put(WidgetConstants.DATA, filteredObject);
			} else {
				throw new JSONException(WidgetConstants.RESOURCESAREEMPTY);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			responseObject.put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return responseObject;
	}

	public Map<String, String> createMap(String title, String value, String color) {
		Map<String, String> map = new HashMap<>();
		map.put(WidgetConstants.CATEGORYNAME, title);
		map.put(WidgetConstants.NOOFCASES, value);
		map.put(WidgetConstants.COLOR, color);
		return map;
	}
}
