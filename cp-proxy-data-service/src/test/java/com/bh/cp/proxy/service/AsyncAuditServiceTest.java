package com.bh.cp.proxy.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.proxy.dto.request.AuditUsageRequestDTO;
import com.bh.cp.proxy.dto.request.PerformanceRequestDTO;


class AsyncAuditServiceTest {

	@InjectMocks
	private AsyncAuditService asyncAuditService;

	@Mock
	private RestTemplate restTemplate;

	HttpEntity<String> entity;

	HttpEntity<AuditUsageRequestDTO> entityUsage;
	
	HttpEntity<PerformanceRequestDTO> entityPerformance;
	HttpHeaders headers;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		entity = new HttpEntity<>(new String(), headers);
		entityUsage=new HttpEntity<>(new AuditUsageRequestDTO(), headers);
		entityPerformance=new HttpEntity<>(new PerformanceRequestDTO(),headers);
	}

	@Test
	void testAuditTrailUsage() {
		restTemplate.exchange("http://test", HttpMethod.POST, entity, String.class);
		asyncAuditService.saveAuditTrailUsage("http://test",entityUsage);
		assertNotNull(entity);
	}
	
	@Test
	void testAuditTrailUsageFaliure() {
		entity=null;
		entityUsage=null;
		restTemplate.exchange("http://test", HttpMethod.POST, entity, String.class);
		asyncAuditService.saveAuditTrailUsage("http://test",entityUsage);
		assertNull(entity);
		assertNull(entityUsage);
	}
	
	@Test
	void testAuditTrailAction() {
		restTemplate.exchange("http://test", HttpMethod.POST, entity, String.class);
		asyncAuditService.saveAuditTrailPerformance("http://test",entityPerformance);
		assertNotNull(entity);
	}
	
	@Test
	void testAuditTrailActionFailure() {
		entity=null;
		entityPerformance=null;
		restTemplate.exchange("http://test", HttpMethod.POST, entity, String.class);
		asyncAuditService.saveAuditTrailPerformance("http://test",entityPerformance);
		assertNull(entity);
		assertNull(entityPerformance);
	}
	

}
