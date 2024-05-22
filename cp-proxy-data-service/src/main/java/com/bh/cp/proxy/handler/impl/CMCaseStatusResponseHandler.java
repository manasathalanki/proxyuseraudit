package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.exception.ProxyException;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.service.ProxyService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CMCaseStatusResponseHandler<T> extends JsonResponseHandler<T> {

	private HttpServletRequest httpServletRequest;

	private ProxyService proxyService;

	private static final Logger logger = LoggerFactory.getLogger(CMCaseStatusResponseHandler.class);

	JSONObject responseObject;
	JSONObject openResponseObject;

	Integer openResults;
	Integer closedResults;
	Integer deletedResults;

	List<JSONObject> closedList;
	List<JSONObject> openList;
	List<JSONObject> deletedList;

	List<JSONObject> dataList;

	@Autowired
	@SuppressWarnings("unchecked")
	public CMCaseStatusResponseHandler(HttpServletRequest httpServletRequest, ProxyService proxyService) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.proxyService = proxyService;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		logger.info("CM Case Status Response Handler----->");
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		if (!(response.containsKey("resources"))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data", JSONObject.NULL);
			return nullObject;
		}
		JSONObject jsonObject = new JSONObject(response);
		JSONObject object;

		JSONArray resourceList = jsonObject.getJSONArray(WidgetConstants.RESOURCES);
		dataList = new ArrayList<>();

		for (int i = 0; i < resourceList.length(); i++) {
			object = resourceList.getJSONObject(i);
			dataList.add(object);
		}

		responseObject = resultStatusCount(request, dataList);

		return responseObject;
	}

	private JSONObject resultStatusCount(Map<String, Object> request, List<JSONObject> dataList2) {

		responseObject = new JSONObject();
		openResponseObject = new JSONObject();

		openResults = 0;
		closedResults = 0;
		deletedResults = 0;

		closedList = null;
		openList = new ArrayList<>();
		deletedList = null;
		dataList = dataList2;

		if (request.containsKey(ProxyConstants.OPEN_CASE_RESULT)) {

			openList = openCaseResponse(request);

			responseObject.put(ProxyConstants.OPEN_CASE_RES, openList != null ? (Integer) openList.size() : "");
			responseObject.put("ClosedCaseResponse", request.get("CloseCaseResult"));
			responseObject.put("DeletedCaseResponse", request.get("DeleteCaseResult"));

			return openResponseObject.put(WidgetConstants.DATA, responseObject);

		} else {

			responseObject = dataStored(dataList, request);

		}

		return responseObject;
	}

	private JSONObject dataStored(List<JSONObject> dataList2, Map<String, Object> request) {

		JSONObject proxyJsonObject;
		String closedCase = "ClosedCaseResponse";
		String deletedCase = "DeletedCaseResponse";

		closedList = dataList2.stream().filter(x -> x.get(WidgetConstants.STATUS).equals("CLOSED")).toList();
		deletedList = dataList2.stream().filter(x -> x.get(WidgetConstants.STATUS).equals("DELETED")).toList();

		if (request.containsKey(WidgetConstants.STARTDATE) && request.containsKey(WidgetConstants.ENDDATE)) {
			String startDate = request.get(WidgetConstants.STARTDATE).toString();
			if (startDate != null) {
				request.remove(WidgetConstants.STARTDATE);
			}

			String endDate = request.get(WidgetConstants.ENDDATE).toString();
			if (endDate != null) {
				request.remove(WidgetConstants.ENDDATE);
			}
			request.put(ProxyConstants.OPEN_CASE_RESULT, ProxyConstants.OPEN_CASE_RES);
			request.put("CloseCaseResult", closedList.size());
			request.put("DeleteCaseResult", deletedList.size());
			request.remove(ProxyConstants.REPLACE_VALUES);

			try {
				proxyJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);

				openResults = genericCaseResponseResult(proxyJsonObject, ProxyConstants.OPEN_CASE_RES);
				closedResults = genericCaseResponseResult(proxyJsonObject, closedCase);
				deletedResults = genericCaseResponseResult(proxyJsonObject, deletedCase);

			} catch (JsonProcessingException | ClassNotFoundException | IllegalArgumentException | SecurityException
					| ProxyException e) {
				logger.info(e.getMessage(), e);
			}
		} else {
			openList = dataList.stream().filter(x -> x.get(WidgetConstants.STATUS).equals("OPEN")).toList();
			openResults = openList.size();
			closedResults = closedList.size();
			deletedResults = deletedList.size();
		}

		if ((openResults != 0 || closedResults != 0 || deletedResults != 0)) {
			Map<String, Integer> statusCount = new HashMap<>();
			statusCount.put(WidgetConstants.OPEN, openResults);
			statusCount.put(WidgetConstants.CLOSED, closedResults);
			statusCount.put(WidgetConstants.DELETED, deletedResults);
			responseObject.put(WidgetConstants.DATA, openClosedList(statusCount));
		} else {
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}

		return responseObject;
	}

	@SuppressWarnings("unchecked")
	private Integer genericCaseResponseResult(JSONObject proxyJsonObject, String genericCaseResponse) {

		Integer result = 0;
		if (proxyJsonObject != null) {
			Map<String, Object> taskListResponse = (Map<String, Object>) proxyJsonObject.toMap().get("data");
			result = (Integer) taskListResponse.get(genericCaseResponse);
		}

		return result;
	}

	private List<JSONObject> openCaseResponse(Map<String, Object> request) {

		if (request.get(ProxyConstants.OPEN_CASE_RESULT).equals(ProxyConstants.OPEN_CASE_RES)) {

			openList = dataList.stream().filter(x -> x.get(WidgetConstants.STATUS).equals("OPEN")).toList();
		}

		return openList;
	}

	private List<Map<String, Object>> openClosedList(Map<String, Integer> countMap) {
		Map<String, String> colorMap = new HashMap<>();
		colorMap.put(WidgetConstants.OPEN, "#666EB4");
		colorMap.put(WidgetConstants.CLOSED, "#299BA3");
		colorMap.put(WidgetConstants.DELETED, "#D0D0D0");

		List<Map<String, Object>> statuslist = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
			statuslist.add(createCategoryObject(entry.getKey(), entry.getValue(),
					colorMap.getOrDefault(entry.getKey(), "#555555")));
		}
		return statuslist;
	}

	private Map<String, Object> createCategoryObject(String categoryName, int noofcases, String color) {
		Map<String, Object> categoryObj = new HashMap<>();
		categoryObj.put(WidgetConstants.CATEGORYNAME, categoryName);
		categoryObj.put(WidgetConstants.NOOFCASES, noofcases);
		categoryObj.put(WidgetConstants.COLOR, color);
		return categoryObj;
	}

}
