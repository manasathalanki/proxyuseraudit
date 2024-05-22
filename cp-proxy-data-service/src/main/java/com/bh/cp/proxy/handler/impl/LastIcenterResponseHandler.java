package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ColorConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.helper.WidgetSubscriptionCheckHelper;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class LastIcenterResponseHandler<T> extends JsonResponseHandler<T> {

	private WidgetSubscriptionCheckHelper widgetSubscriptionCheckHelper;

	private static final Logger logger = LoggerFactory.getLogger(LastIcenterResponseHandler.class);

	@Autowired
	@SuppressWarnings("unchecked")
	public LastIcenterResponseHandler(WidgetSubscriptionCheckHelper widgetSubscriptionCheckHelper) {
		super((T) new HashMap<String, Object>());
		this.widgetSubscriptionCheckHelper = widgetSubscriptionCheckHelper;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		JSONObject inputObject = new JSONObject(response);
		JSONObject responseObject;
		JSONArray resourcesArray;
		Integer caseId = 0;
		String type = "";
		String unitName = "";
		String date = "";
		String level = "";
		String title = "";
		JSONObject resources;
		try {
			resourcesArray = inputObject.getJSONArray(WidgetConstants.RESOURCES);
			if (resourcesArray.length() > 0) {
				resources = resourcesArray.getJSONObject(0);
				date = resources.optString(WidgetConstants.LASTNOTIFICATIONUTC, WidgetConstants.EMPTYSTRING);
				unitName = resources.optString(WidgetConstants.UNITNAME, WidgetConstants.EMPTYSTRING);
				type = resources.optString(WidgetConstants.TYPE, WidgetConstants.EMPTYSTRING);
				caseId = resources.optInt(WidgetConstants.CASEID, 0);
				title = resources.optString(WidgetConstants.TITLE, WidgetConstants.EMPTYSTRING);
				if (Boolean.TRUE.equals(widgetSubscriptionCheckHelper.checkAdvanceServicePrivilegeAccess(
						(HttpServletRequest) request.get(ProxyConstants.HTTPSERVLETREQUEST),
						(String) request.get(ProxyConstants.VID)))) {
					level = resources.optString(WidgetConstants.CRITICALITY, WidgetConstants.EMPTYSTRING);
				}
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		responseObject = new JSONObject();
		getColourCode(level, responseObject);
		responseObject.put(WidgetConstants.CRITICALITY,
				(level.length() > 0) ? level.charAt(0) : WidgetConstants.EMPTYSTRING);
		responseObject.put(WidgetConstants.UNITNAMER, (unitName.length() > 0) ? unitName : WidgetConstants.NODATAFOUND);
		responseObject.put(WidgetConstants.EVENTDATER, (date.length() > 0) ? date : WidgetConstants.NODATAFOUND);
		responseObject.put(WidgetConstants.TYPE, (type.length() > 0) ? type : WidgetConstants.NODATAFOUND);
		responseObject.put(WidgetConstants.CASEIDR, (caseId != 0) ? caseId.toString() : WidgetConstants.NODATAFOUND);
		responseObject.put(WidgetConstants.CASETITLER, (title.length() > 0) ? title : WidgetConstants.NODATAFOUND);
		return new JSONObject().put(WidgetConstants.DATA, responseObject);
	}

	private JSONObject getColourCode(String level, JSONObject responseObject) {
		if (level.equals(WidgetConstants.HIGH)) {
			responseObject.put(WidgetConstants.BGCOLOR, ColorConstants.CRITICALITYHIGHNEW);
			responseObject.put(WidgetConstants.FONTCOLOR, ColorConstants.CRITICALITYHIGHFONT);
		} else if (level.equals(WidgetConstants.MEDIUM)) {
			responseObject.put(WidgetConstants.BGCOLOR, ColorConstants.CRITICALITYMEDIUMNEW);
			responseObject.put(WidgetConstants.FONTCOLOR, ColorConstants.CRITICALITYMEDIUMFONT);
		} else if (level.equals(WidgetConstants.LOW)) {
			responseObject.put(WidgetConstants.BGCOLOR, ColorConstants.CRITICALITYLOWNEW);
			responseObject.put(WidgetConstants.FONTCOLOR, ColorConstants.CRITICALITYLOWFONT);
		} else {
			responseObject.put(WidgetConstants.BGCOLOR, ColorConstants.CRITICALITYGREY);
			responseObject.put(WidgetConstants.FONTCOLOR, "black");
		}
		return responseObject;
	}
}