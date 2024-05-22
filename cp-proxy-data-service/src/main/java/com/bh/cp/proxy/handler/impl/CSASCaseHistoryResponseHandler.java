package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.service.ProxyService;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CSASCaseHistoryResponseHandler<T> extends JsonResponseHandler<T> {

	private Integer kpiTokenId;
	private ProxyService proxyService;
	private HttpServletRequest httpServletRequest;

	private JSONArray tokenArray;

	private static final Logger logger = LoggerFactory.getLogger(CSASCaseHistoryResponseHandler.class);

	@SuppressWarnings("unchecked")
	protected CSASCaseHistoryResponseHandler(HttpServletRequest httpServletRequest, ProxyService proxyService,
			@Value("${kpi.token.service-id}") Integer kpiTokenId) {
		super((T) new HashMap<String, Object>());
		this.proxyService = proxyService;
		this.kpiTokenId = kpiTokenId;
		this.httpServletRequest = httpServletRequest;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		Map<String, Object> tokenListResponse;

		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		if (!(response.containsKey(WidgetConstants.RESOURCES))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(WidgetConstants.DATA, JSONObject.NULL);
			return nullObject;
		}
		JSONObject jsonObject = new JSONObject(response);
		JSONObject outputObject = new JSONObject();

		JSONArray array = jsonObject.getJSONArray(WidgetConstants.RESOURCES);
		JSONArray list = new JSONArray();

		List<String> caseIdList = new ArrayList<>();
		List<String> openCaseIdList = new ArrayList<>();

		try {

			if (array.length() != 0) {
				for (int i = 0; i < array.length(); i++) {
					caseIdList.add(array.getJSONObject(i).optString(WidgetConstants.CASEIDR));
					String status = array.getJSONObject(i).optString(WidgetConstants.STATUS);
					if (WidgetConstants.OPEN.equalsIgnoreCase(status)) {
						openCaseIdList.add(array.getJSONObject(i).optString(WidgetConstants.CASEIDR));
					}
				}
			}
			request.put(WidgetConstants.SERVICE_ID, kpiTokenId);
			request.put(WidgetConstants.CASEIDR, openCaseIdList.toString());
			JSONObject proxyJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);
			if (proxyJsonObject != null) {
				tokenListResponse = (Map<String, Object>) proxyJsonObject.toMap().get(WidgetConstants.DATA);
				if (tokenListResponse != null) {
					outputObject.put(ProxyConstants.TOKEN_COUNTS, tokenListResponse.get(ProxyConstants.TOKEN_COUNTS));
					String tokenAr = new Gson().toJson(tokenListResponse.get(ProxyConstants.TOKEN_LISTS));
					if (!"null".equals(tokenAr)) {
						tokenArray = new JSONArray(tokenAr);
					}
				}
			}

			JSONObject map;
			for (int i = 0; i < array.length(); i++) {

				map = new JSONObject();

				map.put(ProxyConstants.DATE_OPEN,
						array.getJSONObject(i).optString((ProxyConstants.OPEN_DATE_UTC), "").replace('-', '/'));
				map.put(ProxyConstants.DATE_CLOSED,
						array.getJSONObject(i).optString((ProxyConstants.CLOSEDDATES), "").replace('-', '/'));
				map.put(ProxyConstants.TASK_STATUS, array.getJSONObject(i).optString((WidgetConstants.STATUS), ""));
				map.put(ProxyConstants.PROBLEM_STATEMENT,
						array.getJSONObject(i).optString((ProxyConstants.PROBLEM_STATEMENTS), ""));
				map.put(ProxyConstants.IMPLEMENTATION_DATE,
						array.getJSONObject(i).optString((ProxyConstants.IMPLEMENTATION_DATES), ""));
				map.put(ProxyConstants.ACTION_TAKEN,
						array.getJSONObject(i).optString((ProxyConstants.ACTION_TAKENS), ""));
				map.put(ProxyConstants.TITLE, array.getJSONObject(i).optString((ProxyConstants.TITLE), ""));
				map.put(ProxyConstants.ATACHMENT, array.getJSONObject(i).optString((ProxyConstants.ATTCHMENTID), ""));
				map.put(ProxyConstants.LINE_UP, array.getJSONObject(i).optString((ProxyConstants.LINEUP_ID), ""));
				String caseId = array.getJSONObject(i).optString((WidgetConstants.CASEIDR), "");
				map.put(ProxyConstants.CASE_NUMBER, caseId);
				map.put(WidgetConstants.TOKEN, addToken(caseId, tokenArray));
				list.put(map);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		outputObject.put(ProxyConstants.LIST, list);
		outputObject.put(ProxyConstants.CASE_COUNT, caseIdList.size());
		outputObject.put("caseList", caseIdList);

		return new JSONObject().put(WidgetConstants.DATA, outputObject);
	}

	private String addToken(String caseId, JSONArray tokenArray) {
		String token = null;
		for (int i = 0; i < tokenArray.length(); i++) {
			String tokenCaseId = tokenArray.getJSONObject(i).optString((WidgetConstants.CASEIDR), "");
			if (caseId.equals(tokenCaseId)) {
				token = tokenArray.getJSONObject(i).optString((ProxyConstants.TOKEN_SMALL), "");
				break;
			} else {
				token = "";

			}
		}
		return token;
	}
}
