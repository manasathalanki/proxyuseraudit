package com.bh.cp.proxy.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.dto.request.AttachmentsDTO;
import com.bh.cp.proxy.dto.request.CaseCommentsDTO;
import com.bh.cp.proxy.dto.request.CaseDetailsDTO;
import com.bh.cp.proxy.dto.request.CaseTaskDTO;
import com.bh.cp.proxy.dto.request.EditCase;
import com.bh.cp.proxy.pojo.AttachmentResponse;
import com.bh.cp.proxy.pojo.AuditDate;
import com.bh.cp.proxy.pojo.CommentsResponse;
import com.bh.cp.proxy.pojo.EditCaseResponse;
import com.bh.cp.proxy.pojo.TaskResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CaseDetailsAppender {
	private static final Logger logger = LoggerFactory.getLogger(CaseDetailsAppender.class);

	private HttpServletRequest httpServletRequest;

	private UMSClientService umsClientService;

	private CaseDetailsExecuter caseDetailsExecuter;

	private AuditTrailAspect auditTrailAspect;

	public CaseDetailsAppender(@Autowired HttpServletRequest httpServletRequest,
			@Autowired UMSClientService umsClientService, @Autowired CaseDetailsExecuter caseDetailsExecuter,
			@Autowired AuditTrailAspect auditTrailAspect) {
		super();
		this.httpServletRequest = httpServletRequest;
		this.umsClientService = umsClientService;
		this.caseDetailsExecuter = caseDetailsExecuter;
		this.auditTrailAspect = auditTrailAspect;
	}

	private List<MultipartFile> file;

	public Map<String, Object> appendRequestData(String dataRequest, List<MultipartFile> file2)
			throws JsonProcessingException {
		Map<String, Object> outputObject = new HashMap<>();
		CaseDetailsDTO caseDetailsDTO;

		caseDetailsDTO = new Gson().fromJson(dataRequest.replaceAll(ProxyConstants.FIELD_EXPRESSION, ""),
				CaseDetailsDTO.class);

		this.file = file2;

		String message = "Success";

		setOutputObject(caseDetailsDTO, outputObject, message);
		if (!(caseDetailsDTO.getAttachmentList() == null && caseDetailsDTO.getAttachmentList().isEmpty())) {
			List<AttachmentResponse> list = setAttachmentListData(caseDetailsDTO.getAttachmentList()).stream()
					.map(attachmentResponseJson -> {
						if (attachmentResponseJson.contains(JSONUtilConstants.ERROR)) {
							return new AttachmentResponse(0, 0, attachmentResponseJson);
						}
						JSONObject attachmentResponseJsonObj = new JSONObject(attachmentResponseJson);
						Integer attachId = 0;
						if (attachmentResponseJsonObj.has(JSONUtilConstants.ERROR_DETAILS)) {
							JSONObject error = (JSONObject) attachmentResponseJsonObj
									.get(JSONUtilConstants.ERROR_DETAILS);
							return new AttachmentResponse(0, 0, error.get(JSONUtilConstants.ERROR_GENERIC).toString());
						}
						String attachmentId = attachmentResponseJsonObj.has("attachmentId")
								? attachmentResponseJsonObj.get("attachmentId").toString()
								: "";
						attachId = attachIdvalue(attachmentId);

						return new AttachmentResponse(attachmentResponseJsonObj.getInt("issueId"), attachId, message);
					}).toList();
			outputObject.put("AttachmentResponse", list);
		}
		if (caseDetailsDTO.getEditCase() != null) {

			List<Object> res = setCase(caseDetailsDTO.getEditCase()).stream().map(editCaseResponseJson -> {
				if (editCaseResponseJson.contains(JSONUtilConstants.ERROR)) {
					return editCaseResponseJson;
				}
				JSONObject editCaseResponseJsonObj = new JSONObject(editCaseResponseJson);
				return new EditCaseResponse(editCaseResponseJsonObj.getString("caseId"),
						editCaseResponseJsonObj.getString("rev"));

			}).toList();

			outputObject.put("EditCaseResponse", res);
		}

		return outputObject;

	}

	private List<String> setCase(EditCase editCase) {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		List<String> caseEdit = new ArrayList<>();
		try {
			MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, "59");
			Map<String, Object> caseData = new HashMap<>();
			Map<String, Object> caseData1 = null;
			Map<String, Object> caseData2 = null;
			JSONObject obj = null;
			JSONObject obj1 = null;
			org.json.JSONArray array = null;

			if (!(StringUtil.isEmptyCaseString(editCase.getCaseId()))) {
				caseData.put("caseId", editCase.getCaseId());
			}
			if (editCase.getCustomerPriority() != null) {
				caseData1 = new HashMap<>();
				caseData1.put("parameter", ProxyConstants.FIELD_CUSTPRIOR);
				caseData1.put("value", editCase.getCustomerPriority());
				obj = new JSONObject(caseData1);
				array = new org.json.JSONArray("[" + obj.toString() + "]");
			}
			if (editCase.getCustomerWO() != null) {
				caseData2 = new HashMap<>();
				caseData2.put("parameter", ProxyConstants.FIELD_CUSTWO);
				caseData2.put("value", editCase.getCustomerWO());
				obj1 = new JSONObject(caseData2);
				array = new org.json.JSONArray("[" + obj1.toString() + "]");
			}
			if (obj != null && obj1 != null) {
				array = new org.json.JSONArray("[" + obj.toString() + "," + obj1.toString() + "]");
			}

			caseData.put("user", getUserEmail());

			JSONObject jsonObj = new JSONObject(caseData);
			String editCaseData = jsonObj.put("caseParams", array).toString();
			caseEdit.add(editCaseData);

			return caseDetailsExecuter.execute(caseEdit, 59);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(caseEdit), new AuditDate(startTime, endTime, executionTime), true);
		}
	}

	private Map<String, Object> setOutputObject(CaseDetailsDTO caseDetailsDTO, Map<String, Object> outputObject,
			String message) throws JsonProcessingException {

		List<TaskResponse> taskResponseList;

		if (!(caseDetailsDTO.getCommentsList() == null && caseDetailsDTO.getCommentsList().isEmpty())) {
			List<CommentsResponse> list = setCommentListData(caseDetailsDTO.getCommentsList()).stream()
					.map(commentsResponseJson -> {
						if (commentsResponseJson.contains(JSONUtilConstants.ERROR)) {
							return new CommentsResponse(0, 0, commentsResponseJson);
						}
						JSONObject commentsResponseJsonObj = new JSONObject(commentsResponseJson);
						if (commentsResponseJsonObj.has(JSONUtilConstants.ERROR_DETAILS)) {
							JSONObject error = (JSONObject) commentsResponseJsonObj
									.get(JSONUtilConstants.ERROR_DETAILS);
							return new CommentsResponse(0, 0, error.get(JSONUtilConstants.ERROR_GENERIC).toString());
						}
						return new CommentsResponse(commentsResponseJsonObj.getInt("issueId"),
								commentsResponseJsonObj.getInt("commentId"), message);
					}).toList();
			outputObject.put("CommentsResponse", list);
		}

		taskResponseList = listResponse(caseDetailsDTO, message);

		outputObject.put("TaskResponse", taskResponseList);

		return outputObject;
	}

	private List<TaskResponse> listResponse(CaseDetailsDTO caseDetailsDTO, String message)
			throws JsonProcessingException {

		List<TaskResponse> list = new ArrayList<>();

		if (!(caseDetailsDTO.getTaskList() == null && caseDetailsDTO.getTaskList().isEmpty())) {
			list = setTaskListData(caseDetailsDTO.getTaskList()).stream().map(taskResponseJson -> {
				if (taskResponseJson.contains(JSONUtilConstants.ERROR)) {
					return new TaskResponse(0, "", "", taskResponseJson);
				}
				JSONObject taskResponseJsonObj = new JSONObject(taskResponseJson);
				if (taskResponseJsonObj.has(JSONUtilConstants.ERROR_DETAILS)) {
					JSONObject error = (JSONObject) taskResponseJsonObj.get(JSONUtilConstants.ERROR_DETAILS);
					return new TaskResponse(0, "", "", error.get(JSONUtilConstants.ERROR_GENERIC).toString());
				}
				return new TaskResponse(taskResponseJsonObj.getInt("taskId"), taskResponseJsonObj.getString("status"),
						taskResponseJsonObj.getString("rootCause"), message);
			}).toList();
		}

		return list;
	}

	private List<String> setCommentListData(List<CaseCommentsDTO> caseCommentList) throws JsonProcessingException {
		List<String> commentsList = new ArrayList<>();
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		try {
			MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, "52");
			Map<String, Object> caseData;
			for (CaseCommentsDTO comments : caseCommentList) {
				caseData = new HashMap<>();
				if (!(StringUtil.isEmptyCaseString(comments.getCaseId()))) {
					caseData.put(JSONUtilConstants.FIELD_CASEID, Integer.parseInt(comments.getCaseId()));
				}
				if (!(StringUtil.isEmptyCaseString(comments.getAction()))) {
					caseData.put("action", comments.getAction());
				}
				if (!(StringUtil.isEmptyCaseString(comments.getCommentDesc()))) {
					caseData.put("commentDesc", comments.getCommentDesc());
				}
				responseResult(comments, caseData);
				ObjectMapper objectMapper = new ObjectMapper();
				String jacksonData = objectMapper.writeValueAsString(caseData);

				commentsList.add(jacksonData);
			}
			return caseDetailsExecuter.execute(commentsList, 52);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(commentsList), new AuditDate(startTime, endTime, executionTime), true);
		}
	}

	private void responseResult(CaseCommentsDTO comments, Map<String, Object> caseData) {
		if (!(StringUtil.isEmptyCaseString(comments.getCommentId()))) {
			caseData.put("commentId", Integer.parseInt(comments.getCommentId()));
		}
		if (!(StringUtil.isEmptyCaseString(comments.getCommentType()))) {
			caseData.put("commentType", comments.getCommentType());
		}
		if (!(StringUtil.isEmptyCaseString(comments.getCommentVisible()))) {
			caseData.put("commentVisible", comments.getCommentVisible());
		}
		if (!(StringUtil.isEmptyCaseString(comments.getUserType()))) {
			caseData.put("userType", comments.getUserType());
		}
		if (!(StringUtil.isEmptyCaseString(getUserEmail()))) {
			caseData.put("user", getUserEmail());
		}
	}

	private List<String> setTaskListData(List<CaseTaskDTO> caseTaskList) throws JsonProcessingException {
		List<String> taskList = new ArrayList<>();
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		try {
			Map<String, Object> caseData;
			MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, "55");
			for (CaseTaskDTO tasks : caseTaskList) {
				caseData = new HashMap<>();
				if (!(StringUtil.isEmptyCaseString(tasks.getCaseId()))) {
					caseData.put(JSONUtilConstants.FIELD_CASEID, Integer.parseInt(tasks.getCaseId()));
				}
				if (!(StringUtil.isEmptyCaseString(tasks.getTaskId()))) {
					caseData.put("taskId", Integer.parseInt(tasks.getTaskId()));
				}
				if (!(StringUtil.isEmptyCaseString(tasks.getRootCause()))) {
					caseData.put("rootCause", tasks.getRootCause());
				}
				if (!(StringUtil.isEmptyCaseString(tasks.getStatus()))) {
					caseData.put("status", tasks.getStatus());
				}
				if (!(StringUtil.isEmptyCaseString(getUserEmail()))) {
					caseData.put("user", getUserEmail());
				}

				ObjectMapper objectMapper = new ObjectMapper();
				String jacksonData = objectMapper.writeValueAsString(caseData);
				taskList.add(jacksonData);
			}
			return caseDetailsExecuter.execute(taskList, 55);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(taskList), new AuditDate(startTime, endTime, executionTime), true);
		}
	}

	private List<String> setAttachmentListData(List<AttachmentsDTO> attachmentList) throws JsonProcessingException {
		List<String> attachList = new ArrayList<>();
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		try {
			Map<String, Object> caseData;
			MDC.put(ProxyConstants.PERF_AUDIT_SERVICE_ID, "58");
			for (AttachmentsDTO attachDto : attachmentList) {
				caseData = new HashMap<>();
				if (!(StringUtil.isEmptyCaseString(attachDto.getCaseId()))) {
					caseData.put(JSONUtilConstants.FIELD_CASEID, Integer.parseInt(attachDto.getCaseId()));
				}
				if (!(StringUtil.isEmptyCaseString(attachDto.getAttachtId()))) {
					caseData.put("attachtId", Integer.parseInt(attachDto.getAttachtId()));
				}
				if (!(StringUtil.isEmptyCaseString(attachDto.getAction()))) {
					caseData.put("action", attachDto.getAction());
				}
				if (!(StringUtil.isEmptyCaseString(attachDto.getAttachmentTypeId()))) {
					caseData.put("attachmentTypeId", attachDto.getAttachmentTypeId());
				}
				if (!(StringUtil.isEmptyCaseString(attachDto.getMimeType()))) {
					caseData.put("mimeType", attachDto.getMimeType());
				}
				if (!(StringUtil.isEmptyCaseString(attachDto.getUserType()))) {
					caseData.put("userType", attachDto.getUserType());
				}
				if (!(StringUtil.isEmptyCaseString(getUserEmail()))) {
					caseData.put("user", getUserEmail());
				}
				getStringFile(file, caseData, attachDto);

				ObjectMapper objectMapper = new ObjectMapper();
				String jacksonData = objectMapper.writeValueAsString(caseData);

				attachList.add(jacksonData);
			}

			return caseDetailsExecuter.execute(attachList, 58);
		} finally {
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
			long executionTime = endTime.getTime() - startTime.getTime();
			auditTrailAspect.saveAuditTrailPerformance(
					(new StringBuilder(this.getClass().getCanonicalName()).append(".")
							.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
					new JSONObject(attachList), new AuditDate(startTime, endTime, executionTime), true);
		}
	}

	private String getUserEmail() {
		String userEmail = null;
		try {
			Map<String, Object> userResponse = umsClientService.getUserDetails(httpServletRequest);

			String email = (String) userResponse.get("email");

			if (!(StringUtil.isEmptyCaseString(email))) {
				userEmail = email;
			}

		} catch (JsonProcessingException e) {

			logger.info(e.getMessage(), e);
		}
		return userEmail;
	}

	private Integer attachIdvalue(String attachmentId) {

		Integer attachId = 0;

		if (!attachmentId.equals("null")) {
			attachId = Integer.parseInt(attachmentId);
		}

		return attachId;
	}

	public Map<String, Object> getStringFile(List<MultipartFile> attachments, Map<String, Object> caseData,
			AttachmentsDTO attachDto) {

		byte[] fileBytes;
		String fileName = null;
		String encodedString = null;
		try {
			if (attachments != null && !attachments.isEmpty()) {
				for (MultipartFile file1 : attachments) {
					fileName = file1.getOriginalFilename();
					if (fileName != null && fileName.equalsIgnoreCase(attachDto.getFileName())) {
						fileBytes = file1.getBytes();
						encodedString = Base64.getEncoder().encodeToString(fileBytes);
						callEncodedString(encodedString,fileName,caseData);
					}
				}
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return caseData;

	}

	private void callEncodedString(String encodedString, String fileName, Map<String, Object> caseData) {
		if (encodedString != null) {
			caseData.put("fileName", fileName);
			if (encodedString.equals("")) {
				encodedString = "null";
			}
			caseData.put("file", encodedString);
		}
	}

}
