package com.bh.cp.dashboard.exception.handler;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.bh.cp.dashboard.constants.ExceptionConstants;
import com.bh.cp.dashboard.exception.ServerUnavailableException;
import com.bh.cp.dashboard.util.SecurityUtil;
import com.bh.ip.exception.handler.ControllerExcpHandler;
import com.bh.ip.exception.handler.RestServiceError;
import com.bh.ip.exception.handler.RestServiceError.ErrorInfo;
import com.bh.ip.exception.model.DefaultErrorCode;
import com.bh.ip.exception.model.RestServiceErrorException;
import com.bh.ip.exception.model.SCIMServiceException;
import com.bh.ip.exception.utils.ExceptionUtil;

import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

@Priority(value = 1)
@ControllerAdvice
public class DashboardExceptionHandler extends ControllerExcpHandler {

	private static final Logger dehlogger = LoggerFactory.getLogger(DashboardExceptionHandler.class);

	@Override
	@ResponseBody
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleException(HttpServletRequest request, Exception exception) {
		return getResponseEntity(getErrorMessageForExcptionType(request, (HttpStatus) null, exception));
	}

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException excp,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		HttpServletRequest servletRequest = ExceptionUtil.getServletRequest(request);
		HttpStatus httpStatus = HttpStatus.PRECONDITION_FAILED;

		HttpMethod httpMethod = ExceptionUtil.getHttpMethod(servletRequest);
		String requestUrl = ExceptionUtil.getRequestUrl(servletRequest);

		RestServiceErrorException restServiceErrorException = getRestServiceErrorExcepForMethodArgsNotValid(excp,
				requestUrl, httpMethod.name(), httpStatus);

