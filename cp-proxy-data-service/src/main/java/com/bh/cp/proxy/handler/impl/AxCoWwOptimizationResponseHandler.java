package com.bh.cp.proxy.handler.impl;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class AxCoWwOptimizationResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public AxCoWwOptimizationResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	protected Object parse(Map<String, Object> request) {
		JSONObject output = new JSONObject();
		try {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			JSONObject inputObject = new JSONObject(response).getJSONObject(WidgetConstants.DATA);
			if (inputObject.get(WidgetConstants.AXCO_EFFICIENCY) != null) {
				String axcoEfficiency = inputObject.optString(WidgetConstants.AXCO_EFFICIENCY,null);
				String lastAxcoEfficiency = inputObject.optString(WidgetConstants.LAST_AXCO_EFFICIENCY,null);
				int suggestedHoursNextWW = inputObject.optInt(WidgetConstants.SUGGESTED_HOURS_NEXT_WW, 0);
				long lastAxcoWWTimestamp = inputObject.optLong(WidgetConstants.LAST_AXCO_WW_TIMESTAMP);
				double axcoEfficiencyPercantage = (axcoEfficiency!=null)?convertToPercantage(Double.valueOf(axcoEfficiency)):0.0;
				double lastAxcoEfficiencyPercantage = (lastAxcoEfficiency!=null)?convertToPercantage(Double.valueOf(lastAxcoEfficiency)):0.0;

				double decreaseInLastAxcoEfficiency = 0;
				String finalDecrease = "";
				if (axcoEfficiencyPercantage > 0.0 && lastAxcoEfficiencyPercantage >0.0) {
					decreaseInLastAxcoEfficiency = Double.valueOf(axcoEfficiencyPercantage)
							- Double.valueOf(lastAxcoEfficiencyPercantage);
					DecimalFormat df = new DecimalFormat("0.0");
					finalDecrease = String.valueOf(df.format(decreaseInLastAxcoEfficiency) + "%");
				}
				String utcDate = convertToUtc(lastAxcoWWTimestamp);
				Map<String, String> colorInfo = determineColor(decreaseInLastAxcoEfficiency);
				output.put(WidgetConstants.NEXT_OFFLINE_WW_SUGGESTED_ON,
						String.format("%dHours", suggestedHoursNextWW));
				output.put(WidgetConstants.LAST_AXCO_EFFICIENCY,
						finalDecrease.equals("-0.0%") ? "0.0%" : finalDecrease);
				output.put(WidgetConstants.AXCO_EFFICIENCY, String.format("%.1f", axcoEfficiencyPercantage));
				output.put(WidgetConstants.LAST_WW_EXECUTED_ON, utcDate);
				output.put(WidgetConstants.COLORCODE, colorInfo.get(WidgetConstants.COLORCODE));
				output.put(WidgetConstants.BACKGROUND, colorInfo.get(WidgetConstants.BACKGROUND));
				output.put(WidgetConstants.COLORLABLE, colorInfo.get(WidgetConstants.COLORLABLE));
				output.put(WidgetConstants.STATUS, colorInfo.get(WidgetConstants.LABEL));

				output.put(WidgetConstants.TITLE, WidgetConstants.GT_AXCO_OPTIMIZATION);
			}
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return new JSONObject().put(WidgetConstants.DATA, output);
	}

	private static String convertToUtc(long lastTimestamp) {

		if (lastTimestamp > 0) {
			Instant instant = Instant.ofEpochMilli(lastTimestamp);
			LocalDateTime dataTimeUtc = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			return dataTimeUtc.format(formatter);
		}
		return "-";
	}

	private static double convertToPercantage(double value) {
		return value * 100;

	}

	private Map<String, String> determineColor(double decrementValue) {
		Map<String, String> colorInfo = new HashMap<>();
		if (decrementValue < -0.5) {
			colorInfo.put(WidgetConstants.COLORCODE, "#e87516");
			colorInfo.put(WidgetConstants.BACKGROUND, "#fefbe6");
			colorInfo.put(WidgetConstants.COLORLABLE, "red");
			colorInfo.put(WidgetConstants.LABEL, "Decrement since last offline WW");

		} else if (decrementValue > 0.5) {
			colorInfo.put(WidgetConstants.COLORCODE, "#02a783");
			colorInfo.put(WidgetConstants.BACKGROUND, "#effcf6");
			colorInfo.put(WidgetConstants.COLORLABLE, "green");
			colorInfo.put(WidgetConstants.LABEL, "Increment since last offline WW");
		} else if (decrementValue >= -0.5 && decrementValue <= 0.5) {
			colorInfo.put(WidgetConstants.COLORCODE, "#595959");
			colorInfo.put(WidgetConstants.BACKGROUND, "#ebefee");
			colorInfo.put(WidgetConstants.COLORLABLE, "grey");
			colorInfo.put(WidgetConstants.LABEL, "Equal since last offline WW");
		}
		return colorInfo;

	}

}
