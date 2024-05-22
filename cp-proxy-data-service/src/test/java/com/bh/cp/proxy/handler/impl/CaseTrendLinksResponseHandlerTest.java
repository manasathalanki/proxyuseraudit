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

class CaseTrendLinksResponseHandlerTest {
	private static class TestableCaseTrendLinksResponseHandler
			extends CaseTrendLinksResponseHandler<Map<String, Object>> {
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

		TestableCaseTrendLinksResponseHandler handler = new TestableCaseTrendLinksResponseHandler();

		JSONArray array = new JSONArray()
				.put(new JSONObject().put("d_ins", "2023-02-27T14:28:01.000Z").put("expiration", "365")
						.put("trendLink",
								"https://dev-srm.icenter.azure.bakerhughes.com/configuration/ov7miw78ulisdc7qy2mo")
						.put("user", "Liliana.DErrico@bakerhughes.com"));

		Map<String, Object> response = new HashMap<>();
		response.put("data", array);
		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "3M");
		JSONObject result = (JSONObject) handler.callParse(request);

		assertEquals("Liliana.DErrico@bakerhughes.com", result.getJSONObject(WidgetConstants.DATA).getJSONArray("list")
				.getJSONObject(0).get("User").toString());

		assertEquals("27-Feb-2023 14:28:01", result.getJSONObject(WidgetConstants.DATA).getJSONArray("list")
				.getJSONObject(0).get("InsertDate&Time").toString());

		assertEquals("https://dev-srm.icenter.azure.bakerhughes.com/configuration/ov7miw78ulisdc7qy2mo", result
				.getJSONObject(WidgetConstants.DATA).getJSONArray("list").getJSONObject(0).get("Link").toString());

	}

	@Test
	@DisplayName("Parse the Response - data are Empty In the Input")
	void testParseResourcesIsEmpty() {
		TestableCaseTrendLinksResponseHandler handler = new TestableCaseTrendLinksResponseHandler();
		Map<String, Object> mockResponseData = new HashMap<>();
		mockResponseData.put("data", new JSONArray());
		handler.setResponseData(mockResponseData);

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "3M");

		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

	@Test
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableCaseTrendLinksResponseHandler handler = new TestableCaseTrendLinksResponseHandler();
		handler.setResponseData(new HashMap<>());

		Map<String, Object> request = new HashMap<>();
		request.put(WidgetConstants.DATERANGE, "3M");
		JSONObject result = (JSONObject) handler.callParse(request);
		assertEquals(JSONObject.NULL, result.get(WidgetConstants.DATA));

	}

	@Test
	void testConstructor() throws Exception {
		TestableCaseTrendLinksResponseHandler handler = new TestableCaseTrendLinksResponseHandler();
		assertNotNull(handler);
	}

}
