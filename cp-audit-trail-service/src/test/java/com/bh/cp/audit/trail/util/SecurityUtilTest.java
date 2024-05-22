package com.bh.cp.audit.trail.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jboss.logging.MDC;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.audit.trail.constants.SecurityUtilConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Getter;

class SecurityUtilTest {

	private MockHttpServletRequest mockHttpServletRequest;

	private JwtUtil jwtUtil;

	private String valid = null;

	private String invalid = null;

	@BeforeEach
	void setUp() throws IOException, JOSEException {
		MockitoAnnotations.openMocks(this);
		jwtUtil = new JwtUtil();
		mockHttpServletRequest = new MockHttpServletRequest();

		// Sample Token Generation using nimbus JWT with RS256 Algorithm
		RSAKey rsaKey = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE)
				.algorithm(new com.nimbusds.jose.Algorithm("RS256")).keyID("abc").generate();
		RSASSASigner signer = new RSASSASigner(rsaKey);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim("sub", "1234567890").claim("name", "John Doe")
				.claim("sample_claim", "test").claim(SecurityUtilConstants.KEY_PREFERRED_USERNAME, "testuser")
				.claim("admin", true).issueTime(new Date(1516239022)).build();
		SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(),
				claimsSet);
		signedJWT.sign(signer);
		valid = signedJWT.serialize();
		invalid = signedJWT.serialize().substring(20);
	}

	@Test
	@DisplayName("Test getClaims1 -- Valid Token")
	void testGetClaims_Positive1() {
		mockHttpServletRequest.addHeader(SecurityUtilConstants.KEY_AUTHORIZATION,
				SecurityUtilConstants.KEY_BEARER + valid);
		Map<String, Claim> claims = SecurityUtil.getClaims(mockHttpServletRequest, jwtUtil);
		assertEquals("1234567890", claims.get("sub").as(String.class));
		assertEquals("John Doe", claims.get("name").as(String.class));
		assertEquals(true, claims.get("admin").as(Boolean.class));
		assertEquals(1516239, claims.get("iat").as(Long.class));
		assertEquals("testuser", claims.get("preferred_username").as(String.class));
	}

	@Test
	@DisplayName("Test getClaims2 -- Invalid Token")
	void testGetClaims_Negative1() {
		mockHttpServletRequest.addHeader(SecurityUtilConstants.KEY_AUTHORIZATION,
				SecurityUtilConstants.KEY_BEARER + invalid);
		Exception outputException = assertThrows(AccessDeniedException.class,
				() -> SecurityUtil.getClaims(mockHttpServletRequest, jwtUtil));
		assertEquals("Not logged in.", outputException.getMessage());
	}

	@Test
	@DisplayName("Test getSSO1 -- Valid Claims")
	void testGetSSO_Positive1() {
		mockHttpServletRequest.addHeader(SecurityUtilConstants.KEY_AUTHORIZATION,
				SecurityUtilConstants.KEY_BEARER + valid);
		Map<String, Claim> claims = SecurityUtil.getClaims(mockHttpServletRequest, jwtUtil);
		assertEquals("testuser", SecurityUtil.getSSO(claims));
	}

	@Test
	@DisplayName("Test getSSO2 -- Invalid Claims")
	void testGetSSO_Negative1() {
		Map<String, Claim> claims = new HashMap<>();
		assertThatThrownBy(() -> SecurityUtil.getSSO(claims)).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("Test getThreadName1 -- MDC with Thread Id")
	void testGetThreadName_Positive1() {
		MDC.put(SecurityUtilConstants.PERF_AUDIT_THREAD_ID, "sample_thread_id");
		assertEquals("sample_thread_id", SecurityUtil.getThreadName());
	}

	@Test
	@DisplayName("Test getThreadName2 -- MDC without Thread Id")
	void testGetThreadName_Negative1() {
		MDC.clear();
		UUID result = UUID.randomUUID();
		MockedStatic<UUID> mockedUUID = mockStatic(UUID.class);
		mockedUUID.when(UUID::randomUUID).thenReturn(result);
		assertEquals(result.toString(), SecurityUtil.getThreadName());
	}

	@Nested
	class TestConvertFieldNameMethods {

		@Getter
		public class TestDTO {

			private String id;

			private String name;

			public TestDTO(String id, String name) {
				super();
				this.id = id;
				this.name = name;
			}

		}

		@Test
		@DisplayName("Test convertFieldNameInJsonArrayBody1 -- Check Path Varialbe Replacement")
		void testConvertFieldNameInJsonArrayBody_Positive1() throws JsonProcessingException {
			String output = SecurityUtil.convertFieldNameInJsonArrayBody(new TestDTO("123", "name"), "id", "groupId");
			assertDoesNotThrow(() -> new JSONArray(output));
			assertFalse(output.contains("\"id\""));
			assertTrue(output.contains("\"groupId\""));
		}

		@Test
		@DisplayName("Test convertFieldNameInJsonArrayBody2 -- Null Response dto")
		void testConvertFieldNameInJsonArrayBody_Negative1() throws JsonProcessingException {
			String output = SecurityUtil.convertFieldNameInJsonArrayBody(null, "id", "groupId");
			assertDoesNotThrow(() -> new JSONArray(output));
		}

		@Test
		@DisplayName("Test convertFieldNameInJsonObjectBody1 -- Check Path Varialbe Replacement")
		void testConvertFieldNameInJsonObjectBody_Positive1() throws JsonProcessingException {
			String output = SecurityUtil.convertFieldNameInJsonObjectBody(new TestDTO("123", "name"), "id", "groupId");
			assertDoesNotThrow(() -> new JSONObject(output));
			assertFalse(output.contains("\"id\""));
			assertTrue(output.contains("\"groupId\""));
		}

		@Test
		@DisplayName("Test convertFieldNameInJsonObjectBody2 -- Null Response dto")
		void testConvertFieldNameInJsonObjectBody_Negative1() throws JsonProcessingException {
			String output = SecurityUtil.convertFieldNameInJsonObjectBody(null, "id", "groupId");
			assertThrows(JSONException.class, () -> new JSONObject(output));
		}
	}

	@Nested
	class TestSanitizeLogging {

		class TestAppender extends AppenderBase<ILoggingEvent> {

			String message;

			String level;

			@Override
			protected void append(ILoggingEvent eventObject) {
				message = eventObject.getFormattedMessage();
				level = eventObject.getLevel().toString();
			}
		}

		@Test
		@DisplayName("Test sanitizeLogging1 -- Check Sanitize Logging")
		void testSanitizeLogging_Positive1() {
			Logger logger = (Logger) LoggerFactory.getLogger("Test Logger");
			TestAppender testAppender = new TestAppender();
			logger.addAppender(testAppender);
			testAppender.start();
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "replaced {}", "[SQUARE_BRACKETS] && \"DOUBLE QUOTES\"");
			assertEquals("INFO", testAppender.level);
			assertEquals("replaced SQUARE_BRACKETS && DOUBLE QUOTES", testAppender.message);
		}

		@Test
		@DisplayName("Test sanitizeLogging2 -- Null Object")
		void testSanitizeLogging_Negative1() {
			Logger logger = (Logger) LoggerFactory.getLogger("Test Logger");
			TestAppender testAppender = new TestAppender();
			logger.addAppender(testAppender);
			testAppender.start();
			Object[] objArr = null;
			SecurityUtil.sanitizeLogging(logger, Level.INFO, "replaced {}", objArr);
			assertEquals("INFO", testAppender.level);
			assertEquals("replaced {}", testAppender.message);
		}

	}
}
