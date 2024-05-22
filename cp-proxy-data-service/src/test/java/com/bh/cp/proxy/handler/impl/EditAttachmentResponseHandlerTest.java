package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bh.cp.proxy.constants.WidgetConstants;

class EditAttachmentResponseHandlerTest {
	private static class TestableEditAttachmentResponseHandlerTest extends EditAttachmentResponseHandler<Map<String, Object>> {
		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParse() throws Exception {
		TestableEditAttachmentResponseHandlerTest handler = new TestableEditAttachmentResponseHandlerTest();
		List<HashMap<String, Object>> list=new ArrayList<>();
		HashMap<String, Object> map=new HashMap<>();
		map.put("issueId", "166761147");
		map.put("commentId", "2095");
		list.add(map);
		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.DATA, list);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put("action","ADD");
		request.put("caseId","166761147");
		request.put("commentDesc","desc");
		request.put("commentId",942);
		request.put("commentType",45);
		request.put("commentVisible","Y");
		request.put("user","kumuda.kurli@bakerhughes.com");
		request.put("userType","INTERNAL");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(list.size(), result.length());

	}
	
	@Test
	void testProtectedConstructor() throws Exception {
		TestableEditAttachmentResponseHandlerTest handler = new TestableEditAttachmentResponseHandlerTest();
		assertNotNull(handler);
	}
}