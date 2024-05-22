package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class CeCoOperatingProfileResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public CeCoOperatingProfileResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		List<Map<String, Object>> polyGraphicHeadArray = null;
		List<Map<String, Object>> cecoData;
		List<Map<String, Object>> outputArray = new ArrayList<>();
		Map<String, Object> phasesList;
		Integer phase;
		Map<String, Object> dataMap;
		try {
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			dataMap = (Map<String, Object>) response.getOrDefault(WidgetConstants.DATA, new HashMap<>());
			if (!dataMap.isEmpty()) {
				cecoData = (List<Map<String, Object>>) dataMap.get(WidgetConstants.CECODATA);
				if (!cecoData.isEmpty()) {
					for (Map<String, Object> obj : cecoData) {
						phasesList = new LinkedHashMap<>();
						polyGraphicHeadArray = new ArrayList<>();
						phase = (Integer) obj.getOrDefault(WidgetConstants.PHASE, 0);
						phasesList.put(WidgetConstants.PARAMETERDATA, processCategoryNames(obj, polyGraphicHeadArray));
						phasesList.put(WidgetConstants.PHASE, phase);
						outputArray.add(phasesList);
					}
				} else {
					throw new JSONException(WidgetConstants.RESOURCESAREEMPTY);
				}
			} else {
				throw new JSONException(WidgetConstants.RESOURCESAREEMPTY);
			}
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		return new JSONObject().put(WidgetConstants.DATA, outputArray);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> processCategoryNames(Map<String, Object> politropicHeadGraphData,
			List<Map<String, Object>> polyGraphicHeadArray) {
		Map<String, Object> parameterData;
		Map<String, Object> polyGraphicHead;
		List<String> parametersList = Arrays.asList(WidgetConstants.POLITROPICHEADDATA,
				WidgetConstants.POLITROPICEFFICIENCYDATA, WidgetConstants.PRESSURERATIOGRAPHDATA);
		for (int i = 0; i < parametersList.size(); i++) {
			polyGraphicHead = new LinkedHashMap<>();
			parameterData = (Map<String, Object>) politropicHeadGraphData.get(parametersList.get(i));
			polyGraphicHead.put(WidgetConstants.DISPLAYNAME,
					parameterData.getOrDefault(WidgetConstants.GRAPHDISPLAYNAME, WidgetConstants.EMPTYSTRING));
			polyGraphicHead.put(WidgetConstants.XAXIS,
					parameterData.getOrDefault(WidgetConstants.XAXIS, WidgetConstants.EMPTYSTRING));
			polyGraphicHead.put(WidgetConstants.YAXIS,
					parameterData.getOrDefault(WidgetConstants.YAXIS, WidgetConstants.EMPTYSTRING));
			polyGraphicHead.put(WidgetConstants.HISTOGRAMDATA,
					parameterData.getOrDefault(WidgetConstants.HISTOGRAMDATA, new HashMap<>()));
			polyGraphicHeadArray.add(polyGraphicHead);
		}
		return polyGraphicHeadArray;
	}
}
