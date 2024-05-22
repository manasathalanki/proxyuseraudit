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

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.helper.OktaUserDetails;

import jakarta.servlet.http.HttpServletRequest;

class CaseLockResponseHandlerTest {

	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private UMSClientService umsClientService;
	@Mock
	private OktaUserDetails oktaUserDetails;
	@InjectMocks
	private CaseLockResponseHandler<?> caseLockResponseHandler;
	HashMap<String, Object> map = new HashMap<>();
	HashMap<String, Object> resultMap = new HashMap<>();
	Map<String, Object> request = new HashMap<>();
	List<Map<String, Object>> reposnseList = new ArrayList<>();

	String valuesApiResponse;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		map.put("caseId", 788);
		map.put("caseRev", 3);
		map.put("lockResponse", "locked");
		map.put("lockTimestamp", "2023-10-31T09:38:33.000Z");
		map.put("lockingUser", "name.surname@bakerhughes.com");
		map.put("UserName", "Govind");

		request.put("caseId", 166761288);
		request.put("lockType", "lock");
		request.put("user", "name.surname@bakerhughes.com");

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("id", "567788");
		responseMap.put("name", "Mahaveer");
		responseMap.put("email", "name.surname@bakerhughes.com");
		responseMap.put("userName", "00u8n40nv3ruehtuw1d7");
		responseMap.put("surName", "Penna");
		responseMap.put("enabled", "Y");
		reposnseList.add(responseMap);

		ReflectionTestUtils.setField(caseLockResponseHandler, "t", map);
		ReflectionTestUtils.setField(caseLockResponseHandler, "httpServletRequest", httpServletRequest);
		ReflectionTestUtils.setField(caseLockResponseHandler, "umsClientService", umsClientService);
		ReflectionTestUtils.setField(caseLockResponseHandler, "oktaUserDetails", oktaUserDetails);

	}

	private static class TestableCaseLockResponseHandler extends CaseLockResponseHandler<Map<String, Object>> {
		public TestableCaseLockResponseHandler(HttpServletRequest httpServletRequest, UMSClientService umsClientService,
				OktaUserDetails oktaUserDetails) {
			super(httpServletRequest, umsClientService, oktaUserDetails);
			// TODO Auto-generated constructor stub
		}

		@SuppressWarnings("unused")
		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		@SuppressWarnings("unused")
		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void parse() throws Exception {
		Map<String, List<String>> userMap = new HashMap<>();
		List<String> details = new ArrayList<>();
		details.add("Mahaveer Derrico");
		details.add("EXTERNAL");
		userMap.put("name.surname@bakerhughes.com", details);
		Map<String, Object> userResponse = new HashMap<>();
		userResponse.put(ProxyConstants.TITLE, ProxyConstants.EXTERNAL_USER);
		when(umsClientService.getOktaUserDetails(eq(httpServletRequest), any(JSONObject.class)))
				.thenReturn(reposnseList);
		when(umsClientService.getUserDetails(httpServletRequest)).thenReturn(userResponse);
		when(umsClientService.getOktaUserDetails(eq(httpServletRequest), any(JSONObject.class)))
				.thenReturn(reposnseList);
		when(oktaUserDetails.mappingUserData(reposnseList)).thenReturn(userMap);
		JSONObject result = (JSONObject) caseLockResponseHandler.parse(request);
		resultMap.put("data", map);
		assertEquals(resultMap.size(), result.length());
	}

	@Test
	void testProtectedConstructor() throws Exception {
		TestableCaseLockResponseHandler handler = new TestableCaseLockResponseHandler(httpServletRequest,
				umsClientService, oktaUserDetails);
		assertNotNull(handler);
	}
}
