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
public class SpinningReserveSummaryResponseHandler<T> extends JsonResponseHandler<T> {

	private AssetHierarchyFilterService assetHierarchyFilterService;

	@SuppressWarnings("unchecked")
	public SpinningReserveSummaryResponseHandler(@Autowired AssetHierarchyFilterService assetHierarchyFilterService) {
		super((T) new HashMap<String, Object>());
		this.assetHierarchyFilterService = assetHierarchyFilterService;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		try {
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			JSONObject dataObject = new JSONObject(response);
			JSONArray jsonArray = dataObject.getJSONObject(WidgetConstants.DATA).getJSONArray(WidgetConstants.DATA);
			List<Map<String, Object>> filteredAssetHierarchy = (List<Map<String, Object>>) request
					.get(ProxyConstants.FILTEREDASSETHIERARCHY);
			Map<String, Object> assetsMap = (Map<String, Object>) request.get(ProxyConstants.ASSETSIDMAP);
			List<String> lineupIds = (List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_LINEUPS,
					new ArrayList<>());

			JSONObject object;
			String assetId;
			Double powerPercentValue;
			String colorCode;
			String lineupId;
			JSONArray tableDataArray;
			List<JSONObject> chartDataList = new ArrayList<>();
			for (Object obj : jsonArray) {
				object = (JSONObject) obj;
				assetId = (String) object.get(WidgetConstants.ASSETID);
				powerPercentValue = object.optDouble(WidgetConstants.POWERPERCENTVALUE, 0.0);
				if (powerPercentValue > 0)
					powerPercentValue = formateToDecimalPlace(powerPercentValue);
				colorCode = getColorCode(powerPercentValue);
				lineupId = assetHierarchyFilterService.getImmediateParentField(filteredAssetHierarchy,
						ProxyConstants.PREFIX_MC.concat(assetId), WidgetConstants.ID);
				if (lineupIds.contains(lineupId)) {
					tableDataArray = object.getJSONArray(WidgetConstants.TABLEDATA);
					chartDataList = prepareJSONObject(tableDataArray, colorCode, powerPercentValue, lineupId,
							chartDataList);
				}
			}
			Collections.sort(chartDataList,
					Comparator.comparing(chartData -> chartData.getString(WidgetConstants.LINEUPID)));
			return createResponseJsonObject(chartDataList);
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
	}

	private List<JSONObject> prepareJSONObject(JSONArray tableDataArray, String colorCode, Double powerPercentValue,
			String lineupId, List<JSONObject> chartDataList) {
		JSONObject object1;
		String parameter;
		Double actualPowerValue;
		String unit;
		if (tableDataArray.length() != 0) {
			for (Object obj1 : tableDataArray) {
				object1 = (JSONObject) obj1;
				parameter = object1.optString(WidgetConstants.PARAMETER, "");
				if (parameter.equals(WidgetConstants.ACTUALPOWERR)) {
					actualPowerValue = object1.optDouble(WidgetConstants.VALUE, 0.0);
					unit = object1.optString(WidgetConstants.UNIT, "");
					if (WidgetConstants.KW.equals(unit)) {
						actualPowerValue = actualPowerValue / 1000.0;
						unit = WidgetConstants.MW;
					}
					actualPowerValue = formateToDecimalPlace(actualPowerValue);
					chartDataList.add(createResponse(lineupId, actualPowerValue + unit, colorCode, powerPercentValue));
					break;
				}
			}
		}
		return chartDataList;
	}

	private String getColorCode(double powerPercentValue) {
		return (powerPercentValue >= 100.00) ? ColorConstants.SPINNINGRESERVEORANGE
				: ColorConstants.SPINNINGRESERVEGREEN;
	}

	private double formateToDecimalPlace(Double value) {
		DecimalFormat decimalFormat = new DecimalFormat("#.0");
		return Double.parseDouble(decimalFormat.format(value));
	}

	private JSONObject createResponse(String lineupId, String actualPowerValue, String colorCode,
			Double powerPercentValue) {
		JSONObject object = new JSONObject();
		object.put(WidgetConstants.LINEUPID, lineupId);
		object.put(WidgetConstants.VALUE, powerPercentValue);
		object.put(WidgetConstants.COLOR, colorCode);
		object.put(WidgetConstants.ACTUALPOWER, actualPowerValue);
		return object;
	}

	private JSONObject createResponseJsonObject(List<JSONObject> chartDataList) {
		JSONObject outputJsonObj = new JSONObject();
		outputJsonObj.put(WidgetConstants.CHARTDATA, chartDataList);
		outputJsonObj.put(WidgetConstants.VALUEPERBLOCK, 10);
		outputJsonObj.put(WidgetConstants.XSCALE, List.of(0, 50, 100));
		return new JSONObject().put(WidgetConstants.DATA, outputJsonObj);
	}

}
