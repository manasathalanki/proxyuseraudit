package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CaseStatusResponseHandler<T> extends JsonResponseHandler<T> {

	private HttpServletRequest httpServletRequest;

	private ProxyService proxyService;

	private Integer closedCasesId;

	private static final Logger logger = LoggerFactory.getLogger(CaseStatusResponseHandler.class);

	@Autowired
	@SuppressWarnings("unchecked")
	public CaseStatusResponseHandler(HttpServletRequest httpServletRequest, ProxyService proxyService,
			@Value("${closed.cases.recursive.widget-id}") Integer closedCasesId) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.proxyService = proxyService;
		this.closedCasesId = closedCasesId;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		try {
			HashMap<String, Object> openResponse = (HashMap<String, Object>) getT();
			Integer widgetId = (Integer) request.get(WidgetConstants.WIDGETID);
			if (Objects.equals(widgetId, closedCasesId)) {
				return new JSONObject().put(WidgetConstants.DATA, openResponse);
			}

			JSONObject responseObject = new JSONObject();

			// Calling recursively for getting closed case response
			request.put(WidgetConstants.WIDGETID, closedCasesId);
			JSONObject proxyJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);
			Map<String, Object> closedResponse = (Map<String, Object>) proxyJsonObject.toMap()
					.get(WidgetConstants.DATA);

			Integer openCasesResults = (Integer) openResponse.get(WidgetConstants.TOTALRESULTS);
			Integer closedCasesResults = (Integer) closedResponse.get(WidgetConstants.TOTALRESULTS);
			Map<String, Integer> statusCount = new HashMap<>();
			if ((openResponse.containsKey(WidgetConstants.TOTALRESULTS))
					&& (closedResponse.containsKey(WidgetConstants.TOTALRESULTS))
					&& (openCasesResults != 0 && closedCasesResults != 0)) {
				statusCount.put(WidgetConstants.OPEN, (Integer) openResponse.get(WidgetConstants.TOTALRESULTS));
				statusCount.put(WidgetConstants.CLOSED, (Integer) closedResponse.get(WidgetConstants.TOTALRESULTS));
				responseObject.put(WidgetConstants.DATA, openClosedList(statusCount));
			} else if ((openResponse.containsKey(WidgetConstants.TOTALRESULTS))
					&& (closedResponse.containsKey(WidgetConstants.TOTALRESULTS))
					&& (openCasesResults == 0 && closedCasesResults != 0)) {
				statusCount.put(WidgetConstants.CLOSED, (Integer) closedResponse.get(WidgetConstants.TOTALRESULTS));
				responseObject.put(WidgetConstants.DATA, openClosedList(statusCount));
			} else if ((openResponse.containsKey(WidgetConstants.TOTALRESULTS))
					&& (closedResponse.containsKey(WidgetConstants.TOTALRESULTS))
					&& (openCasesResults != 0 && closedCasesResults == 0)) {
				statusCount.put(WidgetConstants.OPEN, (Integer) openResponse.get(WidgetConstants.TOTALRESULTS));
				responseObject.put(WidgetConstants.DATA, openClosedList(statusCount));
			} else {
				return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
			}
			return responseObject;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
		}
	}

	private List<Map<String, Object>> openClosedList(Map<String, Integer> countMap) {
		Map<String, String> colorMap = new HashMap<>();
		colorMap.put(WidgetConstants.OPEN, ColorConstants.OPENCASES);
		colorMap.put(WidgetConstants.CLOSED, ColorConstants.CLOSEDCASES);
		List<Map<String, Object>> statuslist = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
			statuslist.add(createCategoryObject(entry.getKey(), entry.getValue(),
					colorMap.getOrDefault(entry.getKey(), ColorConstants.OTHERCASES)));
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
