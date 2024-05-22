package com.bh.cp.proxy.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.bh.cp.proxy.constants.WidgetConstants;
import com.bh.cp.proxy.pojo.ResponseFormat;

class ResponseEntityBuilderTest {

	@InjectMocks
	private ResponseEntityBuilder responseEntityBuilder;
	
	private ResponseFormat actualResponseFormat;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		actualResponseFormat=new ResponseFormat();
		actualResponseFormat.setData(new JSONObject().put(WidgetConstants.DATA, WidgetConstants.NODATAFOUND));
		actualResponseFormat.setHttpCode(503);
		actualResponseFormat.setUserMessage("Internal Server Error");
	}

	@Test
	@DisplayName("TestCreateFailureResponse - With HttpCode")
	void testCreateFailureResponseWithHttpStatusCode() {
		actualResponseFormat.setUserMessage("Failure");
		actualResponseFormat.setData(null);
		ResponseEntity<Object> expectedResponse = responseEntityBuilder.createFailureResponse(503);
		ResponseFormat expectedResponseFormat=(ResponseFormat) expectedResponse.getBody();
		assertEquals(actualResponseFormat.getHttpCode(), expectedResponseFormat.getHttpCode());
		assertEquals(actualResponseFormat.getData(), expectedResponseFormat.getData());
		assertEquals(actualResponseFormat.getUserMessage(), expectedResponseFormat.getUserMessage());
	}
	@Test
	@DisplayName("TestCreateFailureResponse - With HttpCode And Data")
	void testCreateFailureResponseWithHttpStatusCodeAndData() {
		actualResponseFormat.setUserMessage("Failure");
		actualResponseFormat.setHttpCode(404);
		ResponseEntity<Object> expectedResponse = responseEntityBuilder.createFailureResponse(404,actualResponseFormat.getData());
		ResponseFormat expectedResponseFormat=(ResponseFormat) expectedResponse.getBody();
		assertEquals(actualResponseFormat.getHttpCode(), expectedResponseFormat.getHttpCode());
		assertEquals(actualResponseFormat.getData(), expectedResponseFormat.getData());
		assertEquals(actualResponseFormat.getUserMessage(), expectedResponseFormat.getUserMessage());
	}

	@Test
	@DisplayName("TestCreateSuccessResponse - With HttpCode And Data")
	void testCreateSuccessResponseWithHttpStatusCodeAndData() {
		actualResponseFormat.setUserMessage("User Created SuccessFully");
		actualResponseFormat.setHttpCode(201);
		ResponseEntity<Object> expectedResponse = responseEntityBuilder.createSuccessResponse(201,actualResponseFormat.getUserMessage(),actualResponseFormat.getData());
		ResponseFormat expectedResponseFormat=(ResponseFormat) expectedResponse.getBody();
		assertEquals(actualResponseFormat.getHttpCode(), expectedResponseFormat.getHttpCode());
		assertEquals(actualResponseFormat.getUserMessage(), expectedResponseFormat.getUserMessage());
	}
	@Test
	@DisplayName("TestCreateExceptionResponse - With HttpCode And User Message")
	void testCreateExceptionResponseWithHttpStatusCodeAndData() {
		actualResponseFormat.setUserMessage("User Not Found");
		actualResponseFormat.setHttpCode(404);
		actualResponseFormat.setData(null);
		ResponseEntity<Object> expectedResponse = responseEntityBuilder.createExceptionResponse(404,actualResponseFormat.getUserMessage());
		ResponseFormat expectedResponseFormat=(ResponseFormat) expectedResponse.getBody();
		assertEquals(actualResponseFormat.getHttpCode(), expectedResponseFormat.getHttpCode());
		assertEquals(actualResponseFormat.getData(), expectedResponseFormat.getData());
		assertEquals(actualResponseFormat.getUserMessage(), expectedResponseFormat.getUserMessage());
	}
}
