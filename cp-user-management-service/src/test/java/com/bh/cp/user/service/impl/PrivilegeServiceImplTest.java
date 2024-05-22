package com.bh.cp.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.user.pojo.Policy;
import com.bh.cp.user.pojo.PolicyResponse;
import com.bh.cp.user.pojo.Privilege;

class PrivilegeServiceImplTest {

	@InjectMocks
	private PrivilegeServiceImpl privilegeServiceImpl;

	@Mock
	private RestTemplate restTemplate;

	@Value("${client-id-pk}")
	private String clientIdPk;

	@Value("${get-all-policy-uri}")
	private String getAllPolicyUri;

	private List<PolicyResponse> listPolicyResponse = new ArrayList<>();

	private PolicyResponse policyResponse;

	private List<Privilege> listPrivilege;

	private Policy policy;

	private List<Policy> listPolicy;

	private Privilege privilege;

	private MockHttpServletRequest mockHttpServletRequest;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtils.setField(privilegeServiceImpl, "clientIdPk", "testClientId");
		ReflectionTestUtils.setField(privilegeServiceImpl, "getAllPolicyUri",
				"https://icenter-sparq.np-0000029.npaeuw1.bakerhughes.com/dfc-svc/api/v1/policy?type=role");
		policyResponse = new PolicyResponse();
		policyResponse.setDescription("test_descp");
		policyResponse.setId("test-id");
		policyResponse.setName("test-name");
		listPolicyResponse.add(policyResponse);

		policy = new Policy();
		policy.setDescription("test-desp");
		policy.setId("policy-id");
		policy.setType("role");
		policy.setName("test-name");
		policy.setDescription("test-descp");
		List<LinkedHashMap<String, String>> roles = new ArrayList<>();
		policy.setRoles(roles);
		policy.getRoles();
		
		listPolicy = new ArrayList<>();
		listPolicy.add(policy);
		privilege = new Privilege();
		privilege.setClientId("testClientId");
		privilege.setPolicies(listPolicy);
		listPrivilege = new ArrayList<>();
		listPrivilege.add(privilege);
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");

	}


	@Test
	void testGetAllPrivileges() {
		ResponseEntity<List<Privilege>> clientsResponseEntity = ResponseEntity.ok(listPrivilege);
		when(restTemplate.exchange(
				eq("https://icenter-sparq.np-0000029.npaeuw1.bakerhughes.com/dfc-svc/api/v1/policy?type=role"),
				eq(HttpMethod.GET), any(HttpEntity.class), Mockito.<ParameterizedTypeReference<List<Privilege>>>any()))
				.thenReturn(clientsResponseEntity);
		listPolicyResponse = privilegeServiceImpl.getAllPrivilege(mockHttpServletRequest);
		assertNotNull(listPolicyResponse);
	}

}
