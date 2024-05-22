package com.bh.cp.proxy.handler.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;



@Component
public class DeviceDetailsResponseHandler<T> extends JsonResponseHandler<T> {

	@Autowired
	@SuppressWarnings("unchecked")
	protected DeviceDetailsResponseHandler(HttpServletRequest httpServletRequest,ProxyService proxyService) {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {

		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		
		if (!(response.containsKey(WidgetConstants.DATA))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(WidgetConstants.DATA, JSONObject.NULL);
			return nullObject;
		}
		List<Map<String,Object>> list;
		JSONArray finalJsonArray = new JSONArray();
		JSONObject finalJsonObject = new JSONObject();
		list = (List<Map<String, Object>>) response.get(ProxyConstants.DATA);
		JSONObject resultObject = null;
		List<Long> dates = new ArrayList<>();
		for (int j = 0; j < list.size(); j++) {
			Map<String, Object> resultResponse = list.get(j);
			resultObject = new JSONObject();
			resultObject.put(ProxyConstants.LINEUPDESCRIPTION,resultResponse.getOrDefault(ProxyConstants.LINEUPDESCRIPTION, ""));
			resultObject.put(ProxyConstants.LINEUP_NAME,resultResponse.getOrDefault(ProxyConstants.LINEUP_NAME, ""));
			resultObject.put(ProxyConstants.DEVICENAME,resultResponse.getOrDefault(ProxyConstants.DEVICENAME, ""));
			resultObject.put(ProxyConstants.HARDWARE_DEVICE_TYPE,resultResponse.getOrDefault(ProxyConstants.HARDWARE_DEVICE_VALUE, ""));
			resultObject.put(ProxyConstants.HARDWARE_DEVICETYPE_DESCRIPTION1,resultResponse.getOrDefault(ProxyConstants.HARDWARE_DEVICETYPE_DESCRIPTION, ""));
			resultObject.put(ProxyConstants.DEVICEID,resultResponse.getOrDefault(ProxyConstants.DEVICEID, ""));
			dates.add((Long) resultResponse.get(ProxyConstants.LAST_UPDATED));
			resultObject.put(ProxyConstants.LAST_UPDATED,resultResponse.getOrDefault(ProxyConstants.LAST_UPDATED, ""));
			String status = resultResponse.get(ProxyConstants.DEVICE_STATUS)!=null ? resultResponse.get(ProxyConstants.DEVICE_STATUS).toString():"";
			if(ProxyConstants.OK.equalsIgnoreCase(status))
			{
				resultObject.put(ProxyConstants.CASELISTSTATUS,ProxyConstants.UPDATED);
			}
			else
			{
				resultObject.put(ProxyConstants.CASELISTSTATUS,ProxyConstants.NOT_UPDATED);
			}
			finalJsonArray.put(resultObject);
		}
		dates.removeIf(Objects::isNull);
		Collections.sort(dates, Collections.reverseOrder());
		if(!dates.isEmpty())
		{
			convertLondDateToDateFormat(finalJsonObject,list);
		}
		finalJsonObject.put(WidgetConstants.DEVICE_LIST_DETAILS,finalJsonArray);
		return new JSONObject().put(WidgetConstants.DATA, finalJsonObject); 
	}

	private void convertLondDateToDateFormat(JSONObject finalJsonObject, List<Map<String, Object>> list) {
		Map<String,Object> updatedDate;
		updatedDate = list.get(0);
		Long lastUpdatedDate = (Long) updatedDate.get(ProxyConstants.LAST_UPDATED);
		String dateUpdate = new SimpleDateFormat(ProxyConstants.DATE_FORMAT_TO_UTC_DATE)
				.format(new Date(lastUpdatedDate));
		finalJsonObject.put(ProxyConstants.LAST_UPDATED_DATE,dateUpdate);
	}
}
