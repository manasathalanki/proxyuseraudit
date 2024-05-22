package com.bh.cp.user.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.jboss.logging.MDC;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.user.constants.SecurityUtilConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriBuilder;

public final class SecurityUtil {

	private SecurityUtil() {
		super();
	}

	public static Map<String, Claim> getClaims(HttpServletRequest httpRequest, JwtUtil jwtUtil) {
		try {
			return jwtUtil.getClaims(httpRequest.getHeader(SecurityUtilConstants.KEY_AUTHORIZATION).substring(7));
		} catch (Exception e) {
			throw new org.springframework.security.access.AccessDeniedException("Not logged in.", e);
		}
	}

	public static String getSSO(Map<String, Claim> claims) {
		return claims.get(SecurityUtilConstants.KEY_PREFERRED_USERNAME).toString().replace("\"", "");
	}

	public static String getFieldFromClaims(Map<String, Claim> claims, String field, String errorMessage)
			throws NotFoundException {
		Claim fieldOpt = Optional.ofNullable(claims.get(field)).orElseThrow(() -> new NotFoundException(errorMessage));
		return fieldOpt.toString().replace("\"", "");
	}

	public static String getThreadName() {
		if (MDC.get(SecurityUtilConstants.PERF_AUDIT_THREAD_ID) == null) {
			MDC.put(SecurityUtilConstants.PERF_AUDIT_THREAD_ID, UUID.randomUUID().toString());
		}
		return MDC.get(SecurityUtilConstants.PERF_AUDIT_THREAD_ID).toString();
	}

	public static String sanitizeUrl(String url, Map<String, Object> templates) {
		templates = new HashMap<>(templates);
		return UriBuilder.fromUri(url).resolveTemplates(templates).build().toString();
	}

	public static String convertFieldNameInJsonArrayBody(Object requestDto, String oldKey, String newKey)
			throws JsonProcessingException {

		try {
			JSONObject jsonObject = new JSONObject(requestDto);
			Object oldValue = jsonObject.get(oldKey);
			jsonObject.remove(oldKey);
			jsonObject.put(newKey, oldValue);
			return new JSONArray(Arrays.asList(jsonObject)).toString();
		} catch (Exception e) {
			return new ObjectMapper().writeValueAsString(Arrays.asList(requestDto));
		}

	}

	public static String convertFieldNameInJsonObjectBody(Object requestDto, String oldKey, String newKey)
			throws JsonProcessingException {

		try {
			JSONObject jsonObject = new JSONObject(requestDto);
			Object oldValue = jsonObject.get(oldKey);
			jsonObject.remove(oldKey);
			jsonObject.put(newKey, oldValue);
			return jsonObject.toString();
		} catch (Exception e) {
			return new ObjectMapper().writeValueAsString(requestDto);
		}

	}

	public static void sanitizeLogging(Logger logger, Level level, String msg, Object... args) {
		Object[] objects = args != null
				? Stream.of(args).filter(Objects::nonNull).map(obj -> StringUtil.encodeString(obj.toString())).toArray()
				: new Object[] {};
		logger.atLevel(level).log(msg, objects);
	}
}
