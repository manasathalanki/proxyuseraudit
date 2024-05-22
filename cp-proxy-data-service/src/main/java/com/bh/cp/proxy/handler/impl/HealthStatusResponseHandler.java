package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class HealthStatusResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public HealthStatusResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		JSONArray outputArray = new JSONArray();
		try {
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			JSONArray jsonArray = new JSONObject(response).getJSONArray(WidgetConstants.DATA);
			JSONObject childObject;
			JSONObject outputObject;

			Map<String, String> colorMappings = new HashMap<>();
			colorMappings.put(WidgetConstants.HEALTHSTATUSGREEN, ColorConstants.HEALTHSTATUSGREEN);
			colorMappings.put(WidgetConstants.HEALTHSTATUSRED, ColorConstants.HEALTHSTATUSRED);
			colorMappings.put(WidgetConstants.HEALTHSTATUSYELLOW, ColorConstants.HEALTHSTATUSYELLOW);

			for (int i = 0; i < jsonArray.length(); i++) {
				childObject = jsonArray.getJSONObject(i);
				outputObject = new JSONObject();
				outputObject.put(WidgetConstants.TITLE, childObject.getString(WidgetConstants.ASSETID));
				outputObject.put(WidgetConstants.VALUE, childObject.getInt(WidgetConstants.VALUE));
				outputObject.put(WidgetConstants.COLOR,
						colorMappings.getOrDefault(childObject.getString(WidgetConstants.COLOR), WidgetConstants.EMPTYSTRING));
				outputObject.put(WidgetConstants.STATUS, childObject.getString(WidgetConstants.STATUS));
				outputArray.put(outputObject);
			}
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return new JSONObject().put(WidgetConstants.DATA, outputArray);
	}


}
