package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import jakarta.servlet.http.HttpServletRequest;

class CaseDetailsResponseHandlerTest {

	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private UMSClientService umsClientService;
	@Mock
	private OktaUserDetails oktaUserDetails;
	@InjectMocks
	private CaseDetailsResponseHandler<?> caseDetailsResponseHandler;
	Map<String,List<HashMap<String, Object>>> list = new HashMap<>();
	List<HashMap<String, Object>> singleList=new ArrayList<>();
	List<Map<String,Object>> reposnseList=new ArrayList<>(); 
	Map<String, Object> request = new HashMap<>();
	Map<String, List<String>> userMap;
	
	String valuesApiResponse;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		HashMap<String, Object> treandMap = new HashMap<>();
		treandMap.put("issueId", "166760808");
		treandMap.put("nameLink", "treend"); 
		treandMap.put("user", "mahaveer.penna@bakerhughes.com");
		treandMap.put("createdBy", "Mahaveer");
		treandMap.put("dIns", "2023-02-27T14:28:01.000Z");
		singleList.add(treandMap);
		list.put("attachments", singleList);
		
		singleList=new ArrayList<>();
		HashMap<String, Object> notiMap = new HashMap<>();
		notiMap.put("issueId", "166760808");
		notiMap.put("nameLink", "treend");
		notiMap.put("userEmail", "mahaveer.penna@bakerhughes.com");
		notiMap.put("dateSent", "2023-02-27T14:28:01.000Z");
		notiMap.put("createdBy", "Mahaveer");
		notiMap.put("dIns", "2023-02-27T14:28:01.000Z");
		singleList.add(notiMap);
		list.put("notifications", singleList);

		singleList=new ArrayList<>();
		HashMap<String, Object> attachMap = new HashMap<>();
		attachMap.put("issueId", "166760808");
		attachMap.put("nameLink", "treend");
		attachMap.put("user", "mahaveer.penna@bakerhughes.com");
		attachMap.put("uploadTimestamp", "2023-02-27T14:28:01.000Z");
		attachMap.put("createdBy", "Mahaveer");
		attachMap.put("dIns", "2023-02-27T14:28:01.000Z");
		singleList.add(attachMap);
		list.put("trends", singleList);

		request.put("issueId", "166760808");
		Map<String, Object> trensResponse = new HashMap<>();
		Map<String, Object> NotiResponse = new HashMap<>();
		Map<String, Object> attchResponse = new HashMap<>();
		trensResponse.put(WidgetConstants.TREANDS, list);
		NotiResponse.put(WidgetConstants.NOTIFICATION, list);
		attchResponse.put(WidgetConstants.ATTACHMENT, list);
		Map<String,Object> responseMap=new HashMap<>();
		responseMap.put("id", "1234");
		responseMap.put("name", "Mahaveer");
		responseMap.put("email", "mahaveer.penna@bakerhughes.com");
		responseMap.put("userName", "00u8n40nv3ruehtuw1d7");
		responseMap.put("surName", "Penna");
		responseMap.put("enabled", "Y");
		reposnseList.add(responseMap);
		
		
		userMap = new HashMap<>();
		List<String> details=new ArrayList<>();
		details.add("Mahaveer Derrico");
		details.add("EXTERNAL");
		userMap.put("mahaveer.penna@bakerhughes.com", details);
		
		ReflectionTestUtils.setField(caseDetailsResponseHandler, "t", list);
		ReflectionTestUtils.setField(caseDetailsResponseHandler, "httpServletRequest", httpServletRequest);
		ReflectionTestUtils.setField(caseDetailsResponseHandler, "umsClientService", umsClientService);
		ReflectionTestUtils.setField(caseDetailsResponseHandler, "oktaUserDetails", oktaUserDetails);		
	}

	@SuppressWarnings("unused")
	private static class TestableCaseDetailsResponseHandler extends CaseDetailsResponseHandler<Map<String, Object>> {
		public TestableCaseDetailsResponseHandler(HttpServletRequest httpServletRequest,
				UMSClientService umsClientService, OktaUserDetails oktaUserDetails) {
			super(httpServletRequest, umsClientService, oktaUserDetails);
			// TODO Auto-generated constructor stub
		}

		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void parse() throws Exception {
		
		when(umsClientService.getOktaUserDetails(eq(httpServletRequest),any(JSONObject.class)))
		.thenReturn(reposnseList);
		when(oktaUserDetails.mappingUserData(anyList())).thenReturn(userMap);
		JSONObject result = (JSONObject) caseDetailsResponseHandler.parse(request);
		JSONArray openCasesArray = result.getJSONArray(WidgetConstants.DATA);
		assertEquals(list.size(), openCasesArray.length());
	}
	@Test
	void testProtectedConstructor() throws Exception {
		TestableCaseDetailsResponseHandler handler = new TestableCaseDetailsResponseHandler(httpServletRequest, umsClientService, oktaUserDetails);
		assertNotNull(handler);
	}
}