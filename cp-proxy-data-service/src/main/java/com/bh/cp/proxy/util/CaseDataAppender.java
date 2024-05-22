package com.bh.cp.proxy.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.dto.request.CasesDataRequestDTO;
import com.bh.cp.proxy.exception.ProxyException;
import com.bh.cp.proxy.service.ProxyService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CaseDataAppender {
	private static final Logger logger = LoggerFactory.getLogger(CaseDataAppender.class);

	private HttpServletRequest httpServletRequest;

	private UMSClientService umsClientService;

	private ProxyService proxyService;

	public CaseDataAppender(@Autowired HttpServletRequest httpServletRequest,
			@Autowired UMSClientService umsClientService, @Autowired ProxyService proxyService) {
		super();
		this.httpServletRequest = httpServletRequest;
		this.umsClientService = umsClientService;
		this.proxyService = proxyService;

	}

	public Map<String, Object> appendRequestData(CasesDataRequestDTO dataRequest,
			Map<String, Object> widgetsDataRequest) {

		if (!(StringUtil.isEmptyList(dataRequest.getProjectId()))) {
			widgetsDataRequest.put("projectId", dataRequest.getProjectId());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getPlantId()))) {
			widgetsDataRequest.put("plantId", dataRequest.getPlantId());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getTrainId()))) {
			widgetsDataRequest.put("trainId", dataRequest.getTrainId());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getLineupId()))) {
			widgetsDataRequest.put("lineupId", dataRequest.getLineupId());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getMachineId()))) {
			widgetsDataRequest.put("machineId", dataRequest.getMachineId());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getCaseNumber()))) {
			widgetsDataRequest.put("caseNumber", dataRequest.getCaseNumber());
			widgetsDataRequest.put("caseId", Integer.parseInt(dataRequest.getCaseNumber()));
		}
		if (!(StringUtil.isEmptyList(dataRequest.getCriticalityId()))) {
			widgetsDataRequest.put("criticality", dataRequest.getCriticalityId());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getCustomerPriorityId()))) {
			widgetsDataRequest.put("customerPriority", dataRequest.getCustomerPriorityId());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getStatus()))) {
			widgetsDataRequest.put("status", dataRequest.getStatus());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getStartDate()))) {
			widgetsDataRequest.put("startDate", dataRequest.getStartDate());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getEndDate()))) {
			widgetsDataRequest.put("endDate", dataRequest.getEndDate());
		}
		firstMethodToAppend(widgetsDataRequest, dataRequest);
		secondMethodToAppend(widgetsDataRequest, dataRequest);
		appenduser(widgetsDataRequest);
		return widgetsDataRequest;
	}

	private void firstMethodToAppend(Map<String, Object> widgetsDataRequest, CasesDataRequestDTO dataRequest) {
		if (!(StringUtil.isEmptyCaseString(dataRequest.getLockType()))) {
			widgetsDataRequest.put("lockType", dataRequest.getLockType());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getRootCause()))) {
			widgetsDataRequest.put("rootCause", dataRequest.getRootCause());
		}

		if (!(StringUtil.isEmptyCaseString(dataRequest.getTaskId()))) {
			widgetsDataRequest.put("taskId", dataRequest.getTaskId());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getAction()))) {
			widgetsDataRequest.put("action", dataRequest.getAction());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getCommentDesc()))) {
			widgetsDataRequest.put("commentDesc", dataRequest.getCommentDesc());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getCommentId()))) {
			widgetsDataRequest.put("commentId", Integer.parseInt(dataRequest.getCommentId()));
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getCommentType()))) {
			widgetsDataRequest.put("commentType", dataRequest.getCommentType());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getCommentVisible()))) {
			widgetsDataRequest.put("commentVisible", dataRequest.getCommentVisible());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getUserType()))) {
			widgetsDataRequest.put("userType", dataRequest.getUserType());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getMimeType()))) {
			widgetsDataRequest.put("mimeType", dataRequest.getMimeType());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getAttachmentTypeId()))) {
			widgetsDataRequest.put("attachmentTypeId", dataRequest.getAttachmentTypeId());
		}
	}

	private void secondMethodToAppend(Map<String, Object> widgetsDataRequest, CasesDataRequestDTO dataRequest) {
		if (!(StringUtil.isEmptyList(dataRequest.getCaseType()))) {
			widgetsDataRequest.put("caseType", dataRequest.getCaseType());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getCatagoryId()))) {
			widgetsDataRequest.put("catagoryId", dataRequest.getCatagoryId());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getParentCaseId()))) {
			widgetsDataRequest.put("parentCaseId", dataRequest.getParentCaseId());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getImage()))) {
			widgetsDataRequest.put("image", dataRequest.getImage());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getIssueId()))) {
			widgetsDataRequest.put("issueId", dataRequest.getIssueId());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getNameLink()))) {
			widgetsDataRequest.put("nameLink", dataRequest.getNameLink());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getAttachment()))) {
			widgetsDataRequest.put("attachment", dataRequest.getAttachment());
		}
		if (!(StringUtil.isEmptyList(dataRequest.getAttachmentId()))) {
			widgetsDataRequest.put("attachmentId", dataRequest.getAttachmentId());
		}
	}

	public void appenduser(Map<String, Object> widgetsDataRequest) {
		try {
			Map<String, Object> userResponse = umsClientService.getUserDetails(httpServletRequest);

			String email = (String) userResponse.get("email");

			if (!(StringUtil.isEmptyCaseString(email))) {
				widgetsDataRequest.put("user", email);
			}
			String title = (String) userResponse.get("title");

			if (!(StringUtil.isEmptyCaseString(title)) && (title.equalsIgnoreCase(ProxyConstants.EXTERNAL_USER))) {
				widgetsDataRequest.put("flagInternal", ProxyConstants.FLAG_INTERNAL);

			}

		} catch (JsonProcessingException e) {

			logger.info(e.getMessage(), e);

		}
	}

	@SuppressWarnings("unchecked")
	public void getAttachment(Map<String, Object> widgetsDataRequest, HttpServletRequest httpServletRequest2,
			HttpServletResponse response) {
		try {
			JSONObject proxyJsonObject = (JSONObject) proxyService.execute(widgetsDataRequest, httpServletRequest2);

			Map<String, Object> caseAttachResponse = null;

			if (proxyJsonObject != null) {
				caseAttachResponse = (Map<String, Object>) proxyJsonObject.toMap().get(WidgetConstants.DATA);
			}
			if (caseAttachResponse != null) {
				List<Map<String, Object>> attachFileList = (List<Map<String, Object>>) caseAttachResponse
						.get(WidgetConstants.LIST);

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				fileDownloadExtraction(byteArrayOutputStream, attachFileList, response);
			}

		} catch (ProxyException | IOException | ClassNotFoundException | IllegalArgumentException
				| SecurityException e) {
			logger.info(e.getMessage(), e);
		}
	}

	private void fileDownloadExtraction(ByteArrayOutputStream byteArrayOutputStream,
			List<Map<String, Object>> attachFileList, HttpServletResponse response)
			throws IOException {
		String caseId = null;
		List<String> fName = new ArrayList<>();
		if (attachFileList.size() > 1) {
			try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
				for (int i = 0; i < attachFileList.size(); i++) {
					Map<String, Object> mapRes1 = attachFileList.get(i);
					caseId = mapRes1.get(ProxyConstants.ISSUE_ID).toString();
					String fileName = mapRes1.get(ProxyConstants.FILE_NAME).toString();
					if (!fName.contains(fileName)) {
						fName.add(fileName);
					} else {
						String[] split = fileName.split("\\.");
						String part1 = split[0];
						String part2 = split[1];
						String j = Integer.toString(i);
						fileName = part1.concat(ProxyConstants.UNDERSCORE).concat(j).concat(ProxyConstants.DOT)
								.concat(part2);
					}
					addToZip(fileName, Base64.getDecoder().decode(mapRes1.get("file").toString()), zipOut);
				}

				zipOut.finish();
				byte[] zipBytes = byteArrayOutputStream.toByteArray();
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment; filename=" + caseId + ".zip");
				response.setContentLength(zipBytes.length);
				response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
				FileCopyUtils.copy(zipBytes, response.getOutputStream());
				response.flushBuffer();
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
		} else {
			String fileName = attachFileList.get(0).get(ProxyConstants.FILE_NAME).toString();
			byte[] file = Base64.getDecoder().decode(attachFileList.get(0).get("file").toString());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentLength(file.length);
			response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
			FileCopyUtils.copy(file, response.getOutputStream());
			response.flushBuffer();
		}

	}

	private static void addToZip(String fileName, byte[] fileContent, ZipOutputStream zipOut) throws IOException {
		zipOut.putNextEntry(new ZipEntry(fileName));
		zipOut.write(fileContent);
		zipOut.closeEntry();
	}

	public void getStringFile(MultipartFile file, Map<String, Object> widgetsDataRequest) {

		byte[] fileBytes;
		String fileName = null;
		String encodedString = null;
		try {
			fileName = file.getOriginalFilename();
			fileBytes = file.getBytes();
			encodedString = Base64.getEncoder().encodeToString(fileBytes);

			if (encodedString != null) {
				widgetsDataRequest.put("fileName", fileName);
				widgetsDataRequest.put("file", encodedString);

			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

	}

}
