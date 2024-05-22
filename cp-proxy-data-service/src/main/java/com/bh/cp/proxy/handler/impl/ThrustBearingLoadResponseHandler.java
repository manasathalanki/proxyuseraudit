package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.WidgetConstants;

@Component
public class ThrustBearingLoadResponseHandler {

	private static final Logger logger = LoggerFactory.getLogger(ThrustBearingLoadResponseHandler.class);

	protected Object parse(HashMap<String, Object> response) {
		JSONObject responseObject = null;
		try {
			JSONObject json = null;
			JSONObject object = null;
			List<String> colorList = new ArrayList<>();
			colorList.add(ColorConstants.THRUSTOVER);
			colorList.add(ColorConstants.THRUSTNORMAL);
			Double recoup = 0.0;
			JSONArray array = new JSONObject(response).getJSONArray(WidgetConstants.DATA);

			for (int i = 0; i < array.length(); i++) {
				json = array.getJSONObject(i);
				responseObject = new JSONObject();
				responseObject.put(WidgetConstants.RECOUPTYPE, json.get(WidgetConstants.RECOUPTYPE));
				object = new JSONObject(json.get(WidgetConstants.SCALE).toString());
				responseObject.put(WidgetConstants.LOADMINSCALE, object.get(WidgetConstants.LOADMINSCALE));
				responseObject.put(WidgetConstants.LOADMAX, object.get(WidgetConstants.LOADMAX));
				responseObject.put(WidgetConstants.LOADMIN, object.get(WidgetConstants.LOADMIN));
				responseObject.put(WidgetConstants.LOADMAXSCALE, object.get(WidgetConstants.LOADMAXSCALE));
				recoup += json.has(WidgetConstants.RECOUP) ? (Double) (json.get(WidgetConstants.RECOUP)) : 0.0;
				responseObject.put(WidgetConstants.RECOUP, Math.round(recoup));
				responseObject.put(WidgetConstants.UNIT, WidgetConstants.LBF);
				responseObject.put(WidgetConstants.ORIFICEINSTALLED,
						json.has(WidgetConstants.ORIFICEINSTALLED) ? json.get(WidgetConstants.ORIFICEINSTALLED)
								: JSONObject.NULL);
				responseObject.put(WidgetConstants.COLOR, colorList);
			}

			if (responseObject == null || responseObject.getDouble(WidgetConstants.RECOUP) == 0.0) {
				return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return new JSONObject().put(WidgetConstants.DATA, responseObject);
	}
}
