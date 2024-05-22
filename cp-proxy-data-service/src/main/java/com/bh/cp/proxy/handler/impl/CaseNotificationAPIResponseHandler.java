package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class CaseNotificationAPIResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public CaseNotificationAPIResponseHandler() {
		super((T) new HashMap<String, Object>());

	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		if (!(response.containsKey(ProxyConstants.DATA))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(ProxyConstants.DATA, "No data found");
			return nullObject;
		}
		List<HashMap<String, Object>> mapResponse = (List<HashMap<String, Object>>) response.get(ProxyConstants.DATA);

		JSONArray list = new JSONArray();
		if (!mapResponse.isEmpty()) {
			list = filterResponse(mapResponse);
		}
		return new JSONObject().put("data", list);
	}

	private JSONArray filterResponse(List<HashMap<String, Object>> mapResponse) {
		JSONArray list = new JSONArray();
		JSONObject map;
		for (int i = 0; i < mapResponse.size(); i++) {
			String userEmail=mapResponse.get(i).get(ProxyConstants.USER_EMAIL)!=null ? mapResponse.get(i).get(ProxyConstants.USER_EMAIL).toString():"";
			if(!userEmail.equalsIgnoreCase(ProxyConstants.AUTOMATIC_EMAIL)) {
			map = new JSONObject();
			map.put(ProxyConstants.ISSUE_ID,mapResponse.get(i).getOrDefault(ProxyConstants.ISSUE_ID,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.NOTIFICATION_ID,mapResponse.get(i).getOrDefault(ProxyConstants.NOTIFICATION_ID,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.USER_EMAIL,userEmail);
			map.put(ProxyConstants.USER_ID,mapResponse.get(i).getOrDefault(ProxyConstants.USER_ID,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.DATE_SENT,mapResponse.get(i).getOrDefault(ProxyConstants.DATE_SENT,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.MAIL_TYPE,mapResponse.get(i).getOrDefault(ProxyConstants.MAIL_TYPE,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.COMMENT_ID,mapResponse.get(i).getOrDefault(ProxyConstants.COMMENT_ID,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.TREND_NAME,mapResponse.get(i).getOrDefault(ProxyConstants.TREND_NAME,WidgetConstants.EMPTYSTRING));
			list.put(map);
			}
		}
		return list;
	}

}
