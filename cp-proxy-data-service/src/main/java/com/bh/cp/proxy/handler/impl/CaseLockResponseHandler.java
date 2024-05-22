package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.helper.OktaUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CaseLockResponseHandler<T> extends JsonResponseHandler<T> {
	private static final Logger logger = LoggerFactory.getLogger(CaseLockResponseHandler.class);

	private HttpServletRequest httpServletRequest;

	private UMSClientService umsClientService;

	private OktaUserDetails oktaUserDetails;

	Map<String, List<String>> userMap = new HashMap<>();
	String firstName = null;
	String lastName = null;
	String userName = null;
	String userType = null;

	@Autowired
	@SuppressWarnings("unchecked")
	public CaseLockResponseHandler(HttpServletRequest httpServletRequest, UMSClientService umsClientService,
			OktaUserDetails oktaUserDetails) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.umsClientService = umsClientService;
		this.oktaUserDetails = oktaUserDetails;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		Map<String, Object> userResponse = null;
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		String email = response.get(ProxyConstants.LOCKING_USER) != null
				? response.get(ProxyConstants.LOCKING_USER).toString()
				: "";
		List<String> mailList = new ArrayList<>();
		List<Map<String, Object>> allOktaUserResponse = new ArrayList<>();
		mailList.add(email);
		JSONObject userObject = new JSONObject();
		userObject.put("field", "profile.email");
		userObject.put("values", mailList);
		try {
			userResponse = umsClientService.getUserDetails(httpServletRequest);
			allOktaUserResponse = umsClientService.getOktaUserDetails(httpServletRequest, userObject);
			userType = (String) userResponse.get(ProxyConstants.TITLE);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		userMap = oktaUserDetails.mappingUserData(allOktaUserResponse);
		JSONObject jsonObject = new JSONObject(response);
		JSONObject responseObj = new JSONObject();

		try {

			JSONObject map;

			if (jsonObject.length() != 0) {

				map = new JSONObject();

				map.put("LockResponse",
						jsonObject.optString("lockResponse") != null ? jsonObject.optString("lockResponse") : "");

				map.put("CaseId", jsonObject.optString("caseId") != null ? jsonObject.optString("caseId") : "");

				map.put("LockingUser",
						jsonObject.optString(ProxyConstants.LOCKING_USER) != null
								? jsonObject.optString(ProxyConstants.LOCKING_USER)
								: "");

				caseLockUserType(email, map);
				map.put("LockTimestamp",
						jsonObject.optString("lockTimestamp") != null ? jsonObject.optString("lockTimestamp") : "");

				map.put("CaseRev", jsonObject.optString("caseRev") != null ? jsonObject.optString("caseRev") : "");

				responseObj.put("data", map);

			}
		} catch (JSONException e) {
			responseObj.put(WidgetConstants.DATA, JSONObject.NULL);
		}

		return responseObj;

	}

	private void caseLockUserType(String email, JSONObject map) {
		String isTypeCheck = null;
		isTypeCheck = userMap.get(email) != null ? userMap.get(email).get(1) : "";
		userName = userMap.get(email) != null ? userMap.get(email).get(0) : "";
		boolean oneInternal = ProxyConstants.EXTERNAL_USER.equalsIgnoreCase(userType)
				&& isTypeCheck.equalsIgnoreCase(ProxyConstants.INTERNAL_USER);
		if (oneInternal) {
			map.put(ProxyConstants.LOCKUSERNAME, ProxyConstants.BAKER_HUGHES);
		} else {
			map.put(ProxyConstants.LOCKUSERNAME, userName);
		}

	}
}
