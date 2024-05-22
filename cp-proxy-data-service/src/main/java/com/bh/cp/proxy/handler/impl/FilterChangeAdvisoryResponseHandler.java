package com.bh.cp.proxy.handler.impl;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class FilterChangeAdvisoryResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public FilterChangeAdvisoryResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		JSONObject noDataFound = new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		try {
			HashMap<String, Object> response = (HashMap<String, Object>) getT();
			Map<String, Object> finalResponse;
			List<Map<String, Object>> finalResponseList = new ArrayList<>();
			Map<String, Object> data = (Map<String, Object>) response.get(WidgetConstants.DATA);
			List<Map<String, Object>> data1 = data.containsKey(WidgetConstants.DATA)
					? (List<Map<String, Object>>) data.get(WidgetConstants.DATA)
					: new ArrayList<>();
			if (!data1.isEmpty()) {
				List<Map<String, Object>> filterData;
				for (Map<String, Object> obj : data1) {
					filterData = (List<Map<String, Object>>) obj.get(WidgetConstants.FILTERDATA);
					if (!filterData.isEmpty()) {
						finalResponse = getFinalObject();
						finalResponse.put(WidgetConstants.ASSETID, obj.get(WidgetConstants.ASSETID));
						finalResponse.put(WidgetConstants.FILTERDATA, prepareFilterData(filterData));
						finalResponse.put(WidgetConstants.NEXTFILTERCHANGE,
								getTimeStamp(filterData, WidgetConstants.NEXTFILTERCHANGE));
						finalResponse.put(WidgetConstants.LASTFILTERCHANGE,
								getTimeStamp(filterData, WidgetConstants.LASTFILTERCHANGE));
						finalResponseList.add(finalResponse);
					}
				}
				return !finalResponseList.isEmpty() ? finalResponseList : noDataFound;
			} else
				throw new JSONException(WidgetConstants.NODATAFOUND);
		} catch (Exception e) {
			return noDataFound;
		}
	}

	private List<Map<String, Object>> prepareFilterData(List<Map<String, Object>> filterData) {
		List<Map<String, Object>> filterDataList = new ArrayList<>();
		Map<String, Object> filterDataNew;
		Object thresholdMin;
		Object thresholdMax;
		Object dpValue;
		if (!filterData.isEmpty()) {
			for (Map<String, Object> obj : filterData) {
				filterDataNew = getDefaultFilterDataObject();
				thresholdMin = obj.get(WidgetConstants.THRESHOLDMINVALUE) != null
						? obj.get(WidgetConstants.THRESHOLDMINVALUE)
						: JSONObject.NULL;
				thresholdMax = obj.get(WidgetConstants.THRESHOLDMAXVALUE) != null
						? obj.get(WidgetConstants.THRESHOLDMAXVALUE)
						: JSONObject.NULL;
				dpValue = obj.get(WidgetConstants.DIFFERENTIALPRESSUREVALUE) != null
						? obj.get(WidgetConstants.DIFFERENTIALPRESSUREVALUE)
						: JSONObject.NULL;
				filterDataNew.put(WidgetConstants.FILTERNAME,
						getFilterName((String) obj.get(WidgetConstants.FILTERNAME)));
				filterDataNew.put(WidgetConstants.DIFFERENTIALPRESSUREVALUE, formateToDecimalPlace((Double) dpValue));
				filterDataNew.put(WidgetConstants.THRESHOLDMINVALUE, thresholdMin);
				filterDataNew.put(WidgetConstants.THRESHOLDMAXVALUE, thresholdMax);
				filterDataNew.put(WidgetConstants.THRESHOLDMAXNAME, obj.get(WidgetConstants.THRESHOLDMAXNAME));
				filterDataNew.put(WidgetConstants.THRESHOLDMINNAME, obj.get(WidgetConstants.THRESHOLDMINNAME));
				filterDataNew.put(WidgetConstants.COLORCODE, getColorCode(thresholdMin, thresholdMax, dpValue));
				filterDataNew.put(WidgetConstants.UOM, obj.get(WidgetConstants.UOM));
				filterDataList.add(filterDataNew);
			}
		}
		return filterDataList;
	}

	private double formateToDecimalPlace(Double value) {
		DecimalFormat decimalFormat = new DecimalFormat("#.0");
		return Double.parseDouble(decimalFormat.format(value));
	}

	private String getFilterName(String filterName) {
		if (filterName.equals("pre-filter"))
			return "Pre-Filter";
		else if (filterName.equals("high-efficiency-filter"))
			return "High Efficiency";
		else if (filterName.equals("hepa-filter"))
			return "HEPA";
		else
			return WidgetConstants.NOTAPPLICABLE;
	}

	private String getTimeStamp(List<Map<String, Object>> filterData, String filterChange) {
		Long timeStamp;
		TreeSet<String> timeStampSet = new TreeSet<>();
		for (Map<String, Object> obj : filterData) {
			timeStamp = obj.get(filterChange) != null ? (Long) obj.get(filterChange) : 0;
			if (timeStamp != 0)
				timeStampSet.add(convertToUtc(timeStamp));
		}
		// Define a date format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(WidgetConstants.FILTERTIMESTAMPPATTERN);

		// Convert strings to LocalDate objects
		Set<LocalDate> dateSet = new TreeSet<>();
		timeStampSet.forEach(dateString -> dateSet.add(LocalDate.parse(dateString, formatter)));

		// Retrieve and print the element with the least date
		LocalDate leastDate = dateSet.isEmpty() ? null : ((TreeSet<LocalDate>) dateSet).first();
		if (leastDate != null)
			return formatter.format(leastDate);
		else
			return WidgetConstants.NOTAPPLICABLE;
	}

	private static String convertToUtc(long powerTimestamp) {
		Instant instant = Instant.ofEpochMilli(powerTimestamp);
		LocalDateTime dataTimeUtc = LocalDateTime.ofInstant(instant, ZoneId.of(WidgetConstants.UTC));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(WidgetConstants.FILTERTIMESTAMPPATTERN);
		return dataTimeUtc.format(formatter);
	}

	private String getColorCode(Object thresholdMin, Object thresholdMax, Object dpValue) {
		if (thresholdMin != JSONObject.NULL && thresholdMax == JSONObject.NULL && dpValue != JSONObject.NULL)
			return colorCode(thresholdMin, dpValue);
		else if (thresholdMin == JSONObject.NULL && thresholdMax != JSONObject.NULL && dpValue != JSONObject.NULL)
			return colorCode(thresholdMax, dpValue);
		else
			return ColorConstants.FCAGREEN;
	}

	private String colorCode(Object threshold, Object dpValue) {
		Double y = (Double) threshold;
		Double x = (Double) dpValue;
		Double eightyPercentOfY = y * 0.8;
		if (x > y)
			return ColorConstants.FCARED;
		else if (x < eightyPercentOfY)
			return ColorConstants.FCAGREEN;
		else
			return ColorConstants.FCAORANGE;
	}

	private Map<String, Object> getDefaultFilterDataObject() {
		Map<String, Object> filterObject = new HashMap<>();
		filterObject.put(WidgetConstants.FILTERNAME, JSONObject.NULL);
		filterObject.put(WidgetConstants.COLORCODE, JSONObject.NULL);
		filterObject.put(WidgetConstants.DIFFERENTIALPRESSUREVALUE, JSONObject.NULL);
		filterObject.put(WidgetConstants.THRESHOLDMINVALUE, JSONObject.NULL);
		filterObject.put(WidgetConstants.THRESHOLDMAXVALUE, JSONObject.NULL);
		filterObject.put(WidgetConstants.THRESHOLDMAXNAME, JSONObject.NULL);
		filterObject.put(WidgetConstants.THRESHOLDMINNAME, JSONObject.NULL);
		filterObject.put(WidgetConstants.UOM, JSONObject.NULL);
		return filterObject;
	}

	private Map<String, Object> getFinalObject() {
		Map<String, Object> outputJsonObj = new HashMap<>();
		outputJsonObj.put(WidgetConstants.FILTERDATA, new ArrayList<>());
		outputJsonObj.put(WidgetConstants.FILTERCHANGELABEL, "Next inlet filter change suggested on : ");
		outputJsonObj.put(WidgetConstants.NEXTFILTERCHANGE, JSONObject.NULL);
		outputJsonObj.put(WidgetConstants.ASSETID, JSONObject.NULL);
		outputJsonObj.put(WidgetConstants.TITLE, "Filter differential pressure[mmH2O]");
		return outputJsonObj;
	}
}
