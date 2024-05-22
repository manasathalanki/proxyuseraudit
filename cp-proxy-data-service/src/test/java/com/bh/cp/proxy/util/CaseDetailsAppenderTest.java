package com.bh.cp.proxy.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.slf4j.MDC;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.pojo.AttachmentResponse;
import com.bh.cp.proxy.pojo.CommentsResponse;
import com.bh.cp.proxy.pojo.TaskResponse;

import jakarta.servlet.http.HttpServletRequest;

class CaseDetailsAppenderTest {

	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private UMSClientService umsClientService;

	@Mock
	private CaseDetailsExecuter caseDetailsExecuter;
	
	@Mock
	private AuditTrailAspect auditTrailAspect;

	@InjectMocks
	private CaseDetailsAppender handler;

	String jsonString;

	Map<String, Object> response;
	JSONObject jsonObj;
	List<MultipartFile> file2 = new ArrayList<>();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, "1");

		jsonString = "{\"taskList\":[],\"commentsList\":[],\"attachmentList\":[],\"editCase\":{\"caseId\":\"166761515\",\"customerPriority\":\"HIGH\",\"customerWO\":\"LOW\"}}";

		ReflectionTestUtils.setField(handler, "httpServletRequest", httpServletRequest);
		ReflectionTestUtils.setField(handler, "umsClientService", umsClientService);
		ReflectionTestUtils.setField(handler, "caseDetailsExecuter", caseDetailsExecuter);

	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for CommentList Edit")
	void testParse_EditCaseCommentsList() throws Exception {
		List<String> commentsList = new ArrayList<>();
		Map<String, Object> userResponse = new HashMap<>();
		userResponse.put("email", "kumuda.kurli@bakerhughes.com");
		String s1 = "{\"issueId\":166761515,\"commentId\":2546}";
		List<String> executeList = new ArrayList<>();
		executeList.add(s1);
		when(umsClientService.getUserDetails(httpServletRequest)).thenReturn(userResponse);
		when(caseDetailsExecuter.execute(commentsList, 52)).thenReturn(executeList);

		Map<String, Object> result = handler.appendRequestData(jsonString, null);
		assertNotNull(result);
		assertEquals(2546, ((List<CommentsResponse>) result.get("CommentsResponse")).get(0).getCommentId());
		assertEquals(166761515, ((List<CommentsResponse>) result.get("CommentsResponse")).get(0).getIssueId());

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for CommentList Edit with inputs")
	void testParse_commentsListInputs() throws Exception {
		jsonString = "{\"taskList\":[],\"commentsList\":[{\"action\":\"ADD\",\"caseId\":\"166761515\",\"commentDesc\":\"Test1\",\"commentId\":\"942\",\"commentType\":\"CASEUPD\",\"commentVisible\":\"Y\",\"userType\":\"INTERNAL\"}],\"attachmentList\":[]}";
		List<String> commentsList = new ArrayList<>();
		Map<String, Object> userResponse = new HashMap<>();
		userResponse.put("email", "kumuda.kurli@bakerhughes.com");
		String s1 = "{\"issueId\":166761515,\"commentId\":2546}";
		List<String> executeList = new ArrayList<>();
		executeList.add(s1);
		when(umsClientService.getUserDetails(httpServletRequest)).thenReturn(userResponse);
		when(caseDetailsExecuter.execute(commentsList, 52)).thenReturn(executeList);
		Map<String, Object> result = handler.appendRequestData(jsonString, null);
		assertNotNull(result);

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for Editcase")
	void testParse_EditCase() throws Exception {

		List<String> caseEdit = new ArrayList<>();

		Map<String, Object> userResponse = new HashMap<>();
		userResponse.put("email", "kumuda.kurli@bakerhughes.com");
		String s1 = "{\"caseId\":\"166761515\",\"rev\":\"70\"}";
		List<String> executeList = new ArrayList<>();
		executeList.add(s1);
		when(umsClientService.getUserDetails(httpServletRequest)).thenReturn(userResponse);
		when(caseDetailsExecuter.execute(caseEdit, 59)).thenReturn(executeList);

		Map<String, Object> result = handler.appendRequestData(jsonString, null);
		assertNotNull(result);

	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for  TaskList Edit")
	void testParse_EditCaseEditTask() throws Exception {

		List<String> taskList = new ArrayList<>();

		Map<String, Object> userResponse = new HashMap<>();
		userResponse.put("email", "kumuda.kurli@bakerhughes.com");

		String s1 = "{\"taskId\":5905,\"status\":\"CLOSED\",\"rootCause\":\"YES\"}";
		List<String> executeList = new ArrayList<>();
		executeList.add(s1);
		when(umsClientService.getUserDetails(httpServletRequest)).thenReturn(userResponse);
		when(caseDetailsExecuter.execute(taskList, 55)).thenReturn(executeList);

		Map<String, Object> result = handler.appendRequestData(jsonString, null);
		assertNotNull(result);
		assertEquals(5905, ((List<TaskResponse>) result.get("TaskResponse")).get(0).getTaskId());
		assertEquals("CLOSED", ((List<TaskResponse>) result.get("TaskResponse")).get(0).getStatus());

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for  TaskList Edit Inputs")
	void testParse_EditCaseEditTaskInputs() throws Exception {

		jsonString = "{\"taskList\":[{\"caseId\":\"166761515\",\"rootCause\":\"YES\",\"status\":\"CLOSED\",\"taskId\":\"5905\"}],\"commentsList\":[],\"attachmentList\":[]}";
		List<String> taskList = new ArrayList<>();

		Map<String, Object> userResponse = new HashMap<>();
		userResponse.put("email", "kumuda.kurli@bakerhughes.com");

		String s1 = "{\"taskId\":5905,\"status\":\"CLOSED\",\"rootCause\":\"YES\"}";
		List<String> executeList = new ArrayList<>();
		executeList.add(s1);
		when(umsClientService.getUserDetails(httpServletRequest)).thenReturn(userResponse);
		when(caseDetailsExecuter.execute(taskList, 55)).thenReturn(executeList);

		Map<String, Object> result = handler.appendRequestData(jsonString, null);
		assertNotNull(result);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for AttachmentList Edit")
	void testParse_EditCaseAttachment() throws Exception {
		List<String> attachList = new ArrayList<>();
		Map<String, Object> userResponse = new HashMap<>();
		userResponse.put("email", "kumuda.kurli@bakerhughes.com");
		String s1 = "{\"issueId\":166761515,\"attachmentId\":1612}";
		List<String> executeList = new ArrayList<>();
		executeList.add(s1);
		when(umsClientService.getUserDetails(httpServletRequest)).thenReturn(userResponse);
		when(caseDetailsExecuter.execute(attachList, 58)).thenReturn(executeList);

		Map<String, Object> result = handler.appendRequestData(jsonString, file2);
		assertNotNull(result);
		assertEquals(1612, ((List<AttachmentResponse>) result.get("AttachmentResponse")).get(0).getAttachmentId());
		assertEquals(166761515, ((List<AttachmentResponse>) result.get("AttachmentResponse")).get(0).getIssueId());

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for AttachmentList Edit Inputs")
	void testParse_EditCaseAttachmentInputs() throws Exception {

		jsonString = "{\"taskList\":[],\"commentsList\":[],\"attachmentList\":[{\"action\":\"ADD\",\"attachtId\":\"1234\",\"caseId\":\"166761515\",\"attachmentTypeId\":\"PICTURE\",\"fileName\":\"Case Lifecycles (1).png\",\"mimeType\":\"application/pdf\",\"userType\":\"INTERNAL\"}]}";

		List<String> attachList = new ArrayList<>();
		Map<String, Object> userResponse = new HashMap<>();
		userResponse.put("email", "kumuda.kurli@bakerhughes.com");
		String s1 = "{\"issueId\":166761515,\"attachmentId\":1612}";
		List<String> executeList = new ArrayList<>();
		executeList.add(s1);
		when(umsClientService.getUserDetails(httpServletRequest)).thenReturn(userResponse);
		when(caseDetailsExecuter.execute(attachList, 58)).thenReturn(executeList);

		Map<String, Object> result = handler.appendRequestData(jsonString, null);
		assertNotNull(result);

	}

}
