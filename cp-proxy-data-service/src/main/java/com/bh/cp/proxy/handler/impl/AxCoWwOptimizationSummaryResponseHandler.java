package com.bh.cp.proxy.handler.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class AxCoWwOptimizationSummaryResponseHandler<T> extends JsonResponseHandler<T> {

	private AssetHierarchyFilterService assetHierarchyFilterService;

	@SuppressWarnings("unchecked")
	public AxCoWwOptimizationSummaryResponseHandler(
			@Autowired AssetHierarchyFilterService assetHierarchyFilterService) {
		super((T) new HashMap<String, Object>());
		this.assetHierarchyFilterService = assetHierarchyFilterService;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		try {
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			JSONObject dataObject = new JSONObject(response);
			JSONArray jsonArray = dataObject.getJSONArray(WidgetConstants.DATA);
			List<Map<String, Object>> filteredAssetHierarchy = (List<Map<String, Object>>) request
					.get(ProxyConstants.FILTEREDASSETHIERARCHY);
			Map<String, Object> assetsMap = (Map<String, Object>) request.get(ProxyConstants.ASSETSIDMAP);
			List<String> lineupIds = (List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_LINEUPS,
					new ArrayList<>());
			List<JSONObject> outputDataList = prepareJsonObject(jsonArray, lineupIds, filteredAssetHierarchy);
			Collections.sort(outputDataList,
					Comparator.comparing(chartData -> chartData.getString(WidgetConstants.LINEUPID)));
			return createResponseJsonObject(outputDataList);
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
	}

	private List<JSONObject> prepareJsonObject(JSONArray inputArray, List<String> lineupIds,
			List<Map<String, Object>> filteredAssetHierarchy) {

		String lineupId = "";

		JSONObject response;
		JSONObject inputObject;
		List<JSONObject> outputDataList = new ArrayList<>();
		for (int i = 0; i < inputArray.length(); i++) {
			inputObject = inputArray.getJSONObject(i);
			String axcoEfficiency = inputObject.optString(WidgetConstants.AXCO_EFFICIENCY,null);
			if (axcoEfficiency == null) {
				continue;
			}
			String lastAxcoEfficiency = inputObject.optString(WidgetConstants.LAST_AXCO_EFFICIENCY,null);
			double axcoEfficiencyPercantage = (axcoEfficiency!=null)?convertToPercantage(Double.valueOf(axcoEfficiency)):0.0;
			double lastAxcoEfficiencyPercantage = (lastAxcoEfficiency!=null)?convertToPercantage(Double.valueOf(lastAxcoEfficiency)):0.0;

			double decreaseInLastAxcoEfficiency = 0;
			String finalDecrease = "";
			if (axcoEfficiencyPercantage > 0.0 && lastAxcoEfficiencyPercantage >0.0) {
				decreaseInLastAxcoEfficiency = Double.valueOf(axcoEfficiencyPercantage)
						- Double.valueOf(lastAxcoEfficiencyPercantage);
				DecimalFormat df = new DecimalFormat("0.0");
				finalDecrease = String.valueOf(df.format(decreaseInLastAxcoEfficiency) + "%");
			}
			String assetId = inputObject.getString(WidgetConstants.ASSETID);
			lineupId = assetHierarchyFilterService.getImmediateParentField(filteredAssetHierarchy,
					ProxyConstants.PREFIX_MC.concat(assetId), WidgetConstants.ID);
			if (lineupIds.contains(lineupId)) {
				Map<String, String> colorInfo = determineColor(decreaseInLastAxcoEfficiency);
				response = new JSONObject();
				response.put(WidgetConstants.DECREMENT_SINCE_OFFLINE_WW,
						finalDecrease.equals("-0.0%") ? "0.0%" : finalDecrease);
				response.put(WidgetConstants.VALUE, String.format("%.1f", axcoEfficiencyPercantage));
				response.put(WidgetConstants.COLORCODE, colorInfo.get(WidgetConstants.COLORCODE));
				response.put(WidgetConstants.BACKGROUND, colorInfo.get(WidgetConstants.BACKGROUND));
				response.put(WidgetConstants.COLORLABLE, colorInfo.get(WidgetConstants.COLORLABLE));
				response.put(WidgetConstants.STATUS, colorInfo.get(WidgetConstants.LABEL));
				response.put(WidgetConstants.UNIT, "%");
				response.put(WidgetConstants.COLOR, ColorConstants.AXCO_WW_COLOR);
				response.put(WidgetConstants.LINEUPID, lineupId);
				outputDataList.add(response);
			}
		}
		return outputDataList;
	}

	private static Double convertToPercantage(Double value) {
		return value * 100;

	}

	private Map<String, String> determineColor(double decrementValue) {
		Map<String, String> colorInfo = new HashMap<>();
		if (decrementValue < -0.5) {
			colorInfo.put(WidgetConstants.COLORCODE, "#e87516");
			colorInfo.put(WidgetConstants.BACKGROUND, "#fefbe6");
			colorInfo.put(WidgetConstants.COLORLABLE, "red");
			colorInfo.put(WidgetConstants.LABEL, "Decrement since last offline WW");
		} else if (decrementValue > 0.5) {
			colorInfo.put(WidgetConstants.COLORCODE, "#02a783");
			colorInfo.put(WidgetConstants.BACKGROUND, "#effcf6");
			colorInfo.put(WidgetConstants.COLORLABLE, "green");
			colorInfo.put(WidgetConstants.LABEL, "Increment since last offline WW");
		} else if (decrementValue >= -0.5 && decrementValue <= 0.5) {
			colorInfo.put(WidgetConstants.COLORCODE, "#595959");
			colorInfo.put(WidgetConstants.BACKGROUND, "#ebefee");
			colorInfo.put(WidgetConstants.COLORLABLE, "grey");
			colorInfo.put(WidgetConstants.LABEL, "Equal since last offline WW");
		}
		return colorInfo;

	}

	private JSONObject createResponseJsonObject(List<JSONObject> outputDataList) {
		if(outputDataList.isEmpty()) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
		JSONObject outputJsonObj = new JSONObject();
		outputJsonObj.put(WidgetConstants.CHARTDATA, outputDataList);
		outputJsonObj.put(WidgetConstants.VALUEPERBLOCK, 10);
		outputJsonObj.put(WidgetConstants.XSCALE, List.of(0, 10, 50, 100));
		return new JSONObject().put(WidgetConstants.DATA, outputJsonObj);
	}
}
