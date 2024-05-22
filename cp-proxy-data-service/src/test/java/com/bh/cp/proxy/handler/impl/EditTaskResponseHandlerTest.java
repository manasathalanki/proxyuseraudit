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

class EditTaskResponseHandlerTest {

	private static class TestableEditTaskResponseHandler extends EditTaskResponseHandler<Map<String, Object>> {
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

		TestableEditTaskResponseHandler handler = new TestableEditTaskResponseHandler();

		List<HashMap<String, Object>> list = new ArrayList<>();

		HashMap<String, Object> response = new HashMap<>();
		response.put("taskId", "5905");
		response.put("status", "CLOSED");
		response.put("rootCause", "YES");

		list.add(response);

		handler.setResponseData(response);

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);

		assertEquals(list.size(), result.length());

	}

	@Test
	@SuppressWarnings("static-access")
	@DisplayName("Parse the Response - Input Is Empty")
	void testParseInputIsEmpty() {
		TestableEditTaskResponseHandler handler = new TestableEditTaskResponseHandler();
		handler.setResponseData(new HashMap<>());

		Map<String, Object> request = new HashMap<>();
		JSONObject result = (JSONObject) handler.callParse(request);

		assertEquals(JSONObject.NULL, result.NULL);

	}

	@Test
	void testConstructor() throws Exception {
		TestableEditTaskResponseHandler handler = new TestableEditTaskResponseHandler();
		assertNotNull(handler);
	}

}
