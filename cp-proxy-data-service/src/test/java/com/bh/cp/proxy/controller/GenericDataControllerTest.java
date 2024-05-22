package com.bh.cp.proxy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.MDC;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.dto.request.CSASRequestDTO;
import com.bh.cp.proxy.dto.request.CasesDataRequestDTO;
import com.bh.cp.proxy.dto.request.DeviceDataRequestDTO;
import com.bh.cp.proxy.dto.request.WidgetsDataRequestDTO;
import com.bh.cp.proxy.pojo.AuditDate;
import com.bh.cp.proxy.service.ProxyService;
import com.bh.cp.proxy.util.CaseDataAppender;
import com.bh.cp.proxy.util.CaseDetailsAppender;
import com.bh.cp.proxy.util.CaseHistoryAppender;
import com.bh.cp.proxy.util.DeviceDataAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class GenericDataControllerTest {
	@InjectMocks
	GenericDataController dataController;
	HttpServletRequest httpServletRequest;
	HttpServletResponse httpServletResponse;

	@Mock
	private ProxyService proxyService;
	@Autowired
	private MockMvc mockMvc;
	private WidgetsDataRequestDTO dataRequest;
	private CasesDataRequestDTO casesDataRequestDTO = new CasesDataRequestDTO();
	private DeviceDataRequestDTO deviceDataRequestDTO = new DeviceDataRequestDTO();
	@Mock
	private CaseDataAppender caseDataAppender;
	@Mock
	private CaseHistoryAppender caseHistoryAppender;
	@Mock
	private DeviceDataAppender deviceDataAppender;
	@Mock
	private CaseDetailsAppender saveCaseDetailsAppender;
	@Mock
	private CSASRequestDTO csasrequestDTO;
	ObjectMapper objectMapper = new ObjectMapper();
	Map<String, Object> widgetsDataRequest;
	Map<String, Object> caseDataRequest = new HashMap<>();
	Map<String, Object> csasDataRequest = new HashMap<>();
	List<HashMap<String, Object>> response = new ArrayList<>();
	HashMap<String, Object> map = new HashMap<>();
	List<HashMap<String, Object>> casemMap = new ArrayList<>();
	Map<String,Map<String,List<Map<String,Object>>>> result ;
	
	@Mock
	private AuditTrailAspect auditTrailAspect;

//	HashMap<String, Object> casemMap=new HashMap<>(); 

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(dataController).build();
		MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, "1");
		dataRequest = new WidgetsDataRequestDTO();
		dataRequest.setLevel("projects");
		dataRequest.setDateRange("3M");
		dataRequest.setWidgetId(21);
		dataRequest.setVid("PR_NOBLE");
		widgetsDataRequest = new HashMap<>();
		widgetsDataRequest.put(ProxyConstants.WIDGET_ID, dataRequest.getWidgetId());
		widgetsDataRequest.put(ProxyConstants.DATE_RANGE, dataRequest.getDateRange());
		widgetsDataRequest.put(ProxyConstants.VID, dataRequest.getVid());
		widgetsDataRequest.put(ProxyConstants.LEVEL, dataRequest.getLevel());
		List<String> list = new ArrayList<>();
		list.add("166760720");
		list.add("166760699");
		casesDataRequestDTO.setServiceId(45);
		casesDataRequestDTO.setIssueId(list);
		caseDataRequest.put(WidgetConstants.SERVICE_ID, casesDataRequestDTO.getServiceId());
		caseDataRequest.put("issueId", casesDataRequestDTO.getIssueId());

		map = new HashMap<>();
		map.put("issueId", "166760720");
		map.put("notificationId", "3");
		map.put("userEmail", "alessandro1.sarti@bakerhughes.com");
		map.put("userId", "105046221");
		map.put("dateSent", "2023-02-06T14:35:55.000Z");
		map.put("mailType", "CASE");
		map.put("commentId", "12");
		map.put("trendName", "TEST");
		response.add(map);

		List<String> caseList = new ArrayList<>();
		map = new HashMap<>();
		map.put("EWSCloseCount", 0);
		map.put("EWSCloseCount", 0);
		map.put("list", caseList);
		map.put("ETSOpenCount", 0);
		map.put("EWSOpenCount", 0);
		map.put("ETSCloseCount", 0);
		casemMap.add(map);
		
		
	    result = new HashMap<>();
		List<Map<String,Object>> resultlist = new ArrayList<>();
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("data",2);
		resultlist.add(resultMap);
		Map<String,List<Map<String,Object>>> resultMap2 = new HashMap<>();
		resultMap2.put("DeviceNotUpdatedCount", resultlist);
		result.put("data", resultMap2);
	}

	String resultResponse = response.toString();
	String caseResponse = casemMap.toString();

	@SuppressWarnings("unchecked")
	@Test
	void testWidgetsData() throws JsonProcessingException, Exception {
    when(proxyService.execute(any(Map.class),any(HttpServletRequest.class))).thenReturn("ok");
    auditTrailAspect.saveAuditTrailPerformance( "testFunctionality", new JSONObject(), new AuditDate(Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()), 0), false);
		
    mockMvc.perform(MockMvcRequestBuilders.post("/v1/widgetsdata").header("Authorization", "Bearer abc")
				.content(new ObjectMapper().writeValueAsString(dataRequest)).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content()
								.string("ok"));
		verify(proxyService).execute(any(Map.class),any(HttpServletRequest.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testcasesData() throws JsonProcessingException, Exception {
		List<String> list = new ArrayList<>();
		list.add("166760720");
		list.add("166760699");
		casesDataRequestDTO.setServiceId(45);
		casesDataRequestDTO.setIssueId(list);
		when(caseDataAppender.appendRequestData(any(), any(Map.class))).thenReturn(caseDataRequest);
		when(proxyService.execute(any(Map.class), any(HttpServletRequest.class))).thenReturn(resultResponse);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/casesdata").header("Authorization", "Bearer abc")
				.content(new ObjectMapper().writeValueAsString(casesDataRequestDTO))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(resultResponse));
	}

	@SuppressWarnings("unchecked")
	@Test
	void casesData() throws JsonProcessingException, Exception {
    when(proxyService.execute(any(Map.class),any(HttpServletRequest.class))).thenReturn(casemMap);
    Integer caseId=166760786;
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/casesdata/{caseId}",caseId).header("Authorization", "Bearer abc")
			.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
					MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
					MockMvcResultMatchers.content()
					.string(casemMap.toString()));
	}

	@SuppressWarnings("unchecked")
	@Test
	void attachmentDownload() throws JsonProcessingException, Exception {
		map = new HashMap<>();
		map.put("serviceId", 42);
		map.put("attachment", "Y");
		when(caseDataAppender.appendRequestData(any(), any(Map.class))).thenReturn(map);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/attachment/download").header("Authorization", "Bearer abc")
				.content(new ObjectMapper().writeValueAsString(casesDataRequestDTO))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk());
	}

	@SuppressWarnings("unchecked")
	@Test
	void uploadFile() throws JsonProcessingException, Exception {
		MockMultipartFile file = new MockMultipartFile("uploadFile", "filename.txt", MediaType.TEXT_PLAIN_VALUE,
				"miles to go before sleep".getBytes());
		when(caseDataAppender.appendRequestData(any(), any(Map.class))).thenReturn(map);
		when(proxyService.execute(any(Map.class), any(HttpServletRequest.class))).thenReturn(resultResponse);
		mockMvc.perform((MockMvcRequestBuilders.multipart("/v1/upload-files").file(file)
				.param("serviceId","44"))
				.contentType(MediaType.MULTIPART_FORM_DATA)).andExpectAll(MockMvcResultMatchers.status().isOk());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void cyberSecurity() throws JsonProcessingException, Exception {
		deviceDataRequestDTO.setServiceId(45);
		deviceDataRequestDTO.setPlantId("ABENGOA");
		when(deviceDataAppender.appendRequestData(any(), any(Map.class))).thenReturn(caseDataRequest);
		when(proxyService.execute(any(Map.class), any(HttpServletRequest.class))).thenReturn(result);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/cyberData").header("Authorization", "Bearer abc")
				.content(new ObjectMapper().writeValueAsString(deviceDataRequestDTO))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(result.toString()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void casesDataCsas() throws JsonProcessingException, Exception {
		csasrequestDTO.setServiceId(69);
		csasrequestDTO.setPlantId("ABENGOA");
		csasDataRequest.put("startDate","2023-03-25 0:0:0");
		csasDataRequest.put("endDate","2024-03-25 0:0:0");
		csasDataRequest.put("fromDate","2024-03-25 0:0:0");
		csasDataRequest.put("toDate","2024-03-25 0:0:0");
		csasDataRequest.put("dateRange","3M");
		csasDataRequest.put("lineupId","LN_L0574");
		when(caseHistoryAppender.appendRequestData(any(), any(Map.class))).thenReturn(caseDataRequest);
		when(proxyService.execute(any(Map.class), any(HttpServletRequest.class))).thenReturn(resultResponse);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/csasdata").header("Authorization", "Bearer abc")
				.content(new ObjectMapper().writeValueAsString(csasrequestDTO))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().json(resultResponse));
	}
	
	
	@Test
	void healthCheck() throws JsonProcessingException, Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/caseData/healthCheck").header("Authorization", "Bearer abc")
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk());
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	void casesDataList() throws JsonProcessingException, Exception {
    when(proxyService.execute(any(Map.class),any(HttpServletRequest.class))).thenReturn(casemMap);
    Integer caseId=166760786;
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/casesdatalist/{caseId}",caseId).header("Authorization", "Bearer abc")
			.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
					MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
					MockMvcResultMatchers.content()
					.string(casemMap.toString()));
	}
}	