package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class TrpResponseHandler<T> extends JsonResponseHandler<T> {

	private static final Logger logger = LoggerFactory.getLogger(TrpResponseHandler.class);

	@SuppressWarnings("unchecked")
	public TrpResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected JSONObject parse(Map<String, Object> request) {
		JSONObject responseObject = new JSONObject();
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject inputObject = new JSONObject(response);
		Map<String, String> openCasesStatusMap = new HashMap<>();
		openCasesStatusMap.put(WidgetConstants.ANALYSE, WidgetConstants.NODATAFOUND);
		openCasesStatusMap.put(WidgetConstants.VALIDATE, WidgetConstants.NODATAFOUND);
		openCasesStatusMap.put(WidgetConstants.APPLY, WidgetConstants.NODATAFOUND);
		Integer analyse;
		Integer apply;
		Integer validate;
		Integer openCases;
		try {
			JSONArray resourcesArray = inputObject.getJSONArray(WidgetConstants.RESOURCES);
			if (resourcesArray.length() != 0) {
				JSONObject filteredObject = new JSONObject();
				List<String> statusList = new ArrayList<>();
				Map<String, Integer> countMap = new HashMap<>();
				for (int i = 0; i < resourcesArray.length(); i++) {
					statusList.add(resourcesArray.getJSONObject(i).optString(WidgetConstants.STATUS,
							WidgetConstants.EMPTYSTRING));
				}
				for (String str : statusList) {
					countMap.put(str, countMap.getOrDefault(str, 0) + 1);
				}
				analyse = countMap.getOrDefault(WidgetConstants.ANALYSER, 0);
				apply = countMap.getOrDefault(WidgetConstants.APPLYR, 0);
				validate = countMap.getOrDefault(WidgetConstants.VALIDATER, 0);
				openCases = countMap.getOrDefault(WidgetConstants.OPENR, 0) + (analyse + apply + validate);
				openCasesStatusMap.put(WidgetConstants.ANALYSE, analyse.toString());
				openCasesStatusMap.put(WidgetConstants.VALIDATE, validate.toString());
				openCasesStatusMap.put(WidgetConstants.APPLY, apply.toString());
				filteredObject.put(WidgetConstants.OPENCASES, openCases.toString());
				filteredObject.put(WidgetConstants.CLOSEDCASES,
						countMap.getOrDefault(WidgetConstants.CLOSEDR, 0).toString());
				filteredObject.put(WidgetConstants.ONHOLDCASES,
						countMap.getOrDefault(WidgetConstants.ONHOLDR, 0).toString());
				filteredObject.put(WidgetConstants.NAME, WidgetConstants.OPENCASESSTATUSR);
				filteredObject.put(WidgetConstants.OPENCASESSTATUS, openCasesStatusMap);
				responseObject.put(WidgetConstants.DATA, filteredObject);
			} else {
				throw new JSONException(WidgetConstants.RESOURCESAREEMPTY);
			}

		} catch (JSONException e) {
			logger.error(e.getMessage());
			responseObject.put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return responseObject;
	}

}