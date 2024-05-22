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

class CaseAttachmentAPIResponseHandlerTest {
	private static class TestableCaseAttachmentAPIResponseHandler extends CaseAttachmentAPIResponseHandler<Map<String, Object>> {
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
		TestableCaseAttachmentAPIResponseHandler handler = new TestableCaseAttachmentAPIResponseHandler();
		List<HashMap<String, Object>> list=new ArrayList<>();
		HashMap<String, Object> map=new HashMap<>();
		map.put("fileName", "105_QG_LNG_TRAIN1_00105.pdf");
		map.put("fileMime", "application/pdf");
		map.put("user", "503131073");
		map.put("attachType", "INSIGHT");
		list.add(map);

		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.DATA, list);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put("serviceId",42);
		request.put("issueId","166760527");
		request.put("attachment","n");
		JSONObject result = (JSONObject) handler.callParse(request);
		JSONArray openCasesArray = result.getJSONObject(WidgetConstants.DATA).getJSONArray("list");
		assertEquals(list.size(), openCasesArray.length());

	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableCaseAttachmentAPIResponseHandler handler = new TestableCaseAttachmentAPIResponseHandler();
		handler.setResponseData(new HashMap<>());
		Map<String, Object> request = new HashMap<>();
		request.put("issueId","166760527");
		request.put("attachment","n");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Resources Found In the Input But Length is Zero")
	void testParseLevel_ResourcesLengthZero() {
		TestableCaseAttachmentAPIResponseHandler handler = new TestableCaseAttachmentAPIResponseHandler();
		HashMap<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put(WidgetConstants.RESOURCES, new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		request.put("issueId","166760527");
		request.put("attachment","n");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertNotNull(result);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	void testProtectedConstructor() throws Exception {
		TestableCaseAttachmentAPIResponseHandler handler = new TestableCaseAttachmentAPIResponseHandler();
		assertNotNull(handler);
	}
}