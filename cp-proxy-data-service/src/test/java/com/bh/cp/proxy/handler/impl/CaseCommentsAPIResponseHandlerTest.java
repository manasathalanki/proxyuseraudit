package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.helper.OktaUserDetails;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;

class CaseCommentsAPIResponseHandlerTest {

	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private UMSClientService umsClientService;
	@Mock
	private ProxyService proxyService;
	@Mock
	private OktaUserDetails oktaUserDetails;

	@InjectMocks
	private CaseCommentsAPIResponseHandler<?> caseCommentsAPIResponseHandler;

	List<HashMap<String, Object>> list = new ArrayList<>();

	Map<String, Object> request = new HashMap<>();
	List<Map<String, Object>> reposnseList = new ArrayList<>();
	Map<String, Object> responseMap;
	List<Map<String, Object>> typeDescriptionList = new ArrayList<>();
	JSONObject typedsci = new JSONObject();
	JSONObject data = new JSONObject();
	Map<String, Object> InternalUserResponse = new HashMap<>();
	Map<String, Object> ExternalUserResponse = new HashMap<>();
	String valuesApiResponse;
	String actualValue;
	JSONObject userObject = new JSONObject();
	Map<String, List<String>> userMap;
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		HashMap<String, Object> mailResponse = new HashMap<String, Object>();
		mailResponse.put("textBody", null);
		mailResponse.put("htmlBody", "body");
		HashMap<String, Object> map = new HashMap<>();
		map.put("issueId", "166760808");
		map.put("customer", "Y");
		map.put("user", "ku.derrico@bakerhughes.com");
		map.put("commentType", "FIRSTCOMM");
		map.put("mailSent", "Y");
		map.put("mailText", mailResponse);
		map.put("dIns", "2023-12-11T16:31:58.000Z");
		map.put("dAck", "2023-12-21T09:21:06.000Z");
		map.put("commentId", 229);
		map.put("text", null);
		map.put("COMMENT_DESC", "desc");
		list.add(map);
		map = new HashMap<>();
		map.put("issueId", "166760808");
		map.put("customer", "Y");
		map.put("user", "kumuda.kurli@bakerhughes.com");
		map.put("commentType", "FBKFROMBH");
		map.put("mailSent", "Y");
		map.put("mailText", mailResponse);
		map.put("dIns", "2023-12-11T16:31:58.000Z");
		map.put("dAck", "2023-12-21T09:21:06.000Z");
		map.put("commentId", 229);
		map.put("text", "Desc");
		map.put("COMMENT_DESC", "desc");
		list.add(map);

		request.put("issueId", "166761147");

		Map<String, Object> profile = new HashMap<>();
		profile.put("firstName", "Kumuda");
		profile.put("lastName", "Kurli");
		profile.put("login", "Kumuda.Kurli@bakerhughes.com");
		profile.put("email", "Kumuda.Kurli@bakerhughes.com");

