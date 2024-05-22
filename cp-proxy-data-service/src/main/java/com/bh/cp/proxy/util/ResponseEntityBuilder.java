package com.bh.cp.proxy.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.ResponseEntity;

import com.bh.cp.proxy.pojo.ResponseFormat;
import com.google.gson.Gson;

public class ResponseEntityBuilder {

	private static final Logger logger = LogManager.getLogger(ResponseEntityBuilder.class);

	private ResponseFormat createResponseObject(int httpCode, String userMessage, Object data) {
		ThreadContext.put("LEGLevel", "LEG4");
		ResponseFormat responseFormat = new ResponseFormat();
		responseFormat.setData(data);
		responseFormat.setHttpCode(httpCode);
		responseFormat.setUserMessage(userMessage);
		responseFormat.setUserMessageCode(ThreadContext.get("correlationId"));
		logger.info("In ResponseEntityBuilder {} ", responseFormat);
		return responseFormat;
	}

	public ResponseEntity<Object> createFailureResponse(int httpCode, Object data) {
		ResponseFormat responseFormat = createResponseObject(httpCode, "Failure", data);
		return ResponseEntity.status(httpCode).body(responseFormat);
	}

	public ResponseEntity<Object> createFailureResponse(int httpCode) {
		ResponseFormat responseFormat = createResponseObject(httpCode, "Failure", null);
		return ResponseEntity.status(httpCode).body(responseFormat);
	}

	public ResponseEntity<Object> createSuccessResponse(int httpCode, String userMessage, Object data) {
		ResponseFormat responseFormat = createResponseObject(httpCode, userMessage,
				new Gson().fromJson(data.toString(), Object.class));
		return ResponseEntity.status(httpCode).body(responseFormat);
	}

	public ResponseEntity<Object> createExceptionResponse(int httpCode, String userMessage) {
		ResponseFormat responseFormat = createResponseObject(httpCode, userMessage, null);
		logger.error("Exception: {} ", responseFormat);
		return ResponseEntity.status(httpCode).body(responseFormat);
	}

}
