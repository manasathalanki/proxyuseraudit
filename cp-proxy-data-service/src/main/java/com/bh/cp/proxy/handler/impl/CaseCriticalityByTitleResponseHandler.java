package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class CaseCriticalityByTitleResponseHandler<T> extends JsonResponseHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(CaseCriticalityByTitleResponseHandler.class);

	@SuppressWarnings("unchecked")
	public CaseCriticalityByTitleResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {

		logger.info("CM Case Title Response Handler----->");
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		if (!(response.containsKey("resources"))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data", JSONObject.NULL);
			return nullObject;
		}
		JSONObject jsonObject = new JSONObject(response);

		JSONArray resourceList = jsonObject.getJSONArray("resources");
		List<JSONObject> dataList = new ArrayList<>();
		JSONObject responseObject = new JSONObject();
		for (int i = 0; i < resourceList.length(); i++) {
			if (resourceList.getJSONObject(i).has("type")) {
				JSONObject object = resourceList.getJSONObject(i);
				dataList.add(object);
			}
		}
		List<JSONObject> ewsList = dataList.stream().filter(x -> x.get("type").equals(ProxyConstants.TYPE_EWS))
				.toList();
		List<JSONObject> etsList = dataList.stream().filter(x -> x.get("type").equals(ProxyConstants.TYPE_ETS))
				.toList();
		logger.info("CM Case ewsList Response Handler--ewsList---> {}", ewsList.size());
		logger.info("CM Case etsList Response Handler--etsList---> {}", etsList.size());

		Integer emsCount = ewsList.size();
		Integer etsCount = etsList.size();

		if ((emsCount != 0 || etsCount != 0)) {
			Map<String, Integer> typeCount = new HashMap<>();
			typeCount.put(WidgetConstants.TYPE_EARLYWARNING, emsCount);
			typeCount.put(WidgetConstants.TYPE_EVENTTROUBLESHOOTING, etsCount);
			typeCount.put("bars", 1);
			typeCount.put("xaxis", 1);
			responseObject.put(WidgetConstants.DATA, typeCount);
		} else {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}

		return responseObject;
	}

}
