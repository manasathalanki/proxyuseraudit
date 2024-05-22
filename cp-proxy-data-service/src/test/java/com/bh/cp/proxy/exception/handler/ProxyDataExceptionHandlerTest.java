package com.bh.cp.proxy.exception.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.NestedCheckedException;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.bh.cp.proxy.constants.ExceptionConstants;
import com.bh.cp.proxy.exception.ProxyException;
import com.bh.cp.proxy.exception.ServerUnavailableException;
import com.bh.cp.proxy.test.exception.TestException;
import com.bh.ip.exception.model.RestServiceErrorException;

import jakarta.ws.rs.NotFoundException;

class ProxyDataExceptionHandlerTest {

	@InjectMocks
	private ProxyDataExceptionHandler proxyDataExceptionHandler;

	private MockHttpServletRequest mockHttpServletRequest;

	private HttpStatus status;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.setMethod("GET");
		mockHttpServletRequest.setServerName("test.com");
		status = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	private void assertInternalServerException(RestServiceErrorException output, String msg) {
		msg = msg != null ? msg : "\"detail\" : \"Internal Server error. Please contact support.\"";
		assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
		assertTrue(output.getLocalizedMessage().contains(msg));
		assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
		assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"500\""));
		assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
		assertEquals(status, output.getHttpStatus());
	}

	private void assertServiceUnAvailableException(RestServiceErrorException output, String msg) {
		HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
		msg = msg != null ? msg : "\"detail\" : \"Service down/Temporarily shut down\"";
		assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
		assertTrue(output.getLocalizedMessage().contains(msg));
		assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
		assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"503\""));
		assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
		assertEquals(status, output.getHttpStatus());
	}

	@Nested
	class ServiceSpecificException {

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType1 -- ServiceSpecific -- NotFoundException")
		void testGetErrorMessageForExcptionType_Positive1() {
			Exception exception = new NotFoundException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
			assertTrue(output.getLocalizedMessage().contains("\"detail\" : \"Test Message\""));
			assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"404\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
			assertEquals(HttpStatus.NOT_FOUND, output.getHttpStatus());
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType2 -- ServiceSpecific -- ServerUnavailableException")
		void testGetErrorMessageForExcptionType_Positive2() {
			Exception exception = new ServerUnavailableException("Service down/Temporarily shut down");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertServiceUnAvailableException(output, null);
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType3 -- ServiceSpecific -- ServerUnavailableException")
		void testGetErrorMessageForExcptionType_Positive3() {
			Exception exception = new ProxyException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType4 -- ServiceSpecific -- Unhandled ServiceSpecific Exception")
		void testGetErrorMessageForExcptionType_Negative1() {
			Exception exception = new TestException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "Test Message");
		}
	}

	@Test
	@DisplayName("Test HandleException -- Exception")
	void testHandleException_Positive1() {
		Exception exception = new Exception("Test Message");
		ResponseEntity<Object> output = proxyDataExceptionHandler.handleException(mockHttpServletRequest, exception);
		assertTrue(output.getBody().toString().contains(
				"httpStatusCode=500, code=500, message=null, detail=Internal Server error. Please contact support., requestId=null"));
		assertTrue(output.getBody().toString().contains("path=[GET] http://test.com"));
	}

	@Test
	@DisplayName("Test HandleMethodArgumentNotValid -- MethodArgumentNotValidException")
	void testHandleMethodArgumentNotValid_Positive1() {
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.getAllErrors())
				.thenReturn(List.of(new FieldError("xyzObject", "xyz", "field should not blank"),
						new ObjectError("abc", "object should not null")));
		WebRequest request = new ServletWebRequest(mockHttpServletRequest);
		MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
		when(exception.getBindingResult()).thenReturn(bindingResult);
		ResponseEntity<Object> output = proxyDataExceptionHandler.handleMethodArgumentNotValid(exception,
				mock(HttpHeaders.class), status, request);
		assertTrue(output.getBody().toString().contains(
				"httpStatusCode=412, code=412, message=Validation failed in xyz field, detail=field should not blank, requestId=null"));
		assertTrue(output.getBody().toString()
				.contains("httpStatusCode=412, code=412, message=null, detail=object should not null, requestId=null"));
		assertTrue(output.getBody().toString().contains("path=[GET] http://test.com"));
	}

	@Test
	@DisplayName("Test HandleErrorResponseException -- ErrorResponseException")
	void testHandleErrorResponseException_Positive1() {
		ErrorResponseException exception = new ErrorResponseException(status);
		exception.setDetail("Test Message");
		WebRequest request = new ServletWebRequest(mockHttpServletRequest);
		ResponseEntity<Object> output = proxyDataExceptionHandler.handleErrorResponseException(exception,
				mock(HttpHeaders.class), status, request);
		assertTrue(output.getBody().toString()
				.contains("httpStatusCode=500, code=500, message=null, detail=Test Message, requestId=null"));
		assertTrue(output.getBody().toString().contains("path=[GET] http://test.com"));
	}

	@Nested
	class TestGetErrorMessageForExcptionType {

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType1 -- ConversionNotSupportedException")
		void testGetErrorMessageForExcptionType_Positive1() {
			HttpStatus status = HttpStatus.UNAUTHORIZED;
			Exception exception = new ConversionNotSupportedException(proxyDataExceptionHandler,
					ConversionNotSupportedException.class, new NotFoundException("Test Message"));
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
			assertTrue(output.getLocalizedMessage().contains("\"detail\" : \"Test Message\""));
			assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"401\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
			assertEquals(status, output.getHttpStatus());
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType2 -- HttpMediaTypeNotAcceptableException")
		void testGetErrorMessageForExcptionType_Positive2() {
			Exception exception = mock(HttpMediaTypeNotAcceptableException.class);
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "Supported accept types: ");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType3 -- HttpMediaTypeNotSupportedException")
		void testGetErrorMessageForExcptionType_Positive3() {
			status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
			Exception exception = mock(HttpMediaTypeNotSupportedException.class);
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
			assertTrue(output.getLocalizedMessage().contains("\"detail\" : \"Unsupported content type:"));
			assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"415\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
			assertEquals(status, output.getHttpStatus());
		}

		@Test
		@SuppressWarnings("deprecation")
		@DisplayName("Test GetErrorMessageForExcptionType4 -- HttpMessageNotReadableException")
		void testGetErrorMessageForExcptionType_Positive4() {
			Exception exception = new HttpMessageNotReadableException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "Invalid Payload");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType5 -- HttpMessageNotWritableException")
		void testGetErrorMessageForExcptionType_Positive5() {
			Exception exception = new HttpMessageNotWritableException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType6 -- HttpRequestMethodNotSupportedException")
		void testGetErrorMessageForExcptionType_Positive6() {
			Exception exception = mock(HttpRequestMethodNotSupportedException.class);
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "Request method 'null' not supported.");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType7 -- BindException")
		void testGetErrorMessageForExcptionType_Positive7() {
			BindingResult bindingResult = mock(BindingResult.class);
			when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("xyz", "field should not blank")));
			Exception exception = new BindException(bindingResult);
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "field should not blank");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType8 -- MissingServletRequestParameterException")
		void testGetErrorMessageForExcptionType_Positive8() {
			Exception exception = new MissingServletRequestParameterException("name", "String");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output,
					"Required request parameter 'name' for method parameter type String is not present");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType9 -- MissingServletRequestPartException")
		void testGetErrorMessageForExcptionType_Positive9() {
			Exception exception = new MissingServletRequestPartException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "Required part 'Test Message' is not present.");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType10 -- ServletRequestBindingException")
		void testGetErrorMessageForExcptionType_Positive10() {
			Exception exception = new ServletRequestBindingException("Internal Server error. Please contact support.");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, null);
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType11 -- TypeMismatchException")
		void testGetErrorMessageForExcptionType_Positive11() {
			Exception exception = new TypeMismatchException(new Object(), ProxyDataExceptionHandler.class);
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output,
					"\"detail\" : \"Failed to convert value of type 'java.lang.Object' to required type 'com.bh.cp.proxy.exception.handler.ProxyDataExceptionHandler'\"");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType12 -- HttpMessageConversionException")
		void testGetErrorMessageForExcptionType_Positive12() {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			Exception exception = new HttpMessageConversionException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
			assertTrue(output.getLocalizedMessage().contains("\"detail\" : \"Invalid Payload\""));
			assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"400\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
			assertEquals(status, output.getHttpStatus());
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType13 -- ResourceAccessException")
		void testGetErrorMessageForExcptionType_Positive13() {
			Exception exception = new ResourceAccessException("Service down/Temporarily shut down");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertServiceUnAvailableException(output, null);
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType14 -- NoHandlerFoundException")
		void testGetErrorMessageForExcptionType_Positive14() {
			Exception exception = mock(NoHandlerFoundException.class);
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "\"detail\" : \"The URI \'\' is not found.");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType15 -- NoSuchElementException")
		void testGetErrorMessageForExcptionType_Positive15() {
			Exception exception = new NoSuchElementException("Internal Server error. Please contact support.");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, null);
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType16 -- InvalidDataAccessApiUsageException")
		void testGetErrorMessageForExcptionType_Positive16() {
			Exception exception = new InvalidDataAccessApiUsageException(
					"Internal Server error. Please contact support.");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, null);
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType17 -- TimeoutException")
		void testGetErrorMessageForExcptionType_Positive17() throws IllegalAccessException {
			HttpStatus status = HttpStatus.REQUEST_TIMEOUT;
			Exception exception = new TimeoutException("Timeout, operation has not been completed");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
			assertTrue(output.getLocalizedMessage()
					.contains("\"detail\" : \"Timeout, operation has not been completed\""));
			assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"408\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
			assertEquals(status, output.getHttpStatus());
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType18 -- AccessDeniedException")
		void testGetErrorMessageForExcptionType_Positive18() throws IllegalAccessException {
			HttpStatus status = HttpStatus.FORBIDDEN;
			Exception exception = new AccessDeniedException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
			assertTrue(output.getLocalizedMessage().contains("\"detail\" : \"Test Message\""));
			assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"403\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
			assertEquals(status, output.getHttpStatus());
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType19 -- HttpClientErrorException")
		void testGetErrorMessageForExcptionType_Positive19() throws IllegalAccessException {
			Exception exception = new HttpClientErrorException(status, "500 : {\"error\":\"Test Message\"}");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, "\"detail\" : \"Test Message\"");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType20 -- Other Exception with null status")
		void testGetErrorMessageForExcptionType_Positive20() throws IllegalAccessException {
			Exception exception = new Exception("Internal Server error. Please contact support.");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, null, exception);
			assertInternalServerException(output, null);
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType21 -- ResponseStatusException")
		void testGetErrorMessageForExcptionType_Positive21() {
			Exception exception = new ResponseStatusException(status, "Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
			assertTrue(output.getLocalizedMessage()
					.contains("\"detail\" : \"500 INTERNAL_SERVER_ERROR \\\"Test Message\\\"\""));
			assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"412\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
			assertEquals(HttpStatus.PRECONDITION_FAILED, output.getHttpStatus());
		}

		@ParameterizedTest
		@DisplayName("Test GetErrorMessageForExcptionType22 -- HttpServerErrorException")
		@CsvSource({ "500 : {\"error\":\"Test Message\"}, \"detail\" : \"Test Message\"",
				"<head><title>Test Message</title></head>, Test Message" })
		void testGetErrorMessageForExcptionType_Positive22(String actualMsg, String expectedMsg)
				throws IllegalAccessException {
			Exception exception = new HttpServerErrorException(status, actualMsg);
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, expectedMsg);
		}

		void assertExtractedMessage(String parentJsonKey, String childJsonKey, String excpMsg, String outputMsg) {
			Exception exception = new HttpServerErrorException(status, excpMsg != null ? excpMsg
					: "{\"" + parentJsonKey + "\":{\"" + childJsonKey + "\":\"Test Message\"}}");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, outputMsg);
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType23 -- HttpServerErrorException - errorDetail and generic")
		void testGetErrorMessageForExcptionType_Positive23() throws IllegalAccessException {
			assertExtractedMessage(ExceptionConstants.ERRORDETAIL, ExceptionConstants.GENERIC, null, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType24 -- HttpServerErrorException - errorDetail and assets")
		void testGetErrorMessageForExcptionType_Positive24() throws IllegalAccessException {
			assertExtractedMessage(ExceptionConstants.ERRORDETAIL, ExceptionConstants.ASSETS, null, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType25 -- HttpServerErrorException - errorDetail and assets")
		void testGetErrorMessageForExcptionType_Positive25() throws IllegalAccessException {
			assertExtractedMessage(ExceptionConstants.ERRORDETAIL, ExceptionConstants.VIDS, null, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType26 -- HttpServerErrorException - errorDetail and asset")
		void testGetErrorMessageForExcptionType_Positive26() throws IllegalAccessException {
			assertExtractedMessage(ExceptionConstants.ERRORDETAIL, ExceptionConstants.ASSET, null, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType27 -- HttpServerErrorException - errors and detail")
		void testGetErrorMessageForExcptionType_Positive27() throws IllegalAccessException {
			assertExtractedMessage(null, null,
					"{\"errors\":[{\"detail\":\"Test Message1\"},{\"detail\":\"Test Message2\"},{\"detail1\":\"Skip Message\"}]}",
					"Test Message1 Test Message2");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType28 -- HttpServerErrorException - Error")
		void testGetErrorMessageForExcptionType_Positive28() throws IllegalAccessException {
			assertExtractedMessage(null, null, "{\"" + ExceptionConstants.ERROR + "\":\"Test Message\"}",
					"Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType29 -- HttpServerErrorException - error")
		void testGetErrorMessageForExcptionType_Positive29() throws IllegalAccessException {
			assertExtractedMessage(null, null, "{\"" + ExceptionConstants.ERROR.toLowerCase() + "\":\"Test Message\"}",
					"Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType30 -- HttpServerErrorException - errorMessage")
		void testGetErrorMessageForExcptionType_Positive30() throws IllegalAccessException {
			assertExtractedMessage(null, null, "{\"" + ExceptionConstants.ERRORMESSAGE + "\":\"Test Message\"}",
					"Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType31 -- HttpServerErrorException - errorCauses and errorSummary")
		void testGetErrorMessageForExcptionType_Positive31() throws IllegalAccessException {
			assertExtractedMessage(ExceptionConstants.ERRORDETAIL, ExceptionConstants.ERRORSUMMARY, null,
					"Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType32 -- HttpServerErrorException - errorDetail and Others")
		void testGetErrorMessageForExcptionType_Positive32() throws IllegalAccessException {
			assertExtractedMessage(ExceptionConstants.ERRORCAUSES, "generic1", null,
					"{\\\"errorCauses\\\":{\\\"generic1\\\":\\\"Test Message\\\"}}");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType33 -- ResourceAccessException")
		void testGetErrorMessageForExcptionType_Negative1() {
			Exception exception = new ResourceAccessException("Service down/Temporarily shut down",
					new IOException("service down"));
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertServiceUnAvailableException(output, "service down");
		}

		@ParameterizedTest
		@DisplayName("Test GetErrorMessageForExcptionType34 -- HttpServerErrorException")
		@CsvSource({ "Test Exception,500 Test Exception", "{Test Exception},500 {Test Exception}" })
		void testGetErrorMessageForExcptionType_Negative2(String actualMsg, String expectedMsg)
				throws IllegalAccessException {
			Exception exception = new HttpServerErrorException(status, actualMsg);
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, expectedMsg);
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType35 -- Unhandled java.util Exception")
		void testGetErrorMessageForExcptionType_Negative3() {
			Exception exception = new java.util.concurrent.BrokenBarrierException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, null, exception);
			assertInternalServerException(output, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType36 -- Unhandled org.springframework.web Exception")
		void testGetErrorMessageForExcptionType_Negative4() {
			Exception exception = new org.springframework.web.HttpSessionRequiredException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, null, exception);
			assertInternalServerException(output, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType37 -- Unhandled org.springframework.validation Exception")
		void testGetErrorMessageForExcptionType_Negative5() {
			Exception exception = mock(org.springframework.validation.method.MethodValidationException.class);
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, null, exception);
			assertInternalServerException(output, "INTERNAL_SERVER_ERROR");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType38 -- Unhandled org.springframework.security Exception")
		void testGetErrorMessageForExcptionType_Negative6() {
			Exception exception = new org.springframework.security.authentication.AccountExpiredException(
					"Test Message");
			HttpStatus status = HttpStatus.FORBIDDEN;
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertDoesNotThrow(() -> new JSONObject(output.getLocalizedMessage()));
			assertTrue(output.getLocalizedMessage().contains("\"detail\" : \"Test Message\""));
			assertTrue(output.getLocalizedMessage().contains("\"path\" : \"[GET] http://test.com\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpStatusCode\" : \"403\""));
			assertTrue(output.getLocalizedMessage().contains("\"httpMethod\" : \"GET\""));
			assertEquals(status, output.getHttpStatus());
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType39 -- Unhandled org.springframework.dao Exception")
		void testGetErrorMessageForExcptionType_Negative7() {
			Exception exception = new org.springframework.dao.ConcurrencyFailureException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, null, exception);
			assertInternalServerException(output, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType40 -- Unhandled org.springframework.beans Exception")
		void testGetErrorMessageForExcptionType_Negative8() {
			Exception exception = new org.springframework.beans.factory.BeanCreationException("Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, null, exception);
			assertInternalServerException(output, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType41 -- Unhandled org.springframework.http Exception")
		void testGetErrorMessageForExcptionType_Negative9() {
			Exception exception = new org.springframework.http.InvalidMediaTypeException(
					MediaType.APPLICATION_JSON_VALUE, "Test Message");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, null, exception);
			assertInternalServerException(output, "Test Message");
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType42 -- TaskRejectedException")
		void testGetErrorMessageForExcptionType_Positive33() {
			Exception exception = new TaskRejectedException("Internal Server error. Please contact support.");
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, status, exception);
			assertInternalServerException(output, null);
		}

		@Test
		@DisplayName("Test GetErrorMessageForExcptionType43 -- Unhandled org.springframework.core Exception")
		void testGetErrorMessageForExcptionType_Negative10() {
			Exception exception = new NestedCheckedException("Test Message") {
				private static final long serialVersionUID = 967966669651647415L;
			};
			RestServiceErrorException output = ProxyDataExceptionHandler
					.getErrorMessageForExcptionType(mockHttpServletRequest, null, exception);
			assertInternalServerException(output, "Test Message");
		}

	}

}