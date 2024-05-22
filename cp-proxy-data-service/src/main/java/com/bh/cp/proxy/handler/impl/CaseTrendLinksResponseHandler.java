package com.bh.cp.proxy.handler.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.util.DateUtility;
import com.bh.cp.proxy.util.StringUtil;


@Component
public class CaseTrendLinksResponseHandler<T> extends JsonResponseHandler<T> {

	
	@SuppressWarnings("unchecked")
	protected CaseTrendLinksResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ProxyConstants.DATE_FORMAT_TO_UTC);
	JSONObject map;
	int expInDays;
	JSONArray list;
	DateTimeFormatter dtfInput=DateTimeFormatter.ofPattern(ProxyConstants.CASE_TREND_DATE_FORMAT, Locale.ENGLISH);
	DateTimeFormatter dtfOutputEng=DateTimeFormatter.ofPattern(ProxyConstants.DATE_FORMAT_TO_UTC, Locale.ENGLISH);
	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {

		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		JSONObject jsonObject = new JSONObject(response);
		JSONObject outputObject = new JSONObject();

		if (!(response.containsKey("data"))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data", JSONObject.NULL);
			return nullObject;
		}
		JSONArray array = jsonObject.getJSONArray("data");

		expInDays = 0;

		if (array.length() != 0) {

			list = new JSONArray();
			list = storedData(array);

		} else {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data", JSONObject.NULL);
			return nullObject;
		}
		outputObject.put("list", list);

		return new JSONObject().put("data", outputObject);
	}

	private JSONArray storedData(JSONArray array) {
		list = new JSONArray();
		for (int i = 0; i < array.length(); i++) {

			map = new JSONObject();
			String endDate = array.getJSONObject(i).has("d_ins") ? array.getJSONObject(i).get("d_ins").toString() : "";
			endDate=formatDate(endDate);
			if (!StringUtil.isEmptyString(endDate)) {
				LocalDateTime suggestedDate = LocalDateTime.parse(endDate, formatter);

				LocalDateTime currentDateTime = LocalDateTime.now();
				
				DateUtility utility=new DateUtility();
				Map<String, Integer> dateDiff;
				dateDiff=utility.customDateDifference(suggestedDate.toString(), currentDateTime.toString());
				
				String expiration = array.getJSONObject(i).has("expiration")
						? array.getJSONObject(i).get("expiration").toString()
						: "";

				expInDays = Integer.parseInt(expiration) - dateDiff.get(ProxyConstants.DAYS);

			}
			map.put("expirationInDayd", expInDays);

			map.put(ProxyConstants.NAME_LINK,
					array.getJSONObject(i).has(ProxyConstants.NAME_LINK)
							? array.getJSONObject(i).get(ProxyConstants.NAME_LINK).toString()
							: "");

			map.put("Link",
					array.getJSONObject(i).has("trendLink") ? array.getJSONObject(i).get("trendLink").toString() : "");

			map.put("InsertDate&Time", endDate);

			map.put("User", array.getJSONObject(i).has("user") ? array.getJSONObject(i).get("user").toString() : "");

			list.put(map);

		}

		return list;
	}

	private String formatDate(String endDate) {
		LocalDateTime date1 = null;
		String formatedEndDate=null;
		if (!(endDate == null || endDate.equals(""))) {
			date1 = LocalDateTime.parse(endDate, dtfInput);
			formatedEndDate = dtfOutputEng.format(date1);
		}	
		return formatedEndDate;
	}

}
