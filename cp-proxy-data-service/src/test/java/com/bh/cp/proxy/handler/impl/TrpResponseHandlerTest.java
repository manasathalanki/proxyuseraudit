package com.bh.cp.proxy.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bh.cp.proxy.constants.WidgetConstants;

class TrpResponseHandlerTest {
	private static class TestableTrpResponseHandler extends TrpResponseHandler<Map<String, Object>> {
		public void setResponseData(Map<String, Object> responseData) {
			setT(responseData);
		}

		public Object callParse(Map<String, Object> request) {
			return parse(request);
		}
	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output")
	void testParse() {
		TestableTrpResponseHandler handler = new TestableTrpResponseHandler();
		JSONArray array = new JSONArray().put(new JSONObject().put(WidgetConstants.STATUS, "Open"))
				.put(new JSONObject().put(WidgetConstants.STATUS, "Closed"))
				.put(new JSONObject().put(WidgetConstants.STATUS, "Open"))
				.put(new JSONObject().put(WidgetConstants.STATUS, "Analyse"));

		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.RESOURCES, array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals("3", result.getJSONObject(WidgetConstants.DATA).getString(WidgetConstants.OPENCASES));
		assertEquals("1", result.getJSONObject(WidgetConstants.DATA).getString(WidgetConstants.CLOSEDCASES));
		assertEquals("0", result.getJSONObject(WidgetConstants.DATA).getString(WidgetConstants.ONHOLDCASES));

		JSONObject openCasesStatus = result.getJSONObject(WidgetConstants.DATA)
				.getJSONObject(WidgetConstants.OPENCASESSTATUS);
		assertEquals("1", openCasesStatus.getString(WidgetConstants.ANALYSE));
		assertEquals("0", openCasesStatus.getString(WidgetConstants.VALIDATE));
		assertEquals("0", openCasesStatus.getString(WidgetConstants.APPLY));

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Analyse,validate and Apply are zero")
	void testParse_openCasesStatusIsZero() {
		TestableTrpResponseHandler handler = new TestableTrpResponseHandler();
		Map<String, Object> response = new HashMap<>();
		response.put(WidgetConstants.RESOURCES, new JSONArray());
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));
	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableTrpResponseHandler handler = new TestableTrpResponseHandler();
		handler.setResponseData(new HashMap<>());

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, WidgetConstants.DATERANGE3);
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(WidgetConstants.NODATAFOUND, result.get(WidgetConstants.DATA));

	}

	@Test
	void testConstructor() throws Exception {
		TestableTrpResponseHandler handler = new TestableTrpResponseHandler();
		assertNotNull(handler);
	}
}