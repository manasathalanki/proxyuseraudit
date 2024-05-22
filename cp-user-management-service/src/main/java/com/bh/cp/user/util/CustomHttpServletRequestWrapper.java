package com.bh.cp.user.util;

import com.bh.cp.user.constants.SecurityUtilConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private String token;

	private boolean dynamicToken;

	public CustomHttpServletRequestWrapper(HttpServletRequest request, JwtUtil jwtUtil) {
		super(request);
		this.dynamicToken = true;
		this.token = jwtUtil.generateAdminToken();
	}

	public CustomHttpServletRequestWrapper(HttpServletRequest request, String token) {
		super(request);
		this.dynamicToken = false;
		this.token = token;
	}

	@Override
	public String getHeader(String name) {
		if (SecurityUtilConstants.KEY_AUTHORIZATION.equals(name) && dynamicToken) {
			return SecurityUtilConstants.KEY_BEARER + token;
		} else if (SecurityUtilConstants.KEY_AUTHORIZATION.equals(name)) {
			return token;
		}
		return super.getHeader(name);
	}

}
