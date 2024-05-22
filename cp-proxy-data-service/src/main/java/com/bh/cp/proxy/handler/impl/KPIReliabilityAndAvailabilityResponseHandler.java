package com.bh.cp.proxy.handler.impl;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;

@Component
public class KPIReliabilityAndAvailabilityResponseHandler {

	protected Object parse(HashMap<String, Object> response, Map<String, Object> request) {
		try {
			String level = (String) request.get(WidgetConstants.LEVEL);
			JSONArray dataArray = new JSONObject(response).optJSONArray(WidgetConstants.DATA);
			if (dataArray.length() > 0)
				return prepareFinalObject(dataArray.optJSONObject(0), level);
			else
				throw new JSONException(WidgetConstants.DATAOBJECTISEMPTY);
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
	}

	private JSONObject prepareFinalObject(JSONObject data, String level) {
		JSONObject outputObject = new JSONObject();
		JSONObject finalObject = new JSONObject();
		if (level.equals(ProxyConstants.LEVEL_PROJECTS) || level.equals(ProxyConstants.LEVEL_PLANTS)
				|| level.equals(ProxyConstants.LEVEL_TRAINS)) {
			outputObject.put(WidgetConstants.SINGLE, JSONObject.NULL);
			outputObject.put(WidgetConstants.MAXASSETID, retriveString(data.optJSONObject(WidgetConstants.MAX)));
			outputObject.put(WidgetConstants.MINASSETID, retriveString(data.optJSONObject(WidgetConstants.MIN)));
			outputObject.put(WidgetConstants.MAX, retriveDouble(data.optJSONObject(WidgetConstants.MAX)));
			outputObject.put(WidgetConstants.MIN, retriveDouble(data.optJSONObject(WidgetConstants.MIN)));
			outputObject.put(WidgetConstants.ENDDATE, retriveEndDate(data.optJSONObject(WidgetConstants.MAX)));
		} else {
			outputObject.put(WidgetConstants.ENDDATE, retriveEndDate(data.optJSONObject(WidgetConstants.SINGLE)));
			outputObject.put(WidgetConstants.SINGLE, retriveDouble(data.optJSONObject(WidgetConstants.SINGLE)));
			outputObject.put(WidgetConstants.MAX, JSONObject.NULL);
			outputObject.put(WidgetConstants.MIN, JSONObject.NULL);
		}
		return finalObject.put(WidgetConstants.DATA, outputObject);
	}

	private double formateToDecimalPlace(Double value) {
		DecimalFormat decimalFormat = new DecimalFormat("#.0");
		return Double.parseDouble(decimalFormat.format(value));
	}

	private String retriveEndDate(JSONObject endDateObj) {
		String endDate = endDateObj.optString(WidgetConstants.ENDDATE, WidgetConstants.EMPTYSTRING);
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(WidgetConstants.INPUTFORMATTER);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(WidgetConstants.OUTPUTFORMATTER);
		return WidgetConstants.DESCAVAILIABILITY+ outputFormatter.format(inputFormatter.parse(endDate));
	}

	private Object retriveDouble(JSONObject objectRetrived) {
		Object value = objectRetrived.optDouble(WidgetConstants.VALUE, 0.0);
		return (Double) value > 0.0 ? formateToDecimalPlace((Double) value) : JSONObject.NULL;
	}

	private String retriveString(JSONObject objectRetrived) {
		return objectRetrived.optString(WidgetConstants.ASSETID, WidgetConstants.EMPTYSTRING);
	}
}