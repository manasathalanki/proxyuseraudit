package com.bh.cp.proxy.handler.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.controller.GenericDataController;
import com.bh.cp.proxy.exception.ProxyException;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.util.DateUtility;
import com.bh.cp.proxy.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class TaskTableAPIResponseHandler<T> extends JsonResponseHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(TaskTableAPIResponseHandler.class);

	private String eveMaintUri;

	private GenericDataController dataController;

	private HttpServletRequest httpServletRequest;

	private AssetHierarchyFilterService assetHierarchyFilterService;

	private RestTemplate restTemplate;

	DateTimeFormatter formatter;
	DateTimeFormatter dtfOutputEng;
	String colourCode = null;
	String toolTip = null;
	LocalDateTime date1 = null;
	Map<String, Object> children = new HashMap<>();
	JSONArray list = new JSONArray();
	String ids = null;
	String mainDesc = null;

	@Autowired
	@SuppressWarnings("unchecked")
	public TaskTableAPIResponseHandler(GenericDataController dataController, HttpServletRequest httpServletRequest,
			AssetHierarchyFilterService assetHierarchyFilterService, RestTemplate restTemplate,
			@Value("${event.maintainance.uri}") String eveMaintUri) {
		super((T) new HashMap<String, Object>());

		this.dataController = dataController;
		this.httpServletRequest = httpServletRequest;
		this.assetHierarchyFilterService = assetHierarchyFilterService;
		this.restTemplate = restTemplate;
		this.eveMaintUri = eveMaintUri;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		if (!(response.containsKey(WidgetConstants.FIELD_TASK))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(WidgetConstants.DATA, "No data found");
			return nullObject;
		}
		try {

			LocalDate date = LocalDate.now();

			Integer caseId = Integer
					.parseInt(StringUtil.encodeString(request.get(WidgetConstants.FIELD_PARENTCASE_ID).toString()));

			
			String mechId = getMechId(caseId,dataController);

			@SuppressWarnings("deprecation")
			List<Map<String, Object>> assetList = assetHierarchyFilterService.getSubTreeForWidget(
					(List<Map<String, Object>>) request.get(ProxyConstants.FILTEREDASSETHIERARCHY), mechId);

			List<String> fieldList = new ArrayList<>();

			if (!assetList.isEmpty()) {
				for (Map<String, Object> asset : assetList) {
					Map<String, Object> fields = (Map<String, Object>) asset.get(JSONUtilConstants.FIELDS);
					fieldList = checkFieldList(fields,fieldList);
				}
			}

			if (!fieldList.isEmpty()) {
				for (int i = 0; i < fieldList.size(); i++) {
					if (ids != null) {
						ids = ids.concat(",").concat(fieldList.get(i));
					} else {
						ids = fieldList.get(i);
					}
				}
			}

			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> httpEntity = new HttpEntity<>(headers);
			@SuppressWarnings("rawtypes")
			ResponseEntity<Map> responseEntity = restTemplate.exchange(eveMaintUri + ids + "&" + date, HttpMethod.GET,
					httpEntity, Map.class);
			Map<String, Object> result = responseEntity.getBody();
			List<Map<String, Object>> bodyRes = result != null
					? (List<Map<String, Object>>) result.get(WidgetConstants.DATA)
					: new ArrayList<>();
			if (bodyRes != null) {
				setChildren(bodyRes);
			}

			List<HashMap<String, Object>> mapResponse = (List<HashMap<String, Object>>) response
					.get(WidgetConstants.FIELD_TASK);
			formatter = DateTimeFormatter.ofPattern(ProxyConstants.DATE_FORMAT_FOR_UTC, Locale.ENGLISH);
			dtfOutputEng = DateTimeFormatter.ofPattern(ProxyConstants.DATE_FORMAT_TO_UTC, Locale.ENGLISH);
			list = new JSONArray();
			if (!mapResponse.isEmpty()) {
				list = resultResponse(mapResponse);
			}
		} catch (JSONException | ClassNotFoundException | IllegalArgumentException | SecurityException | IOException
				| ProxyException e) {
			logger.info(e.getMessage(), e);
		}
		return new JSONObject().put(WidgetConstants.DATA, list);
	}

	private List<String> checkFieldList(Map<String, Object> fields, List<String> fieldList) {
		if(fields!=null)
		{
		fieldList.add((String) fields.get(JSONUtilConstants.GIBSERIALNO));
		}
		return fieldList;
	}

	private String getMechId(Integer caseId, GenericDataController dataController) throws ClassNotFoundException, IllegalArgumentException, SecurityException, IOException, ProxyException {
		String mechineData = dataController.retrieveCasesData(httpServletRequest, caseId).getBody();
		JSONObject obj = new JSONObject(mechineData);
		JSONObject mapRes = obj.get(WidgetConstants.DATA)!=JSONObject.NULL ? (JSONObject) obj.get(WidgetConstants.DATA) : new JSONObject();
		return mapRes!=JSONObject.NULL && mapRes.has(WidgetConstants.ASSETID) ? mapRes.get(WidgetConstants.ASSETID).toString():"";
	}

	private void setChildren(List<Map<String, Object>> bodyRes) {
		for (int i = 0; i < bodyRes.size(); i++) {
			String eventId = bodyRes.get(i).get(WidgetConstants.FIELD_RMD_EVENT).toString();
			String typeDesc = bodyRes.get(i).get(WidgetConstants.FIELD_EVENT_TYPE_DESC).toString();
			children.putIfAbsent(eventId, typeDesc);
		}
	}

	private JSONArray resultResponse(List<HashMap<String, Object>> mapResponse) {
		JSONObject map;
		for (int i = 0; i < mapResponse.size(); i++) {
			map = new JSONObject();
			String suggestedDate = mapResponse.get(i)
					.getOrDefault(ProxyConstants.SUGGESTED_DATE, WidgetConstants.EMPTYSTRING).toString();
			ZonedDateTime convertedDate = null;
			if (!StringUtil.isEmptyString(suggestedDate)) {
				LocalDateTime localDateTime = LocalDateTime.parse(suggestedDate, formatter);

				ZoneId zoneId = ZoneId.systemDefault();
				ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
				convertedDate = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
				ZonedDateTime currentDateTime = ZonedDateTime.now();

				DateUtility dateUtility = new DateUtility();
				Map<String, Integer> dateDiffMap;
				dateDiffMap = dateUtility.customDateDifference(convertedDate.toString(), currentDateTime.toString());
				String isUrgent = mapResponse.get(i).get("isUrgent")!=null ? mapResponse.get(i).get("isUrgent").toString():"";
				map = colorCode(map, dateDiffMap,isUrgent);
			}

			if (!(suggestedDate == null || suggestedDate.equals(""))) {
				suggestedDate = dtfOutputEng.format(convertedDate);
			}
			map.put(ProxyConstants.SUGGESTED_DATE, suggestedDate);
			map.put(ProxyConstants.S_NO, i + 1);
			appendData(map, mapResponse, i);
			map.put(ProxyConstants.UOM,
					mapResponse.get(i).get(ProxyConstants.UOM) != null
							? mapResponse.get(i).get(ProxyConstants.UOM).toString()
							: "");
			String maintenance = mapResponse.get(i).get(ProxyConstants.MAINTAINANCE_EVENT_ID) != null
					? mapResponse.get(i).get(ProxyConstants.MAINTAINANCE_EVENT_ID).toString()
					: "";
			mainDesc = mainDesc(children, maintenance);
			map.put(ProxyConstants.MAINTAINANCE, mainDesc);
			list.put(map);
		}
		return list;
	}

	private void appendData(JSONObject map, List<HashMap<String, Object>> mapResponse, int i) {
		map.put(ProxyConstants.TASK_STATUS,
				mapResponse.get(i).get(ProxyConstants.TASK_STATUS) != null
						? mapResponse.get(i).get(ProxyConstants.TASK_STATUS).toString()
						: "");
		map.put(ProxyConstants.SHORT_DESC,
				mapResponse.get(i).get(ProxyConstants.SHORT_DESC) != null
						? mapResponse.get(i).get(ProxyConstants.SHORT_DESC).toString()
						: "");
		map.put(ProxyConstants.TYPE,
				mapResponse.get(i).get(ProxyConstants.TYPE) != null
						? mapResponse.get(i).get(ProxyConstants.TYPE).toString()
						: "");
		map.put(ProxyConstants.IS_ROOT_CAUSE,
				mapResponse.get(i).get(ProxyConstants.IS_ROOT_CAUSE) != null
						? mapResponse.get(i).get(ProxyConstants.IS_ROOT_CAUSE).toString()
						: "");
		map.put(ProxyConstants.PID_TAG,
				mapResponse.get(i).get(ProxyConstants.PID_TAG) != null
						? mapResponse.get(i).get(ProxyConstants.PID_TAG).toString()
						: "");
		map.put(ProxyConstants.ALARM_THRESHOLD_L,
				mapResponse.get(i).get(ProxyConstants.ALARM_THRESHOLD_L) != null
						? mapResponse.get(i).get(ProxyConstants.ALARM_THRESHOLD_L).toString()
						: "");
		map.put(ProxyConstants.ALARM_THRESHOLD_LL,
				mapResponse.get(i).get(ProxyConstants.ALARM_THRESHOLD_LL) != null
						? mapResponse.get(i).get(ProxyConstants.ALARM_THRESHOLD_LL).toString()
						: "");
		map.put(ProxyConstants.ALARM_THRESHOLD_H,
				mapResponse.get(i).get(ProxyConstants.ALARM_THRESHOLD_H) != null
						? mapResponse.get(i).get(ProxyConstants.ALARM_THRESHOLD_H).toString()
						: "");
		map.put(ProxyConstants.ALARM_THRESHOLD_HH,
				mapResponse.get(i).get(ProxyConstants.ALARM_THRESHOLD_HH) != null
						? mapResponse.get(i).get(ProxyConstants.ALARM_THRESHOLD_HH).toString()
						: "");
		map.put(ProxyConstants.SET_POINT,
				mapResponse.get(i).get(ProxyConstants.SET_POINT) != null
						? mapResponse.get(i).get(ProxyConstants.SET_POINT).toString()
						: "");
		map.put(ProxyConstants.TASK_ID,
				mapResponse.get(i).get(ProxyConstants.TASK_ID) != null
						? mapResponse.get(i).get(ProxyConstants.TASK_ID).toString()
						: "");
	}

	private JSONObject colorCode(JSONObject map, Map<String, Integer> dateDiffMap, String isUrgent) {
		dateDiffMap.put(ProxyConstants.WEEKS, Math.abs(dateDiffMap.get(ProxyConstants.WEEKS)));
		if (!isUrgent.equalsIgnoreCase(ProxyConstants.ISURGENTFALSE)) {
			colourCode = ProxyConstants.TODAY;
			toolTip = "Urgent";
		} else if (isUrgent.equalsIgnoreCase(ProxyConstants.ISURGENTFALSE) && dateDiffMap.get(ProxyConstants.WEEKS) <= 2) {
			colourCode = ProxyConstants.TWO_WEEK;
			toolTip = "<2 Weeks";
		} else if (isUrgent.equalsIgnoreCase(ProxyConstants.ISURGENTFALSE) && dateDiffMap.get(ProxyConstants.WEEKS) > 2 && dateDiffMap.get(ProxyConstants.WEEKS) <= 6) {
			colourCode = ProxyConstants.SIX_WEEK;
			toolTip = "<6 Weeks";
		} else if (isUrgent.equalsIgnoreCase(ProxyConstants.ISURGENTFALSE) && dateDiffMap.get(ProxyConstants.WEEKS) > 6) {
			colourCode = ProxyConstants.ONE_YEAR;
			toolTip = ">6 Weeks";
		}
		map.put(ProxyConstants.COLOR_CODE, colourCode != null ? colourCode : "");
		map.put(ProxyConstants.TOOLTIP, toolTip);
		return map;
	}

	private String mainDesc(Map<String, Object> children, String maintenance) {
		if (children != null && children.containsKey(maintenance)) {
			mainDesc = children.get(maintenance).toString();
		} else {
			mainDesc = "NA";
		}
		return mainDesc;
	}
}
