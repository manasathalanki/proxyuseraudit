package com.bh.cp.proxy.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.dto.request.CasesDataRequestDTO;
import com.bh.cp.proxy.service.ProxyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class CaseDataAppenderTest {

	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpServletResponse httpServletResponse;
	@Mock
	private UMSClientService umsClientService;
	@Mock
	private ProxyService proxyService;
	@InjectMocks
	private CaseDataAppender handler;

	CasesDataRequestDTO dataRequest;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		dataRequest = new CasesDataRequestDTO();
		List<String> projectId = new ArrayList<>();
		projectId.add("1");
		List<String> trainId = new ArrayList<>();
		trainId.add("2");
		List<String> lineupId = new ArrayList<>();
		lineupId.add("3");
		List<String> machineId = new ArrayList<>();
		machineId.add("4");
		List<String> criticalityId = new ArrayList<>();
		criticalityId.add("6");
		List<String> customerPriorityId = new ArrayList<>();
		customerPriorityId.add("7");
		List<String> status = new ArrayList<>();
		status.add("open");
		dataRequest.setProjectId(projectId);
		dataRequest.setTrainId(trainId);
		dataRequest.setLineupId(lineupId);
		dataRequest.setMachineId(machineId);
		dataRequest.setCaseNumber("5");
		dataRequest.setCriticalityId(criticalityId);
		dataRequest.setCustomerPriorityId(customerPriorityId);
		dataRequest.setStatus(status);
		dataRequest.setStartDate("123");
		dataRequest.setEndDate("143");

		dataRequest.setLockType("lock");
		dataRequest.setRootCause("No");
		dataRequest.setTaskId("123");
		dataRequest.setAction("action");
		dataRequest.setCommentDesc("desc");
		dataRequest.setCommentId("143");
		dataRequest.setCommentType("type");
		dataRequest.setCommentVisible("no");
		dataRequest.setUserType("EXTERNAL");
		dataRequest.setMimeType("check");
		dataRequest.setAttachmentTypeId("173");

		List<String> caseType = new ArrayList<>();
		caseType.add("External");
		List<String> catagoryTypeId = new ArrayList<>();
		catagoryTypeId.add("123");
		List<String> parentCaseId = new ArrayList<>();
		parentCaseId.add("123");
		List<String> caseTypes = new ArrayList<>();
		caseType.add("External");
		List<String> issueId = new ArrayList<>();
		issueId.add("123");
		List<String> nameLink = new ArrayList<>();
		nameLink.add("External");
		List<String> attachId = new ArrayList<>();
		attachId.add("123");

		dataRequest.setCaseType(caseTypes);
		dataRequest.setCatagoryId(catagoryTypeId);
		dataRequest.setParentCaseId(parentCaseId);
		dataRequest.setIssueId(issueId);
		dataRequest.setNameLink(nameLink);
		dataRequest.setAttachmentId(attachId);
		dataRequest.setAttachment("no");
		dataRequest.setImage("123");

		ReflectionTestUtils.setField(handler, "httpServletRequest", httpServletRequest);
		ReflectionTestUtils.setField(handler, "umsClientService", umsClientService);
		ReflectionTestUtils.setField(handler, "proxyService", proxyService);

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for CommentList Edit")
	void appendData() throws Exception {
		Map<String, Object> widgetsDataRequest = new HashMap<>();
		widgetsDataRequest.put("email", "kumuda.kurli@bakerhughes.com");
		widgetsDataRequest.put("title", "EXTERNAL");
		widgetsDataRequest.put(null, widgetsDataRequest);
		when(umsClientService.getUserDetails(httpServletRequest)).thenReturn(widgetsDataRequest);
		widgetsDataRequest = new HashMap<>();
		List<String> issueId = new ArrayList<>();
		issueId.add("166760527");
		issueId.add("166760264");
		widgetsDataRequest.put("flagInternal", "N");
		widgetsDataRequest.put("issueId", issueId);
		widgetsDataRequest.put("attachment", "Y");
		widgetsDataRequest.put("serviceId", "42");
		widgetsDataRequest.put("user", "kumuda.kurli@bakerhughes.com");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("attachmentCount", 4);
		List<Map<String, Object>> listFile = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("issueId", "166760264");
		map.put("fileName", "105_QG_LNG_TRAIN1_00105.pdf");
		map.put("attachType", "INSIGHT");
		map.put("file", "JVBERi0xLjUNCiW1tbW1DQoxIDAgb2JqDQo");
		map.put("fileMime", "application/octet-stream");
		map.put("uploadTimestamp", "2022-12-23T10:00:13.000Z");
		map.put("attachId", "365");
		listFile.add(map);
		map = new HashMap<>();
		map.put("issueId", "166760264");
		map.put("fileName", "105_QG_LNG_TRAIN1_00105.pdf");
		map.put("attachType", "INSIGHT");
		map.put("file", "JVBERi0xLjUNCiW1tbW1DQoxIDAgb2JqDQk");
		map.put("fileMime", "application/octet-stream");
		map.put("uploadTimestamp", "2022-12-23T10:00:13.000Z");
		map.put("attachId", "365");
		listFile.add(map);
		jsonObject.put("list", listFile);
		JSONObject response = new JSONObject();
		response.put("data", jsonObject);
		when(proxyService.execute(widgetsDataRequest, httpServletRequest)).thenReturn(response);
		handler.getAttachment(widgetsDataRequest, httpServletRequest, httpServletResponse);
		Map<String, Object> result = handler.appendRequestData(dataRequest, widgetsDataRequest);
		assertNotNull(result);
	}
}
