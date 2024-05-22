package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class EditCaseCommentResponseHandler<T> extends JsonResponseHandler<T> {
	
	@SuppressWarnings("unchecked")
	public EditCaseCommentResponseHandler() {
		super((T) new HashMap<String, Object>());

	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		
		if (response.isEmpty()) {
			JSONObject nullObject = new JSONObject();
			nullObject.put("data","No data found");
			return nullObject;
		}
		response.put("Message","Comment edited successfully");
		return new JSONObject().put("data",response);
	}
}