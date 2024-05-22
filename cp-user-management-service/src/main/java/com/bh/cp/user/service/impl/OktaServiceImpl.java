package com.bh.cp.user.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.request.SearchRequestDTO;
import com.bh.cp.user.dto.response.OktaUserDetailsResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.service.OktaService;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.util.CustomHttpServletRequestWrapper;
import com.bh.cp.user.util.JwtUtil;
import com.bh.cp.user.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OktaServiceImpl implements OktaService {

	private static final Logger logger = LoggerFactory.getLogger(OktaServiceImpl.class);

	private final String oktaUsersUri;

	private final String oktaCredValue;

	private final String oktaSearchQueryParam;

	private final String oktaSearchQueryParamEquals;

	private final String oktaSearchQueryParamOr;

	private final String keycloakUsersUri;

	private final String keycloakSearchQueryParam;

	private JwtUtil jwtUtil;

	private RestClientWrapperService restClientWrapperService;

	public OktaServiceImpl(@Autowired JwtUtil jwtUtil, @Autowired RestClientWrapperService restClientWrapperService,
			@Value("${okta.service.get.users.uri}") String oktaUsersUri,
			@Value("${okta.service.secret}") String oktaCredValue,
			@Value("${okta.service.search.query.param}") String oktaSearchQueryParam,
			@Value("${okta.service.search.query.param.equals}") String oktaSearchQueryParamEquals,
			@Value("${okta.service.search.query.param.or}") String oktaSearchQueryParamOr,
			@Value("${keycloak.users.uri}") String keycloakUsersUri,
			@Value("${keycloak.user.search.query.param}") String keycloakSearchQueryParam) {
		super();
		this.jwtUtil = jwtUtil;
		this.restClientWrapperService = restClientWrapperService;
		this.oktaUsersUri = oktaUsersUri;
		this.oktaCredValue = oktaCredValue;
		this.oktaSearchQueryParam = oktaSearchQueryParam;
		this.oktaSearchQueryParamEquals = oktaSearchQueryParamEquals;
		this.oktaSearchQueryParamOr = oktaSearchQueryParamOr;
		this.keycloakUsersUri = keycloakUsersUri;
		this.keycloakSearchQueryParam = keycloakSearchQueryParam;
	}

	@Override
	public List<OktaUserDetailsResponseDTO> getUsersByField(HttpServletRequest httpServletRequest,
			SearchRequestDTO requestDto) {
		String field = requestDto.getField();
		Set<String> searchValues = new HashSet<>(requestDto.getValues());
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Getting users from okta by {}", field);
		CustomHttpServletRequestWrapper customHttpServletRequestWrapper = new CustomHttpServletRequestWrapper(
				httpServletRequest, oktaCredValue);
		String oktaUsersSearchUri = UriComponentsBuilder.fromUriString(oktaUsersUri)
				.queryParam(oktaSearchQueryParam, buildOktaSearchQueryParam(field, searchValues)).build(false).toString();

		ResponseEntity<String> oktaUsersResponse = restClientWrapperService
				.getResponseFromUrl(customHttpServletRequestWrapper, oktaUsersSearchUri);
		return convertResponseToDetailDto(httpServletRequest, oktaUsersResponse);
	}

	public String buildOktaSearchQueryParam(String field, Set<String> values) {

		AtomicInteger replaceItemIndex = new AtomicInteger(1);
		StringJoiner strJoiner = new StringJoiner(" ");
		values.forEach(value -> {
			strJoiner.add(field).add(oktaSearchQueryParamEquals).add("\"" + value + "\"");
			if (replaceItemIndex.getAndIncrement() == values.size()) {
				return;
			}
			strJoiner.add(oktaSearchQueryParamOr);
		});

		return strJoiner.toString();

	}

	private List<OktaUserDetailsResponseDTO> convertResponseToDetailDto(HttpServletRequest httpServletRequest,
			ResponseEntity<String> oktaUsersResponse) {

		JSONArray jsonArray = new JSONArray(oktaUsersResponse.getBody());
		List<OktaUserDetailsResponseDTO> oktaUserDetailsList = new ArrayList<>();
		for (Object obj : jsonArray) {
			JSONObject jsonObj = (JSONObject) obj;
			OktaUserDetailsResponseDTO oktaUserDetail = new OktaUserDetailsResponseDTO();
			oktaUserDetail.setId(jsonObj.getString("id"));
			oktaUserDetail.setStatus(jsonObj.getString("status"));
			oktaUserDetail.setProfile(jsonObj.getJSONObject("profile").toMap());
			JSONObject profileJsonObj = jsonObj.getJSONObject("profile");
			oktaUserDetail
					.setAuth(getKeycloakUserDetail(httpServletRequest, profileJsonObj.getString(UMSConstants.EMAIL)));
			oktaUserDetailsList.add(oktaUserDetail);
		}

		return oktaUserDetailsList;
	}

	private UserDetailsResponseDTO getKeycloakUserDetail(HttpServletRequest httpServletRequest, String email) {
		CustomHttpServletRequestWrapper customHttpServletRequestWrapper = new CustomHttpServletRequestWrapper(
				httpServletRequest, jwtUtil);
		String keycloakUsersSearchUri = UriComponentsBuilder.fromUriString(keycloakUsersUri)
				.queryParam(keycloakSearchQueryParam, email).build(false).toString();
		ResponseEntity<String> keycloakUsersResponse = restClientWrapperService
				.getResponseFromUrl(customHttpServletRequestWrapper, keycloakUsersSearchUri);
		UserDetailsResponseDTO keycloakUserDetail = null;
		JSONArray jsonArray = new JSONArray(keycloakUsersResponse.getBody());
		for (Object obj : jsonArray) {
			JSONObject jsonObj = (JSONObject) obj;
			keycloakUserDetail = new UserDetailsResponseDTO();
			keycloakUserDetail.setId(jsonObj.getString(UMSConstants.ID));
			keycloakUserDetail.setName(jsonObj.getString(UMSConstants.FIRSTNAME));
			keycloakUserDetail.setSurName(jsonObj.getString(UMSConstants.LASTNAME));
			keycloakUserDetail.setUserName(jsonObj.getString(UMSConstants.TOKEN_USERNAME));
			keycloakUserDetail.setEmail(jsonObj.getString(UMSConstants.EMAIL));
			keycloakUserDetail.setTitle("EXTERNAL");
			keycloakUserDetail.setEnabled(jsonObj.optBoolean("enabled") ? "Y" : "N");
			keycloakUserDetail.setStatus(jsonObj.optBoolean("enabled") ? "ACTIVE" : "INACTIVE");
			JSONObject attrObj = jsonObj.optJSONObject("attributes");
			if (attrObj == null) {
				continue;
			}
			JSONArray titleList = attrObj.getJSONArray("title");
			keycloakUserDetail.setTitle(titleList.get(0) != null ? titleList.get(0).toString() : "EXTERNAL");
		}
		return keycloakUserDetail;
	}

}
