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
public class CaseAttachmentAPIResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public CaseAttachmentAPIResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject outputObject = new JSONObject();
		if (!(response.containsKey("data"))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data", "No data found");
			return nullObject;
		}

		List<HashMap<String, Object>> mapResponse = (List<HashMap<String, Object>>) response.get("data");

		
		if (!mapResponse.isEmpty()) {
			outputObject=responseResult(mapResponse);
		}
		return new JSONObject().put("data", outputObject);
	}

	private JSONObject responseResult(List<HashMap<String, Object>> mapResponse) {
		JSONObject map = null;
		JSONObject outputObject = new JSONObject();
		JSONArray list = new JSONArray();
		int count = 0;
		for (int i = 0; i < mapResponse.size(); i++) {
			map = new JSONObject();
			map.put(ProxyConstants.ISSUE_ID,mapResponse.get(i).getOrDefault(ProxyConstants.ISSUE_ID,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.ATTACH_ID,mapResponse.get(i).getOrDefault(ProxyConstants.ATTACH_ID,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.FILE_NAME,mapResponse.get(i).getOrDefault(ProxyConstants.FILE_NAME,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.FILE_MIME,mapResponse.get(i).getOrDefault(ProxyConstants.FILE_MIME,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.USER,mapResponse.get(i).getOrDefault(ProxyConstants.USER,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.UPLOAD_TIME_STAMP,mapResponse.get(i).getOrDefault(ProxyConstants.UPLOAD_TIME_STAMP,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.ATTACH_TYPE,mapResponse.get(i).getOrDefault(ProxyConstants.ATTACH_TYPE,WidgetConstants.EMPTYSTRING));
			map.put(ProxyConstants.FILE,mapResponse.get(i).getOrDefault(ProxyConstants.FILE,WidgetConstants.EMPTYSTRING));
			list.put(map);
			count++;
		}
		outputObject.put("list", list);
		outputObject.put("attachmentCount", count);
	
		return outputObject;
	}

}
