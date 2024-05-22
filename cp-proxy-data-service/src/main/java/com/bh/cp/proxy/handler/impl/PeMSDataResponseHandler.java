package com.bh.cp.proxy.handler.impl;

import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;
import com.bh.cp.proxy.helper.OktaUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PeMSDataResponseHandler<T> extends JsonResponseHandler<T> {

	private HttpServletRequest httpServletRequest;

	private UMSClientService umsClientService;

	private OktaUserDetails oktaUserDetails;

	@Autowired
	@SuppressWarnings("unchecked")
	public PeMSDataResponseHandler(HttpServletRequest httpServletRequest, UMSClientService umsClientService,
                                   OktaUserDetails oktaUserDetails) {
		super((T) new HashMap<String, Object>());
		this.httpServletRequest = httpServletRequest;
		this.umsClientService = umsClientService;
		this.oktaUserDetails = oktaUserDetails;

	}


	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		if (response == null) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND);
			return nullObject;
		}
		return new JSONObject().put(WidgetConstants.DATA, response.get("data"));
	}

}