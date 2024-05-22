package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
public class OpenCaseStatusResponseHandler<T> extends JsonResponseHandler<T> {

	private static final Logger logger = LoggerFactory.getLogger(OpenCaseStatusResponseHandler.class);

	@SuppressWarnings("unchecked")
	public OpenCaseStatusResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {

		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject requestObject = new JSONObject(response);
		JSONObject responseObject = new JSONObject();
		Map<String, String> colorMap = new HashMap<>();
		colorMap.put(WidgetConstants.PERFORMANCE, ColorConstants.CASE_PERFORMANCE);
		colorMap.put(WidgetConstants.LUBRICATION, ColorConstants.CASE_LUBRICATIONS);
		colorMap.put(WidgetConstants.COMBUSTION, ColorConstants.CASE_CUMBITION);
		colorMap.put(WidgetConstants.DQUALITY, ColorConstants.CASE_DATAQUALITYC);
		colorMap.put(WidgetConstants.DRYGAS, ColorConstants.CASE_DRY_GAS);
		colorMap.put(WidgetConstants.ROTORDYN, ColorConstants.CASE_ROTORDYN);
		colorMap.put(WidgetConstants.ENCLTEMP, ColorConstants.CASE_ENCLTEMP);

		List<String> anomalyList = new ArrayList<>();
		List<Map<String, String>> outputList = new ArrayList<>();
		try {
			JSONArray resourcesArray = requestObject.getJSONArray(WidgetConstants.RESOURCES);
			JSONObject filteredObject = new JSONObject();
			if (resourcesArray.length() != 0) {
				anomalyList = resultResponse(resourcesArray, anomalyList, colorMap);
				Map<String, Integer> countMap = new HashMap<>();
				for (String str : anomalyList) {
					countMap.put(str, countMap.getOrDefault(str, 0) + 1);
				}

				LinkedHashMap<String, Integer> map2 = countMap.entrySet().stream()
						.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> null,
								(() -> new LinkedHashMap<String, Integer>())));

				outputList = new ArrayList<>();
				for (Map.Entry<String, Integer> map : map2.entrySet()) {
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

	private List<String> resultResponse(JSONArray resourcesArray, List<String> anomalyList,
			Map<String, String> colorMap) {
		for (int i = 0; i < resourcesArray.length(); i++) {
			anomalyList.add(
					resourcesArray.getJSONObject(i).optString(WidgetConstants.ANOMALYCATEGORY, WidgetConstants.OTHER));
		}

		for (int i = 0; i < anomalyList.size(); i++) {
			String str = anomalyList.get(i);
			if (!(colorMap.containsKey(str))) {
				anomalyList.set(i, WidgetConstants.OTHER);
			}
		}
		return anomalyList;
	}

	public Map<String, String> createMap(String title, String value, String color) {
		Map<String, String> map = new HashMap<>();
		map.put(WidgetConstants.CATEGORYNAME, title);
		map.put(WidgetConstants.NOOFCASES, value);
		map.put(WidgetConstants.COLOR, color);
		return map;
	}

}
