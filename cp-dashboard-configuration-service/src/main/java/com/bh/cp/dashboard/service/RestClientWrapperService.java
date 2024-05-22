package com.bh.cp.dashboard.service;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface RestClientWrapperService {

	public ResponseEntity<String> getResponseFromUrl(HttpServletRequest httpServletRequest, String url);

	public ResponseEntity<String> postBodyToUrl(HttpServletRequest httpServletRequest, String url, String jsonBody);

}
