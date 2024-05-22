package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;

@Component
public class ThrustBearingLoadSummaryResponseHandler {

	private AssetHierarchyFilterService assetHierarchyFilterService;

	public ThrustBearingLoadSummaryResponseHandler(@Autowired AssetHierarchyFilterService assetHierarchyFilterService) {
		super();
		this.assetHierarchyFilterService = assetHierarchyFilterService;
	}

	@SuppressWarnings("unchecked")
	protected Object parse(HashMap<String, Object> response, Map<String, Object> request, String thrustType) {
		try {
			ArrayList<Map<String, Object>> jsonArray = (ArrayList<Map<String, Object>>) response
					.get(WidgetConstants.DATA);

			List<Map<String, Object>> filteredAssetHierarchy = (List<Map<String, Object>>) request
					.get(ProxyConstants.FILTEREDASSETHIERARCHY);
			Map<String, Object> assetsMap = (Map<String, Object>) request.get(ProxyConstants.ASSETSIDMAP);
			List<String> lineupIds = (List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_LINEUPS,
					new ArrayList<>());
			Map<String, Double> lineupRecoupMap = new HashMap<>();
			Map<String, String> lineupOrificeMap = new HashMap<>();
			Map<String, Map<String, Double>> lineupScaleMap = new HashMap<>();
			for (Map<String, Object> obj : jsonArray) {
				String assetId = (String) obj.get(WidgetConstants.ASSETID);
				String lineupId = assetHierarchyFilterService.getImmediateParentField(filteredAssetHierarchy,
						ProxyConstants.PREFIX_MC.concat(assetId), WidgetConstants.ID);
				if (lineupIds.contains(lineupId) && obj.get(WidgetConstants.RECOUP) != null) {
					lineupRecoupMap.put(lineupId,
							lineupRecoupMap.getOrDefault(lineupId, 0.0) + (Double) obj.get(WidgetConstants.RECOUP));
					String orificeInstalled = (String) obj.get(WidgetConstants.ORIFICEINSTALLED);
					lineupScaleMap.put(lineupId, (Map<String, Double>) obj.get(WidgetConstants.SCALE));
					if (orificeInstalled != null) {
						lineupOrificeMap.put(lineupId, orificeInstalled);
					}
				}
			}

			if (lineupRecoupMap.isEmpty()) {
				return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
			}

			List<JSONObject> chartDataList = new ArrayList<>();
			lineupRecoupMap.forEach((lineupId, recoupValue) -> {
				long recoupRoundedValue = Math.round(recoupValue);
				JSONObject chartData = new JSONObject();
				chartData.put(WidgetConstants.LINEUPID, lineupId);
				chartData.put(WidgetConstants.VALUE, recoupRoundedValue);
				chartData.put(WidgetConstants.UNIT, WidgetConstants.LBF);

				Map<String, Double> scaleMap = lineupScaleMap.get(lineupId);
				chartData.put(WidgetConstants.LOADMIN, scaleMap.get(WidgetConstants.LOADMIN));
				chartData.put(WidgetConstants.LOADMAX, scaleMap.get(WidgetConstants.LOADMAX));

				if (scaleMap.get(WidgetConstants.LOADMIN) > recoupRoundedValue) {
					chartData.put(WidgetConstants.COLOR, ColorConstants.THRUSTUNDER);
					chartData.put(WidgetConstants.STATUS, WidgetConstants.UNDERLOAD);
				} else if (scaleMap.get(WidgetConstants.LOADMAX) < recoupRoundedValue) {
					chartData.put(WidgetConstants.COLOR, ColorConstants.THRUSTOVER);
					chartData.put(WidgetConstants.STATUS, WidgetConstants.OVERLOAD);
				} else {
					chartData.put(WidgetConstants.COLOR, ColorConstants.THRUSTNORMAL);
					chartData.put(WidgetConstants.STATUS, WidgetConstants.NORMAL);
				}

				chartData.put(WidgetConstants.ALERT, false);
				chartDataList.add(chartData);

			});

			Collections.sort(chartDataList,
					Comparator.comparing(chartData -> chartData.getString(WidgetConstants.LINEUPID)));
			return createResponseJsonObject(chartDataList, thrustType);
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
	}

	private JSONObject createResponseJsonObject(List<JSONObject> chartDataList, String thrustType) {
		List<JSONObject> legendsList = new ArrayList<>();
		legendsList.add(new JSONObject().put(WidgetConstants.NAME, WidgetConstants.NORMAL).put(WidgetConstants.COLOR,
				ColorConstants.THRUSTNORMAL));
		legendsList.add(new JSONObject().put(WidgetConstants.NAME, WidgetConstants.OVERUNDER).put(WidgetConstants.COLOR,
				ColorConstants.THRUSTUNDER));
		JSONObject outputJsonObj = new JSONObject();
		if (chartDataList != null) {
			outputJsonObj.put(WidgetConstants.CHARTDATA, chartDataList);
		}
		outputJsonObj.put(WidgetConstants.LEGENDS, legendsList);

		switch (thrustType) {
		case ProxyConstants.THRUST_1B:
			outputJsonObj.put(WidgetConstants.VALUEPERBLOCK, 1800);
			outputJsonObj.put(WidgetConstants.XSCALE, List.of(0, -3600, -7200, -10800, -14400, -18000));
			break;
		case ProxyConstants.THRUST_4B:
			outputJsonObj.put(WidgetConstants.VALUEPERBLOCK, 800);
			outputJsonObj.put(WidgetConstants.XSCALE, List.of(0, 1600, 3200, 4800, 6400, 8000));
			break;
		case ProxyConstants.THRUST_7B:
			outputJsonObj.put(WidgetConstants.VALUEPERBLOCK, 1300);
			outputJsonObj.put(WidgetConstants.XSCALE, List.of(0, -2600, -5200, -7800, -10400, -13000));
			break;
		default:
			break;
		}

		return new JSONObject().put(WidgetConstants.DATA, outputJsonObj);
	}
}
