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
public class CeCoMapResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public CeCoMapResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		JSONObject outputObject = new JSONObject();
		try {
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			JSONObject inputObject = new JSONObject(response);
			JSONArray cecoDataArray = inputObject.getJSONObject(ProxyConstants.DATA)
					.getJSONArray(WidgetConstants.CECODATA);
			outputObject.put(WidgetConstants.CECODATA, new JSONArray());
			JSONObject outputCecoData ;
			JSONObject cecoDataObject;
			JSONObject filteredGraphData;
			JSONObject graphDataObject ;
			for (int i = 0; i < cecoDataArray.length(); i++) {
				 cecoDataObject = cecoDataArray.getJSONObject(i);
					outputCecoData = new JSONObject();
				for (String key : cecoDataObject.keySet()) {
					if (key.equals(WidgetConstants.POLITROPIC_HEAD_GRAPHDATA)
							|| key.equals(WidgetConstants.POLITROPIC_EFFICIENCY_GRAPH_DATA)
							|| key.equals(WidgetConstants.PRESSURE_RATIO_GRAPH_DATA)) {
						 graphDataObject = cecoDataObject.getJSONObject(key);
						 filteredGraphData = processGrapData(graphDataObject);
						outputCecoData.put(key, filteredGraphData);
					} else if (!key.equals(WidgetConstants.HISTOGRAM_DATA)
							&& !key.equals(WidgetConstants.TIMESERIESDATA) && !key.equals(WidgetConstants.FROM)
							&& !key.equals(WidgetConstants.TO)) {
						outputCecoData.put(key, cecoDataObject.get(key));
					}
				}
				outputObject.getJSONArray(WidgetConstants.CECODATA).put(outputCecoData);
			}
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return new JSONObject().put(WidgetConstants.DATA, outputObject);
	}

	private static JSONObject processGrapData(JSONObject graphDataInput) {
		JSONObject filterGraphDataObject = new JSONObject();
		for (String key : graphDataInput.keySet()) {
			if (key.equals(WidgetConstants.MAPS_DATA)) {
				JSONArray mapsData = graphDataInput.getJSONArray(key);
				JSONArray filteredMapsData = filterMapsData(mapsData);
				filterGraphDataObject.put(key, filteredMapsData);
			} else if (!key.equals(WidgetConstants.HISTOGRAM_DATA) && !key.equals(WidgetConstants.TIMESERIESDATA)) {
				filterGraphDataObject.put(key, graphDataInput.get(key));
			}
		}
		return filterGraphDataObject;
	}

	private static JSONArray filterMapsData(JSONArray mapsData) {
		JSONArray filterMapsData = new JSONArray();
		JSONObject filterMapsDataObject;
		for (int j = 0; j < filterMapsData.length(); j++) {
			JSONObject mapsDataObject = mapsData.getJSONObject(j);
			filterMapsDataObject = new JSONObject();
			filterMapsDataObject.put(WidgetConstants.MAP_NAME, mapsDataObject.optString(WidgetConstants.MAP_NAME, ""));
			filterMapsDataObject.put(WidgetConstants.X_VALUES, mapsDataObject.optJSONArray(WidgetConstants.X_VALUES));
			filterMapsDataObject.put(WidgetConstants.Y_VALUES, mapsDataObject.optJSONArray(WidgetConstants.Y_VALUES));
			filterMapsDataObject.put(WidgetConstants.LEGEND_VALUE,
					mapsDataObject.optDouble(WidgetConstants.LEGEND_VALUE, 0.00));
			filterMapsDataObject.put(WidgetConstants.LEGEND_UNIT,
					mapsDataObject.optString(WidgetConstants.LEGEND_UNIT, ""));
			filterMapsData.put(filterMapsDataObject);
		}
		return filterMapsData;

	}

}
