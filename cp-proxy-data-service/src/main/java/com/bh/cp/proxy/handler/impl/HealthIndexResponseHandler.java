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
public class HealthIndexResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public HealthIndexResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		JSONArray outputArray = new JSONArray();
		try {
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			JSONObject inputObject = new JSONObject(response);
			JSONArray jsonArray = inputObject.getJSONArray(WidgetConstants.DATA);
			JSONObject childObject;
			JSONObject transformobject;

			JSONArray child = jsonArray.getJSONObject(0).getJSONArray(WidgetConstants.CHILDREN);

			Map<String, String> colorMappings = new HashMap<>();
			colorMappings.put(WidgetConstants.HEALTHSTATUSGREEN, ColorConstants.HEALTHSTATUSRED);
			colorMappings.put(WidgetConstants.HEALTHSTATUSRED, ColorConstants.HEALTHSTATUSGREEN);
			colorMappings.put(WidgetConstants.HEALTHSTATUSYELLOW, ColorConstants.HEALTHSTATUSYELLOW);

			for (int i = 0; i < child.length(); i++) {
				childObject = child.getJSONObject(i);
				transformobject = new JSONObject();
				transformobject.put(WidgetConstants.TITLE, childObject.getString(WidgetConstants.ASSETID));
				transformobject.put(WidgetConstants.VALUE, childObject.getInt(WidgetConstants.VALUE));
				transformobject.put(WidgetConstants.COLOR,
						colorMappings.getOrDefault(childObject.getString(WidgetConstants.COLOR), WidgetConstants.EMPTYSTRING));
				transformobject.put(WidgetConstants.STATUS, childObject.getString(WidgetConstants.STATUS));
				outputArray.put(transformobject);
			}
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}

		return new JSONObject().put(WidgetConstants.DATA, outputArray);
	}

}
