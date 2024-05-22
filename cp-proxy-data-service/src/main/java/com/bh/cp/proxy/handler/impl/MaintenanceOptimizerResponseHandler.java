package com.bh.cp.proxy.handler.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import com.bh.cp.proxy.pojo.MaintenanceOptimizerResponse;
import com.bh.cp.proxy.service.ProxyService;
import com.bh.cp.proxy.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class MaintenanceOptimizerResponseHandler<T> extends JsonResponseHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(MaintenanceOptimizerResponseHandler.class);

	private Integer maintenanceOptimizerWidgetId;

	private Integer maintenanceOptimizerEventWidgetId;

	private HttpServletRequest httpServletRequest;

	private ProxyService proxyService;

	@Autowired
	@SuppressWarnings("unchecked")
	public MaintenanceOptimizerResponseHandler(HttpServletRequest httpServletRequest, ProxyService proxyService,
			@Value("${maintenance.optimizer.recursive.widget-id}") Integer maintenanceOptimizerWidgetId,
			@Value("${maintenance.optimizer.event.recursive.widget-id}") Integer maintenanceOptimizerEventWidgetId) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.proxyService = proxyService;
		this.maintenanceOptimizerWidgetId = maintenanceOptimizerWidgetId;
		this.maintenanceOptimizerEventWidgetId = maintenanceOptimizerEventWidgetId;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		List<Object> finalResponse = new LinkedList<>();
		try {
			HashMap<String, Object> eventsResponse = (HashMap<String, Object>) getT();

			Integer widgetId = (Integer) request.get(WidgetConstants.WIDGETID);
			if (Objects.equals(widgetId, maintenanceOptimizerWidgetId)
					|| Objects.equals(widgetId, maintenanceOptimizerEventWidgetId)) {
				return new JSONObject().put(WidgetConstants.DATA, eventsResponse);
			}
			// 1.Get the cases data on machine serial nos
			JSONObject casesObject = new JSONObject(eventsResponse);
			JSONArray cases = casesObject.getJSONArray(WidgetConstants.RESOURCES);
			List<MaintenanceOptimizerResponse> moResult = new ArrayList<>();
			moResult = settingCaseIdAndLineupId(cases, moResult);
			// 2.Get the tasks data on parent case id which are getting on cases API
			moResult = getTasksDataOnParentCaseIds(moResult, request, cases);
			// 3. Get the event maintenace data on machine gib serial nos
			getMaintainanceDataOnGibSerialNo(request, moResult, finalResponse);
			if (!finalResponse.isEmpty())
				return new JSONObject().put(WidgetConstants.DATA, new JSONArray(finalResponse));
			else
				return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}

	}

	private List<String> retriveParentCaseIds(JSONArray cases) {
		List<String> parentCaseIds = new ArrayList<>();
		JSONObject caseObject;
		for (int i = 0; i < cases.length(); i++) {
			caseObject = cases.getJSONObject(i);
			parentCaseIds.add(caseObject.getNumber(WidgetConstants.CASEID).toString());
		}
		return parentCaseIds;
	}

	private List<MaintenanceOptimizerResponse> settingCaseIdAndLineupId(JSONArray cases,
			List<MaintenanceOptimizerResponse> moResult) {
		MaintenanceOptimizerResponse moObject = null;
		JSONObject caseObject;
		for (int i = 0; i < cases.length(); i++) {
			caseObject = cases.getJSONObject(i);
			moObject = new MaintenanceOptimizerResponse();
			moObject.setCaseId(caseObject.getInt(WidgetConstants.CASEID));
			moObject.setLineupId(caseObject.getString(WidgetConstants.LINEUPID));
			moResult.add(moObject);
		}
		return moResult;
	}

	// 2.Get the tasks data on parent case id which are getting on cases API
	private List<MaintenanceOptimizerResponse> getTasksDataOnParentCaseIds(List<MaintenanceOptimizerResponse> moResult,
			Map<String, Object> request, JSONArray cases) {
		JSONObject taskObject;
		MaintenanceOptimizerResponse getMoObject = null;
		List<MaintenanceOptimizerResponse> moResults = new ArrayList<>();
		JSONObject fetchTasksInfo = fetchTasksInfo(request, StringUtil.toCSV(retriveParentCaseIds(cases), ",", "\""));
		if (fetchTasksInfo != null) {
			JSONArray tasks = fetchTasksInfo.getJSONArray(WidgetConstants.TASKS);
			// Accomplish task info on the cases list
			for (int i = 0; i < tasks.length(); i++) {
				taskObject = tasks.getJSONObject(i);
				Integer parentTaskId = taskObject.getInt("parentCaseId");
				Integer maintEventId = taskObject.has("maintEventId") ? taskObject.getInt("maintEventId") : null;
				getMoObject = moResult.stream().filter(mo -> parentTaskId.equals(mo.getCaseId())).findAny()
						.orElse(null);
				if (null != getMoObject) {
					MaintenanceOptimizerResponse moObject = new MaintenanceOptimizerResponse();
					moObject.setCaseId(getMoObject.getCaseId());
					moObject.setLineupId(getMoObject.getLineupId());
					moObject.setTaskId(taskObject.getInt("taskId"));
					moObject.setStatus(taskObject.getString("status"));
					moObject.setMaintEventId(maintEventId);
					moObject.setTaskdate(LocalDateTime.parse(taskObject.getString("suggestedDate"),
							DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
					moObject.setIsUrgent(taskObject.getBoolean("isUrgent"));
					moObject.setEventTypeDesc("Unassigned tasks");
					moResults.add(moObject);
				}
			}
		}
		return moResults;
	}

	// 3. Get the event maintenace data on machine gib serial nos
	@SuppressWarnings("unchecked")
	private List<Object> getMaintainanceDataOnGibSerialNo(Map<String, Object> request,
			List<MaintenanceOptimizerResponse> moResult, List<Object> finalResponse) {

		if (request.get(ProxyConstants.VID).toString().startsWith("MC_")) {
			Map<String, Object> replaceValues = (Map<String, Object>) request.get(ProxyConstants.REPLACE_VALUES);
			String lineupVid = "LN_".concat((String) replaceValues.get(ProxyConstants.KEY_LINEUP_IDS_CSV));
			request.put(ProxyConstants.VID, lineupVid);
			request.put(ProxyConstants.LEVEL, ProxyConstants.LEVEL_LINEUPS);
		}

		JSONObject fetchEventMaintenanceInfo = fetchEventMaintenaceInfo(request);

		if (fetchEventMaintenanceInfo != null) {
			JSONArray events = fetchEventMaintenanceInfo.getJSONObject(WidgetConstants.DATA)
					.getJSONArray(WidgetConstants.DATA);
			JSONObject eventObject;

			// Accomplish event maintenance info on the cases list
			List<Integer> eventIdsList = new ArrayList<>();
			for (int i = 0; i < events.length(); i++) {
				eventObject = events.getJSONObject(i);
				Integer eventId = eventObject.getInt(WidgetConstants.FIELD_RMD_EVENT);
				String eventTypeDesc = eventObject.getString(WidgetConstants.FIELD_EVENT_TYPE_DESC);
				LocalDateTime eventDate = LocalDateTime.ofInstant(
						Instant.ofEpochMilli(eventObject.getLong(WidgetConstants.EVENT_DATE)), ZoneId.systemDefault());
				moResult.stream().filter(mo -> eventId.equals(mo.getMaintEventId())).forEach(mo -> {
					mo.setEventTypeDesc(eventTypeDesc);
					mo.setTaskdate(eventDate);
				});
				eventIdsList.add(eventId);
			}

			moResult.removeIf(mo -> mo.getMaintEventId() != null && !eventIdsList.contains(mo.getMaintEventId()));
			moResult.removeIf(mo -> !mo.getStatus().equalsIgnoreCase(WidgetConstants.OPEN));

			Map<String, List<MaintenanceOptimizerResponse>> maintEventIdTasks = moResult.stream()
					.filter(e -> e.getMaintEventId() != null)
					.collect(Collectors.groupingBy(MaintenanceOptimizerResponse::getEventTypeDesc));

			List<MaintenanceOptimizerResponse> noMaintEventIds = moResult.stream()
					.filter(e -> e.getMaintEventId() == null).toList();

			// Get results for urgent tasks from tasksAPI result
			List<MaintenanceOptimizerResponse> urgentMap = noMaintEventIds.stream().filter(w -> w.getIsUrgent())
					.toList();
			Map<String, Long> urgentMapLineUpsCount = urgentMap.stream()
					.collect(Collectors.groupingBy(e -> e.getLineupId(), Collectors.counting()));
			urgentMap.stream()
					.forEach(mo -> mo.setEventCount(urgentMapLineUpsCount.containsKey(mo.getLineupId())
							? urgentMapLineUpsCount.get(mo.getLineupId())
							: null));

			List<MaintenanceOptimizerResponse> overdueList = noMaintEventIds.stream().filter(w -> !w.getIsUrgent())
					.filter(td -> td.getTaskdate().compareTo(LocalDateTime.now()) <= 0).toList();
			Map<String, Long> overDueLineUpsCount = overdueList.stream()
					.collect(Collectors.groupingBy(e -> e.getLineupId(), Collectors.counting()));
			overdueList.stream()
					.forEach(mo -> mo.setEventCount(overDueLineUpsCount.containsKey(mo.getLineupId())
							? overDueLineUpsCount.get(mo.getLineupId())
							: null));

			List<MaintenanceOptimizerResponse> futureTasks = noMaintEventIds.stream().filter(w -> !w.getIsUrgent())
					.filter(td -> td.getTaskdate().compareTo(LocalDateTime.now()) > 0).toList();

			Map<LocalDateTime, List<MaintenanceOptimizerResponse>> suggestedDatesMap = futureTasks.stream()
					.collect(Collectors.groupingBy(
							w -> w.getTaskdate().withDayOfMonth(1).withNano(0).withSecond(0).withMinute(0).withHour(0),
							Collectors.toList()));

			// Future Tasks suggestedDate > CurrentDate
			Map<LocalDateTime, List<MaintenanceOptimizerResponse>> futuredTasks = suggestedDatesMap.entrySet().stream()
					.filter(entry -> entry.getKey().compareTo(LocalDateTime.now()) > 0)
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

			Map<LocalDateTime, List<MaintenanceOptimizerResponse>> futureTasksSorted = futuredTasks.entrySet().stream()
					.sorted((i1, i2) -> i1.getKey().compareTo(i2.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, TreeMap::new));

			// Prepare final response for UI
			prepareMaintEventIdResponse(finalResponse, maintEventIdTasks);
			prepareNonMaintEventIdResponse(finalResponse, futureTasksSorted);

			if (!urgentMap.isEmpty()) {
				prepareUrgentMapData(finalResponse, urgentMap);
			}

			if (!overdueList.isEmpty()) {
				prepareOverDueListData(finalResponse, overdueList);
			}
		}

		Collections.sort(finalResponse, new MonthComparator());
		return finalResponse;

	}

	class MonthComparator implements Comparator<Object> {

		@Override
		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yy");
			String month1 = (String) ((Map<String, Object>) o1).get(WidgetConstants.MONTH);
			String month2 = (String) ((Map<String, Object>) o2).get(WidgetConstants.MONTH);
			try {
				LocalDate date1 = LocalDate.parse("01 ".concat(month1), dtf);
				LocalDate date2 = LocalDate.parse("01 ".concat(month2), dtf);
				return date1.compareTo(date2);
			} catch (Exception e) {
				return -1;
			}
		}

	}

	@SuppressWarnings("unchecked")
	private List<Object> prepareMaintEventIdResponse(List<Object> finalResponse,
			Map<String, List<MaintenanceOptimizerResponse>> eventTypeWiseResponseMap) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM yy");
		Map<String, Map<String, Object>> consolidatedMap = new HashMap<>();
		for (Entry<String, List<MaintenanceOptimizerResponse>> entry : eventTypeWiseResponseMap.entrySet()) {
			String eventTypeDesc = entry.getKey();
			Map<LocalDateTime, List<MaintenanceOptimizerResponse>> groupByDate = entry.getValue().stream()
					.collect(Collectors.groupingBy(response -> response.getTaskdate()));
			for (Entry<LocalDateTime, List<MaintenanceOptimizerResponse>> entry1 : groupByDate.entrySet()) {
				String month = entry1.getKey().format(dtf);
				Map<String, Object> mapResponse = consolidatedMap.getOrDefault(month, new HashMap<>());
				mapResponse.put(WidgetConstants.MONTH, month);
				List<Map<String, Object>> overDuetasks = (List<Map<String, Object>>) mapResponse
						.getOrDefault(WidgetConstants.FIELD_TASK, new LinkedList<>());
				List<List<Map<String, Object>>> splitTasks = splitList(processJobs(entry1), 5);
				for (List<Map<String, Object>> jobs : splitTasks) {
					if (overDuetasks.stream()
							.noneMatch(task -> task.get(WidgetConstants.TITLE).equals(eventTypeDesc))) {
						overDuetasks.add(buildJobs(entry1, eventTypeDesc, jobs));
					} else {
						overDuetasks.stream()
								.filter(task -> task.getOrDefault(WidgetConstants.TITLE, null) != null
										&& task.get(WidgetConstants.TITLE).equals(eventTypeDesc))
								.findAny()
								.ifPresent(task -> ((List<Map<String, Object>>) task.get(WidgetConstants.JOBS))
										.addAll(jobs));
					}
				}
				overDuetasks.forEach(task -> ((List<Map<String, Object>>) task.get(WidgetConstants.JOBS))
						.forEach(job -> job.remove(WidgetConstants.DATE)));
				mapResponse.put(WidgetConstants.FIELD_TASK, overDuetasks);
				consolidatedMap.put(month, mapResponse);
			}
		}
		finalResponse.addAll(consolidatedMap.values());
		return finalResponse;
	}

	@SuppressWarnings("unchecked")
	private List<Object> prepareNonMaintEventIdResponse(List<Object> finalResponse,
			Map<LocalDateTime, List<MaintenanceOptimizerResponse>> futureTaskSorted) {

		List<String> existingMonths = finalResponse.stream()
				.map(response -> ((Map<String, Object>) response).get(WidgetConstants.MONTH).toString()).toList();

		for (Entry<LocalDateTime, List<MaintenanceOptimizerResponse>> entry : futureTaskSorted.entrySet()) {
			String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM yy"));
			Map<String, Object> mapResponse = finalResponse.stream().map(x -> (Map<String, Object>) x)
					.filter(y -> y.get(WidgetConstants.MONTH).equals(month)).findFirst().orElse(new HashMap<>());
			mapResponse.put(WidgetConstants.MONTH, month);
			List<Map<String, Object>> overDuetasks = (List<Map<String, Object>>) mapResponse
					.getOrDefault(WidgetConstants.FIELD_TASK, new LinkedList<>());
			Map<Object, List<MaintenanceOptimizerResponse>> groupOfEvents = entry.getValue().stream()
					.collect(Collectors.groupingBy(foo -> foo.getEventTypeDesc()));
			for (Entry<Object, List<MaintenanceOptimizerResponse>> entryl : groupOfEvents.entrySet()) {
				List<List<Map<String, Object>>> splitTasks = splitList(processJobs(entry), 5);
				for (List<Map<String, Object>> tasks : splitTasks) {
					overDuetasks.add(buildJobs(entry, entryl.getKey().toString(), tasks));
				}
			}
			overDuetasks.forEach(task -> ((List<Map<String, Object>>) task.get(WidgetConstants.JOBS))
					.sort(Comparator.comparing(job -> (String) job.get(WidgetConstants.DATE))));
			mapResponse.put(WidgetConstants.FIELD_TASK, overDuetasks);
			if (!existingMonths.contains(month)) {
				finalResponse.add(mapResponse);
			}
		}

		return finalResponse;
	}

	private Map<String, Object> buildJobs(Entry<LocalDateTime, List<MaintenanceOptimizerResponse>> entry,
			String eventTypeDesc, List<Map<String, Object>> jobs) {
		Map<String, Object> pastTask = new HashMap<>();
		pastTask.put(WidgetConstants.TITLE, eventTypeDesc);
		long weeks = ChronoUnit.WEEKS.between(LocalDate.now(), entry.getKey());
		String eventType = null;
		if (eventTypeDesc.equals(WidgetConstants.UNASSIGNED_TASKS)) {
			eventType = String.valueOf(eventTypeDesc);
		} else if (weeks < 2)
			eventType = "<2 Weeks";
		else if (weeks >= 2 && weeks < 6)
			eventType = "<6 Weeks";
		else
			eventType = ">6 Weeks";
		pastTask.put(WidgetConstants.COLOR, getEventColor(eventType));
		pastTask.put("background", ColorConstants.MAINTENANCE_OPTIMIZER_COLOR);
		pastTask.put(WidgetConstants.JOBS, jobs);
		return pastTask;
	}

	private static <T> List<List<T>> splitList(List<T> originalList, int chunkSize) {
		List<List<T>> splitLists = new ArrayList<>();
		for (int i = 0; i < originalList.size(); i += chunkSize) {
			int endIndex = Math.min(i + chunkSize, originalList.size());
			List<T> chunk = originalList.subList(i, endIndex);
			splitLists.add(new ArrayList<>(chunk));
		}
		return splitLists;
	}

	private List<Map<String, Object>> processJobs(Entry<LocalDateTime, List<MaintenanceOptimizerResponse>> entry) {
		Map<String, Object> job;
		List<Map<String, Object>> jobs = new ArrayList<>();
		Map<Integer, Long> taskCount = entry.getValue().stream()
				.collect(Collectors.groupingBy(task -> task.getTaskdate().getDayOfMonth(), Collectors.counting()));
		for (MaintenanceOptimizerResponse mo : entry.getValue()) {
			job = new HashMap<>();
			String day = "";
			try {
				day = new SimpleDateFormat("dd")
						.format(new SimpleDateFormat("yyyy-MM-dd").parse(mo.getTaskdate().toString()));
				job.put(WidgetConstants.DATE, addPostFiXForDate(day));
			} catch (ParseException e3) {
				logger.info(e3.getMessage(), e3);
			}

			job.put("machineId", mo.getLineupId());
			job.put("pendingTask",
					mo.getEventCount() == null ? taskCount.get(Integer.parseInt(day)) : mo.getEventCount());
			if (!jobs.contains(job)) {
				jobs.add(job);
			}
		}
		return jobs;
	}

	private List<Object> prepareOverDueListData(List<Object> finalResponse,
			List<MaintenanceOptimizerResponse> overdue) {
		Map<String, Object> mapResponse;
		List<Map<String, Object>> overDuetasks;
		List<Map<String, Object>> jobs;
		Map<String, Object> pastTask;
		Map<String, Object> job;
		Map<String, Double> sumofLineUpIds;
		mapResponse = new HashMap<>();
		mapResponse.put(WidgetConstants.MONTHSORT, "Past");
		overDuetasks = new ArrayList<>();
		pastTask = new HashMap<>();
		pastTask.put(WidgetConstants.TITLE, WidgetConstants.OVERDUE);
		pastTask.put(WidgetConstants.COLOR, getEventColor(WidgetConstants.OVERDUE));
		pastTask.put(WidgetConstants.BACKGROUND, ColorConstants.MAINTENANCE_OPTIMIZER_COLOR);
		jobs = new ArrayList<>();
		sumofLineUpIds = overdue.stream().collect(
				Collectors.groupingBy(foo -> foo.getLineupId(), Collectors.averagingLong(foo -> foo.getEventCount())));
		for (Entry<String, Double> entry : sumofLineUpIds.entrySet()) {
			job = new HashMap<>();
			job.put(WidgetConstants.MO_MACHINE_ID, entry.getKey());
			job.put(WidgetConstants.MO_PENDING_TASK, entry.getValue().intValue());
			jobs.add(job);
		}
		pastTask.put(WidgetConstants.JOBS, jobs);
		overDuetasks.add(pastTask);
		mapResponse.put(WidgetConstants.FIELD_TASK, overDuetasks);
		finalResponse.add(mapResponse);
		return finalResponse;
	}

	private static String addPostFiXForDate(String date) {
		int dayOfMonth = Integer.parseInt(date);
		String postfix;
		if (dayOfMonth >= 11 && dayOfMonth <= 13) {
			postfix = "th";
		} else {
			switch (dayOfMonth % 10) {
			case 1:
				postfix = "st";
				break;
			case 2:
				postfix = "nd";
				break;
			case 3:
				postfix = "rd";
				break;
			default:
				postfix = "th";
				break;
			}
		}
		return date.concat(postfix);
	}

	private List<Object> prepareUrgentMapData(List<Object> finalResponse,
			List<MaintenanceOptimizerResponse> urgentMap) {
		Map<String, Object> mapResponse = new HashMap<>();
		List<Map<String, Object>> overDuetasks;
		List<Map<String, Object>> jobs;
		Map<String, Object> pastTask;
		Map<String, Object> job;
		Map<String, Double> sumofLineUpIds;
		mapResponse.put(WidgetConstants.MONTH, "Today");
		overDuetasks = new ArrayList<>();
		pastTask = new HashMap<>();
		pastTask.put(WidgetConstants.TITLE, WidgetConstants.URGENT);
		pastTask.put(WidgetConstants.COLOR, getEventColor(WidgetConstants.URGENT));
		pastTask.put("background", ColorConstants.MAINTENANCE_OPTIMIZER_COLOR);
		jobs = new ArrayList<>();
		sumofLineUpIds = urgentMap.stream().collect(
				Collectors.groupingBy(foo -> foo.getLineupId(), Collectors.averagingLong(foo -> foo.getEventCount())));
		for (Entry<String, Double> entryl : sumofLineUpIds.entrySet()) {
			job = new HashMap<>();
			job.put("machineId", entryl.getKey());
			job.put("pendingTask", entryl.getValue().intValue());
			jobs.add(job);
		}
		pastTask.put(WidgetConstants.JOBS, jobs);
		overDuetasks.add(pastTask);
		mapResponse.put(WidgetConstants.FIELD_TASK, overDuetasks);
		finalResponse.add(mapResponse);
		return finalResponse;
	}

	@SuppressWarnings("unchecked")
	private JSONObject fetchTasksInfo(Map<String, Object> request, String parentCaseIdsCSV) {

		Map<String, String> input = (Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES);
		input.putIfAbsent(ProxyConstants.KEY_PARENT_CASE_IDS_TEXT, parentCaseIdsCSV);
		request.put(ProxyConstants.REPLACE_VALUES, input);

		JSONObject eventsApiResponse = null;
		JSONObject proxyJsonObject = null;
		try {
			request.put(WidgetConstants.WIDGETID, maintenanceOptimizerWidgetId);
			proxyJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);
			eventsApiResponse = proxyJsonObject.getJSONObject(WidgetConstants.DATA);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return eventsApiResponse;
	}

	@SuppressWarnings("unchecked")
	private JSONObject fetchEventMaintenaceInfo(Map<String, Object> request) {

		Map<String, String> input = (Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES);
		request.put(ProxyConstants.REPLACE_VALUES, input);

		JSONObject eventsApiResponse = null;
		JSONObject proxyJsonObject = null;
		try {
			request.put(WidgetConstants.WIDGETID, maintenanceOptimizerEventWidgetId);
			proxyJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);
			eventsApiResponse = proxyJsonObject.getJSONObject(WidgetConstants.DATA);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return eventsApiResponse;
	}

	private String getEventColor(String eventType) {
		return switch (eventType) {
		case WidgetConstants.OVERDUE:
			yield ColorConstants.MAINTENANCE_OPTIMIZER_OVERDUE;
		case WidgetConstants.URGENT:
			yield ColorConstants.MAINTENANCE_OPTIMIZER_URGENT;
		case WidgetConstants.UNASSIGNED_TASKS:
			yield ColorConstants.MAINTENANCE_OPTIMIZER_UNASSIGNED_TASKS;
		case "<2 Weeks":
			yield ColorConstants.MAINTENANCE_OPTIMIZER_2WEEKS;
		case "<6 Weeks":
			yield ColorConstants.MAINTENANCE_OPTIMIZER_LESS_6WEEKS;
		case ">6 Weeks":
			yield ColorConstants.MAINTENANCE_OPTIMIZER_MORE_6WEEKS;
		default:
			yield ColorConstants.MAINTENANCE_OPTIMIZER_DEFAULT_COLOR;
		};
	}

}
