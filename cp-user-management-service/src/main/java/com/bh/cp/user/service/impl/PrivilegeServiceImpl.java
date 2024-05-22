package com.bh.cp.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.pojo.Policy;
import com.bh.cp.user.pojo.PolicyResponse;
import com.bh.cp.user.pojo.Privilege;
import com.bh.cp.user.service.PrivilegeService;
import com.bh.cp.user.service.RestClientWrapperService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

	private static final Logger logger = LoggerFactory.getLogger(PrivilegeServiceImpl.class);

	private String clientIdPk;

	private String getAllPolicyUri;

	private String getAllPolicyUriDirect;

	private RestTemplate restTemplate;

	private RestClientWrapperService restClientWrapperService;

	public PrivilegeServiceImpl(@Autowired RestTemplate restTemplate,
			@Autowired RestClientWrapperService restClientWrapperService,
			@Value("${keycloak.client-id-pk}") String clientIdPk,
			@Value("${keycloak.get.all.policy.uri-direct}") String getAllPolicyUriDirect,
			@Value("${sparq.accs.get.all.policy.uri}") String getAllPolicyUri) {
		super();
		this.restTemplate = restTemplate;
		this.restClientWrapperService = restClientWrapperService;
		this.clientIdPk = clientIdPk;
		this.getAllPolicyUri = getAllPolicyUri;
		this.getAllPolicyUriDirect = getAllPolicyUriDirect;
	}

	@Override
	public List<PolicyResponse> getAllPrivilege(HttpServletRequest httpServletRequest) {
		logger.info("get all privileges");
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION,
				UMSConstants.BEARER + httpServletRequest.getHeader(UMSConstants.AUTHORIZATION).substring(7));
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<List<Privilege>> exchange = restTemplate.exchange(getAllPolicyUri, HttpMethod.GET, httpEntity,
				new ParameterizedTypeReference<List<Privilege>>() {
				});
		PolicyResponse response = null;
		List<PolicyResponse> policyList = new ArrayList<>();
		for (Privilege privilege : exchange.getBody()) {
			if (privilege.getClientId().equals(clientIdPk)) {
				for (Policy policy : privilege.getPolicies()) {
					response = new PolicyResponse();
					response.setId(policy.getId());
					response.setName(policy.getName());
					response.setDescription(policy.getDescription());
					policyList.add(response);
				}
				break;
			}
		}
		logger.info("Leaving getAllPrivilege");
		return policyList;
	}

	@Override
	@Cacheable(value = "privileges", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public String getAllPrivileges(HttpServletRequest httpServletRequest) {
		ResponseEntity<String> policyResponse = restClientWrapperService.getResponseFromUrl(httpServletRequest,
				getAllPolicyUriDirect);
		return policyResponse.getBody();
	}
}
