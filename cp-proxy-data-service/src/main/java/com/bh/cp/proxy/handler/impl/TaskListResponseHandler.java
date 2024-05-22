package com.bh.cp.proxy.handler.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.util.DateUtility;
import com.bh.cp.proxy.util.StringUtil;

@Component
public class TaskListResponseHandler<T> extends JsonResponseHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(TaskListResponseHandler.class);
	
	@SuppressWarnings("unchecked")
	public TaskListResponseHandler() {
		super((T) new HashMap<String, Object>());

	}

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	int openCasecount;
	int twoWeekDueTaskCount;

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {

		openCasecount = 0;
		twoWeekDueTaskCount = 0;

		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject jsonObject = new JSONObject(response);
		JSONObject outputObject = new JSONObject();

		JSONArray array = jsonObject.getJSONArray("tasks");

		JSONArray listgroup = new JSONArray();

		JSONObject map = null;

		if (array.length() != 0) {
			for (int i = 0; i < array.length(); i++) {

				map = new JSONObject();

				map.put(ProxyConstants.ISURGENT,
						array.getJSONObject(i).has(ProxyConstants.ISURGENT)
								? array.getJSONObject(i).get(ProxyConstants.ISURGENT).toString()
								: "");

				map.put("Status",
						array.getJSONObject(i).has("status") ? array.getJSONObject(i).get("status").toString() : "");

				map.put(ProxyConstants.SUGGESTED_DATE,
						array.getJSONObject(i).has(ProxyConstants.SUGGESTED_DATE)
								? array.getJSONObject(i).get(ProxyConstants.SUGGESTED_DATE).toString()
								: "");

				openCasecount = openCaseCountValue(map);
				twoWeekDueTaskCount = twoWeekDueTaskCountValue(map);

				listgroup.put(map);
			}
			outputObject.put("OpenTaskCount", openCasecount);
			outputObject.put("TwoWeeksDueTaskCount", twoWeekDueTaskCount);
		} else {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data", JSONObject.NULL);
			return nullObject;
		}

		return new JSONObject().put("data", outputObject);
	}

	private int twoWeekDueTaskCountValue(JSONObject map) {

		if (map.get(ProxyConstants.STATUS).equals(ProxyConstants.CASE_STATUS_OPEN)) {

			try {

				String suggestedDateStr = map.getString("suggestedDate");
				ZonedDateTime convertedDate = null;

				if (!StringUtil.isEmptyString(suggestedDateStr)) {
					LocalDateTime localDateTime = LocalDateTime.parse(suggestedDateStr, formatter);

					ZoneId zoneId = ZoneId.systemDefault();
					ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
					convertedDate = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
					ZonedDateTime currentDateTime = ZonedDateTime.now();

					DateUtility dateUtility = new DateUtility();
					Map<String, Integer> dateDiffMap;
					dateDiffMap = dateUtility.customDateDifference(convertedDate.toString(),
							currentDateTime.toString());

					dateDiffMap.put(ProxyConstants.WEEKS, Math.abs(dateDiffMap.get(ProxyConstants.WEEKS)));

					if (dateDiffMap.get(ProxyConstants.WEEKS) >= 2) {
						twoWeekDueTaskCount++;
					}
				}
			} catch (JSONException e) {
				logger.info(e.getMessage(), e);
			}
		}

		return twoWeekDueTaskCount;
	}

	private int openCaseCountValue(JSONObject map) {

		if (map.get(ProxyConstants.STATUS).equals(ProxyConstants.CASE_STATUS_OPEN)
				&& (map.get(ProxyConstants.ISURGENT).equals(ProxyConstants.BOLEAN_VALUE_TRUE))) {
			openCasecount++;

		}

		return openCasecount;
	}

}