		Map<String, Object> auth = new HashMap<>();
		auth.put("id", "44af77f2-c9ae-4023-964d-d5861e124c28");
		auth.put("name", "Kumuda");
		auth.put("email", "kumuda.kurli@bakerhughes.com");
		auth.put("userName", "kumuda.kurli@bakerhughes.com");
		auth.put("surName", "Kurli");
		auth.put("enabled", "Y");
		auth.put("title", "INTERNAL");
		auth.put("status", "ACTIVE");

		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.DATA, list);
		responseMap = new HashMap<>();
		reposnseList = new ArrayList<>();
		responseMap.put("id", "00ux4emntrZ25fmNr357");
		responseMap.put("status", "ACTIVE");
		responseMap.put("profile", profile);
		responseMap.put("auth", auth);
		reposnseList.add(responseMap);

		profile = new HashMap<>();
		profile.put("firstName", "Liliana");
		profile.put("lastName", "DErrico");
		profile.put("login", "Liliana.DErrico@bakerhughes.com");
		profile.put("email", "Liliana.DErrico@bakerhughes.com");

		responseMap = new HashMap<>();
		responseMap.put("id", "00uhzvvu6960ZtHyB357");
		responseMap.put("status", "ACTIVE");
		responseMap.put("profile", profile);
		reposnseList.add(responseMap);
		Map<String, Object> typeDescriptionMap = new HashMap<>();
		typeDescriptionMap.put("id", "FIRSTCOMM");
		typeDescriptionMap.put("comment_type", "First communication");
		typeDescriptionMap.put("description", "First communication to Customer");
		typeDescriptionMap.put("for_customer", "Y");
		typeDescriptionList.add(typeDescriptionMap);
		typeDescriptionMap = new HashMap<>();
		typeDescriptionMap.put("id", "EBSCHG");
		typeDescriptionMap.put("comment_type", "EBS changed");
		typeDescriptionMap.put("description",
				"EBS has been changed from decision tree during (event/early warning validation**) or ***EBS has been changed");
		typeDescriptionMap.put("for_customer", "N");
		typeDescriptionList.add(typeDescriptionMap);
		typedsci.put("commentType", typeDescriptionList);
		ExternalUserResponse.put("title", "EXTERNAL");
		InternalUserResponse.put("title", "INTERNAL");
		data.put("data", typedsci);

		List<Object> mailList = new ArrayList<>();
		mailList.add("Liliana.DErrico@bakerhughes.com");
		mailList.add("kumuda.kurli@bakerhughes.com");
		mailList.add("matteo.distort@bakerhughes.com");
		mailList.add("Riccardo.Giorgetti@bakerhughes.com");
		mailList.add("maurice.paquette@bakerhughes.com");
		userObject.put("field", "profile.email");
		userObject.put("values", mailList);

		userMap = new HashMap<>();
		List<String> details=new ArrayList<>();
		details.add("Liliana Derrico");
		details.add("EXTERNAL");
		userMap.put("liliana.derrico@bakerhughes.com", details);
		
		details=new ArrayList<>();
		details.add("Liliana Derrico");
		details.add("EXTERNAL");
		userMap.put("liliana.derrico@bakerhughes.com", details);
		
		details=new ArrayList<>();
		details.add("Maurice Paquette");
		details.add("EXTERNAL");
		userMap.put("maurice.paquette@bakerhughes.com", details);
		
		details=new ArrayList<>();
		details.add("Kumuda Kurli");
		details.add("INTERNAL");
		userMap.put("kumuda.kurli@bakerhughes.com", details);
		
		ReflectionTestUtils.setField(caseCommentsAPIResponseHandler, "t", response);
		ReflectionTestUtils.setField(caseCommentsAPIResponseHandler, "httpServletRequest", httpServletRequest);
		ReflectionTestUtils.setField(caseCommentsAPIResponseHandler, "umsClientService", umsClientService);
		ReflectionTestUtils.setField(caseCommentsAPIResponseHandler, "proxyService", proxyService);
		ReflectionTestUtils.setField(caseCommentsAPIResponseHandler, "oktaUserDetails", oktaUserDetails);
		ReflectionTestUtils.setField(caseCommentsAPIResponseHandler, "taskServiceId", 48);
	}

	private static class TestableCaseCommentsAPIResponseHandler
			extends CaseCommentsAPIResponseHandler<Map<String, Object>> {
		public TestableCaseCommentsAPIResponseHandler(HttpServletRequest httpServletRequest, ProxyService proxyService,
				UMSClientService umsClientService, OktaUserDetails oktaUserDetails,Integer taskServiceId,Integer caseListId) {
			super(httpServletRequest,proxyService,umsClientService,oktaUserDetails, taskServiceId, caseListId);
		}

		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output when user is external")
	void testParseInternalExternal() throws Exception {
		when(proxyService.execute(any(Map.class),eq(httpServletRequest)))
		.thenReturn(data); 
		when(umsClientService.getUserDetails(httpServletRequest)) 
		.thenReturn(ExternalUserResponse);
		when(umsClientService.getOktaUserDetails(eq(httpServletRequest),any(JSONObject.class)))
		.thenReturn(reposnseList);
		when(oktaUserDetails.mappingUserData(reposnseList)).thenReturn(userMap);
		JSONObject result = (JSONObject) caseCommentsAPIResponseHandler.parse(request);
		JSONArray openCasesArray = result.getJSONArray(WidgetConstants.DATA);
		assertEquals(list.size(), openCasesArray.length());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output when user is internal")
	void testParseInternal() throws Exception {
		when(proxyService.execute(any(Map.class), eq(httpServletRequest)))
		.thenReturn(data);
		when(umsClientService.getOktaUserDetails(eq(httpServletRequest),any(JSONObject.class)))
		.thenReturn(reposnseList);
		when(umsClientService.getUserDetails(httpServletRequest))
		.thenReturn(InternalUserResponse);
		when(oktaUserDetails.mappingUserData(reposnseList)).thenReturn(userMap);
		JSONObject result = (JSONObject) caseCommentsAPIResponseHandler.parse(request);
		JSONArray openCasesArray = result.getJSONArray(WidgetConstants.DATA);
		assertEquals(list.size(), openCasesArray.length()); 	
	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableCaseCommentsAPIResponseHandler handler = new TestableCaseCommentsAPIResponseHandler(httpServletRequest, proxyService, umsClientService, oktaUserDetails, 1,1);
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		request.put("issueId", "16676147,16676113");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Resources Found In the Input But Length is Zero")
	void testParseLevel_ResourcesLengthZero() {
		TestableCaseCommentsAPIResponseHandler handler = new TestableCaseCommentsAPIResponseHandler(httpServletRequest, proxyService, umsClientService, oktaUserDetails, 1,1);
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		request.put("issueId", "16676147");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testProtectedConstructor() throws Exception {
		TestableCaseCommentsAPIResponseHandler handler = new TestableCaseCommentsAPIResponseHandler(httpServletRequest, proxyService, umsClientService, oktaUserDetails, 1,1);
		assertNotNull(handler);
	}
}