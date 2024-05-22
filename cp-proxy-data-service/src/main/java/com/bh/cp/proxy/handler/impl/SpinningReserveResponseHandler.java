package com.bh.cp.proxy.handler.impl;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class SpinningReserveResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public SpinningReserveResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	protected Object parse(Map<String, Object> request) {

		JSONObject outputdata = new JSONObject();
		try {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			JSONObject inputObject = new JSONObject(response);
			JSONArray data = inputObject.getJSONObject(WidgetConstants.DATA).getJSONArray(WidgetConstants.DATA);
			JSONObject dataObject;
			JSONArray tableDataArray;
			JSONObject tableDataObject;
			JSONArray tableDataArrayFormate = new JSONArray();
			JSONObject valueObject = null;
			JSONObject unitObject = null;
			valueObject = new JSONObject();
			unitObject = new JSONObject();
			for (int i = 0; i < data.length(); i++) {
				dataObject = data.getJSONObject(i);
				Double powerPercentValue = formatetoDecimalPlace(
						dataObject.optDouble(WidgetConstants.POWERPERCENTVALUE, 0.00));
				long powerTimestamp = dataObject.getLong(WidgetConstants.POWER_TIMESTAMP);
				String powerTimeUtc = convertToUtc(powerTimestamp) + " - " + WidgetConstants.AUSTRALIA_BRISBANE;
				String colorCode = getColorCode(powerPercentValue);
				tableDataArray = dataObject.getJSONArray(WidgetConstants.TABLEDATA);

				for (int j = 0; j < tableDataArray.length(); j++) {
					tableDataObject = tableDataArray.getJSONObject(j);
					String parameter = tableDataObject.getString(WidgetConstants.PARAMETER);
					Double value = tableDataObject.optDouble(WidgetConstants.VALUE, 0.00);
					String unit = tableDataObject.getString(WidgetConstants.UNIT);
					if (WidgetConstants.KW.equals(unit)) {
						value = value / 1000.0;
						unit = WidgetConstants.MW;
					}
					if (parameter.equals("Actual power")) {
						unitObject.put("actualPower", unit);
						valueObject.put("actualPower", formatetoDecimalPlace(value));
					} else if (parameter.equals("Maximum power")) {
						unitObject.put("maxDeliverablePower", unit);
						valueObject.put("maxDeliverablePower", formatetoDecimalPlace(value));
					} else if (parameter.equals("SpinningReserve")) {
						unitObject.put("spinningReserve", unit);
						valueObject.put("spinningReserve", formatetoDecimalPlace(value));
					}
					valueObject.put("parameter", "value");
					unitObject.put("parameter", "unit");
				}
				tableDataArrayFormate.put(valueObject);
				tableDataArrayFormate.put(unitObject);
				outputdata.putOnce(WidgetConstants.STATUS, powerTimeUtc);
				outputdata.putOnce(WidgetConstants.LOAD, powerPercentValue);
				outputdata.putOnce(WidgetConstants.MEASUREMENT, "%");
				outputdata.putOnce(WidgetConstants.COLORCODE, colorCode);
				outputdata.putOnce(WidgetConstants.TIMESTAMP, tableDataArrayFormate);
			}

		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return new JSONObject().put(WidgetConstants.DATA, outputdata);

	}

	private static String getColorCode(Double powerPercentValue) {
		return (powerPercentValue > 100) ? "#e6b056" : "#4ca2a8";
	}

	private static String convertToUtc(long powerTimestamp) {
		if (powerTimestamp > 0) {
			Instant instant = Instant.ofEpochMilli(powerTimestamp);
			LocalDateTime dataTimeUtc = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			return dataTimeUtc.format(formatter);
		} else {
			return "null";
		}
	}

	private Double formatetoDecimalPlace(Double value) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		return Double.parseDouble(decimalFormat.format(value));
	}

}