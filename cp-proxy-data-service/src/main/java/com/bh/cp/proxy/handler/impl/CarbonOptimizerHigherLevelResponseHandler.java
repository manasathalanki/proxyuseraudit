package com.bh.cp.proxy.handler.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CarbonOptimizerHigherLevelResponseHandler<T> extends JsonResponseHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(CarbonOptimizerHigherLevelResponseHandler.class);

	private Integer carbonOptimizerWidgetId;

	private Integer runningImageId;

	private Integer stoppedImageId;

	private Integer incrementImageId;

	private Integer decrementImageId;

	private HttpServletRequest httpServletRequest;

	private ProxyService proxyService;

	@Autowired
	@SuppressWarnings("unchecked")
	public CarbonOptimizerHigherLevelResponseHandler(HttpServletRequest httpServletRequest, ProxyService proxyService,
			@Value("${carbon.optimizer.recursive.widget-id}") Integer carbonOptimizerWidgetId,
			@Value("${carbon.optimizer.stopped.image-id}") Integer stoppedImageId,
			@Value("${carbon.optimizer.decre.image-id}") Integer decrementImageId,
			@Value("${carbon.optimizer.running.image-id}") Integer runningImageId,
			@Value("${carbon.optimizer.incre.image-id}") Integer incrementImageId) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.proxyService = proxyService;
		this.carbonOptimizerWidgetId = carbonOptimizerWidgetId;
		this.stoppedImageId = stoppedImageId;
		this.decrementImageId = decrementImageId;
		this.runningImageId = runningImageId;
		this.incrementImageId = incrementImageId;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		JSONObject expectedFilteredResponse = new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		try {
			HashMap<String, Object> openResponse = (HashMap<String, Object>) getT();
			Integer widgetId = (Integer) request.get(WidgetConstants.WIDGETID);
			Map<String, String> replaceValuesMap = (Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES);
			String machineId = replaceValuesMap.get(ProxyConstants.KEY_APPLICABLE_MACHINE_IDS_CSV);
			if (Objects.equals(widgetId, carbonOptimizerWidgetId)) {
				return new JSONObject().put(WidgetConstants.DATA, openResponse);
			}
			JSONObject jsonObject = new JSONObject(openResponse);
			JSONArray jsonArray = jsonObject.getJSONArray(WidgetConstants.DATA);
			List<List<String>> machinesList = new ArrayList<>();
			List<Map<String, Object>> filteredGroupMachinesList = new ArrayList<>();
			JSONObject object;
			JSONArray tabsArray;
			JSONObject object1;
			int k;
			if (jsonArray.length() != 0) {
				for (Object obj : jsonArray) {
					object = (JSONObject) obj;
					tabsArray = object.getJSONArray(WidgetConstants.TABS);
					k = 0;
					for (Object obj1 : tabsArray) {
						object1 = (JSONObject) obj1;
						machinesList.add(object1.getJSONArray(WidgetConstants.MACHINES).toList().stream()
								.map(Object::toString).toList());
						filteredGroupMachinesList
								.add(createMap(object1.get(WidgetConstants.TABNAME).toString(), machinesList.get(k++)));
					}
				}
				expectedFilteredResponse = new JSONObject().put(WidgetConstants.DATA,
						filterExpectedResponse(filteredGroupMachinesList, fetchMachinesInfo(request, machineId)));
			}
			return expectedFilteredResponse;
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> fetchMachinesInfo(Map<String, Object> request, String machinesPlaceholder) {
		Map<String, String> input = (Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES);
		input.put(WidgetConstants.MACHINESPLACEHOLDER, machinesPlaceholder);
		Map<String, Object> valuesApiResponse = null;
		JSONObject proxyJsonObject = null;
		try {
			request.put(WidgetConstants.WIDGETID, carbonOptimizerWidgetId);
			proxyJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);
			valuesApiResponse = (Map<String, Object>) proxyJsonObject.toMap().get(WidgetConstants.DATA);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return valuesApiResponse;
	}

	@SuppressWarnings("unchecked")
	public JSONArray filterExpectedResponse(List<Map<String, Object>> filteredGroupMachinesList,
			Map<String, Object> valuesApiResponse) {

		List<Map<String, Object>> filteredMachinesInfoList = null;
		JSONArray filteredGroupMachines = new JSONArray();
		List<String> machinesList;
		Map<String, Object> groupMachinesMap;

		for (Map<String, Object> obj : filteredGroupMachinesList) {
			groupMachinesMap = obj;
			machinesList = (List<String>) groupMachinesMap.get(WidgetConstants.MACHINES);
			filteredMachinesInfoList = new ArrayList<>();
			for (String obj1 : machinesList) {
				if (filterExpectedResponse(obj1, filterValuesResponse(valuesApiResponse)).size() > 0) {
					filteredMachinesInfoList.add(filterExpectedResponse(obj1, filterValuesResponse(valuesApiResponse)));
				}
			}
			if (!filteredMachinesInfoList.isEmpty()) {
				filteredGroupMachines.put(
						new JSONObject().put(WidgetConstants.GROUPNAME, groupMachinesMap.get(WidgetConstants.TABNAME))
								.put(WidgetConstants.MACHINES, filteredMachinesInfoList));
			}
		}
		return filteredGroupMachines;

	}

	public Map<String, Object> filterExpectedResponse(String machineId,
			List<Map<String, Object>> filteredValuesResponse) {

		Map<String, Object> valuesMap = null;
		Map<String, Object> unitsMap = new HashMap<>();
		for (Map<String, Object> obj : filteredValuesResponse) {
			valuesMap = obj;
			for (Map.Entry<String, Object> entry : valuesMap.entrySet()) {
				if (entry.getValue().equals(machineId)) {
					unitsMap = valuesMap;
					break;
				}
			}
		}
		return unitsMap;
	}

	public List<Map<String, Object>> filterValuesResponse(Map<String, Object> valuesApiResponse) {
		String actualValue;
		String suggestedValue;
		Boolean runningValue;
		String unitMeasure;
		String label = null;
		String assetId;
		String expectedValue;
		List<Map<String, Object>> list1 = new ArrayList<>();
		Map<String, Object> map;
		JSONObject obj1;
		JSONObject valuesResponse = new JSONObject(valuesApiResponse);
		JSONArray valuesArray = valuesResponse.getJSONArray(WidgetConstants.DATA);
		if (valuesArray.length() != 0) {
			for (Object obj : valuesArray) {
				obj1 = (JSONObject) obj;
				actualValue = obj1.optString(WidgetConstants.ACTUALVALUE, WidgetConstants.EMPTYSTRING);
				suggestedValue = obj1.optString(WidgetConstants.SUGGESTEDVALUE, WidgetConstants.EMPTYSTRING);
				runningValue = obj1.optBoolean(WidgetConstants.RUNNING, false);
				unitMeasure = obj1.optString(WidgetConstants.UNITMEASURE, WidgetConstants.EMPTYSTRING);
				assetId = obj1.optString(WidgetConstants.ASSETID, WidgetConstants.EMPTYSTRING);
				if (obj1.optJSONObject(WidgetConstants.MESSAGE).length() > 0) {
					label = obj1.getJSONObject(WidgetConstants.MESSAGE).optString(WidgetConstants.LABEL,
							WidgetConstants.EMPTYSTRING);
				}
				if (suggestedValue.length() == 0 && actualValue != null && actualValue.length() != 0) {
					suggestedValue = WidgetConstants.NOTAPPLICABLE;
					expectedValue = WidgetConstants.EMPTYSTRING;
				} else if (suggestedValue != null && suggestedValue.length() != 0 && actualValue != null
						&& actualValue.length() == 0) {
					actualValue = WidgetConstants.NOTAPPLICABLE;
					expectedValue = WidgetConstants.EMPTYSTRING;
				} else if (actualValue != null && actualValue.length() == 0 && suggestedValue != null
						&& suggestedValue.length() == 0) {
					suggestedValue = WidgetConstants.NOTAPPLICABLE;
					actualValue = WidgetConstants.NOTAPPLICABLE;
					expectedValue = WidgetConstants.EMPTYSTRING;
				} else {
					expectedValue = (new BigDecimal(suggestedValue)).subtract(new BigDecimal(actualValue)).toString();
				}
				map = createExpectedFormat(actualValue, suggestedValue, runningValue, unitMeasure, label, assetId,
						expectedValue);
				list1.add(map);
			}
		}
		return list1;

	}

	public Map<String, Object> createExpectedFormat(Object actualValue, Object suggestedValue, Object runningValue,
			String unitMeasure, String label, String assetId, Object incdecValue) {

		Map<String, Object> map = new HashMap<>();
		map.put(WidgetConstants.ACTUALVALUE, actualValue);
		map.put(WidgetConstants.SUGGESTEDVALUE, suggestedValue);
		map.put(WidgetConstants.RUNNING, runningValue);
		map.put(WidgetConstants.UNITMEASURE, unitMeasure);
		map.put(WidgetConstants.LABEL, label);
		map.put(WidgetConstants.UNIT, assetId);
		map.put(WidgetConstants.LABELCOLOR, ColorConstants.CARBONLABELCOLOR);
		map.put(WidgetConstants.CARBONSTATUS, WidgetConstants.EMPTYSTRING);
		if (runningValue.equals(true))
			map.put(WidgetConstants.RUNNINGIMAGEID, runningImageId);
		else
			map.put(WidgetConstants.RUNNINGIMAGEID, stoppedImageId);
		if ((!(suggestedValue.toString().equals(WidgetConstants.NOTAPPLICABLE)
				|| actualValue.toString().equals(WidgetConstants.NOTAPPLICABLE)))) {
			map.put(WidgetConstants.ACTUALVALUE, roundingDecimal(actualValue,unitMeasure) + unitMeasure);
			map.put(WidgetConstants.SUGGESTEDVALUE, roundingDecimal(suggestedValue,unitMeasure) + unitMeasure);
			map.put(WidgetConstants.CHANGEREQUIRED, roundingDecimal(incdecValue,unitMeasure));
			if ((new BigDecimal(suggestedValue.toString())).compareTo(new BigDecimal(actualValue.toString())) > 0) {
				map.put(WidgetConstants.IMAGEID, incrementImageId);
				map.put(WidgetConstants.COLORCODE, ColorConstants.INCREEMENTBG);
				map.put(WidgetConstants.FONTCOLOR, ColorConstants.INCREEMENTFONT);
				map.put(WidgetConstants.CARBONSTATUS, WidgetConstants.INCREEMENTREQUIRED);
			} else if ((new BigDecimal(suggestedValue.toString()))
					.compareTo(new BigDecimal(actualValue.toString())) < 0) {
				map.put(WidgetConstants.IMAGEID, decrementImageId);
				map.put(WidgetConstants.COLORCODE, ColorConstants.DECCREEMENTBG);
				map.put(WidgetConstants.FONTCOLOR, ColorConstants.DECCREEMENTFONT);
				map.put(WidgetConstants.CARBONSTATUS, WidgetConstants.DECREEMENTREQUIRED);
			} else {
				map.put(WidgetConstants.IMAGEID, WidgetConstants.EMPTYSTRING);
				map.put(WidgetConstants.COLORCODE, WidgetConstants.EMPTYSTRING);
				map.put(WidgetConstants.FONTCOLOR, WidgetConstants.EMPTYSTRING);
			}
		} else if (suggestedValue.toString().equals(WidgetConstants.NOTAPPLICABLE)
				&& actualValue.toString().equals(WidgetConstants.NOTAPPLICABLE)) {
			map.put(WidgetConstants.SUGGESTEDVALUE, suggestedValue);
			map.put(WidgetConstants.ACTUALVALUE, actualValue);
			map.put(WidgetConstants.CHANGEREQUIRED, incdecValue);
			map.put(WidgetConstants.IMAGEID, WidgetConstants.EMPTYSTRING);
			map.put(WidgetConstants.COLORCODE, WidgetConstants.EMPTYSTRING);
			map.put(WidgetConstants.FONTCOLOR, WidgetConstants.EMPTYSTRING);
		} else if (suggestedValue.toString().equals(WidgetConstants.NOTAPPLICABLE)) {
			map.put(WidgetConstants.SUGGESTEDVALUE, suggestedValue);
			map.put(WidgetConstants.ACTUALVALUE, roundingDecimal(actualValue,unitMeasure) + unitMeasure);
			map.put(WidgetConstants.CHANGEREQUIRED, incdecValue);
			map.put(WidgetConstants.IMAGEID, WidgetConstants.EMPTYSTRING);
			map.put(WidgetConstants.COLORCODE, WidgetConstants.EMPTYSTRING);
			map.put(WidgetConstants.FONTCOLOR, WidgetConstants.EMPTYSTRING);
		} else {
			map.put(WidgetConstants.ACTUALVALUE, actualValue);
			map.put(WidgetConstants.SUGGESTEDVALUE, roundingDecimal(suggestedValue,unitMeasure) + unitMeasure);
			map.put(WidgetConstants.CHANGEREQUIRED, incdecValue);
			map.put(WidgetConstants.IMAGEID, WidgetConstants.EMPTYSTRING);
			map.put(WidgetConstants.COLORCODE, WidgetConstants.EMPTYSTRING);
			map.put(WidgetConstants.FONTCOLOR, WidgetConstants.EMPTYSTRING);
		}
		return map;
	}

	private BigDecimal roundingDecimal(Object value, String unitMeasure) {
		if (unitMeasure.equals(WidgetConstants.UNITSRPM))
			return new BigDecimal(value.toString()).setScale(0, RoundingMode.HALF_UP);
		else
			return new BigDecimal(value.toString()).setScale(1, RoundingMode.HALF_UP);
	}

	public Map<String, Object> createMap(String title, List<String> value) {
		Map<String, Object> map = new HashMap<>();
		map.put(WidgetConstants.TABNAME, title);
		map.put(WidgetConstants.MACHINES, value);
		return map;
	}

}
