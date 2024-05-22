package com.bh.cp.proxy.handler.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class EditAttachmentResponseHandler<T> extends JsonResponseHandler<T> {

	@SuppressWarnings("unchecked")
	public EditAttachmentResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object parse(Map<String, Object> request) {
		
		HashMap<String, Object> response = (HashMap<String, Object>) getT();

		
		JSONObject jsonObject = new JSONObject(response);
		
		return new JSONObject().put(WidgetConstants.DATA, jsonObject);
	}
}
