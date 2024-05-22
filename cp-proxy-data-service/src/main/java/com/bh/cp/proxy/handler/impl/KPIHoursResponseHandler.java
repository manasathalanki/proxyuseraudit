package com.bh.cp.proxy.handler.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class KPIHoursResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public KPIHoursResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		JSONArray resultArray = new JSONArray();
		JSONObject outputJson = new JSONObject();
		JSONObject outputResult = new JSONObject();
		try {
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			JSONArray dataArray = new JSONObject(response).getJSONArray(WidgetConstants.DATA);
			if (dataArray.length() > 0) {
				double lastValue = extractLastValue(dataArray);
				DecimalFormat decimalFormat = new DecimalFormat("#.#");
				lastValue = Double.parseDouble(decimalFormat.format(lastValue));
				outputJson.put(WidgetConstants.VALUE, lastValue);
				resultArray.put(outputJson);
				outputResult.put(WidgetConstants.DATA, resultArray);

			} else {
				throw new JSONException(WidgetConstants.DATAOBJECTISEMPTY);
			}

		} catch (Exception e) {
			return outputResult.put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return outputResult;
	}

	private static double extractLastValue(JSONArray jsonArray) {
		double lastValue = 0;
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject lineupObject = jsonArray.getJSONObject(i);
			JSONArray modulesArray = lineupObject.getJSONArray(WidgetConstants.MODULES);
			for (int j = 0; j < modulesArray.length(); j++) {
				JSONObject sectionsObject = modulesArray.getJSONObject(j).getJSONArray(WidgetConstants.SECTIONS)
						.getJSONObject(0);
				JSONObject calculationsObject = sectionsObject.getJSONArray(WidgetConstants.CALCULATIONS)
						.getJSONObject(0);
				JSONArray outputArray = calculationsObject.getJSONArray(WidgetConstants.OUTPUTS);
				for (int k = 0; k < outputArray.length(); k++) {
					JSONObject outputsObject = outputArray.getJSONObject(k);
					JSONObject summaryObject = outputsObject.getJSONObject(WidgetConstants.SUMMARY);
					lastValue = summaryObject.optDouble(WidgetConstants.LAST_VALUE);

				}
			}
		}
		return lastValue;

	}

}