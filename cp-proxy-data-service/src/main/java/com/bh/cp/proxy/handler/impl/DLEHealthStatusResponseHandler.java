package com.bh.cp.proxy.handler.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class DLEHealthStatusResponseHandler<T> extends JsonResponseHandler<T> {

	private Integer runningImageId;

	private Integer stoppedImageId;

	@SuppressWarnings("unchecked")
	public DLEHealthStatusResponseHandler(@Value("${dle.health.stopped.image-id}") Integer stoppedImageId,
			@Value("${dle.health.running.image-id}") Integer runningImageId) {
		super((T) new HashMap<String, Object>());
		this.stoppedImageId = stoppedImageId;
		this.runningImageId = runningImageId;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		JSONObject transformedJson = new JSONObject();
		try {
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			JSONObject inputObject = new JSONObject(response);
			JSONArray data = inputObject.getJSONObject(ProxyConstants.DATA)
					.optJSONArray(WidgetConstants.OPERATINGHOURS);
			JSONObject dataObject;
			JSONObject inputdata = null;
			JSONArray arrayofdata = new JSONArray();
			String colorCode;
			if (data.length() > 0) {
				for (int i = 0; i < data.length(); i++) {
					dataObject = data.getJSONObject(i);
					String colorData = dataObject.optString(WidgetConstants.OPBURNERMODE, "-");
					colorCode = getColorCode(colorData);
					inputdata = new JSONObject();
					inputdata.put(WidgetConstants.OPHOURS, dataObject.getDouble(WidgetConstants.OPHOURS));
					inputdata.put(WidgetConstants.OPBURNERMODE, colorData);
					inputdata.put(WidgetConstants.COLOR, colorCode);
					arrayofdata.put(inputdata);
				}
				transformedJson.put(WidgetConstants.TITLE, WidgetConstants.GTDLEHEALTHSTATUS);
				transformedJson.put(WidgetConstants.GTSTATUS,
						inputObject.getJSONObject(ProxyConstants.DATA).optString(WidgetConstants.GT_STATUS, ""));
				if (inputObject.getJSONObject(ProxyConstants.DATA).get(WidgetConstants.GT_STATUS).equals("Running")) {
					transformedJson.put(WidgetConstants.HEALTHSTATUSIMAGEID, runningImageId);
				}	else {
					transformedJson.put(WidgetConstants.HEALTHSTATUSIMAGEID, stoppedImageId);
				}
				transformedJson.put(WidgetConstants.ACTUALBURNERMODE,
						inputObject.getJSONObject(ProxyConstants.DATA).optString(WidgetConstants.BURNER_MODE, ""));
				transformedJson.put(WidgetConstants.DLEMAPPINGNEESES,
						inputObject.getJSONObject(ProxyConstants.DATA).optString(WidgetConstants.MAPPING_NEEDED, "-"));
				long dateFormate = inputObject.getJSONObject(ProxyConstants.DATA).optLong(WidgetConstants.LAST_DLE, 0);
				String lastDle = dateFormate != 0 ? convertToUtcDate(dateFormate) : "-";

				transformedJson.put(WidgetConstants.LAST_BURNER_MODE, inputObject.getJSONObject(ProxyConstants.DATA)
						.optString(WidgetConstants.LAST_BURNER_MODE, "-"));

				transformedJson.put(WidgetConstants.LAST_DLE_MAPPING, lastDle);

				transformedJson.put(WidgetConstants.TITLE, WidgetConstants.BURNER_MODE_HOURS);
				transformedJson.put(WidgetConstants.TOTAL_HOURS,
						inputObject.getJSONObject(ProxyConstants.DATA).optDouble(WidgetConstants.RUNNINGHOURS, 0.0));
				transformedJson.put(WidgetConstants.OPERATINGHOURS, arrayofdata);

				transformedJson.put(WidgetConstants.LASTDLEALERT,
						inputObject.getJSONObject(ProxyConstants.DATA).optString(WidgetConstants.LAST_ALERT, "-"));

				long utcDateFormate = inputObject.getJSONObject(ProxyConstants.DATA)
						.optLong(WidgetConstants.LAST_TIME_STAMP);
				String lastTimestamp = utcDateFormate != 0 ? convertToUtc(utcDateFormate) : "-";
				transformedJson.put(WidgetConstants.ALERT_DATE, lastTimestamp);
			} else {
				throw new JSONException(WidgetConstants.RESOURCESAREEMPTY);
			}
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return new JSONObject().put(WidgetConstants.DATA, transformedJson);
	}

	private static String getColorCode(String colorData) {
		switch (colorData) {
		case "ABC":
			return ColorConstants.ABCCOLOR;
		case "AB9C":
			return ColorConstants.AB9CCOLOR;
		case "AB":
			return ColorConstants.ABCOLOR;
		case "B":
			return ColorConstants.BCOLOR;
		case "BC":
			return ColorConstants.BCCOLOR;
		case "BC/2":
			return ColorConstants.BC2COLOR;
		case "OTHERS":
			return ColorConstants.OTHERS;
		default:
			return " ";
		}
	}

	private static String convertToUtc(long lastTimestamp) {
		Instant instant = Instant.ofEpochMilli(lastTimestamp);
		LocalDateTime dataTimeUtc = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return dataTimeUtc.format(formatter);
	}

	private static String convertToUtcDate(long lastDle) {
		Instant instant = Instant.ofEpochMilli(lastDle);
		LocalDateTime dataUtc = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		return dataUtc.format(formatter);
	}
}
