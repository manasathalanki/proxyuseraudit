package com.bh.cp.proxy.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.dto.request.CSASRequestDTO;
import com.bh.cp.proxy.dto.request.CasesDataRequestDTO;
import com.bh.cp.proxy.dto.request.DeviceDataRequestDTO;
import com.bh.cp.proxy.dto.request.WidgetsDataRequestDTO;
import com.bh.cp.proxy.dto.request.PeMSRequestDTO;
import com.bh.cp.proxy.exception.ProxyException;
import com.bh.cp.proxy.pojo.AuditDate;
import com.bh.cp.proxy.service.ProxyService;
import com.bh.cp.proxy.util.CaseDataAppender;
import com.bh.cp.proxy.util.CaseDetailsAppender;
import com.bh.cp.proxy.util.CaseHistoryAppender;
import com.bh.cp.proxy.util.DeviceDataAppender;
import com.bh.cp.proxy.util.PeMSAppender;
import com.bh.cp.proxy.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("v1")
@Tag(name = "Data Controller")
public class GenericDataController {

	private ProxyService proxyService;

	private CaseDataAppender caseDataAppender;

	private CaseDetailsAppender saveCaseDetailsAppender;

	private AuditTrailAspect auditTrailAspect;
	
	private DeviceDataAppender deviceDataAppender;
	
	private CaseHistoryAppender caseHistoryAppender;
	private PeMSAppender peMSAppender;

	public GenericDataController(@Autowired ProxyService proxyService, @Autowired CaseDataAppender caseDataAppender,
			@Autowired CaseDetailsAppender saveCaseDetailsAppender, @Autowired AuditTrailAspect auditTrailAspect,
			@Autowired DeviceDataAppender deviceDataAppender, @Autowired CaseHistoryAppender caseHistoryAppender,
			@Autowired PeMSAppender peMSAppender) {
		super();
		this.proxyService = proxyService;
		this.caseDataAppender = caseDataAppender;
		this.saveCaseDetailsAppender = saveCaseDetailsAppender;
		this.auditTrailAspect = auditTrailAspect;
		this.deviceDataAppender = deviceDataAppender;
		this.caseHistoryAppender = caseHistoryAppender;
		this.peMSAppender = peMSAppender;
	}

	private static final Logger logger = LoggerFactory.getLogger(GenericDataController.class);

