package com.bh.cp.proxy.handler.impl;

import java.text.DecimalFormat;
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

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class KPIMtbtResponseHandler<T> extends JsonResponseHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(KPIMtbtResponseHandler.class);

	@SuppressWarnings("unchecked")
	public KPIMtbtResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject requestObject = new JSONObject(response);
		JSONObject responseObject = new JSONObject();
		List<Map<String, String>> outputList = new ArrayList<>();
		try {
			JSONArray dataObject = requestObject.getJSONArray(WidgetConstants.RESPONSES);
			if (dataObject.length() != 0) {
				JSONObject object = null;
				for (int i = 0; i < dataObject.length(); i++) {
					object = dataObject.getJSONObject(i);
					Double value = object.optDouble(WidgetConstants.MTBT, 0.0);
					outputList.add(createMap(WidgetConstants.MTBT, formatetoDecimalPlace(value).toString()));
				}
				responseObject.put(WidgetConstants.DATA, outputList);
			} else {
				throw new JSONException(WidgetConstants.RESPONSESAREEMPTY);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			responseObject.put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return responseObject;

	}

	public Map<String, String> createMap(String title, String value) {

		Map<String, String> resultMap = new HashMap<>();
		resultMap.put(WidgetConstants.TITLE, title);
		resultMap.put(WidgetConstants.VALUE, value);
		return resultMap;

	}

	private Double formatetoDecimalPlace(Double value) {
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return Double.parseDouble(decimalFormat.format(value));
	}
}
