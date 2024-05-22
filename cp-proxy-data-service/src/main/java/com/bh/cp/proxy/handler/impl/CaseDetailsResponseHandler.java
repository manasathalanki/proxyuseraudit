package com.bh.cp.proxy.handler.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import net.minidev.json.JSONArray;

@Component
public class CaseDetailsResponseHandler<T> extends JsonResponseHandler<T> {

	private HttpServletRequest httpServletRequest;

	private UMSClientService umsClientService;

	private OktaUserDetails oktaUserDetails;

	Map<String, List<String>> userMap;
	private String checkLoginUserType = null;
	List<String> mailList = new ArrayList<>();
	List<String> samAccountNameList = new ArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(CaseDetailsResponseHandler.class);

	@Autowired
	@SuppressWarnings("unchecked")
	public CaseDetailsResponseHandler(HttpServletRequest httpServletRequest, UMSClientService umsClientService,
			OktaUserDetails oktaUserDetails) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.umsClientService = umsClientService;
		this.oktaUserDetails = oktaUserDetails;

	}

	JSONArray caseDetailsList = new JSONArray();
	LocalDateTime date1 = null;
	DateTimeFormatter dtfInput;
	DateTimeFormatter dtfOutputEng;
	String commentedUserType = "";

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		mailList = new ArrayList<>();
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		if (response == null) {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data", "No data found");
			return nullObject;
		}
		prepareMailAndSamAccountNameList(response);
		dtfInput = DateTimeFormatter.ofPattern(ProxyConstants.CASE_TREND_DATE_FORMAT, Locale.ENGLISH);
		dtfOutputEng = DateTimeFormatter.ofPattern(ProxyConstants.DATE_FORMAT_TO_UTC, Locale.ENGLISH);
		if (response.get(ProxyConstants.ATTACHMENTS) != null) {
			List<HashMap<String, Object>> attachmentsResponse = (List<HashMap<String, Object>>) response
					.get(ProxyConstants.ATTACHMENTS);
			callCaseDeatilsResponse(attachmentsResponse, ProxyConstants.ATTACHMENTS);
		}
		if (response.get(ProxyConstants.NOTIFICATIONS) != null) {
			List<HashMap<String, Object>> notificationsResponse = (List<HashMap<String, Object>>) response
					.get(ProxyConstants.NOTIFICATIONS);
			callCaseDeatilsResponse(notificationsResponse, ProxyConstants.NOTIFICATIONS);
		}
		if (response.get(ProxyConstants.TRENDS) != null) {
			List<HashMap<String, Object>> trendsResponse = (List<HashMap<String, Object>>) response
					.get(ProxyConstants.TRENDS);
			callCaseDeatilsResponse(trendsResponse, ProxyConstants.TRENDS);
		}
		return new JSONObject().put("data", caseDetailsList);
	}

	private void prepareMailAndSamAccountNameList(HashMap<String, Object> response) {
		Map<String, Object> loginUserDetails = null;
		caseDetailsList = new JSONArray();
		List<Map<String, Object>> oktaUserResponseMail;
		List<Map<String, Object>> oktaUserResponseSam;
		prepareMailListToCallUserMng(response);
		JSONObject userObjectMail = new JSONObject();
		JSONObject userObjectSam = new JSONObject();
		userObjectMail.put("field", "profile.email");
		userObjectMail.put("values", mailList);
		userObjectSam.put("field", "profile.samAccountName");
		userObjectSam.put("values", samAccountNameList);
		try {
			loginUserDetails = umsClientService.getUserDetails(httpServletRequest);
			checkLoginUserType = (String) loginUserDetails.get(ProxyConstants.TITLE);
			oktaUserResponseMail = umsClientService.getOktaUserDetails(httpServletRequest, userObjectMail);
			oktaUserResponseSam = umsClientService.getOktaUserDetails(httpServletRequest, userObjectSam);
			List<Map<String, Object>> combinedList = new ArrayList<>();
			combinedList.addAll(oktaUserResponseMail);
			combinedList.addAll(oktaUserResponseSam);
			userMap = oktaUserDetails.mappingUserData(combinedList);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}

	}

	private void prepareMailListToCallUserMng(HashMap<String, Object> response) {
		List<String> iterationList = new ArrayList<>();
		iterationList.add(ProxyConstants.NOTIFICATIONS);
		iterationList.add(ProxyConstants.TRENDS);
		iterationList.add(ProxyConstants.ATTACHMENTS);
		for (int i = 0; i < response.size() - 1; i++) {
			@SuppressWarnings("unchecked")
			List<HashMap<String, Object>> responseData = (List<HashMap<String, Object>>) response
					.get(iterationList.get(i));
			mappingEmailList(responseData, i);
		}
	}

	private void callCaseDeatilsResponse(List<HashMap<String, Object>> response, String responseType) {
		for (int i = 0; i < response.size(); i++) {
			JSONObject map = new JSONObject();
			String presentMail = presentMail(response, responseType, i);
			boolean present = userMap.containsKey(presentMail) && !presentMail.isEmpty();
			if (presentMail.isEmpty() || presentMail.equalsIgnoreCase(ProxyConstants.AUTOMATIC_EMAIL)) {
				continue;
			}
			if (!presentMail.contains("@")) {
				callSSOUser(map);
			} else {
				callCaseDeatailsInactiveUser(present, presentMail, map);
			}
			ifActiveUser(map, presentMail);
			if (ProxyConstants.NOTIFICATIONS.equalsIgnoreCase(responseType)) {
				notificationResponse(response, map, i);
			} else if (ProxyConstants.TRENDS.equalsIgnoreCase(responseType)) {
				trendResponse(response, map, i);
			} else if (ProxyConstants.ATTACHMENTS.equalsIgnoreCase(responseType)) {
				attachmentsResponse(response, map, i);
			}
		}
	}

	private void callSSOUser(JSONObject map) {
		map.put(ProxyConstants.CREATED_BY, ProxyConstants.BAKER_HUGHES);
		map.put(ProxyConstants.CREATED_USER_TYPE, ProxyConstants.INTERNAL_USER);
		map.put(ProxyConstants.USER_EMAIL, ProxyConstants.BAKER_HUGHES);
	}

	private void callCaseDeatailsInactiveUser(boolean present, String presentMail, JSONObject map) {
		String createdBy = userMap.get(presentMail) != null ? userMap.get(presentMail).get(0) : "";
		if (!present || ProxyConstants.INACTIVE_USER.equalsIgnoreCase(createdBy) || !presentMail.contains("@")) {
			callCreatedByForExternalUser(presentMail, map);
		}
	}

	private void ifActiveUser(JSONObject map, String mail) {
		String jsonCreatedBy = !map.has(ProxyConstants.CREATED_BY) ? "" : ProxyConstants.NOT_EMPTY;
		String jsonCreatedUserType = !map.has(ProxyConstants.CREATED_USER_TYPE) ? "" : ProxyConstants.NOT_EMPTY;
		if (jsonCreatedBy.isEmpty() && jsonCreatedUserType.isEmpty()) {
			createBy(mail, map);
			callUserMail(mail, map);
		}
	}

	private void trendResponse(List<HashMap<String, Object>> trendsResponse, JSONObject map, int i) {
		map.put(ProxyConstants.COMMENT_ID,
				trendsResponse.get(i).getOrDefault(ProxyConstants.ISSUE_ID, WidgetConstants.EMPTYSTRING).toString());
		map.put(ProxyConstants.COMMENT_DESC,
				trendsResponse.get(i).get(ProxyConstants.IMAGE) != null
						? trendsResponse.get(i).get(ProxyConstants.IMAGE).toString()
						: "");
		map.put(ProxyConstants.COMMENT_TYPE, ProxyConstants.COMMENT_TREAND);
		map.put(ProxyConstants.ISSUE_ID,
				trendsResponse.get(i).getOrDefault(ProxyConstants.ISSUE_ID, WidgetConstants.EMPTYSTRING).toString());
		map.put(ProxyConstants.NAME_LINK,
				trendsResponse.get(i).get(ProxyConstants.NAME_LINK) != null
						? trendsResponse.get(i).get(ProxyConstants.NAME_LINK)
						: "");
		map.put(ProxyConstants.TREND_LINK,
				trendsResponse.get(i).get(ProxyConstants.TREND_LINK) != null
						? trendsResponse.get(i).get(ProxyConstants.TREND_LINK)
						: "");
		String date = trendsResponse.get(i).getOrDefault(ProxyConstants.D_INS, WidgetConstants.EMPTYSTRING).toString();
		setDate(date, map);
		caseDetailsList.add(map);
	}

	private void notificationResponse(List<HashMap<String, Object>> notificationsResponse, JSONObject map, int i) {
		Map<String, String> mailDescMap = new HashMap<>();
		mailDescMap.put("DMLS2", ProxyConstants.DMLS2);
		mailDescMap.put("CASE", ProxyConstants.CASE);
		mailDescMap.put("FASTCOMM", ProxyConstants.FASTCOMM);
		mailDescMap.put("BOP", ProxyConstants.BOP);
		mailDescMap.put("FPDDE", ProxyConstants.FPDDE);
		mailDescMap.put("ESCN_DDEFP", ProxyConstants.ESCN_DDEFP);
		String userEmail = map.get(ProxyConstants.USER_EMAIL).toString();
		if (!userEmail.equalsIgnoreCase(ProxyConstants.AUTOMATIC_EMAIL) && !userEmail.isEmpty()) {
			mailDescMap(map, mailDescMap, i, notificationsResponse);
			map.put(ProxyConstants.COMMENT_ID,
					notificationsResponse.get(i).getOrDefault(ProxyConstants.COMMENT_ID, WidgetConstants.EMPTYSTRING));
			String date = notificationsResponse.get(i)
					.getOrDefault(ProxyConstants.DATE_SENT, WidgetConstants.EMPTYSTRING).toString();
			setDate(date, map);
			map.put(ProxyConstants.COMMENT_VISIBILITY, "Y");
			map.put(ProxyConstants.COMMENT_TYPE, ProxyConstants.COMMENT_NOTIFICATION);
			map.put("IssueId",
					notificationsResponse.get(i).get(ProxyConstants.ISSUE_ID) != null
							? notificationsResponse.get(i).get(ProxyConstants.ISSUE_ID).toString()
							: "");
			caseDetailsList.add(map);
		}
	}

	private void attachmentsResponse(List<HashMap<String, Object>> attachmentsResponse, JSONObject map, int i) {
		map.put(ProxyConstants.ATTACH_ID,
				attachmentsResponse.get(i).get(ProxyConstants.ATTACH_ID) != null
						? attachmentsResponse.get(i).get(ProxyConstants.ATTACH_ID).toString()
						: "");
		map.put(ProxyConstants.COMMENT_VISIBILITY, ProxyConstants.COMMENT_VISIBILITY_VALUE);
		map.put(ProxyConstants.COMMENT_TYPE, ProxyConstants.COMMENT_FILEUPLOAD);
		map.put(ProxyConstants.ISSUE_ID,
				attachmentsResponse.get(i).get(ProxyConstants.ISSUE_ID) != null
						? attachmentsResponse.get(i).get(ProxyConstants.ISSUE_ID).toString()
						: "");
		map.put(ProxyConstants.COMMENT_ID,
				attachmentsResponse.get(i).getOrDefault(ProxyConstants.ATTACH_ID, WidgetConstants.EMPTYSTRING));
		map.put(ProxyConstants.COMMENT_DESC,
				attachmentsResponse.get(i).get(ProxyConstants.FILE_NAME) != null
						? attachmentsResponse.get(i).get(ProxyConstants.FILE_NAME).toString()
						: "");
		String date = attachmentsResponse.get(i)
				.getOrDefault(ProxyConstants.UPLOAD_TIME_STAMP, WidgetConstants.EMPTYSTRING).toString();
		setDate(date, map);
		caseDetailsList.add(map);
	}

	private void callCreatedByForExternalUser(String presentMail, JSONObject map) {
		String[] checkInactiveBHMail = presentMail.split("@");
		String afterAt = checkInactiveBHMail[1];
		map.put(ProxyConstants.CREATED_BY,
				ProxyConstants.AFTER_AT.equalsIgnoreCase(afterAt) ? ProxyConstants.BAKER_HUGHES_INACTIVE_USER
						: ProxyConstants.CUSTOMER_INACTIVE_USER);
		map.put(ProxyConstants.CREATED_USER_TYPE,
				ProxyConstants.AFTER_AT.equalsIgnoreCase(afterAt) ? ProxyConstants.INTERNAL_USER
						: ProxyConstants.EXTERNAL_USER);
		map.put(ProxyConstants.USER_EMAIL,
				ProxyConstants.AFTER_AT.equalsIgnoreCase(afterAt) ? ProxyConstants.BAKER_HUGHES_INACTIVE_USER
						: ProxyConstants.CUSTOMER_INACTIVE_USER);
	}

	private void mailDescMap(JSONObject map, Map<String, String> mailDescMap, int i,
			List<HashMap<String, Object>> notificationsResponse) {
		String key = notificationsResponse.get(i).get("mailType") != null
				? notificationsResponse.get(i).get("mailType").toString()
				: "";
		map.put(ProxyConstants.COMMENT_DESC, mailDescMap.get(key) != null ? mailDescMap.get(key) : "");
	}

	private void setDate(String date, JSONObject map) {
		if (!(date == null || date.equals(""))) {
			date1 = LocalDateTime.parse(date, dtfInput);
			date = dtfOutputEng.format(date1);
		}
		map.put("dIns", date);
	}

	void createBy(String mail, JSONObject map) {
		String commentCreatedBy = userMap.get(mail) != null ? userMap.get(mail).get(0) : "";
		commentedUserType = userMap.get(mail) != null && userMap.get(mail).get(1) != null ? userMap.get(mail).get(1)
				: "";
		map.put(ProxyConstants.CREATED_BY,
				(commentedUserType != null && commentedUserType.equalsIgnoreCase(ProxyConstants.INTERNAL_USER)
						&& checkLoginUserType.equalsIgnoreCase(ProxyConstants.EXTERNAL_USER))
								? ProxyConstants.BAKER_HUGHES
								: commentCreatedBy);
		map.put(ProxyConstants.USER_EMAIL,
				(commentedUserType != null && commentedUserType.equalsIgnoreCase(ProxyConstants.INTERNAL_USER)
						&& checkLoginUserType.equalsIgnoreCase(ProxyConstants.EXTERNAL_USER))
								? ProxyConstants.BAKER_HUGHES
								: mail);
	}

	private void callUserMail(String mail, JSONObject map) {
		commentedUserType = userMap.get(mail) != null && userMap.get(mail).get(1) != null ? userMap.get(mail).get(1)
				: "";
		map.put(ProxyConstants.CREATED_USER_TYPE, commentedUserType);
	}

	private void mappingEmailList(List<HashMap<String, Object>> commentResponse, int k) {
		for (int i = 0; i < commentResponse.size(); i++) {
			String containesMail;
			if (k != 0) {
				containesMail = commentResponse.get(i).get(ProxyConstants.USER) != null
						? commentResponse.get(i).get(ProxyConstants.USER).toString()
						: "";
			} else {
				containesMail = commentResponse.get(i).get(ProxyConstants.USER_EMAIL) != null
						? commentResponse.get(i).get(ProxyConstants.USER_EMAIL).toString()
						: "";
			}
			addMailInFinalList(containesMail);
		}
	}

	private void addMailInFinalList(String containesMail) {
		final Pattern validEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = validEmail.matcher(containesMail);
		boolean validMail = matcher.matches();
		if (!mailList.contains(containesMail) && !containesMail.isEmpty() && validMail) {
			mailList.add(containesMail);
		}
		if (!validMail && !containesMail.matches("\\d+") && !samAccountNameList.contains(containesMail)) {
			samAccountNameList.add(containesMail);
		}
	}

	private String presentMail(List<HashMap<String, Object>> response, String responseType, int i) {
		String userEmail = (String) response.get(i).get(ProxyConstants.USER_EMAIL);
		String user = (String) response.get(i).get(ProxyConstants.USER);
		if (userEmail == null && user == null) {
			return "";
		}
		return responseType.equalsIgnoreCase(ProxyConstants.NOTIFICATIONS)
				? response.get(i).get(ProxyConstants.USER_EMAIL).toString().toLowerCase()
				: response.get(i).get(ProxyConstants.USER).toString().toLowerCase();
	}
}