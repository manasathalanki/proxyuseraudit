package com.bh.cp.dashboard.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bh.cp.dashboard.constants.JwtUtilConstants;
import com.bh.cp.dashboard.dto.request.LoginRequestDTO;
import com.bh.cp.dashboard.dto.response.LoginResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	private final String clientId;

	private final String clientCredValue;

	private final String grantType;

	private final String tokenGenerationUrl;

	private final String credKeyword;

	private RestTemplate restTemplate;

	public JwtUtil(@Autowired RestTemplate restTemplate, @Value("${keycloak.client-id}") String clientId,
			@Value("${keycloak.client-secret}") String clientCredValue,
			@Value("${keycloak.token.generation.grant-type}") String grantType,
			@Value("${keycloak.token.generation.url}") String tokenGenerationUrl,
			@Value("${keycloak.token.generation.cred-keyword}") String credKeyword) {
		super();
		this.restTemplate = restTemplate;
		this.clientId = clientId;
		this.clientCredValue = clientCredValue;
		this.grantType = grantType;
		this.tokenGenerationUrl = tokenGenerationUrl;
		this.credKeyword = credKeyword;
	}

	public Map<String, Claim> getClaims(String key) {
		DecodedJWT jwt = JWT.decode(key);
		return jwt.getClaims();
	}

	public LoginResponseDTO generateAccessToken(LoginRequestDTO loginCredential) {
		return generateToken(tokenGenerationUrl, loginCredential.getUsername(), loginCredential.getPassword(), clientId,
				clientCredValue, grantType);
	}

	private LoginResponseDTO generateToken(String tokenGenerationUrl, String username, String credKeysValue,
			String clientId, String credValue, String grantType) {
		try {
			logger.info("generateToken=> generate token");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add(JwtUtilConstants.TOKEN_USERNAME, username);
			map.add(credKeyword, credKeysValue);
			map.add(JwtUtilConstants.CLIENT_ID, clientId);
			map.add(JwtUtilConstants.CLIENT_CRED_TYPE, credValue);
			map.add(JwtUtilConstants.GRANT_TYPE, grantType);
			HttpEntity<MultiValueMap<String, String>> http = new HttpEntity<>(map, headers);
			logger.info("URI=>{}", tokenGenerationUrl);
			ResponseEntity<JsonNode> loginResponseRest = restTemplate.postForEntity(tokenGenerationUrl, http,
					JsonNode.class);
			JsonNode jsonNode = new ObjectMapper().valueToTree(loginResponseRest.getBody());
			LoginResponseDTO loginResponse = new LoginResponseDTO();
			if (!jsonNode.isNull() && jsonNode.has(JwtUtilConstants.ACCESS_TOKEN)) {
				logger.info("generateToken=>Token generated");
				String token = jsonNode.get(JwtUtilConstants.ACCESS_TOKEN).toString();
				loginResponse.setUsername(username);
				loginResponse.setToken(token.substring(1, token.length() - 1));
			}
			return loginResponse;
		} catch (HttpClientErrorException e) {
			throw new HttpClientErrorException(e.getStatusCode(), "Token Expired/Invalid username or password!");
		}
	}

}