		return getResponseEntity(restServiceErrorException);

	}

	public static RestServiceErrorException getRestServiceErrorExcepForMethodArgsNotValid(
			MethodArgumentNotValidException actualExcp, String url, String httpMethod, HttpStatus httpStatus) {
		String traceId = "";
		String code = Integer.toString(httpStatus.value());
		String[] schemas = null;

		List<ErrorInfo> errorInfoList = actualExcp.getBindingResult().getAllErrors().stream()
				.map(error -> new ErrorInfo(String.valueOf(httpStatus.value()), code,
						error instanceof FieldError fieldError
								? String.format("Validation failed in %s field", fieldError.getField())
								: null,
						error.getDefaultMessage(), null))
				.toList();

		RestServiceError restServiceError = new RestServiceError(errorInfoList);
		restServiceError.setSchemas(schemas);
		restServiceError.setTraceId(traceId);
		restServiceError.setPath("[" + httpMethod + "] " + url);
		return new RestServiceErrorException(DefaultErrorCode.create(httpStatus.name(), ""), httpMethod, httpStatus,
				restServiceError);
	}

	@Override
	public ResponseEntity<Object> handleErrorResponseException(ErrorResponseException excp, HttpHeaders headers,
			HttpStatusCode status, WebRequest request) {

		HttpServletRequest servletRequest = ExceptionUtil.getServletRequest(request);
		HttpStatus httpStatus = (HttpStatus) excp.getStatusCode();

		HttpMethod httpMethod = ExceptionUtil.getHttpMethod(servletRequest);
		String requestUrl = ExceptionUtil.getRequestUrl(servletRequest);

		RestServiceErrorException restServiceErrorException = ExceptionUtil.getRestServiceErrorException(excp,
				requestUrl, httpMethod.name(), httpStatus, excp.getBody().getDetail());
		return getResponseEntity(restServiceErrorException);

	}

	public static RestServiceErrorException getErrorMessageForExcptionType(HttpServletRequest httpServletRequest,
			HttpStatus status, Exception exception) {

		Object[] errorArr = null;
		String packageStartsWith = getPackageNameStartsWith(exception);
		switch (packageStartsWith) {
		case "java.util", "java.util.concurrent":
			errorArr = getJavaUtilErrorMessage(exception);
			break;
		case "org.springframework.web":
			errorArr = getSpringWebErrorMessage(httpServletRequest, exception);
			break;
		case "org.springframework.validation":
			errorArr = getSpringValidationErrorMessage(exception);
			break;
		case "org.springframework.security":
			errorArr = getSpringSecurityErrorMessage(exception);
			break;
		case "org.springframework.dao":
			errorArr = getSpringDaoErrorMessage(exception);
			break;
		case "org.springframework.beans":
			errorArr = getSpringBeansErrorMessage(exception);
			break;
		case "org.springframework.core":
			errorArr = getSpringCoreErrorMessage(exception);
			break;
		case "org.springframework.http":
			errorArr = getSpringHttpErrorMessage(exception);
			break;
		case "com.bh.cp", "io.github.resilience4j", "jakarta.ws.rs":
			errorArr = getServiceSpecificErrorMessage(exception);
			break;
		default:
			return ExceptionUtil.getExceptionForNonControllerAdviceExcp(exception, dehlogger, httpServletRequest);
		}

		String errorMessage = errorArr[0] != null ? (String) errorArr[0] : HttpStatus.INTERNAL_SERVER_ERROR.name();
		HttpStatus httpStatus = errorArr[1] != null ? (HttpStatus) errorArr[1] : status;
		Exception newException = errorArr[2] != null ? (Exception) errorArr[2] : exception;

		if (httpStatus == null) {
			httpStatus = ExceptionUtil.getHttpStatus(httpServletRequest);
		}

		SecurityUtil.sanitizeLogging(dehlogger, Level.ERROR, ExceptionConstants.ERROR, newException);

		HttpMethod httpMethod = ExceptionUtil.getHttpMethod(httpServletRequest);
		String requestUrl = ExceptionUtil.getRequestUrl(httpServletRequest);
		return ExceptionUtil.getRestServiceErrorException(newException, requestUrl, httpMethod.name(), httpStatus,
				errorMessage);
	}

	private static String getPackageNameStartsWith(Exception exception) {
		String packageName = exception.getClass().getPackageName();
		int foundIndex = 0;
		for (int i = 0; i < 3; i++) {
			foundIndex = packageName.indexOf(".", foundIndex + 1);
			if (foundIndex == -1) {
				foundIndex = packageName.length();
				break;
			}
		}
		return packageName.substring(0, foundIndex);
	}

	private static Object[] getJavaUtilErrorMessage(Exception exception) {
		String errorMessage = null;
		HttpStatus status = null;
		Exception newException = null;
		if (exception instanceof TimeoutException) {
			errorMessage = ExceptionConstants.TIMEOUT_OPERATION_HAS_NOT_BEEN_COMPLETED;
			status = HttpStatus.REQUEST_TIMEOUT;
		} else if (exception instanceof NoSuchElementException noSuchElementException) {
			errorMessage = noSuchElementException.getMessage();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		} else {
			errorMessage = exception.getLocalizedMessage();
		}
		return new Object[] { errorMessage, status, newException };
	}

	private static Object[] getSpringWebErrorMessage(HttpServletRequest httpServletRequest, Exception exception) {
		String errorMessage = null;
		HttpStatus status = null;
		Exception newException = null;
		if (exception instanceof HttpMediaTypeNotAcceptableException httpMediaTypeNotAcceptableException) {
			errorMessage = httpMediaTypeNotAcceptableException.getMessage() + System.lineSeparator()
					+ ExceptionConstants.SUPPORTED_ACCEPT_TYPES
					+ MediaType.toString(httpMediaTypeNotAcceptableException.getSupportedMediaTypes());
		} else if (exception instanceof HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException) {
			String requestUrl1 = ExceptionConstants.UNSUPPORTED_CONTENT_TYPE + ExceptionConstants.SINGLE_QUOTE
					+ httpMediaTypeNotSupportedException.getContentType() + ExceptionConstants.SINGLE_QUOTE;
			String supported1 = httpMediaTypeNotSupportedException.getMessage() + System.lineSeparator()
					+ ExceptionConstants.SUPPORTED_CONTENT_TYPE + ExceptionConstants.SINGLE_QUOTE
					+ MediaType.toString(httpMediaTypeNotSupportedException.getSupportedMediaTypes())
					+ ExceptionConstants.SINGLE_QUOTE;
			errorMessage = requestUrl1 + " " + System.lineSeparator() + " " + supported1;
			status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
		} else if (exception instanceof HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
			errorMessage = httpRequestMethodNotSupportedException.getMessage() + System.lineSeparator()
					+ ExceptionConstants.REQUEST_METHOD + ExceptionConstants.SINGLE_QUOTE
					+ httpRequestMethodNotSupportedException.getMethod() + ExceptionConstants.SINGLE_QUOTE
					+ ExceptionConstants.NOT_SUPPORTED + System.lineSeparator() + ExceptionConstants.SUPPORTED_METHODS
					+ ExceptionConstants.SINGLE_QUOTE
					+ StringUtils.join(httpRequestMethodNotSupportedException.getSupportedHttpMethods(), ", ");
		} else if (exception instanceof MissingServletRequestParameterException) {
			errorMessage = exception.getMessage();
		} else if (exception instanceof MissingServletRequestPartException) {
			errorMessage = exception.getMessage();
		} else if (exception instanceof ServletRequestBindingException) {
			errorMessage = exception.getMessage();
		} else if (exception instanceof HttpClientErrorException httpClientErrorException) {
			errorMessage = extractMsgFromExternalAPI(httpClientErrorException);
			status = (HttpStatus) httpClientErrorException.getStatusCode();
		} else if (exception instanceof HttpServerErrorException httpServerErrorException) {
			errorMessage = extractMsgFromExternalAPI(httpServerErrorException);
			status = (HttpStatus) httpServerErrorException.getStatusCode();
		} else if (exception instanceof ResourceAccessException resourceAccessException) {
			errorMessage = resourceAccessException.getCause() == null
					? ExceptionConstants.SERVICE_DOWN_TEMPORARILY_SHUT_DOWN
					: ExceptionUtil.getResourceAccessExceptionMsg(resourceAccessException);
			status = HttpStatus.SERVICE_UNAVAILABLE;
		} else if (exception instanceof ResponseStatusException responseStatusException) {
			errorMessage = responseStatusException.getMessage();
			status = HttpStatus.PRECONDITION_FAILED;
		} else if (exception instanceof NoHandlerFoundException) {
			errorMessage = String.format(ExceptionConstants.THE_URL_IS_NOT_FOUND, httpServletRequest.getRequestURI());
		} else {
			errorMessage = exception.getLocalizedMessage();
		}
		return new Object[] { errorMessage, status, newException };
	}

	private static Object[] getSpringValidationErrorMessage(Exception exception) {
		String errorMessage = null;
		HttpStatus status = null;
		Exception newException = null;
		if (exception instanceof BindException bindException) {
			List<?> requestUrl = bindException.getAllErrors();
			StringBuilder supported = new StringBuilder();
			requestUrl.forEach(error -> supported.append(error.toString()));
			errorMessage = StringUtils.join(supported, System.lineSeparator());
		} else {
			errorMessage = exception.getLocalizedMessage();
		}
		return new Object[] { errorMessage, status, newException };
	}

	private static Object[] getSpringSecurityErrorMessage(Exception exception) {
		String errorMessage = null;
		HttpStatus status = null;
		Exception newException = null;
		if (exception instanceof AccessDeniedException accessDeniedException) {
			errorMessage = accessDeniedException.getMessage();
			status = HttpStatus.FORBIDDEN;
		} else {
			errorMessage = exception.getLocalizedMessage();
		}
		return new Object[] { errorMessage, status, newException };
	}

	private static Object[] getSpringCoreErrorMessage(Exception exception) {
		String errorMessage = null;
		HttpStatus status = null;
		Exception newException = null;
		if (exception instanceof TaskRejectedException taskRejectedException) {
			errorMessage = taskRejectedException.getMessage();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		} else {
			errorMessage = exception.getLocalizedMessage();
		}
		return new Object[] { errorMessage, status, newException };
	}

	private static Object[] getSpringDaoErrorMessage(Exception exception) {
		String errorMessage = null;
		HttpStatus status = null;
		Exception newException = null;
		if (exception instanceof InvalidDataAccessApiUsageException invalidDataAccessApiUsageException) {
			errorMessage = invalidDataAccessApiUsageException.getMessage();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		} else {
			errorMessage = exception.getLocalizedMessage();
		}
		return new Object[] { errorMessage, status, newException };
	}

	private static Object[] getSpringBeansErrorMessage(Exception exception) {
		String errorMessage = null;
		HttpStatus status = null;
		Exception newException = null;
		if (exception instanceof ConversionNotSupportedException conversionNotSupportedException) {
			errorMessage = getErrorMessage(conversionNotSupportedException);
		} else if (exception instanceof TypeMismatchException) {
			errorMessage = exception.getMessage();
		} else {
			errorMessage = exception.getLocalizedMessage();
		}
		return new Object[] { errorMessage, status, newException };

	}

	private static Object[] getSpringHttpErrorMessage(Exception exception) {
		String errorMessage = null;
		HttpStatus status = null;
		Exception newException = null;
		if (exception instanceof HttpMessageNotReadableException) {
			errorMessage = ExceptionConstants.INVALID_PAYLOAD;
			newException = new SCIMServiceException(errorMessage);
		} else if (exception instanceof HttpMessageNotWritableException httpMessageNotWritableException) {
			errorMessage = getErrorMessage(httpMessageNotWritableException);
		} else if (exception instanceof HttpMessageConversionException) {
			newException = new SCIMServiceException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
					ExceptionConstants.INVALID_PAYLOAD, (Object) null);
			errorMessage = ExceptionConstants.INVALID_PAYLOAD;
			status = HttpStatus.BAD_REQUEST;
		} else {
			errorMessage = exception.getLocalizedMessage();
		}

		return new Object[] { errorMessage, status, newException };

	}

	private static <T extends NestedRuntimeException> String getErrorMessage(T ex) {
		Throwable mostSpecificCause = ex.getMostSpecificCause();
		return mostSpecificCause.getMessage();
	}

	private static String extractMsgFromExternalAPI(Exception exception) {
		String errorMessage = null;
		String responseBody = exception.getLocalizedMessage();
		Pattern pattern = Pattern.compile("\\{.*.}");
		Matcher matcher = pattern.matcher(responseBody);
		JSONObject jsonObj = new JSONObject();
		matcher.find();

		try {
			jsonObj = new JSONObject(matcher.group(0));
		} catch (Exception e) {
			dehlogger.error("No JSON Object Match Found in Exception Message");
			try {
				pattern = Pattern.compile("(?<=<title>)(.*.)(?=</title>)");
				matcher = pattern.matcher(responseBody);
				matcher.find();
				return matcher.group(0);
			} catch (Exception ex) {
				dehlogger.error(
						"Exception occurred when parsing Exception Message with JSON or HTML Tag in Exception Handler",
						ex);
				return exception.getLocalizedMessage();
			}
		}

		if (jsonObj.has(ExceptionConstants.ERRORDETAIL)) {
			JSONObject errorDetail = jsonObj.getJSONObject(ExceptionConstants.ERRORDETAIL);
			errorMessage = extractMessageFromJsonObj(errorDetail);
		} else if (jsonObj.has(ExceptionConstants.ERRORS)) {
			JSONArray jsonArray = jsonObj.getJSONArray(ExceptionConstants.ERRORS);
			StringBuilder strBuilder = new StringBuilder();
			jsonArray.forEach(obj -> {
				if (((JSONObject) obj).has(ExceptionConstants.DETAIL)) {
					strBuilder.append(extractMessageFromJsonObj(((JSONObject) obj))).append(" ");
				}
			});
			errorMessage = strBuilder.toString().trim();
		} else if (jsonObj.has(ExceptionConstants.ERROR.toLowerCase())) {
			errorMessage = extractMessageFromJsonObj(jsonObj);
		} else if (jsonObj.has(ExceptionConstants.ERROR)) {
			errorMessage = extractMessageFromJsonObj(jsonObj);
		} else if (jsonObj.has(ExceptionConstants.ERRORCAUSES)) {
			errorMessage = extractMessageFromJsonObj(jsonObj);
		} else {
			errorMessage = extractMessageFromJsonObj(jsonObj);
		}

		return errorMessage;
	}

	private static String extractMessageFromJsonObj(JSONObject errorJsonObj) {
		if (errorJsonObj.has(ExceptionConstants.GENERIC)) {
			return errorJsonObj.getString(ExceptionConstants.GENERIC);
		} else if (errorJsonObj.has(ExceptionConstants.ASSETS)) {
			return errorJsonObj.getString(ExceptionConstants.ASSETS);
		} else if (errorJsonObj.has(ExceptionConstants.VIDS)) {
			return errorJsonObj.getString(ExceptionConstants.VIDS);
		} else if (errorJsonObj.has(ExceptionConstants.ASSET)) {
			return errorJsonObj.getString(ExceptionConstants.ASSET);
		} else if (errorJsonObj.has(ExceptionConstants.DETAIL)) {
			return errorJsonObj.getString(ExceptionConstants.DETAIL);
		} else if (errorJsonObj.has(ExceptionConstants.ERROR)) {
			return errorJsonObj.getString(ExceptionConstants.ERROR);
		} else if (errorJsonObj.has(ExceptionConstants.ERROR.toLowerCase())) {
			return errorJsonObj.getString(ExceptionConstants.ERROR.toLowerCase());
		} else if (errorJsonObj.has(ExceptionConstants.ERRORMESSAGE)) {
			return errorJsonObj.getString(ExceptionConstants.ERRORMESSAGE);
		} else if (errorJsonObj.has(ExceptionConstants.ERRORSUMMARY)) {
			return errorJsonObj.getString(ExceptionConstants.ERRORSUMMARY);
		} else {
			return errorJsonObj.toString();
		}
	}

	private static Object[] getServiceSpecificErrorMessage(Exception exception) {
		String errorMessage = null;
		HttpStatus status = null;
		Exception newException = null;
		if (exception instanceof NotFoundException notFoundException) {
			errorMessage = notFoundException.getMessage();
			status = HttpStatus.NOT_FOUND;
		} else if (exception instanceof ServerUnavailableException serverUnavailableException) {
			errorMessage = serverUnavailableException.getMessage();
			status = HttpStatus.SERVICE_UNAVAILABLE;
		} else {
			errorMessage = exception.getLocalizedMessage();
		}
		return new Object[] { errorMessage, status, newException };
	}

}