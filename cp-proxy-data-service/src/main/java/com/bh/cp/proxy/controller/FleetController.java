package com.bh.cp.proxy.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.proxy.dto.request.FleetRequestDTO;
import com.bh.cp.proxy.exception.ProxyException;
import com.bh.cp.proxy.service.FleetService;
import com.bh.cp.proxy.service.ProxyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController

@Tag(name = "Fleet Controller")
public class FleetController {

	private Integer fleetDataWidgetId;

	private ProxyService proxyService;

	private FleetService fleetService;

	public FleetController(@Autowired ProxyService proxyService, @Autowired FleetService fleetService,
			@Value("${fleet.data.widget-id}") Integer fleetDataWidgetId) {
		super();
		this.proxyService = proxyService;
		this.fleetDataWidgetId = fleetDataWidgetId;
		this.fleetService = fleetService;
	}

	@Operation(summary = "Get Fleet data of Asset", description = "Get Fleet data of Asset")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/v1/fleetsdata", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fleetData(@RequestBody FleetRequestDTO dataRequest,
			HttpServletRequest httpServletRequest)
			throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, ProxyException {
		Map<String, Object> fleetDataRequest = new HashMap<>();
		fleetDataRequest.put("widgetId", fleetDataWidgetId);
		fleetDataRequest.put("vid", dataRequest.getVid());
		fleetDataRequest.put("dateRange", "3M");
		httpServletRequest.setAttribute("fleetDataWidgetId", fleetDataWidgetId);
		return new ResponseEntity<>(proxyService.execute(fleetDataRequest, httpServletRequest).toString(),
				HttpStatusCode.valueOf(200));
	}

	@Operation(summary = "Get Fleet data of Asset", description = "Get Fleet data of Asset")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/v2/fleetsdata", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fleetDataV2(@RequestBody FleetRequestDTO dataRequest,
			HttpServletRequest httpServletRequest)
			throws IOException, IllegalArgumentException, SecurityException, InterruptedException {
		Map<String, Object> fleetDataRequest = new HashMap<>();
		fleetDataRequest.put("widgetId", fleetDataWidgetId);
		fleetDataRequest.put("vid", dataRequest.getVid());
		httpServletRequest.setAttribute("fleetDataWidgetId", fleetDataWidgetId);
		return new ResponseEntity<>(fleetService.execute(fleetDataRequest, httpServletRequest).toString(),
				HttpStatusCode.valueOf(200));
	}
}
