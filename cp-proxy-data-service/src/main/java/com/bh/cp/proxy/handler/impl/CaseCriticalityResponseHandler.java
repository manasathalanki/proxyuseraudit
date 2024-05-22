package com.bh.cp.proxy.handler.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.util.DateUtility;

@Component
public class CaseCriticalityResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public CaseCriticalityResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		List<Map<String, Object>> resourcesList = (List<Map<String, Object>>) response.get(WidgetConstants.RESOURCES);
		String dateRange = (String) request.get(WidgetConstants.DATERANGE);
		if ((dateRange == null) && (request.get(WidgetConstants.STARTDATE)!=null && request.get(WidgetConstants.ENDDATE)!=null)) {
			dateRange=WidgetConstants.DATECUSTOM;
		}
		Map<String, Object> startEndDateMap = null;
		Date startDate;
		Date endDate;
		Integer days;
		try {
			if (!resourcesList.isEmpty()) {
				startEndDateMap = retriveStartEndDate(dateRange, request);
				startDate = (Date) startEndDateMap.get(WidgetConstants.STARTDATE);
				endDate = (Date) startEndDateMap.get(WidgetConstants.ENDDATE);
				days = (Integer) startEndDateMap.get(WidgetConstants.NOOFDAYS);
				Map<String, Object> monthlyDataSortBy = processResources(resourcesList, startDate, endDate, dateRange,
						days);
				Map<String, Map<String, Integer>> monthlyData = (Map<String, Map<String, Integer>>) monthlyDataSortBy
						.get(WidgetConstants.MONTHLYDATA);
				String sortBy = (String) monthlyDataSortBy.get(WidgetConstants.SORTBY);
				List<Map<String, Object>> createMap = createMap(monthlyData);
				return sortBy.equals(WidgetConstants.WEEKSORT) || sortBy.equals(WidgetConstants.QUARTERSORT)
						? weekAppend(sortMap(createMap, sortBy), sortBy)
						: sortMap(createMap, sortBy);
			} else
				throw new JSONException(WidgetConstants.RESOURCESAREEMPTY);
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
	}

	private static Map<String, Object> retriveStartEndDate(String dateRange, Map<String, Object> request)
			throws ParseException {
		Map<String, Object> startEndDateMap = new HashMap<>();
		SimpleDateFormat customDateFormat = new SimpleDateFormat(ProxyConstants.SIMPLE_DATE_FORMAT);
		Map<String, Integer> customDateMap = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Date startDate = null;
		Date endDate = calendar.getTime(); // Current date as end date
		if (dateRange.equals(WidgetConstants.DATERANGE3)) {
			calendar.add(Calendar.MONTH, -3);
			startDate = calendar.getTime();
		} else if (dateRange.equals(WidgetConstants.DATERANGE6)) {
			calendar.add(Calendar.MONTH, -6);
			startDate = calendar.getTime();
		} else if (dateRange.equals(WidgetConstants.DATERANGE1Y)) {
			calendar.add(Calendar.MONTH, -12);
			startDate = calendar.getTime();
		} else if (dateRange.equals(WidgetConstants.DATERANGEYTD)) {
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			startDate = calendar.getTime();
		} else {
			// Handle custom date range if needed
			String stDate = (String) request.get(WidgetConstants.STARTDATE);
			String edDate = (String) request.get(WidgetConstants.ENDDATE);
			startDate = customDateFormat.parse(stDate);
			endDate = customDateFormat.parse(edDate);
			DateUtility dateUtility = new DateUtility();
			customDateMap = dateUtility.customDateDifference(stDate, edDate);
		}
		startEndDateMap.put(WidgetConstants.STARTDATE, startDate);
		startEndDateMap.put(WidgetConstants.ENDDATE, endDate);
		startEndDateMap.put(WidgetConstants.NOOFDAYS, customDateMap.get(WidgetConstants.NOOFDAYS));
		return startEndDateMap;
	}

	private static Map<String, Object> processResources(List<Map<String, Object>> resourcesList, Date startDate,
			Date endDate, String dateRange, Integer days) throws ParseException {
		Map<String, Map<String, Integer>> monthlyData = new HashMap<>();
		Map<String, Object> monthlyDataSortBy = new HashMap<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
		SimpleDateFormat monthFormat = new SimpleDateFormat(WidgetConstants.MONTHSORTPATTERN, Locale.ENGLISH);
		String sortBy = "";
		Calendar calendar = Calendar.getInstance();
		String criticality;
		String eventDate;
		Date date;
		Map<String, Object> resource;
		for (int i = 0; i < resourcesList.size(); i++) {
			resource = resourcesList.get(i);
			eventDate = (String) resource.get(WidgetConstants.EVENT_DATE);
			criticality = (String) resource.get(WidgetConstants.CRITICALITY);
			date = dateFormat.parse(eventDate);
			if (dateInRange(date, startDate, endDate)) {
				sortBy = calculateSortBy(dateRange, days, calendar);
				switch (dateRange) {
				case WidgetConstants.DATERANGE3:
					weekWise(monthlyData, criticality, date, calendar);
					break;
				case WidgetConstants.DATERANGE6, WidgetConstants.DATERANGE1Y:
					monthWise(monthlyData, criticality, monthFormat.format(date));
					break;
				case WidgetConstants.DATERANGEYTD:
					calculateYTDData(monthlyData, criticality, date, calendar);
					break;
				default:
					calculateCustomDateData(monthlyData, criticality, date, days, calendar);
					break;
				}
			}
		}

		monthlyDataSortBy.put(WidgetConstants.MONTHLYDATA, monthlyData);
		monthlyDataSortBy.put(WidgetConstants.SORTBY, sortBy);
		return monthlyDataSortBy;
	}

	private static boolean dateInRange(Date date, Date startDate, Date endDate) {
		return date.after(startDate) && date.before(endDate);
	}

	private static String calculateSortBy(String dateRange, Integer days, Calendar calendar) {
		String sortBy = "";
		if (dateRange.equals(WidgetConstants.DATERANGE3)) {
			sortBy = WidgetConstants.WEEKSORT;
		} else if (dateRange.equals(WidgetConstants.DATERANGE6) || dateRange.equals(WidgetConstants.DATERANGE1Y)) {
			sortBy = WidgetConstants.MONTHSORT;
		} else if (dateRange.equals(WidgetConstants.DATERANGEYTD)) {
			sortBy = calculateYTDSortBy(calendar);
		} else {
			sortBy = calculateCustomDateSortBy(days);
		}

		return sortBy;
	}

	private static String calculateYTDSortBy(Calendar calendar) {
		int daysYTD = calendar.get(Calendar.DAY_OF_YEAR);
		if (daysYTD <= 14) {
			return WidgetConstants.DAYSORT;
		} else if (daysYTD <= 3 * 30) {
			return WidgetConstants.WEEKSORT;
		} else {
			return WidgetConstants.MONTHSORT;
		}
	}

	private static String calculateCustomDateSortBy(Integer days) {
		if (days <= 14) {
			return WidgetConstants.DAYSORT;
		} else if (days <= 3 * 30) {
			return WidgetConstants.WEEKSORT;
		} else if (days > 3 * 30 && days < 365) {
			return WidgetConstants.MONTHSORT;
		} else if (days >= 365 && days < 3 * 365) {
			return WidgetConstants.QUARTERSORT;
		} else {
			return WidgetConstants.YEARSORT;
		}
	}

	private static Map<String, Map<String, Integer>> calculateYTDData(Map<String, Map<String, Integer>> monthlyData,
			String criticality, Date date, Calendar calendar) {
		int daysYTD = calendar.get(Calendar.DAY_OF_YEAR);
		if (daysYTD <= 14) {
			dayWise(monthlyData, criticality, calendar, date);
		} else if (daysYTD <= 3 * 30) {
			weekWise(monthlyData, criticality, date, calendar);
		} else {
			monthWise(monthlyData, criticality,
					new SimpleDateFormat(WidgetConstants.MONTHSORTPATTERN, Locale.ENGLISH).format(date));
		}
		return monthlyData;
	}

	private static Map<String, Map<String, Integer>> calculateCustomDateData(
			Map<String, Map<String, Integer>> monthlyData, String criticality, Date date, Integer days,
			Calendar calendar) {
		if (days <= 14) {
			dayWise(monthlyData, criticality, calendar, date);
		} else if (days <= 3 * 30) {
			weekWise(monthlyData, criticality, date, calendar);
		} else if (days > 3 * 30 && days < 365) {
			monthWise(monthlyData, criticality,
					new SimpleDateFormat(WidgetConstants.MONTHSORTPATTERN, Locale.ENGLISH).format(date));
		} else if (days >= 365 && days < 3 * 365) {
			quarterWise(monthlyData, criticality, calendar, date);
		} else {
			yearWise(monthlyData, criticality, date);
		}

		return monthlyData;
	}

	private static Map<String, Map<String, Integer>> dayWise(Map<String, Map<String, Integer>> monthlyData,
			String criticality, Calendar calender, Date date) {
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		calender.setTime(date);
		String monthYear = dayFormat.format(date);
		monthlyData.putIfAbsent(monthYear, new HashMap<>());
		Map<String, Integer> criticalityMap = monthlyData.get(monthYear);
		criticalityMap.put(criticality, criticalityMap.getOrDefault(criticality, 0) + 1);
		return monthlyData;
	}

	private static Map<String, Map<String, Integer>> weekWise(Map<String, Map<String, Integer>> monthlyData,
			String criticality, Date date, Calendar calender) {
		SimpleDateFormat yearFormat = new SimpleDateFormat("yy", Locale.ENGLISH);
		String year = yearFormat.format(date);
		calender.setTime(date);
		Integer week = calender.get(Calendar.WEEK_OF_YEAR);
		String weekYear = (week.toString() + "- " + year);
		monthlyData.putIfAbsent(weekYear, new HashMap<>());
		Map<String, Integer> criticalityMap = monthlyData.get(weekYear);
		criticalityMap.put(criticality, criticalityMap.getOrDefault(criticality, 0) + 1);
		return monthlyData;
	}

	private static Map<String, Map<String, Integer>> monthWise(Map<String, Map<String, Integer>> monthlyData,
			String criticality, String monthYear) {
		monthlyData.putIfAbsent(monthYear, new HashMap<>());
		Map<String, Integer> criticalityMap = monthlyData.get(monthYear);
		criticalityMap.put(criticality, criticalityMap.getOrDefault(criticality, 0) + 1);
		return monthlyData;
	}

	private static Map<String, Map<String, Integer>> quarterWise(Map<String, Map<String, Integer>> monthlyData,
			String criticality, Calendar calender, Date date) {
		SimpleDateFormat yearFormat = new SimpleDateFormat("yy", Locale.ENGLISH);
		calender.setTime(date);
		String monthYear = yearFormat.format(date);
		Integer month = calender.get(Calendar.MONTH);
		Integer quarter = (month / 3) + 1;
		String quarterYear = (quarter.toString() + "- " + monthYear);
		monthlyData.putIfAbsent(quarterYear, new HashMap<>());
		Map<String, Integer> criticalityMap = monthlyData.get(quarterYear);
		criticalityMap.put(criticality, criticalityMap.getOrDefault(criticality, 0) + 1);
		return monthlyData;
	}

	private static Map<String, Map<String, Integer>> yearWise(Map<String, Map<String, Integer>> monthlyData,
			String criticality, Date date) {
		SimpleDateFormat yearFormatFull = new SimpleDateFormat("yyyy", Locale.ENGLISH);
		String monthYear = yearFormatFull.format(date);
		monthlyData.putIfAbsent(monthYear, new HashMap<>());
		Map<String, Integer> criticalityMap = monthlyData.get(monthYear);
		criticalityMap.put(criticality, criticalityMap.getOrDefault(criticality, 0) + 1);
		return monthlyData;
	}

	private static Map<String, Object> appendWToMonth(Map<String, Object> map, String appender) {
		String currentMonth = (String) map.get(WidgetConstants.MONTHSORT);
		String[] parts = currentMonth.split("-");
		if (parts.length == 2) {
			String newMonth = appender + parts[0] + " " + parts[1].replace(" ", "'");
			map.put(WidgetConstants.MONTHSORT, newMonth);
		}
		return map;
	}

	private List<Map<String, Object>> weekAppend(List<Map<String, Object>> dataList, String sortBy) {
		String[] appender = { "FW", "Q" };
		if (sortBy.equals(WidgetConstants.WEEKSORT)) {
			dataList = dataList.stream()
					.map(map -> map.containsKey(WidgetConstants.MONTHSORT) ? appendWToMonth(map, appender[0]) : map)
					.toList();
		} else {
			dataList = dataList.stream()
					.map(map -> map.containsKey(WidgetConstants.MONTHSORT) ? appendWToMonth(map, appender[1]) : map)
					.toList();
		}
		return dataList;
	}

	private List<Map<String, Object>> sortMap(List<Map<String, Object>> dataList, String sortBy) {
		dataList.sort((o1, o2) -> {
			String month1 = (String) o1.get(WidgetConstants.MONTHSORT);
			String month2 = (String) o2.get(WidgetConstants.MONTHSORT);
			try {
				Date date1 = parseMonthString(month1, sortBy);
				Date date2 = parseMonthString(month2, sortBy);
				return date1.compareTo(date2);
			} catch (Exception e) {
				e.getMessage();
				return 0;
			}
		});
		return dataList;
	}

	private static Date parseMonthString(String month, String sortBy) throws ParseException {
		SimpleDateFormat date;
		if (sortBy.equals(WidgetConstants.WEEKSORT) || sortBy.equals(WidgetConstants.QUARTERSORT)) {
			date = new SimpleDateFormat(WidgetConstants.WEEKSORTPATTERN);
		} else if (sortBy.equals(WidgetConstants.MONTHSORT)) {
			date = new SimpleDateFormat(WidgetConstants.MONTHSORTPATTERN);
		} else if (sortBy.equals(WidgetConstants.DAYSORT)) {
			date = new SimpleDateFormat(WidgetConstants.DAYSORTPATTERN);
		} else {
			date = new SimpleDateFormat(WidgetConstants.YEARSORTPATTERN);
		}
		return date.parse(month);
	}

	private List<Map<String, Object>> createMap(Map<String, Map<String, Integer>> monthlyData) {
		List<String> sortedMonths = new ArrayList<>(monthlyData.keySet());
		List<Map<String, Object>> monthlyObjectList = new ArrayList<>();
		for (String month : sortedMonths) {
			Map<String, Integer> dateForMonth = monthlyData.get(month);
			String formattedMonth = month;
			Map<String, Object> monthObject = new HashMap<>();
			monthObject.put(WidgetConstants.MONTHSORT, formattedMonth);
			monthObject.put(WidgetConstants.HIGHR, dateForMonth.getOrDefault(WidgetConstants.HIGH, 0));
			monthObject.put(WidgetConstants.MEDIUMR, dateForMonth.getOrDefault(WidgetConstants.MEDIUM, 0));
			monthObject.put(WidgetConstants.LOWR, dateForMonth.getOrDefault(WidgetConstants.LOW, 0));
			monthObject.put(WidgetConstants.BARSR, 1);
			monthlyObjectList.add(monthObject);
		}
		return monthlyObjectList;
	}
}