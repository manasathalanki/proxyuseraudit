package com.bh.cp.proxy.handler.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.helper.OktaUserDetails;
import com.bh.cp.proxy.service.ProxyService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CaseCommentsAPIResponseHandler<T> extends JsonResponseHandler<T> {

	private HttpServletRequest httpServletRequest;

	private ProxyService proxyService;

	private UMSClientService umsClientService;

	private OktaUserDetails oktaUserDetails;

	private final Integer taskServiceId;

	private final Integer caseListId;

	private static final Logger logger = LoggerFactory.getLogger(CaseCommentsAPIResponseHandler.class);

	List<String> caseNoList;
	HashMap<String, Object> mailResponse = new HashMap<>();

	Map<String, List<String>> userMap = new HashMap<>();
	Map<String, String> commentMap = new HashMap<>();
	DateTimeFormatter dtfInput;
	DateTimeFormatter dtfOutputEng;
	String userType;

	@Autowired
	@SuppressWarnings("unchecked")
	public CaseCommentsAPIResponseHandler(HttpServletRequest httpServletRequest, ProxyService proxyService,
			UMSClientService umsClientService, OktaUserDetails oktaUserDetails,
			@Value("${task.service-id}") Integer taskServiceId, @Value("${case.list-id}") Integer caseListId) {
		super((T) new HashMap<String, Object>());

		this.httpServletRequest = httpServletRequest;
		this.proxyService = proxyService;
		this.umsClientService = umsClientService;
		this.oktaUserDetails = oktaUserDetails;
		this.taskServiceId = taskServiceId;
		this.caseListId = caseListId;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {

		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		if (response == null || !(response.containsKey("data"))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data", "No data found");
			return nullObject;
		}

		dtfInput = DateTimeFormatter.ofPattern(ProxyConstants.CASE_TREND_DATE_FORMAT, Locale.ENGLISH);
		dtfOutputEng = DateTimeFormatter.ofPattern(ProxyConstants.DATE_FORMAT_TO_UTC, Locale.ENGLISH);
		userType = "";
		Map<String, Object> userResponse = null;
		List<Map<String, Object>> allOktaUserResponse = null;
		List<String> mailList = new ArrayList<>();

		List<HashMap<String, Object>> commentResponse = (List<HashMap<String, Object>>) response.get("data");
		for (int i = 0; i < commentResponse.size(); i++) {
			String containesMail = commentResponse.get(i).get("user") != null
					? commentResponse.get(i).get("user").toString()
					: "";
			if (!mailList.contains(containesMail) && !containesMail.isEmpty()) {
				mailList.add(containesMail);
			}
		}
		JSONObject userObject = new JSONObject();
		userObject.put("field", "profile.email");
		userObject.put("values", mailList);
		try {
			userResponse = umsClientService.getUserDetails(httpServletRequest);
			allOktaUserResponse = umsClientService.getOktaUserDetails(httpServletRequest, userObject);
			userType = (String) userResponse.get(ProxyConstants.TITLE);
		} catch (JsonProcessingException e) {
			logger.info(e.getMessage(), e);
		}
		Map<String, Object> dataMap = null;
		JSONObject proxyJsonObject = null;

		request.replace(WidgetConstants.SERVICE_ID, taskServiceId);
		try {
			proxyJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (proxyJsonObject != null) {
			dataMap = (Map<String, Object>) proxyJsonObject.toMap().get(WidgetConstants.DATA);
		}

		if (dataMap == null) {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data", "No data found");
			return nullObject;
		}
		HashMap<String, Object> typeDescriptionResponse = (HashMap<String, Object>) dataMap;
		List<HashMap<String, Object>> commentType = (List<HashMap<String, Object>>) typeDescriptionResponse
				.get(ProxyConstants.COMMENT_TYPE);

		mappingComment(commentType);
		userMap = oktaUserDetails.mappingUserData(allOktaUserResponse);

		JSONArray listForExternalUser;
		JSONArray listForInternalUser;
		if (ProxyConstants.EXTERNAL_USER.equalsIgnoreCase(userType)) {
			listForExternalUser = externalUserRespose(commentResponse);
			callCaseList(listForExternalUser, commentResponse);
			return new JSONObject().put("data", listForExternalUser);
		}
		if (!(ProxyConstants.EXTERNAL_USER.equalsIgnoreCase(userType))) {
			listForInternalUser = internalUserRespose(commentResponse);
			callCaseList(listForInternalUser, commentResponse);
			return new JSONObject().put("data", listForInternalUser);
		}
		if (!(ProxyConstants.EXTERNAL_USER.equalsIgnoreCase(userType))) {
			listForInternalUser = internalUserRespose(commentResponse);
			return new JSONObject().put("data", listForInternalUser);
		}
		return null;
	}

	private JSONArray internalUserRespose(List<HashMap<String, Object>> commentResponse) {
		JSONObject internal = null;

		JSONArray listForInternalUser = new JSONArray();
		for (int i = 0; i < commentResponse.size(); i++) {
			internal = new JSONObject();
			String userMail = null;
			String presentMail = presentMail(commentResponse, i);
			boolean present = userMap.containsKey(presentMail);
			if (presentMail.isEmpty()) {
				continue;
			}
			if (!presentMail.contains("@")) {
				callSSOUser(internal);
			} else {
				userMail = checkCommentTypeAndInactiveUser(commentResponse, i, presentMail, internal, userMail,
						present);
			}
			internal = callInternal(commentResponse, i, internal, userMail);
			listForInternalUser.put(internal);
		}
		return listForInternalUser;
	}

	private String checkCommentTypeAndInactiveUser(List<HashMap<String, Object>> commentResponse, int i,
			String presentMail, JSONObject internal, String userMail, boolean present) {
		String checkCommentType = commentResponse.get(i).get(ProxyConstants.COMMENT_TYPE).toString();
		String createdByIu = userMap.get(presentMail) != null ? userMap.get(presentMail).get(0) : "";
		List<String> mailContaines = new ArrayList<>();
		mailContaines.add(ProxyConstants.FBKFROMCST);
		mailContaines.add(ProxyConstants.FBKFROMBH);
		if ((!present && !mailContaines.contains(checkCommentType))
				|| ProxyConstants.INACTIVE_USER.equalsIgnoreCase(createdByIu)) {
			callInactiveUserCreatedBy(internal, presentMail);
		} else {
			userMail = presentMail;
		}
		internal.put(ProxyConstants.ISSUE_ID,
				commentResponse.get(i).getOrDefault(ProxyConstants.ISSUE_ID, WidgetConstants.EMPTYSTRING));
		String date = commentResponse.get(i).get(ProxyConstants.DINS) != null
				? commentResponse.get(i).get(ProxyConstants.DINS).toString()
				: "";

		date = dateFormater(date);
		internal.put("dIns", date);
		return userMail;
	}

	@SuppressWarnings("unchecked")
	private JSONObject callInternal(List<HashMap<String, Object>> commentResponse, int i, JSONObject internal,
			String userMail) {
		String isTypeCheck = null;
		internal.put(ProxyConstants.COMMENT_ID,
				commentResponse.get(i).get("commentId") != null ? commentResponse.get(i).get("commentId").toString()
						: "");
		String date2 = commentResponse.get(i).get("dAck") != null ? commentResponse.get(i).get("dAck").toString() : "";

		date2 = dateFormater(date2);

		internal.put("dAck", date2);

		callCommentDesc(commentResponse, i, internal);
		mailResponse = (HashMap<String, Object>) commentResponse.get(i).get(ProxyConstants.MAIL_TEXT);
		String mailSent = commentResponse.get(i).get(ProxyConstants.MAIL_SENT) != null
				? commentResponse.get(i).get(ProxyConstants.MAIL_SENT).toString()
				: "";
		String text = commentResponse.get(i).get("text") != null ? commentResponse.get(i).get("text").toString() : "";

		if (text.isEmpty() && mailSent.equalsIgnoreCase("y")) {
			internal.put(ProxyConstants.COMMENT_DESC,
					mailResponse.get(ProxyConstants.HTML_BODY) != null ? mailResponse.get(ProxyConstants.HTML_BODY)
							: "");
		} else {
			internal.put(ProxyConstants.COMMENT_DESC, text);
		}
		isTypeCheck = typeCheck(userMail);
		internal.put(ProxyConstants.USER, userMail);
		internal.put(ProxyConstants.MAIL_SENT,
				commentResponse.get(i).getOrDefault(ProxyConstants.MAIL_SENT, WidgetConstants.EMPTYSTRING).toString());
		internal.put(ProxyConstants.CUSTOMER,
				commentResponse.get(i).getOrDefault(ProxyConstants.CUSTOMER, WidgetConstants.EMPTYSTRING).toString());

		String commentType = commentType(commentResponse, i);
		if (commentMap.containsKey(commentType)) {
			internal.put(ProxyConstants.COMMENT_TYPE, commentMap.get(commentType));
		} else {
			internal.put(ProxyConstants.COMMENT_TYPE, "");
		}
		String jsonCreatedBy = internal.optString(ProxyConstants.CREATED_BY, "");
		String jsonCreatedUserType = internal.optString(ProxyConstants.CREATED_USER_TYPE, "");
		if (jsonCreatedBy.isEmpty() && jsonCreatedUserType.isEmpty()) {
			callCreatedByAndCreatedUserTypeAndMailForActiveInternal(internal, isTypeCheck, userMail, commentType);
		}
		return internal;
	}

	private void callInactiveUserCreatedBy(JSONObject external, String presentMail) {
		String[] checkInactiveBHMail = presentMail.split("@");
		String afterAt = checkInactiveBHMail[1];
		external.put(ProxyConstants.CREATED_BY,
				ProxyConstants.AFTER_AT.equalsIgnoreCase(afterAt) ? ProxyConstants.BAKER_HUGHES_INACTIVE_USER
						: ProxyConstants.CUSTOMER_INACTIVE_USER);
		external.put(ProxyConstants.CREATED_USER_TYPE,
				ProxyConstants.AFTER_AT.equalsIgnoreCase(afterAt) ? ProxyConstants.INTERNAL_USER
						: ProxyConstants.EXTERNAL_USER);
		external.put(ProxyConstants.USER_EMAIL,
				ProxyConstants.AFTER_AT.equalsIgnoreCase(afterAt) ? ProxyConstants.BAKER_HUGHES_INACTIVE_USER
						: ProxyConstants.CUSTOMER_INACTIVE_USER);

	}

	private String typeCheck(String userMail) {
		return userMail != null && userMap.get(userMail) != null ? userMap.get(userMail).get(1) : "";
	}

	private JSONArray externalUserRespose(List<HashMap<String, Object>> commentResponse) {
		JSONObject external = null;
		String customer = null;
		JSONArray listForExternalUser = new JSONArray();
		String userMail = null;
		String presentMail;
		boolean present = false;
		for (int j = 0; j < commentResponse.size(); j++) {
			external = new JSONObject();
			customer = (String) commentResponse.get(j).get(ProxyConstants.CUSTOMER);
			if (ProxyConstants.EXTERNAL.equalsIgnoreCase(customer)) {
				presentMail = presentMail(commentResponse, j);
				if (presentMail.isEmpty()) {
					continue;
				}
				if (!presentMail.contains("@")) {
					callSSOUser(external);
				} else {
					present = userMap.containsKey(presentMail);
					userMail = checkInactiveUserOrNotForExternal(commentResponse, presentMail, external, j, present);
				}
				external = callExternal(commentResponse, j, external, userMail);
				listForExternalUser.put(external);
			}
		}
		return listForExternalUser;
	}

	private String checkInactiveUserOrNotForExternal(List<HashMap<String, Object>> commentResponse, String presentMail,
			JSONObject external, int j, boolean present) {
		String userMail = null;
		String checkCommentType = commentResponse.get(j).get(ProxyConstants.COMMENT_TYPE).toString();
		String createdByIu = userMap.get(presentMail) != null ? userMap.get(presentMail).get(0) : "";
		List<String> mailContaines = new ArrayList<>();
		mailContaines.add(ProxyConstants.FBKFROMCST);
		mailContaines.add(ProxyConstants.FBKFROMBH);
		if ((!present && !mailContaines.contains(checkCommentType))
				|| ProxyConstants.INACTIVE_USER.equalsIgnoreCase(createdByIu)) {
			callInactiveUserCreatedBy(external, presentMail);
		} else {
			userMail = commentResponse.get(j).get(ProxyConstants.USER) != null
					? commentResponse.get(j).get(ProxyConstants.USER).toString().toLowerCase()
					: "";
		}
		return userMail;
	}

	private JSONObject callExternal(List<HashMap<String, Object>> commentResponse, int j, JSONObject external,
			String userMail) {
		String isTypeCheck = null;
		String commentTypes = commentType(commentResponse, j);
		if (commentMap.containsKey(commentTypes)) {
			external.put(ProxyConstants.COMMENT_TYPE, commentMap.get(commentTypes));
		} else {
			external.put(ProxyConstants.COMMENT_TYPE, "");
		}
		external.put(ProxyConstants.ISSUE_ID,
				commentResponse.get(j).getOrDefault(ProxyConstants.ISSUE_ID, WidgetConstants.EMPTYSTRING).toString());
		String date = commentResponse.get(j).get(ProxyConstants.DINS) != null
				? commentResponse.get(j).get(ProxyConstants.DINS).toString()
				: "";
		date = dateFormater(date);
		external.put(ProxyConstants.DINS, date);
		String date2 = commentResponse.get(j).get(ProxyConstants.DACK) != null
				? commentResponse.get(j).get(ProxyConstants.DACK).toString()
				: "";
		date2 = dateFormater(date2);
		external.put(ProxyConstants.DACK, date2);
		external.put(ProxyConstants.USER, userMail);
		external.put(ProxyConstants.CUSTOMER,
				commentResponse.get(j).get(ProxyConstants.CUSTOMER) != null
						? commentResponse.get(j).get(ProxyConstants.CUSTOMER).toString()
						: "");
		external = mailSentOrNot(commentResponse, j, external);
		external.put(ProxyConstants.COMMENT_ID,
				commentResponse.get(j).get(ProxyConstants.COMMENT_ID) != null
						? commentResponse.get(j).get(ProxyConstants.COMMENT_ID)
						: "");
		external.put(ProxyConstants.MAIL_SENT,
				commentResponse.get(j).getOrDefault(ProxyConstants.MAIL_SENT, WidgetConstants.EMPTYSTRING).toString());
		isTypeCheck = typeCheck(userMail);
		String jsonCreatedBy = external.optString(ProxyConstants.CREATED_BY, "");
		String jsonCreatedUserType = external.optString(ProxyConstants.CREATED_USER_TYPE, "");
		if (jsonCreatedBy.isEmpty() && jsonCreatedUserType.isEmpty()) {
			callCreatedByAndCreatedUserTypeAndMailForActiveExternal(external, isTypeCheck, userMail, commentTypes);
		}
		return external;
	}

	private void callCreatedByAndCreatedUserTypeAndMailForActiveExternal(JSONObject external, String isTypeCheck,
			String userMail, String commentTypes) {
		String createdBy = userMail != null && userMap.get(userMail) != null && userMap.get(userMail).get(0) != null
				? userMap.get(userMail).get(0)
				: "";
		external.put(ProxyConstants.CREATED_BY,
				ProxyConstants.INTERNAL_USER.equalsIgnoreCase(isTypeCheck) ? ProxyConstants.BAKER_HUGHES : createdBy);
		if (ProxyConstants.FBKFROMBH.equalsIgnoreCase(commentTypes)) {
			external.put(ProxyConstants.CREATED_BY, ProxyConstants.BAKER_HUGHES);
			external.put(ProxyConstants.CREATED_USER_TYPE, ProxyConstants.INTERNAL_USER);
			external.put(ProxyConstants.SHARED_EMAIL, ProxyConstants.BAKER_HUGHES);
			external.put(ProxyConstants.USER_EMAIL, ProxyConstants.BAKER_HUGHES);
		} else if (ProxyConstants.FBKFROMCST.equalsIgnoreCase(commentTypes)) {
			external.put(ProxyConstants.CREATED_BY, userMail != null ? callSharedEmail(userMail) : "");
			external.put(ProxyConstants.CREATED_USER_TYPE, ProxyConstants.EXTERNAL_USER);
			external.put(ProxyConstants.SHARED_EMAIL, userMail != null ? callSharedEmail(userMail) : "");
			external.put(ProxyConstants.USER_EMAIL, userMail);
		} else {
			external.put(ProxyConstants.CREATED_USER_TYPE, isTypeCheck);
			external.put(ProxyConstants.USER_EMAIL,
					ProxyConstants.INTERNAL_USER.equalsIgnoreCase(isTypeCheck) ? ProxyConstants.BAKER_HUGHES
							: userMail);
		}
	}

	private void callCreatedByAndCreatedUserTypeAndMailForActiveInternal(JSONObject external, String isTypeCheck,
			String userMail, String commentTypes) {
		String createdBy = userMail != null && userMap.get(userMail) != null && userMap.get(userMail).get(0) != null
				? userMap.get(userMail).get(0)
				: "";
		external.put(ProxyConstants.CREATED_BY, createdBy);
		if (ProxyConstants.FBKFROMBH.equalsIgnoreCase(commentTypes)) {
			external.put(ProxyConstants.CREATED_BY, userMail != null ? callSharedEmail(userMail) : "");
			external.put(ProxyConstants.CREATED_USER_TYPE, ProxyConstants.INTERNAL_USER);
			external.put(ProxyConstants.SHARED_EMAIL, userMail != null ? callSharedEmail(userMail) : "");
			external.put(ProxyConstants.USER_EMAIL, userMail);
		} else if (ProxyConstants.FBKFROMCST.equalsIgnoreCase(commentTypes)) {
			external.put(ProxyConstants.CREATED_BY, userMail != null ? callSharedEmail(userMail) : "");
			external.put(ProxyConstants.CREATED_USER_TYPE, ProxyConstants.EXTERNAL_USER);
			external.put(ProxyConstants.SHARED_EMAIL, userMail != null ? callSharedEmail(userMail) : "");
			external.put(ProxyConstants.USER_EMAIL, userMail);
		} else {
			external.put(ProxyConstants.CREATED_USER_TYPE, isTypeCheck);
			external.put(ProxyConstants.USER_EMAIL, userMail);
		}
	}

	private String presentMail(List<HashMap<String, Object>> commentResponse, int j) {
		return commentResponse.get(j).get(ProxyConstants.USER) != null
				? commentResponse.get(j).get(ProxyConstants.USER).toString().toLowerCase()
				: "";
	}

	private String commentType(List<HashMap<String, Object>> commentResponse, int j) {
		return commentResponse.get(j).get(ProxyConstants.COMMENT_TYPE) != null
				? commentResponse.get(j).get(ProxyConstants.COMMENT_TYPE).toString().toLowerCase()
				: "";
	}

	private String callSharedEmail(String presentMail) {
		String[] mail = null;
		if (!presentMail.isEmpty()) {
			mail = presentMail.split("@");
		}
		if (mail != null) {
			return mail[0];
		} else {
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject mailSentOrNot(List<HashMap<String, Object>> commentResponse, int j, JSONObject external) {
		mailResponse = (HashMap<String, Object>) commentResponse.get(j).get(ProxyConstants.MAIL_TEXT);
		String mailSent = commentResponse.get(j).get(ProxyConstants.MAIL_SENT) != null
				? commentResponse.get(j).get(ProxyConstants.MAIL_SENT).toString()
				: "";
		String text = commentResponse.get(j).get(ProxyConstants.TEXT) != null
				? commentResponse.get(j).get(ProxyConstants.TEXT).toString()
				: "";

		if (text.isEmpty() && mailSent.equalsIgnoreCase("y")) {
			external.put(ProxyConstants.COMMENT_DESC,
					mailResponse.get(ProxyConstants.HTML_BODY) != null ? mailResponse.get(ProxyConstants.HTML_BODY)
							: "");
		} else {
			external.put(ProxyConstants.COMMENT_DESC, text);
		}
		return external;
	}

	@SuppressWarnings("unchecked")
	private void callCommentDesc(List<HashMap<String, Object>> commentResponse, int j, JSONObject result) {
		mailResponse = (HashMap<String, Object>) commentResponse.get(j).get(ProxyConstants.MAIL_TEXT);
		String mailSent = commentResponse.get(j).get(ProxyConstants.MAIL_SENT) != null
				? commentResponse.get(j).get(ProxyConstants.MAIL_SENT).toString()
				: "";
		String text = commentResponse.get(j).get("text") != null ? commentResponse.get(j).get("text").toString() : "";

		if (text.isEmpty() && mailSent.equalsIgnoreCase("y")) {
			result.put(ProxyConstants.COMMENT_DESC,
					mailResponse.get(ProxyConstants.HTML_BODY) != null ? mailResponse.get(ProxyConstants.HTML_BODY)
							: "");
		} else {
			result.put(ProxyConstants.COMMENT_DESC, text);
		}
	}

	private String dateFormater(String date) {
		LocalDateTime date1 = null;
		if (!(date == null || date.equals(""))) {
			date1 = LocalDateTime.parse(date, dtfInput);
			date = dtfOutputEng.format(date1);
		}
		return date;
	}

	private void mappingComment(List<HashMap<String, Object>> commentType) {
		for (int i = 0; i < commentType.size(); i++) {
			commentMap.put(commentType.get(i).get(ProxyConstants.ID).toString().toLowerCase(),
					commentType.get(i).get(ProxyConstants.COMMENT_TYPES) != null
							? commentType.get(i).get(ProxyConstants.COMMENT_TYPES).toString()
							: "");
		}
	}

	private void callSSOUser(JSONObject result) {
		result.put(ProxyConstants.CREATED_BY, ProxyConstants.BAKER_HUGHES);
		result.put(ProxyConstants.CREATED_USER_TYPE, ProxyConstants.INTERNAL_USER);
		result.put(ProxyConstants.USER_EMAIL, ProxyConstants.BAKER_HUGHES);
	}

	private void callCaseList(JSONArray listForExternalUser, List<HashMap<String, Object>> commentResponse) {
		caseNoList = new ArrayList<>();
		JSONObject caseListJsonObject = null;
		for (int i = 0; i < commentResponse.size(); i++) {
			String containesIssueId = commentResponse.get(i).get("issueId") != null
					? commentResponse.get(i).get("issueId").toString()
					: "";
			if (!caseNoList.contains(containesIssueId)) {
				caseNoList.add(containesIssueId);
				Map<String, Object> request = new HashMap<>();
				request.put("serviceId", caseListId);
				request.put("caseNumber", containesIssueId);
				List<String> list = new ArrayList<>();
				list.add(ProxyConstants.CASE_STATUS_CLOSE);
				request.put(ProxyConstants.CASELISTSTATUS, list);
				try {
					caseListJsonObject = (JSONObject) proxyService.execute(request, httpServletRequest);
					JSONArray array = new JSONArray();
					array.put(caseListJsonObject.get(WidgetConstants.DATA));
					if (caseListJsonObject.get(WidgetConstants.DATA) != JSONObject.NULL) {
						callCaseCloser(listForExternalUser, array);
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	private void callCaseCloser(JSONArray caseClosureMap, JSONArray array) {
		Map<String, Object> map = new HashMap<>();
		int i = 0;
		String data = array.getJSONObject(i).get("list").toString();
		JSONArray jsonArray = new JSONArray(data);
		for (Object mapNode : jsonArray) {
			JSONObject obj = (JSONObject) mapNode;
			map.put(ProxyConstants.CASENO, obj.optString(ProxyConstants.CASENO, ""));
			map.put(ProxyConstants.CLOSEDDATE, obj.optString(ProxyConstants.CLOSEDDATE, ""));
			map.put(ProxyConstants.DINS, obj.optString(ProxyConstants.CLOSEDDATE, ""));
			map.put(ProxyConstants.COMMENT_ID, obj.optString("commentIds", ""));
			map.put(ProxyConstants.COMMENT_DESC, "Case Closed Since " + obj.optString(ProxyConstants.CLOSEDDATE, ""));
			map.put(ProxyConstants.ISSUE_ID, obj.optString(ProxyConstants.CASENO, ""));
		}
		map.put(ProxyConstants.CREATED_BY, ProxyConstants.BAKER_HUGHES);
		map.put(ProxyConstants.USER_EMAIL, ProxyConstants.BAKER_HUGHES);
		map.put(ProxyConstants.CREATED_USER_TYPE, ProxyConstants.INTERNAL_USER);
		map.put(ProxyConstants.COMMENT_TYPE, commentMap.get(ProxyConstants.CASECLOSE));
		map.put(ProxyConstants.CUSTOMER, ProxyConstants.INTERNAL);
		caseClosureMap.put(map);
	}
}