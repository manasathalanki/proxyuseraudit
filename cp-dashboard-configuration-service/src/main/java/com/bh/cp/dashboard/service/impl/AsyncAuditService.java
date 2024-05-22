package com.bh.cp.dashboard.service.impl;

import java.util.Arrays;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.dashboard.dto.request.AuditTrailUserActionDTO;
import com.bh.cp.dashboard.dto.request.AuditUsageRequestDTO;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

@Service
@EnableAsync
public class AsyncAuditService {

	Logger logger = org.slf4j.LoggerFactory.getLogger(AsyncAuditService.class);

	
	private RestTemplate resttemplate;

	public AsyncAuditService(@Autowired RestTemplate resttemplate) {
		super();
		this.resttemplate = resttemplate;
	}

	private HttpHeaders getHeaders(HttpHeaders existingHeaders) {
		HttpHeaders headers = new HttpHeaders();
		headers.addAll(existingHeaders);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return headers;
	}

	@Async
	public void saveAuditTrailUsage(String usageUri, HttpEntity<AuditUsageRequestDTO> entity) {
		try {
			ObjectMapper mapper = JsonMapper.builder().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).build();
			HttpEntity<String> httpEntity = new HttpEntity<>(mapper.writeValueAsString(entity.getBody()),
					getHeaders(entity.getHeaders()));
			resttemplate.exchange(usageUri, HttpMethod.POST, httpEntity, String.class);
		} catch (Exception e) {
			logger.error("Rest=> Audit Usage Error", e);
		}
	}

	@Async
	public void saveAuditTrailUserAction(String usageUri, HttpEntity<AuditTrailUserActionDTO> entity) {
		try {
			ObjectMapper mapper = JsonMapper.builder().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
					.serializationInclusion(Include.NON_NULL).build();
			HttpEntity<String> httpEntity = new HttpEntity<>(mapper.writeValueAsString(entity.getBody()),
					getHeaders(entity.getHeaders()));
			resttemplate.exchange(usageUri, HttpMethod.POST, httpEntity, String.class);
		} catch (Exception e) {
			logger.error("Rest=> Audit User Action Error", e);
		}
	}
}
