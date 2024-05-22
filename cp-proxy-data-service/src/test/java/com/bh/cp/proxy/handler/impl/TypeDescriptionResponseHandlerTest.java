package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bh.cp.proxy.constants.WidgetConstants;

class TypeDescriptionResponseHandlerTest {
	private static class TestableTypeDescriptionResponseHandler extends TypeDescriptionResponseHandler<Map<String, Object>> {
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
		TestableTypeDescriptionResponseHandler handler = new TestableTypeDescriptionResponseHandler();
		
		Map<String,List<Map<String,Object>>> responseMap=new  HashMap<>();
		List<Map<String,Object>> list=new ArrayList<>();
		Map<String,Object> map=new HashMap<>();
		map.put("id", "CASEOPEN");
		map.put("comment_type", "Case open");
		map.put("idescriptiond", "Case open by iCenter Ln*");
		map.put("for_customer", "Y");
		list.add(map);
		responseMap.put("commentType", list);
		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.DATA, responseMap);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put("serviceId",48);
		JSONObject result = (JSONObject) handler.callParse(request);
		JSONArray openCasesArray = result.getJSONObject(WidgetConstants.DATA).getJSONArray("commentType");
		assertEquals(list.size()+1, openCasesArray.length());

	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableTypeDescriptionResponseHandler handler = new TestableTypeDescriptionResponseHandler();
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Resources Found In the Input But Length is Zero")
	void testParseLevel_ResourcesLengthZero() {
		TestableTypeDescriptionResponseHandler handler = new TestableTypeDescriptionResponseHandler();
		HashMap<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testProtectedConstructor() throws Exception {
		TestableTypeDescriptionResponseHandler handler = new TestableTypeDescriptionResponseHandler();
		assertNotNull(handler); 
	}
}