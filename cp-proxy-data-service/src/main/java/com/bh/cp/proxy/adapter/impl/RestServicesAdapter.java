package com.bh.cp.proxy.adapter.impl;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.bh.cp.proxy.adapter.ServicesAdapter;
import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.constants.JwtUtilConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.pojo.AuditDate;
import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.bh.cp.proxy.util.JwtUtil;

@Service
public class RestServicesAdapter implements ServicesAdapter {

	private Integer caseDeatilsId;

	private Integer kpiFiredHoursId;

	private JwtUtil jwtUtil;
	private AuditTrailAspect auditTrailAspect;

	public RestServicesAdapter(@Value("${case.details.service-id}") Integer caseDeatilsId,
			@Value("${kpi.fired.hours.widget-id}") Integer kpiFiredHoursId,
			@Autowired AuditTrailAspect auditTrailAspect, @Autowired JwtUtil jwtUtil) {
		this.caseDeatilsId = caseDeatilsId;
		this.kpiFiredHoursId = kpiFiredHoursId;
		this.auditTrailAspect = auditTrailAspect;
		this.jwtUtil = jwtUtil;
	}

	private static final Logger logger = LoggerFactory.getLogger(RestServicesAdapter.class);

	@Override
	public Object execute(ServicesDirectory service) {
		logger.info("service.getInput_data:-> {}", service.getInputData());
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());

		Boolean statusFlag = true;
		try {
			RestTemplate restTemplate = new RestTemplate();
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
			restTemplate.getMessageConverters().add(converter);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");

			if (service.getWidgetId().equals(kpiFiredHoursId)) {
				headers.add(JwtUtilConstants.AUTHORIZATION, JwtUtilConstants.BEARER + jwtUtil.generateCounterToken());
			}

			if (service.getMethod().equals(HttpMethod.GET.name())) {
				HashMap<String, Object> dataMap = new HashMap<>();
				HttpEntity<String> entity = new HttpEntity<>(headers);
				String url = service.getUri() + service.getInputData();
				URI uri = UriComponentsBuilder.fromUriString(url).build().encode().toUri();
				dataMap.put(WidgetConstants.DATA,
						restTemplate.exchange(uri, HttpMethod.GET, entity, Object.class).getBody());
				return ResponseEntity.ok(dataMap).getBody();
			}

			if (service.getMethod().equals(HttpMethod.POST.name()) && (service.getId() == caseDeatilsId)) {
				HttpEntity<String> entity = new HttpEntity<>(service.getInputData(), headers);
				String url = service.getUri() + service.getInputData();
				URI uri = UriComponentsBuilder.fromUriString(url).build().encode().toUri();
				return restTemplate.exchange(uri, HttpMethod.POST, entity, HashMap.class).getBody();
			}

			if (service.getMethod().equals(HttpMethod.POST.name())) {
				HttpEntity<String> entity = new HttpEntity<>(service.getInputData(), headers);
				HashMap<String, Object> dataMap = new HashMap<>();
				Object checkType = new Object();
				checkType = restTemplate.exchange(service.getUri(), HttpMethod.POST, entity, Object.class).getBody();
				if (checkType instanceof Integer) {
					dataMap.put(WidgetConstants.DATA,
							restTemplate.exchange(service.getUri(), HttpMethod.POST, entity, Object.class).getBody());
					return ResponseEntity.ok(dataMap).getBody();
				}
				return checkType;
			}
		} catch (Exception e) {
			logger.info("Error occurred when connecting with External API -{}", e.getMessage());
			statusFlag = false;
		} finally {
			if (!(service.getOutputHandler().equalsIgnoreCase("com.bh.cp.proxy.handler.impl.FleetResponseHandler"))) {
				Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
				long executionTime = endTime.getTime() - startTime.getTime();
				auditTrailAspect.saveAuditTrailPerformance(
						(new StringBuilder(this.getClass().getCanonicalName()).append(".")
								.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
						new JSONObject(service), new AuditDate(startTime, endTime, executionTime), statusFlag);
			}
		}
		logger.error("{} not supported.", service.getMethod());
		return null;
	}

}