package com.bh.cp.proxy.handler.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.helper.WidgetSubscriptionCheckHelper;
import com.bh.cp.proxy.service.ProxyService;
import com.bh.cp.proxy.util.SecurityUtil;
import com.bh.cp.proxy.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class EventsTimelineResponseHandler<T> extends JsonResponseHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(EventsTimelineResponseHandler.class);

	private Integer eventTimelineWidgetId;

	private HttpServletRequest httpServletRequest;

	private ProxyService proxyService;
	
	private String eventTimeLineActivityLogUri;

	private WidgetSubscriptionCheckHelper widgetSubscriptionCheckHelper;

	@SuppressWarnings("unchecked")
	public EventsTimelineResponseHandler(@Autowired HttpServletRequest httpServletRequest,
			@Autowired ProxyService proxyService,
			@Autowired WidgetSubscriptionCheckHelper widgetSubscriptionCheckHelper,
			@Value("${event.timeline.recursive.widget-id}") Integer eventTimelineWidgetId,
			@Value("${event.timeline.activity.log.uri}") String eventTimeLineActivityLogUri) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.proxyService = proxyService;
		this.eventTimelineWidgetId = eventTimelineWidgetId;
		this.widgetSubscriptionCheckHelper = widgetSubscriptionCheckHelper;
		this.eventTimeLineActivityLogUri = eventTimeLineActivityLogUri;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		String criticality = "";
		try {
			// 1.fetch cases info on machine serial nos
			HashMap<String, Object> eventsResponse = (HashMap<String, Object>) getT();

			Integer widgetId = (Integer) request.get(WidgetConstants.WIDGETID);
			if (Objects.equals(widgetId, eventTimelineWidgetId)) {
				return new JSONObject().put(WidgetConstants.DATA, eventsResponse);
			}
			JSONObject casesObject = new JSONObject(eventsResponse);
			JSONArray cases = casesObject.getJSONArray(WidgetConstants.RESOURCES);
			// 2.fetch events info on assetId
			JSONObject fetchEventsInfo = fetchEventInfo(request);
			JSONArray events = null;
			if (fetchEventsInfo != null)
				events = fetchEventsInfo.getJSONArray(WidgetConstants.RESOURCES);
			List<Map<String, Object>> response = new ArrayList<>();
			JSONObject caseObject;
			Map<String, Object> caseResponse = null;
			// 3.accomplish events info into cases list
			for (int i = 0; i < cases.length(); i++) {
				caseResponse = getDefaultObject();
				caseObject = cases.getJSONObject(i);
				caseResponse.put(WidgetConstants.CASE_ID, caseObject.get(WidgetConstants.CASEID));
				caseResponse.put(WidgetConstants.EVENT_DATE_TIME, LocalDateTime.parse(
						caseObject.getString("openDateUTC"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
				caseResponse.put(WidgetConstants.BACKGROUND, ColorConstants.EVENT_TIMELINE_BACKGROUND);

				caseResponse.put(WidgetConstants.LINEUP_NAME, caseObject.get(WidgetConstants.LINEUPID));
				if (Boolean.TRUE.equals(widgetSubscriptionCheckHelper.checkAdvanceServicePrivilegeAccess(
						getHttpServletRequest(request), (String) request.get(ProxyConstants.VID)))) {
					criticality = caseObject.optString(WidgetConstants.CRITICALITY, WidgetConstants.EMPTYSTRING);
				}

				caseResponse.put(WidgetConstants.CASE_CRITICALITY,
						(criticality.length() > 0) ? criticality.charAt(0) : JSONObject.NULL);
				String eventType = caseObject.getString(WidgetConstants.TYPE);
				caseResponse.put(WidgetConstants.COLOR, getEventColor(eventType));
				caseResponse.put(WidgetConstants.EVENT_TYPE, eventType);
				caseResponse.put(WidgetConstants.CASE_LINK, WidgetConstants.EMPTYSTRING);
				caseResponse.put(WidgetConstants.STATUS, caseObject.getString(WidgetConstants.STATUS));
				caseResponse.put(WidgetConstants.ANOMALYCATEGORY,
						StringUtil.isEmptyString(caseObject.getString("anomalyCategory")) ? JSONObject.NULL
								: caseObject.getString("anomalyCategory"));
				
				//Prepare for Balloon Type 3
				if (caseObject.has(WidgetConstants.EVENTLOGIDS)) {
					JSONArray eventLogIds = caseObject.getJSONArray(WidgetConstants.EVENTLOGIDS);
					caseResponse = eventLogIdsMappingWithEventsResponse(eventLogIds, caseResponse, events);
					caseResponse.put(WidgetConstants.COLOR,
							getEventColor(caseResponse.get(WidgetConstants.EVENT_TYPE).toString()));
				}
				response.add(caseResponse);
			}
			// 4.Prepare final response for UI
			List<Map<String, Object>> finalResponse = new ArrayList<>();
			finalResponse.addAll(response);
			if (events != null) {
				finalResponse = preparingFinalResponse(events, finalResponse, response);
			}
			// Sort by event time
			if (finalResponse != null && finalResponse.isEmpty())
				return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
			return sortingFinalResponse(finalResponse);
		} catch (Exception e) {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
	}

	public HttpServletRequest getHttpServletRequest(Map<String, Object> request) {
		if (null == request.get(ProxyConstants.HTTPSERVLETREQUEST)) {
			return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		} else {
			return (HttpServletRequest) request.get(ProxyConstants.HTTPSERVLETREQUEST);
		}
	}

	private Map<String, Object> eventLogIdsMappingWithEventsResponse(JSONArray eventLogIds,
			Map<String, Object> caseResponse, JSONArray events) {
		JSONObject eventObject;
		if (!eventLogIds.isEmpty()) {
			for (int k = 0; k < eventLogIds.length(); k++) {
				Integer eventId = Integer.parseInt((String) eventLogIds.get(k));
				if (events != null) {
					for (int e = 0; e < events.length(); e++) {
						eventObject = events.getJSONObject(e);
						if (eventId.equals(eventObject.getNumber(WidgetConstants.ID))) {							
							caseResponse.put(WidgetConstants.EVENT_ID,eventId);
							caseResponse.put(WidgetConstants.EVENT_DATE_TIME,
									eventObject.getString(WidgetConstants.DEVENT));
							caseResponse.put(WidgetConstants.ANOMALYCATEGORY,
									eventObject.getString(WidgetConstants.FAILUREMODEDESC));
							caseResponse.put(WidgetConstants.EVENT_TYPE,
									eventObject.getString(WidgetConstants.EVENT_TYPE_DESC));
						}
					}
				}
			}
		}
		return caseResponse;
	}

	//Prepare for Balloon Type 2
	private List<Map<String, Object>> preparingFinalResponse(JSONArray events, List<Map<String, Object>> finalResponse,
			List<Map<String, Object>> response) {
		JSONObject eventObject;
		Map<String, Object> caseResponse = null;
		for (int e = 0; e < events.length(); e++) {
			eventObject = events.getJSONObject(e);
			caseResponse = getDefaultObject();
			caseResponse.put(WidgetConstants.EVENT_DATE_TIME, LocalDateTime
					.parse(eventObject.getString(WidgetConstants.DEVENT), DateTimeFormatter.ISO_DATE_TIME));
			caseResponse.put(WidgetConstants.ANOMALYCATEGORY, eventObject.getString(WidgetConstants.FAILUREMODEDESC));
			caseResponse.put(WidgetConstants.COLOR, ColorConstants.EVENT_TIMELINE_START);
			caseResponse.put(WidgetConstants.BACKGROUND, ColorConstants.EVENT_TIMELINE_BACKGROUND);
			Boolean found = checkForMatchingEventId(response, eventObject);
			
			if (!found.booleanValue()) {
				if (!eventObject.getJSONArray(WidgetConstants.CASELIST).isEmpty()) {
					 Optional<JSONArray> caseList= Arrays.asList(eventObject.getJSONArray(WidgetConstants.CASELIST))
					.stream().filter(item -> item.length()>0)
					.findFirst();
					if(caseList.isPresent()) {
						caseResponse.put(WidgetConstants.CASE_ID, caseList.get().get(0));
					}					
				}else { 
					caseResponse.put(WidgetConstants.EVENT_ID_LINK, SecurityUtil.sanitizeUrl(eventTimeLineActivityLogUri, Map.of(WidgetConstants.EVENT_ID, eventObject.getNumber(WidgetConstants.ID))));
					caseResponse.put(WidgetConstants.EVENT_ID, eventObject.getNumber(WidgetConstants.ID));
				}
				
				String type = eventObject.getString(WidgetConstants.EVENT_TYPE_DESC);
				caseResponse.put(WidgetConstants.EVENT_TYPE, type);
				caseResponse.put(WidgetConstants.LINEUP_NAME, eventObject.getString("lineup_id"));
				caseResponse.put(WidgetConstants.COLOR, getEventColor(type));
				finalResponse.add(caseResponse);
			}
		}
		return finalResponse;
	}

	private Boolean checkForMatchingEventId(List<Map<String, Object>> response, JSONObject eventObject) {
		Boolean found = Boolean.FALSE;
		for (Map<String, Object> caseMap : response) {
			if (caseMap != null && caseMap.containsKey(WidgetConstants.EVENT_ID)
					&& caseMap.get(WidgetConstants.EVENT_ID).equals(eventObject.getNumber(WidgetConstants.ID)))
				found = Boolean.TRUE;
		}
		return found;
	}

	private List<Map<String, Object>> sortingFinalResponse(List<Map<String, Object>> finalResponse) {
		Collections.sort(finalResponse,
				(i1, i2) -> (LocalDateTime.parse(i1.get(WidgetConstants.EVENT_DATE_TIME).toString(),
						DateTimeFormatter.ISO_DATE_TIME))
						.compareTo(LocalDateTime.parse(i2.get(WidgetConstants.EVENT_DATE_TIME).toString(),
								DateTimeFormatter.ISO_DATE_TIME)));
		for (Map<String, Object> object : finalResponse) {
			object.put(WidgetConstants.EVENT_DATE_TIME,
					LocalDateTime
							.parse(object.get(WidgetConstants.EVENT_DATE_TIME).toString(),
									DateTimeFormatter.ISO_DATE_TIME)
							.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		}
		return finalResponse;
	}

	@SuppressWarnings("unchecked")
	private JSONObject fetchEventInfo(Map<String, Object> request) {

		Map<String, String> input = (Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES);
		request.put(ProxyConstants.REPLACE_VALUES, input);

		JSONObject eventsApiResponse = null;
		JSONObject proxyJsonObject = null;
		try {
			request.put(WidgetConstants.WIDGETID, eventTimelineWidgetId);
			proxyJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);
			eventsApiResponse = proxyJsonObject.getJSONObject(WidgetConstants.DATA);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return eventsApiResponse;
	}

	private Map<String, Object> getDefaultObject() {
		Map<String, Object> object = new HashMap<>();
		object.put(WidgetConstants.CASE_ID, JSONObject.NULL);
		object.put(WidgetConstants.EVENT_DATE_TIME, JSONObject.NULL);
		object.put(WidgetConstants.COLOR, ColorConstants.EVENT_TIMELINE_EARLY_WARNING);
		object.put(WidgetConstants.BACKGROUND, ColorConstants.EVENT_TIMELINE_BACKGROUND);
		object.put(WidgetConstants.CASE_CRITICALITY, JSONObject.NULL);
		object.put(WidgetConstants.EVENT_TYPE, JSONObject.NULL);
		object.put(WidgetConstants.CASE_LINK, JSONObject.NULL);
		object.put(WidgetConstants.STATUS, JSONObject.NULL);
		object.put(WidgetConstants.ANOMALYCATEGORY, JSONObject.NULL);
		object.put(WidgetConstants.LINEUP_NAME, JSONObject.NULL);
		object.put(WidgetConstants.EVENT_ID, JSONObject.NULL);
		return object;
	}

	private String getEventColor(String eventType) {
		return switch (eventType) {
		case "Early Warning":
			yield ColorConstants.EVENT_TIMELINE_EARLY_WARNING;
		case "Normal Shutdown", "Normal Shutdown Detected":
			yield ColorConstants.EVENT_TIMELINE_NORMAL_SHUTDOWN;
		case "Start", "Start Up Detected", "Warm Start Up Detected", "Hot Start Up Detected", "Cold Start Up Detected":
			yield ColorConstants.EVENT_TIMELINE_START;
		case "Trip":
			yield ColorConstants.EVENT_TIMELINE_TRIP;
		case "Maintenance (Planned)":
			yield ColorConstants.EVENT_TIMELINE_MAINTENANCE;
		case "Maintenance (Unplanned)":
			yield ColorConstants.EVENT_TIMELINE_MAINTENANCE;
		case "Maintenance - Water wash":
			yield ColorConstants.EVENT_TIMELINE_MAINTENANCE;
		case "Maintenance", "Axial Compressor Water Washing (Off-Line)", "Axial Compressor Water Washing (On-Line)":
			yield ColorConstants.EVENT_TIMELINE_MAINTENANCE;
		default:
			yield ColorConstants.EVENT_TIMELINE_OTHERS;
		};
	}

}