	@Operation(summary = "Getting data for given Widget ID", description = "Getting data from supporting API in specific format for Widget based on widgetId")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/widgetsdata", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> widgetsData(@RequestBody WidgetsDataRequestDTO dataRequest,
			HttpServletRequest httpServletRequest)
			throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, ProxyException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		MDC.put(ProxyConstants.PERF_AUDIT_WIDGET_ID, dataRequest.getWidgetId().toString());
		try {
			Map<String, Object> widgetsDataRequest = new HashMap<>();
			widgetsDataRequest.put(ProxyConstants.WIDGET_ID, dataRequest.getWidgetId());
			widgetsDataRequest.put(ProxyConstants.DATE_RANGE, dataRequest.getDateRange());
			widgetsDataRequest.put(ProxyConstants.VID, dataRequest.getVid());
			widgetsDataRequest.put(ProxyConstants.LEVEL, dataRequest.getLevel());

			SecurityUtil.sanitizeLogging(logger, Level.INFO, "[widgetsdata] Request:==> {}",
					widgetsDataRequest.toString());

			return new ResponseEntity<>(proxyService.execute(widgetsDataRequest, httpServletRequest).toString(),
					HttpStatus.OK);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(dataRequest), new AuditDate(startTime, endTime, executionTime), true);
		}
	}

	@Operation(summary = "Getting Cases data for given parameters", description = "Getting data from supporting API in specific format for Cases for given parameters")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/casesdata", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> casesData(@Valid @RequestBody CasesDataRequestDTO dataRequest,
			HttpServletRequest httpServletRequest)
			throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, ProxyException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		try {
			Map<String, Object> widgetsDataRequest = new HashMap<>();
			widgetsDataRequest.put(WidgetConstants.SERVICE_ID, dataRequest.getServiceId());
			MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, dataRequest.getServiceId().toString());
			
			widgetsDataRequest = caseDataAppender.appendRequestData(dataRequest, widgetsDataRequest);
			SecurityUtil.sanitizeLogging(logger, Level.INFO, ProxyConstants.CASE_DATA_REQUEST,
					widgetsDataRequest.toString());

			Object response = proxyService.execute(widgetsDataRequest, httpServletRequest);
			return new ResponseEntity<>(response != null ? response.toString() : "", HttpStatus.OK);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(dataRequest), new AuditDate(startTime, endTime, executionTime), true);
		}
	}

	@Operation(summary = "Getting Cases data for given parameter", description = "Getting data from supporting API in specific format for Cases for given parameter")
	@SecurityRequirement(name = "Keycloak Token")
	@GetMapping(value = "/casesdata/{caseId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> retrieveCasesData(HttpServletRequest httpServletRequest,
			@PathVariable("caseId") Integer caseNumber)
			throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, ProxyException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		Map<String, Object> widgetsDataRequest = new HashMap<>();
		try {
			widgetsDataRequest.put(WidgetConstants.SERVICE_ID, 36);
			MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, "36");
			widgetsDataRequest.put("caseNumber", caseNumber.toString());
			caseDataAppender.appenduser(widgetsDataRequest);
			SecurityUtil.sanitizeLogging(logger, Level.INFO, ProxyConstants.CASE_DATA_REQUEST,
					widgetsDataRequest.toString());
			Object response = proxyService.execute(widgetsDataRequest, httpServletRequest);
			return new ResponseEntity<>(response != null ? response.toString() : "", HttpStatus.OK);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(widgetsDataRequest), new AuditDate(startTime, endTime, executionTime), true);
		}
	}
	
	
	
	@Operation(summary = "Getting Cases data for given parameter", description = "Getting data from supporting API in specific format for Cases for given parameter")
	@SecurityRequirement(name = "Keycloak Token")
	@GetMapping(value = "/casesdatalist/{caseId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> retrieveCasesDataList(HttpServletRequest httpServletRequest,
			@PathVariable("caseId") Integer caseNumber)
			throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, ProxyException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		Map<String, Object> widgetsDataRequest = new HashMap<>();
		try {
			widgetsDataRequest.put(WidgetConstants.SERVICE_ID, 78);
			MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, "78");
			widgetsDataRequest.put("caseNumber", caseNumber.toString());
			caseDataAppender.appenduser(widgetsDataRequest);
			SecurityUtil.sanitizeLogging(logger, Level.INFO, ProxyConstants.CASE_DATA_REQUEST,
					widgetsDataRequest.toString());
			Object response = proxyService.execute(widgetsDataRequest, httpServletRequest);
			return new ResponseEntity<>(response != null ? response.toString() : "", HttpStatus.OK);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(widgetsDataRequest), new AuditDate(startTime, endTime, executionTime), true);
		}
	}

	@Operation(summary = "Getting Cases Attachment for given parameter", description = "Getting file from supporting API in specific format for Cases for given parameter")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/attachment/download", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public void casesAttachment(HttpServletRequest httpServletRequest,
			@Valid @RequestBody CasesDataRequestDTO dataRequest, HttpServletResponse response)
			throws IllegalArgumentException, SecurityException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		Map<String, Object> widgetsDataRequest = new HashMap<>();
		try {
			widgetsDataRequest.put(WidgetConstants.SERVICE_ID, dataRequest.getServiceId());
			MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, dataRequest.getServiceId().toString());
			
			widgetsDataRequest.put("attachment", "Y");

			widgetsDataRequest = caseDataAppender.appendRequestData(dataRequest, widgetsDataRequest);

			SecurityUtil.sanitizeLogging(logger, Level.INFO, "[/attachment/download] Request:==> {}",
					widgetsDataRequest.toString());

			caseDataAppender.getAttachment(widgetsDataRequest, httpServletRequest, response);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(widgetsDataRequest), new AuditDate(startTime, endTime, executionTime), true);
		}
	}

	@Operation(summary = "Save Case Details", description = "Save Case Details")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/caseData/save", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public Object saveCaseDetailsData(HttpServletRequest httpServletRequest, @RequestPart String dataRequest,
			@RequestParam(value = "file", required = false) List<MultipartFile> file)
			throws IllegalArgumentException, SecurityException, JsonProcessingException {
		
		return saveCaseDetailsAppender.appendRequestData(dataRequest, file);

	}

	@Operation(summary = "Getting Cases Attachment for given parameter", description = "Getting file from supporting API in specific format for Cases for given parameter")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/upload-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> casesAttachmentFile(HttpServletRequest httpServletRequest,
			CasesDataRequestDTO dataRequestDTO, @RequestParam("uploadFile") MultipartFile file)
			throws IllegalArgumentException, SecurityException, JsonProcessingException, ClassNotFoundException,
			ProxyException {

		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		Map<String, Object> widgetsDataRequest = new HashMap<>();
		try {

			widgetsDataRequest.put(WidgetConstants.SERVICE_ID, dataRequestDTO.getServiceId());
			MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, dataRequestDTO.getServiceId().toString());
			
			caseDataAppender.getStringFile(file, widgetsDataRequest);
			widgetsDataRequest = caseDataAppender.appendRequestData(dataRequestDTO, widgetsDataRequest);
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "[/upload-files] Request:==> {}",
					widgetsDataRequest.toString());
			Object response = proxyService.execute(widgetsDataRequest, httpServletRequest);
			return new ResponseEntity<>(response != null ? response.toString() : "", HttpStatus.OK);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(widgetsDataRequest), new AuditDate(startTime, endTime, executionTime), true);
		}
	}
	
	@Operation(summary = "Getting Cyber data for given parameter", description = "Getting data from supporting API in specific format for Cases for given parameter")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/cyberData", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> casesData(@Valid @RequestBody DeviceDataRequestDTO dataRequest,
			HttpServletRequest httpServletRequest)
			throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, ProxyException {

		Map<String, Object> widgetsDataRequest = new HashMap<>();
		widgetsDataRequest.put(WidgetConstants.SERVICE_ID, dataRequest.getServiceId());

		widgetsDataRequest = deviceDataAppender.appendRequestData(dataRequest, widgetsDataRequest);

		widgetsDataRequest.put(ProxyConstants.INPUT_PARAM, ProxyConstants.PATH_PARAM);
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "[deviceData] Request:==> {}", widgetsDataRequest.toString());
		Object response = proxyService.execute(widgetsDataRequest, httpServletRequest);
		return new ResponseEntity<>(response != null ? response.toString() : "", HttpStatus.OK);

	}
	
	@Operation(summary = "", description = "")
	@PostMapping(value = "/caseData/healthCheck")
	public String healthCheck(){
			return "Success";
	}
	
	@Operation(summary = "Getting data for given Plant ID ", description = "Getting data from supporting API in specific format for Widget based on plantId")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/csasdata", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> caseHistoryData(@RequestBody CSASRequestDTO dataRequest,
			HttpServletRequest httpServletRequest)
			throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, ProxyException {

		Map<String, Object> widgetsDataRequest = new HashMap<>();
		widgetsDataRequest.put(WidgetConstants.SERVICE_ID, dataRequest.getServiceId());
		widgetsDataRequest.put(ProxyConstants.DATE_RANGE, dataRequest.getDateRange());		

		widgetsDataRequest = caseHistoryAppender.appendRequestData(dataRequest, widgetsDataRequest);

		SecurityUtil.sanitizeLogging(logger, Level.INFO, "[CSASdata] Request:==> {}", widgetsDataRequest.toString());

		return new ResponseEntity<>(proxyService.execute(widgetsDataRequest, httpServletRequest).toString(),
				HttpStatus.OK);
	}


	@Operation(summary = "Getting pems data",
			description = "Getting data from API PeMS")
	@SecurityRequirement(name = "Keycloak Token")
	@PostMapping(value = "/pemsdata", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPemsData(@RequestBody PeMSRequestDTO dataRequest,
												  HttpServletRequest httpServletRequest)
			throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, ProxyException {

		Map<String, Object> widgetsDataRequest = new HashMap<>();
		widgetsDataRequest.put(ProxyConstants.SERVICE_ID, dataRequest.getServiceId());

		widgetsDataRequest = peMSAppender.appendRequestData(dataRequest, widgetsDataRequest);

		SecurityUtil.sanitizeLogging(logger, Level.INFO, "[Pemsdata] Request:==> {}", widgetsDataRequest.toString());

		return new ResponseEntity<>(proxyService.execute(widgetsDataRequest, httpServletRequest).toString(),
				HttpStatus.OK);
	}
	

}
