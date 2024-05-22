package com.bh.cp.user.service.impl;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.user.dto.request.AuditTrailUserActionDTO;
import com.bh.cp.user.dto.request.AuditUsageRequestDTO;

@Service
@EnableAsync
public class AsyncAuditService {

	Logger logger = org.slf4j.LoggerFactory.getLogger(AsyncAuditService.class);

	
	private RestTemplate resttemplate; 

	public AsyncAuditService(@Autowired RestTemplate resttemplate) {
		super();
		this.resttemplate = resttemplate;
	}

	@Async
	public void saveAuditTrailUsage(String usageUri, HttpEntity<AuditUsageRequestDTO> entity) {
		try {
			resttemplate.exchange(usageUri, HttpMethod.POST, entity, AuditUsageRequestDTO.class);
		} catch (Exception e) {
			logger.error("Rest=> Error", e);
		}
	}

	@Async
	public void saveAuditTrailUserAction(String usageUri, HttpEntity<AuditTrailUserActionDTO> entity) {
		try {
			resttemplate.exchange(usageUri, HttpMethod.POST, entity, AuditTrailUserActionDTO.class);
		} catch (Exception e) {
			logger.error("Rest=> Error", e);
		}
	}
}
