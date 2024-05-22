package com.bh.cp.proxy.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.bh.cp.proxy.constants.JwtUtilConstants;
import com.bh.cp.proxy.dto.request.LoginRequestDTO;
import com.bh.cp.proxy.dto.response.LoginResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

class JwtUtilTest {

	@InjectMocks
	private JwtUtil jwtUtil;

	private String valid = null;

	private String invalid = null;

	@Mock
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() throws IOException, JOSEException {
		MockitoAnnotations.openMocks(this);

		// Sample Token Generation using nimbus JWT with RS256 Algorithm
		RSAKey rsaKey = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE)
				.algorithm(new com.nimbusds.jose.Algorithm("RS256")).keyID("abc").generate();
		RSASSASigner signer = new RSASSASigner(rsaKey);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim("sub", "1234567890").claim("name", "John Doe")
				.claim("sample_claim", "test").claim("admin", true).issueTime(new Date(1516239022)).build();
		SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(),
				claimsSet);
		signedJWT.sign(signer);
		valid = signedJWT.serialize();
		invalid = signedJWT.serialize().substring(20);

		ReflectionTestUtils.setField(jwtUtil, "tokenGenerationUrl", "http://test.com/token");
	}

	@Test
	@DisplayName("Test GetClaims1 -- Valid Token")
	void testGetClaims_Positive1() {
		Map<String, Claim> claims = jwtUtil.getClaims(valid);
		assertEquals("1234567890", claims.get("sub").as(String.class));
		assertEquals("John Doe", claims.get("name").as(String.class));
		assertEquals(true, claims.get("admin").as(Boolean.class));
		assertEquals(1516239, claims.get("iat").as(Long.class));
		assertEquals("test", claims.get("sample_claim").as(String.class));
	}

	@Test
	@DisplayName("Test GetClaims2 -- Invalid Token")
	void testGetClaims_Negative1() {
		Exception outputException = assertThrows(JWTDecodeException.class, () -> jwtUtil.getClaims(invalid));
		assertEquals(true, outputException.getMessage().contains("doesn't have a valid JSON format."));
	}

	@Test
	@DisplayName("Test generateAccessToken1 -- Valid Username and Password")
	void testGenerateAccessToken_Positive1() {
		JSONObject jsonObj = new JSONObject().put(JwtUtilConstants.ACCESS_TOKEN, "Bearer ACCESS_TOKEN");
		JsonNode jsonNode = new ObjectMapper().valueToTree(jsonObj.toMap());
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(JsonNode.class)))
				.thenReturn(ResponseEntity.of(Optional.of(jsonNode)));
		LoginRequestDTO requestDto = new LoginRequestDTO();
		requestDto.setUsername("abc");
		requestDto.setPassword("abc");
		LoginResponseDTO output = jwtUtil.generateAccessToken(requestDto);
		assertEquals("abc", output.getUsername());
		assertEquals("Bearer ACCESS_TOKEN", output.getToken());
	}

	@Test
	@DisplayName("Test generateAccessToken2 -- Invalid Username and Password")
	void testGenerateAccessToken_Negative1() {
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(JsonNode.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));
		LoginRequestDTO requestDto = new LoginRequestDTO();
		requestDto.setUsername("abc");
		requestDto.setPassword("abc");
		assertThatThrownBy(()->jwtUtil.generateAccessToken(requestDto)).isInstanceOf(HttpClientErrorException.class).hasMessage("403 Token Expired/Invalid username or password!");
	}

	@Test
	@DisplayName("Test generateAccessToken3 -- Invalid Response")
	void testGenerateAccessToken_Negative2() {
		JsonNode jsonNode = new ObjectMapper().valueToTree("{\"invalid\":\"response\"}");
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(JsonNode.class)))
				.thenReturn(ResponseEntity.of(Optional.of(jsonNode)));
		LoginRequestDTO requestDto = new LoginRequestDTO();
		requestDto.setUsername("abc");
		requestDto.setPassword("abc");
		LoginResponseDTO output = jwtUtil.generateAccessToken(requestDto);
		assertEquals(null, output.getUsername());
		assertEquals(null, output.getToken());
	}

	@Test
	@DisplayName("Test generateAcessToken4 -- Null Response")
	void testGenerateAccessToken_Negative3() {
		JsonNode jsonNode = new ObjectMapper().valueToTree(null);
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(JsonNode.class)))
				.thenReturn(ResponseEntity.of(Optional.of(jsonNode)));
		LoginRequestDTO requestDto = new LoginRequestDTO();
		requestDto.setUsername("abc");
		requestDto.setPassword("abc");
		LoginResponseDTO output = jwtUtil.generateAccessToken(requestDto);
		assertEquals(null, output.getUsername());
		assertEquals(null, output.getToken());
	}

}