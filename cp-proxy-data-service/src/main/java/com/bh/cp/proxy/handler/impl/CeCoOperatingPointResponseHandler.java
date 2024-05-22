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
import com.bh.cp.proxy.helper.CecoWidgetHelper;

@Component
public class CeCoOperatingPointResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public CeCoOperatingPointResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject dataObject = null;
		List<JSONObject> outputArray = new ArrayList<>();
		Map<String, Object> dataMap;
		try {
			dataMap = (Map<String, Object>) response.getOrDefault(WidgetConstants.DATA, new HashMap<>());
			if (!dataMap.isEmpty()) {
				dataObject = new JSONObject(dataMap);
				outputArray = processCecoData(dataObject);
			} else {
				throw new JSONException(WidgetConstants.RESOURCESAREEMPTY);

			}
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return new JSONObject().put(WidgetConstants.DATA, outputArray);
	}

	private List<JSONObject> processCecoData(JSONObject dataObject) {
		JSONObject politropicHeadGraphData = null;
		JSONArray cecoData;
		List<JSONObject> outputArray = new ArrayList<>();
		JSONObject phasesList = null;
		JSONObject timeseriesData = null;
		Integer phase;
		cecoData = dataObject.optJSONArray(WidgetConstants.CECODATA);
		if (cecoData.length() != 0) {
			for (Object obj : cecoData) {
				phasesList = new JSONObject();
				politropicHeadGraphData = (JSONObject) (obj);
				phase = politropicHeadGraphData.optInt(WidgetConstants.PHASE, 0);
				timeseriesData = politropicHeadGraphData.optJSONObject(WidgetConstants.TIMESERIESDATA,
						new JSONObject());
				phasesList.put(WidgetConstants.TIMESERIESDATA,
						timeseriesData.length() > 0 ? prepareTimeSeriesData(timeseriesData) : new JSONObject());
				phasesList.put(WidgetConstants.PHASE, phase);
				outputArray.add(phasesList);
			}
		} else {
			throw new JSONException(WidgetConstants.RESOURCESAREEMPTY);
		}
		return outputArray;
	}

	private JSONObject prepareTimeSeriesData(JSONObject timeseriesData) {
		JSONObject timeSeriesDataNew = new JSONObject();
		timeSeriesDataNew.put("timestampValues", timeseriesData.optJSONArray("timestampValues"));
		timeSeriesDataNew.put(WidgetConstants.DATA,
				prepareCategoryObject(timeseriesData.optJSONArray(WidgetConstants.DATA)));
		return timeSeriesDataNew;
	}

	private List<JSONObject> prepareCategoryObject(JSONArray dataArray) {
		JSONObject categoryObject;
		JSONObject categoryObjectNew;
		List<JSONObject> categoryArrayNew = new ArrayList<>();
		for (Object obj : dataArray) {
			categoryObject = (JSONObject) obj;
			categoryObjectNew = new JSONObject();
			categoryObjectNew.put(WidgetConstants.COLOR, categoryObject.optString(WidgetConstants.COLOR));
			categoryObjectNew.put(WidgetConstants.DISPLAYNAME, categoryObject.optString(WidgetConstants.DISPLAYNAME));
			categoryObjectNew.put(WidgetConstants.UNITS,
					CecoWidgetHelper.retriveUnits(categoryObject.optString(WidgetConstants.DISPLAYNAME)));
			categoryObjectNew.put(WidgetConstants.YVALUES, categoryObject.optJSONArray(WidgetConstants.YVALUES));
			categoryArrayNew.add(categoryObjectNew);
		}
		return categoryArrayNew;
	}
}