package com.bh.cp.user.service.impl;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.exception.SPARQUMSAPIErrorException;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RestClientWrapperServiceImpl implements RestClientWrapperService {

	private static final Logger logger = LoggerFactory.getLogger(RestClientWrapperServiceImpl.class);

	private RestTemplate restTemplate;

	public RestClientWrapperServiceImpl(@Autowired RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}

	private HttpHeaders getHeaders(HttpServletRequest httpServletRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, httpServletRequest.getHeader(UMSConstants.AUTHORIZATION));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return headers;
	}

	@Override
	public ResponseEntity<String> getResponseFromUrl(HttpServletRequest httpServletRequest, String url) {
		HttpEntity<String> entity = new HttpEntity<>(getHeaders(httpServletRequest));
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Calling GET URI=>{}", url);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return validateUMSResponse(response);
	}
	
	@Override
	public ResponseEntity<String> postBodyToUrl(HttpServletRequest httpServletRequest, String url, String jsonBody) {
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, getHeaders(httpServletRequest));
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Calling POST URI=>{} with Request=>{}", url, jsonBody);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		return validateUMSResponse(response);
	}

	@Override
	public ResponseEntity<String> putBodyToUrl(HttpServletRequest httpServletRequest, String url, String jsonBody) {
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, getHeaders(httpServletRequest));
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Calling PUT URI=>{} with Request=>{}", url, jsonBody);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
		return validateUMSResponse(response);
	}

	@Override
	public ResponseEntity<String> deleteBodyToUrl(HttpServletRequest httpServletRequest, String url, String jsonBody) {
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, getHeaders(httpServletRequest));
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Calling DELETE URI=>{} with Request=>{}", url, jsonBody);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
		return validateUMSResponse(response);
	}

	private ResponseEntity<String> validateUMSResponse(ResponseEntity<String> response) {
		try {
			JSONArray jsonArray = new JSONArray(response.getBody());
			for (Object obj : jsonArray) {
				JSONObject jsonObj = new JSONObject(obj.toString());
				Integer statusCode = jsonObj.getInt("status");
				if (statusCode > 299) {
					throw new SPARQUMSAPIErrorException(jsonObj.getString("details"), HttpStatus.valueOf(statusCode));
				}
			}
		} catch (SPARQUMSAPIErrorException ex) {
			throw ex;
		} catch (Exception e) {
			logger.info("Response from External API does not contains Status code and Details in required format");
		}

		return response;
	}

}
