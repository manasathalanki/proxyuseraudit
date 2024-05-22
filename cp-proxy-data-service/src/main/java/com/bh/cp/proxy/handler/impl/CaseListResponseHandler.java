package com.bh.cp.proxy.handler.impl;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.service.ProxyService;
import com.bh.cp.proxy.util.DateUtility;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CaseListResponseHandler<T> extends JsonResponseHandler<T> {

	private AssetHierarchyFilterService assetHierarchyFilterService;

	private HttpServletRequest httpServletRequest;

	private ProxyService proxyService;

	private UMSClientService umsClientService;

	private Integer kpiTaskId;

	private static final Logger logger = LoggerFactory.getLogger(CaseListResponseHandler.class);

	JSONArray list;
	JSONObject map;
	int eWOpenCount;
	int eWClosedCount;
	int eTSOpenCount;
	int eTSClosedCount;
	Boolean privilegesFlag = false;
	Boolean enableServiceFlag;
	String criticality;
	String vid;
	DateTimeFormatter dtfInput;
	DateTimeFormatter dtfOutputEng;
	ZonedDateTime date1;
	DateUtility dateUtility = new DateUtility();

	@Autowired
	@SuppressWarnings("unchecked")
	public CaseListResponseHandler(HttpServletRequest httpServletRequest, ProxyService proxyService,
			UMSClientService umsClientService, AssetHierarchyFilterService assetHierarchyFilterService,
			@Value("${kpi.task.service-id}") Integer kpiTaskId) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.proxyService = proxyService;
		this.umsClientService = umsClientService;
		this.assetHierarchyFilterService = assetHierarchyFilterService;
		this.kpiTaskId = kpiTaskId;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {

		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		if (response == null || !(response.containsKey(WidgetConstants.RESOURCES))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(WidgetConstants.DATA, JSONObject.NULL);
			return nullObject;
		}
		JSONObject jsonObject = new JSONObject(response);
		JSONObject outputObject = new JSONObject();

		JSONArray array = jsonObject.getJSONArray(WidgetConstants.RESOURCES);
		list = new JSONArray();

		List<String> caseIdList = new ArrayList<>();

		if (array.length() != 0) {
			for (int i = 0; i < array.length(); i++) {
				caseIdList.add(array.getJSONObject(i).get(WidgetConstants.CASEIDR).toString());
			}
		}
		privilegesFlag = false;
		try {
			Map<String, Object> userResponse = umsClientService.getUserDetails(httpServletRequest);
			List<String> privilegesResponse = (List<String>) userResponse.get("privileges");
			if (privilegesResponse != null && (privilegesResponse.contains(ProxyConstants.MO)
					|| privilegesResponse.contains(ProxyConstants.HI))) {
				privilegesFlag = true;

			}

			if (array.length() != 0) {

				list = storedData(array, request, privilegesFlag);

			} else {
				JSONObject nullObject = new JSONObject();
				nullObject.put(WidgetConstants.DATA, JSONObject.NULL);
				return nullObject;
			}

			Map<String, Object> taskListResponse = null;
			JSONObject proxyJsonObject = null;

			request.put(WidgetConstants.SERVICE_ID, kpiTaskId);
			request.put("parentCaseId", caseIdList.toString());
			proxyJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);
			if (proxyJsonObject != null) {
				taskListResponse = (Map<String, Object>) proxyJsonObject.toMap().get(WidgetConstants.DATA);
				if (taskListResponse != null) {
					outputObject.put("OpenTaskCount", taskListResponse.get("OpenTaskCount"));
					outputObject.put("TwoWeeksDueTaskCount", taskListResponse.get("TwoWeeksDueTaskCount"));
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		outputObject.put("list", list);
		outputObject.put("EWSOpenCount", eWOpenCount);
		outputObject.put("EWSCloseCount", eWClosedCount);
		outputObject.put("ETSOpenCount", eTSOpenCount);
		outputObject.put("ETSCloseCount", eTSClosedCount);
		outputObject.put("assetId", vid);

		return new JSONObject().put(WidgetConstants.DATA, outputObject);
	}

	@SuppressWarnings("unchecked")
	private JSONArray storedData(JSONArray array, Map<String, Object> request, Boolean privilegesFlagValue) {

		eWOpenCount = 0;
		eWClosedCount = 0;
		eTSOpenCount = 0;
		eTSClosedCount = 0;
		vid = "";
		privilegesFlag = privilegesFlagValue;
		enableServiceFlag = false;
		dtfInput = DateTimeFormatter.ofPattern(ProxyConstants.DATE_FORMAT_FOR_UTC, Locale.ENGLISH);
		dtfOutputEng = DateTimeFormatter.ofPattern(ProxyConstants.DATE_FORMAT_TO_UTC_MM, Locale.ENGLISH);
		String flag = "";

		for (int i = 0; i < array.length(); i++) {

			map = new JSONObject();

			String status = array.getJSONObject(i).optString(("status"), "");

			if (status.equalsIgnoreCase(ProxyConstants.CASE_STATUS_OPEN)
					|| status.equalsIgnoreCase(ProxyConstants.CASE_STATUS_CLOSE)
					|| status.equalsIgnoreCase(ProxyConstants.CASE_STATUS_DELETE)) {

				map.put("Status", status);

				String mechineId = array.getJSONObject(i).optString(("machineSerialNum"), "");
				map.put("Machine", mechineId);

				vid = "MC_" + mechineId;
				Map<String, Map<String, Set<String>>> fieldsEnabledServicesMap = assetHierarchyFilterService
						.getFieldsAndEnabledServicesToMap(
								(List<Map<String, Object>>) request.get(ProxyConstants.FILTEREDASSETHIERARCHY));
				Map<String, Set<String>> vidMap = fieldsEnabledServicesMap.get(vid);

				enableServiceFlag = checkStatus(vidMap);

				if (privilegesFlag && enableServiceFlag) {
					criticality = array.getJSONObject(i).optString((WidgetConstants.CRITICALITY), "NA");

				} else {
					criticality = "NA";
				}
				map.put("Criticality", criticality);

				map.put("AnomalyCat", array.getJSONObject(i).optString(("anomalyCategory"), ""));

				String caseNo = array.getJSONObject(i).optString((WidgetConstants.CASEIDR), "");

				map.put("CaseNo", caseNo);

				String caseUrl = ProxyConstants.CASE_URL.concat(caseNo);

				map.put("CaseUrl", caseUrl);

				map.put("Project", array.getJSONObject(i).optString(("customer"), ""));

				map.put("Title", array.getJSONObject(i).optString(("title"), ""));

				map.put("Train", array.getJSONObject(i).optString(("trainDescription"), ""));

				map.put("commentIds", array.getJSONObject(i).optString(("commentIds"), ""));

				map.put("Case", array.getJSONObject(i).optString(("trainDescription"), ""));

				map.put("Lineup", array.getJSONObject(i).optString(("lineupId"), ""));

				String checkUser = array.getJSONObject(i).optString(("isInternal"), "");
				flag = ProxyConstants.EXTERNAL.equalsIgnoreCase(checkUser) ? ProxyConstants.INTERNAL_USER
						: ProxyConstants.EXTERNAL_USER;
				map.put("internalFlag", flag);

				map.put("serviceNow", array.getJSONObject(i).optString(("linkedExternalCaseIds"), ""));

				String firstEventDate = array.getJSONObject(i).optString(("eventDateUTC"), "");
				map.put("FirstEventDate", formatDate(firstEventDate));

				String updateDate = array.getJSONObject(i).optString(("lastUpdateDateUTC"), "");
				map.put("UpdatedDate", formatDate(updateDate));

				String closeDate = array.getJSONObject(i).optString(("closeDate"), "");
				map.put("CloseDate", formatDate(closeDate));

				map.put("TripId", array.getJSONObject(i).optString(("trpCaseId"), ""));

				String refTags = array.getJSONObject(i).optString(("contributingTags"), "");
				map.put("ReferenceTags", stringReplace(refTags));

				String customerWO = array.getJSONObject(i).optString(("linkedCustomerCaseIds"), "");
				map.put("customerWO", stringReplace(customerWO));

				String linkedCaseIds = array.getJSONObject(i).optString(("linkedCaseIds"), "");
				map.put("LinkedCaseIds", stringReplace(linkedCaseIds));

				String otherLineupIds = array.getJSONObject(i).optString(("othersRelatedLineupsIds"), "");

				map.put("OthersRelatedLineupsIds", stringReplace(otherLineupIds));

				map.put("Ebs", array.getJSONObject(i).optString(("ebsDesc"), ""));
				map.put("Analysis", array.getJSONObject(i).optString(("analysis"), ""));

				map.put("EbsGroup", array.getJSONObject(i).optString(("ebsGroup"), "") + " - "
						+ array.getJSONObject(i).optString(("ebsGroupDesc"), ""));
				map.put(ProxyConstants.FIELD_CUSTPRIOR,
						array.getJSONObject(i).optString((ProxyConstants.FIELD_CUSTPRIOR), ""));
				map.put("EbsSystem", array.getJSONObject(i).optString(("ebsSystemId"), "") + " - "
						+ array.getJSONObject(i).optString(("ebsSystem"), ""));
				map.put("EbsComponent", array.getJSONObject(i).optString(("ebsComponent"), "") + " - "
						+ array.getJSONObject(i).optString(("ebsComponentDesc"), ""));
				map.put("Type", array.getJSONObject(i).optString(("type"), ""));

				String criValue = array.getJSONObject(i).optString(("criticality"), "");

				map = findCriticality(map, criValue);

				map.put("Icon", "visibility");

				String type = array.getJSONObject(i).optString(("type"), "");

				eWOpenCount = eWOpenCountCalculation(type);
				eWClosedCount = eWClosedCountCalculation(type);

				eTSOpenCount = eTSOpenCountCalculation(type);
				eTSClosedCount = eTSClosedCountCalculation(type);

				list.put(map);
			}
		}

		return list;
	}

	private String formatDate(String genericEventDate) {
		if (!(genericEventDate == null || genericEventDate.equals(""))) {
			date1 = dateUtility.convertDateToUTC(genericEventDate);
			genericEventDate = dtfOutputEng.format(date1);
		}
		return genericEventDate;
	}

	private JSONObject findCriticality(JSONObject map, String criValue) {
		if (criValue.equalsIgnoreCase(ProxyConstants.CRITICALITY_HIGH)) {
			map.put(ProxyConstants.FIELD_CRITICALITY, "high.svg");
		} else if (criValue.equalsIgnoreCase(ProxyConstants.CRITICALITY_MEDIUM)) {
			map.put(ProxyConstants.FIELD_CRITICALITY, "medium.svg");
		} else if (criValue.equalsIgnoreCase(ProxyConstants.CRITICALITY_LOW)) {
			map.put(ProxyConstants.FIELD_CRITICALITY, "low.svg");
		} else {
			map.put(ProxyConstants.FIELD_CRITICALITY, "na.svg");
		}
		return map;
	}

	private int eTSClosedCountCalculation(String type) {

		if ((!type.equals("")) && type.equals(ProxyConstants.TYPE_ETS)
				&& ((map.has(ProxyConstants.STATUS) ? map.get(ProxyConstants.STATUS) : "")
						.equals(ProxyConstants.CASE_STATUS_CLOSE))) {
			eTSClosedCount++;

		}

		return eTSClosedCount;
	}

	private int eTSOpenCountCalculation(String type) {

		if ((!type.equals("")) && type.equals(ProxyConstants.TYPE_ETS)
				&& ((map.has(ProxyConstants.STATUS) ? map.get(ProxyConstants.STATUS) : "")
						.equals(ProxyConstants.CASE_STATUS_OPEN))) {
			eTSOpenCount++;

		}

		return eTSOpenCount;
	}

	private int eWClosedCountCalculation(String type) {

		if ((!type.equals("")) && type.equals(ProxyConstants.TYPE_EWS)
				&& ((map.has(ProxyConstants.STATUS) ? map.get(ProxyConstants.STATUS) : "")
						.equals(ProxyConstants.CASE_STATUS_CLOSE))) {
			eWClosedCount++;

		}
		return eWClosedCount;
	}

	private int eWOpenCountCalculation(String type) {

		if ((!type.equals("")) && type.equals(ProxyConstants.TYPE_EWS)
				&& ((map.has(ProxyConstants.STATUS) ? map.get(ProxyConstants.STATUS) : "")
						.equals(ProxyConstants.CASE_STATUS_OPEN))) {
			eWOpenCount++;
		}

		return eWOpenCount;
	}

	private Boolean checkStatus(Map<String, Set<String>> vidMap) {
		enableServiceFlag = false;
		if (vidMap != null) {

			Set<String> enabledServices = vidMap.getOrDefault(JSONUtilConstants.ENABLEDSERVICES, new HashSet<>());

			if (enabledServices != null
					&& (enabledServices.contains("MAINT_OPT") || enabledServices.contains("HEALTH_INDEX"))) {
				enableServiceFlag = true;

			}
		}

		return enableServiceFlag;
	}

	private String stringReplace(String tags) {
		if (tags.contains("[")) {
			tags = tags.replace("[", "");
			tags = tags.replace("]", "");
			tags = tags.replace("\"", "");
		}
		return tags;
	}

}
