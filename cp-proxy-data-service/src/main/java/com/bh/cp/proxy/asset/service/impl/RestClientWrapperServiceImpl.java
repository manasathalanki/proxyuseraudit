package com.bh.cp.proxy.asset.service.impl;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.proxy.asset.service.RestClientWrapperService;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RestClientWrapperServiceImpl implements RestClientWrapperService {

	private RestTemplate restTemplate;

	public RestClientWrapperServiceImpl(@Autowired RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}

	private static final Logger logger = LoggerFactory.getLogger(RestClientWrapperServiceImpl.class);

	private HttpHeaders getHeaders(HttpServletRequest httpServletRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(ProxyConstants.KEY_AUTHORIZATION, ProxyConstants.KEY_BEARER
				+ httpServletRequest.getHeader(ProxyConstants.KEY_AUTHORIZATION).substring(7));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return headers;
	}

	@Override
	public ResponseEntity<String> getResponseFromUrl(HttpServletRequest httpServletRequest, String url) {
		HttpEntity<String> entity = new HttpEntity<>(getHeaders(httpServletRequest));
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Calling GET URI=>{}", url);
		return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	}

	@Override
	public ResponseEntity<String> postBodyToUrl(HttpServletRequest httpServletRequest, String url, String jsonBody) {
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, getHeaders(httpServletRequest));
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Calling POST URI=>{} with Request Body {}", url, jsonBody);
		return restTemplate.postForEntity(url, entity, String.class);
	}

}
