package com.bh.cp.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.management.relation.InvalidRoleInfoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.user.dto.response.RoleResponseDTO;
import com.bh.cp.user.exception.RoleException;
import com.bh.cp.user.pojo.Policy;
import com.bh.cp.user.pojo.PolicyResponse;
import com.bh.cp.user.pojo.Privilege;
import com.bh.cp.user.pojo.Role;
import com.bh.cp.user.pojo.Roles;
import com.bh.cp.user.service.PrivilegeService;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

class RoleServiceImplTest {

	@InjectMocks
	private RoleServiceImpl roleServiceImpl;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private RestTemplate restTemplate;

	private String clientIdPk;

	private String baseRoleId;

	@Mock
	private RestClientWrapperService restClientWrapperService;

	@Mock
	private PrivilegeService privilegeService;

	private List<PolicyResponse> listPolicyResponse = new ArrayList<>();

	private PolicyResponse policyResponse;

	private List<Privilege> listPrivilege;

	private Policy policy;

	private List<Policy> listPolicy;

	private Privilege privilege;

	private Role role;

	private Roles roles;

	private MockHttpServletRequest mockHttpServletRequest;

	private HttpHeaders headers = new HttpHeaders();

	private String token;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(roleServiceImpl, "clientIdPk", "testClientId");
		ReflectionTestUtils.setField(roleServiceImpl, "clientUri", "https://test");
		ReflectionTestUtils.setField(roleServiceImpl, "allRolesUri", "/v1/roles");
		ReflectionTestUtils.setField(roleServiceImpl, "roleDetailsUri", "/v1/roles/<role_id>");
		ReflectionTestUtils.setField(roleServiceImpl, "deleteRoleUri", "https://test/");
		ReflectionTestUtils.setField(roleServiceImpl, "baseRoleId", "1");
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
		policy.setDescription("test-desc");
		List<LinkedHashMap<String, String>> rolesLinkedHashMap = new ArrayList<>();
		policy.setRoles(rolesLinkedHashMap);
		policy.getRoles();
		listPolicy = new ArrayList<>();
		listPolicy.add(policy);
		privilege = new Privilege();
		privilege.setClientId("testClientId");
		privilege.setPolicies(listPolicy);
		listPrivilege = new ArrayList<>();
		listPrivilege.add(privilege);

		role = new Role();
		role.setClientRole(true);
		role.setId("role_id");
		role.setName("role");
		List<String> privilegesList = new ArrayList<>();
		privilegesList.add("privilege1");
		role.setPrivileages(privilegesList);

		roles = new Roles();

		roles.setClientRole(true);
		roles.setId("role_id");
		roles.setName("role");
		roles.setPrivileages(privilegesList);

		token = "eyJhbGciOiJSUzI1NiIsIn";
		headers.set("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");

	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void testCreateRole() throws Exception {
	when(jwtUtil.generateAdminToken()).thenReturn(token);
		
		clientIdPk=	privilege.getClientId();
		HttpEntity<Roles> entity = new HttpEntity<>(roles, headers);
		restTemplate.exchange(
				("https://test"+ "/" + clientIdPk + "/roles"),
				(HttpMethod.POST), entity, Object.class);
		
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		HttpEntity<Object> entityRoleGet = new HttpEntity<>(headers);
		LinkedHashMap hashMap= new LinkedHashMap<>();
		hashMap.put("id", role.getName());
		ResponseEntity<LinkedHashMap> responseRole =ResponseEntity.ok(hashMap);
		when(restTemplate.exchange( "https://test/"+ clientIdPk + "/roles/" + role.getName(), HttpMethod.GET, entityRoleGet,
				LinkedHashMap.class)).thenReturn(responseRole);
	
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		
		ArrayList list= new ArrayList();
		LinkedHashMap hashMap2= new LinkedHashMap<>();
		List<LinkedHashMap<String,String>> roleList= new ArrayList<>();
		LinkedHashMap config=new LinkedHashMap<>();
		LinkedHashMap roleLinkedList= new LinkedHashMap<>();
		roleLinkedList.put("id","role");
		roleLinkedList.put("required", "false");
		roleList.add(roleLinkedList);
		config.put("roles","[{\"id\":"+baseRoleId+",\"required\":false},{\"id\":"+baseRoleId+",\"required\":false}]");
		hashMap2.put("id", "privilegeId");
		hashMap2.put("config", config);
		String privilegeName="privilege1";
		hashMap2.put("name",privilegeName);
		
		list.add(hashMap2);
		ResponseEntity<ArrayList> response = ResponseEntity.ok(list);
		when(restTemplate.exchange(eq("https://test/" + clientIdPk + "/authz/resource-server/policy"), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(ArrayList.class))).thenReturn(response);
		restTemplate.exchange(
				("https://test"+ "/" + clientIdPk + "/authz/resource-server/policy/role/" + policy.getId()),
				(HttpMethod.POST), entity, Object.class);
		Role createRoleAssociatePrivileges = roleServiceImpl.createRoleAssociatePrivileges(role);
		assertNotNull(createRoleAssociatePrivileges);
	}
//
//	@SuppressWarnings("rawtypes")
//	@Test
//	void testRoleIdNull() throws JsonProcessingException, InvalidRoleInfoException {
//		when(jwtUtil.generateAdminToken()).thenReturn(token);
//		
//		clientIdPk=	privilege.getClientId();
//		HttpEntity<Roles> entity = new HttpEntity<>(roles, headers);
//		restTemplate.exchange(
//				("https://test"+ "/" + clientIdPk + "/roles"),
//				(HttpMethod.POST), entity, Object.class);
//		when(jwtUtil.generateAdminToken()).thenReturn(token);
//		HttpEntity<Object> entityRoleGet = new HttpEntity<>(headers);
//		LinkedHashMap hashMap= new LinkedHashMap<>();
//		ResponseEntity<LinkedHashMap> responseRole =ResponseEntity.ok(hashMap);
//		when(restTemplate.exchange( "https://test/"+ clientIdPk + "/roles/" + role.getName(), HttpMethod.GET, entityRoleGet,
//				LinkedHashMap.class)).thenReturn(responseRole);
//		assertThrows(RoleException.class, () -> roleServiceImpl.createRoleAssociatePrivileges(role));
//	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void testUpdateRole() throws Exception {
		
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		
        ArrayList listResponse= new ArrayList<>();
		LinkedHashMap hashMap2= new LinkedHashMap<>();
		LinkedHashMap config=new LinkedHashMap<>();
		config.put("roles","[{\"id\":"+baseRoleId+",\"required\":false},{\"id\":"+baseRoleId+",\"required\":false}]");
		hashMap2.put("id", "privilegeId");
		hashMap2.put("name", "privilege1");
		hashMap2.put("config", config);
		
		listResponse.add(hashMap2);
		ResponseEntity<ArrayList> response = ResponseEntity.ok(listResponse);
		when(restTemplate.exchange(eq("https://test/" + "testClientId" + "/authz/resource-server/policy"), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(ArrayList.class))).thenReturn(response);
		HttpEntity<Roles> entity = new HttpEntity<>(roles, headers);
		restTemplate.exchange(
				("https://test"+ "/"  + "testClientId" + "/authz/resource-server/policy/role/" + policy.getId()),
				(HttpMethod.PUT), entity, Object.class);
		
		ResponseEntity<Object> updateRoleAssociatePrivileges = roleServiceImpl.updateRoleAssociatePrivileges(role);
		assertNotNull(updateRoleAssociatePrivileges);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void testUpdateRoleExistingRole() throws Exception {
		role.setId(baseRoleId);
		when(jwtUtil.generateAdminToken()).thenReturn(token);

		ArrayList listResponse = new ArrayList<>();
		LinkedHashMap hashMap2 = new LinkedHashMap<>();
		LinkedHashMap config = new LinkedHashMap<>();
		config.put("roles", "[{\"id\": " + baseRoleId + ",\"required\":false}]");
		hashMap2.put("id", "privilegeId");
		hashMap2.put("name", "privilegeNotMatch");
		hashMap2.put("config", config);

		listResponse.add(hashMap2);
		ResponseEntity<ArrayList> response = ResponseEntity.ok(listResponse);
		when(restTemplate.exchange(eq("https://test/" + "testClientId" + "/authz/resource-server/policy"),
				eq(HttpMethod.GET), any(HttpEntity.class), eq(ArrayList.class))).thenReturn(response);
		HttpEntity<Roles> entity = new HttpEntity<>(roles, headers);
		restTemplate.exchange(
				("https://test" + "/" + "testClientId" + "/authz/resource-server/policy/role/" + policy.getId()),
				(HttpMethod.PUT), entity, Object.class);

		ResponseEntity<Object> updateRoleAssociatePrivileges = roleServiceImpl.updateRoleAssociatePrivileges(role);
		assertNotNull(updateRoleAssociatePrivileges);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void testUpdateRoleExistingRoleAndPrivilegeExist() throws Exception {
		role.setId(baseRoleId);
		when(jwtUtil.generateAdminToken()).thenReturn(token);

		ArrayList listResponse = new ArrayList<>();
		LinkedHashMap hashMap2 = new LinkedHashMap<>();
		LinkedHashMap config = new LinkedHashMap<>();
		config.put("roles", "[{\"id\": " + baseRoleId + ",\"required\":false}]");
		hashMap2.put("id", "privilegeId");
		hashMap2.put("name", "privilege1");
		hashMap2.put("config", config);

		listResponse.add(hashMap2);
		ResponseEntity<ArrayList> response = ResponseEntity.ok(listResponse);
		when(restTemplate.exchange(eq("https://test/" + "testClientId" + "/authz/resource-server/policy"),
				eq(HttpMethod.GET), any(HttpEntity.class), eq(ArrayList.class))).thenReturn(response);
		HttpEntity<Roles> entity = new HttpEntity<>(roles, headers);
		restTemplate.exchange(
				("https://test" + "/" + "testClientId" + "/authz/resource-server/policy/role/" + policy.getId()),
				(HttpMethod.PUT), entity, Object.class);

		ResponseEntity<Object> updateRoleAssociatePrivileges = roleServiceImpl.updateRoleAssociatePrivileges(role);
		assertNotNull(updateRoleAssociatePrivileges);
	}

	@Test
	void testGetAllRoles() throws JsonProcessingException {
		String outputJson = "[{\"id\": \"role_id1\",\"name\": \"role1\"},{\"id\": \"role_id2\",\"name\": \"role2\"}]";
		when(jwtUtil.generateAdminToken()).thenReturn("Bearer abc");
		when(restClientWrapperService.getResponseFromUrl(any(HttpServletRequest.class), anyString()))
				.thenReturn(ResponseEntity.of(Optional.of(outputJson)));
		List<RoleResponseDTO> actualResponse = roleServiceImpl.getAllRoles(mockHttpServletRequest);
		assertEquals(2, actualResponse.size());
		assertEquals("role_id1", actualResponse.get(0).getId());
		assertEquals("role_id2", actualResponse.get(1).getId());
	}

	@Test
	void testGetPrivilegesForRole() throws JsonProcessingException {

		String roleJson = "{\"id\": \"role_id1\",\"name\": \"role1\"}";
		when(jwtUtil.generateAdminToken()).thenReturn("Bearer abc");
		when(restClientWrapperService.getResponseFromUrl(any(HttpServletRequest.class), anyString()))
				.thenReturn(ResponseEntity.of(Optional.of(roleJson)));
		String policyJson = "[{\"id\": \"policy_id1\",\"name\": \"policy1\",\"config\": {\"roles\":\"[{\\\"id\\\":\\\"role_id1\\\"}]\"}}]";
		when(restClientWrapperService.getResponseFromUrl(any(HttpServletRequest.class), eq("/v1/policy")))
				.thenReturn(ResponseEntity.of(Optional.of(policyJson)));
		when(privilegeService.getAllPrivileges(any(HttpServletRequest.class))).thenReturn(policyJson);
		RoleResponseDTO actualResponse = roleServiceImpl.getPrivilegesForRole(mockHttpServletRequest, "role_id1");
		assertNotNull(actualResponse);
		assertEquals("role_id1", actualResponse.getId());
		assertEquals("role1", actualResponse.getName());
		assertEquals(1, actualResponse.getPrivileges().size());
	}

	@Test
	void testDeleteRole() throws JsonProcessingException, InvalidRoleInfoException, RoleException {
		when(jwtUtil.generateAdminToken()).thenReturn("Bearer abc");
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<Object> response =new ResponseEntity<>(HttpStatus.NO_CONTENT); 
		restTemplate.exchange("https://test/" + 2, HttpMethod.DELETE, httpEntity, Object.class);
		 response = roleServiceImpl.deleteRole("2");
		assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
	}

	@Test
	void testValidateBaseRoleDeletion() throws JsonProcessingException, InvalidRoleInfoException {
		assertThrows(RoleException.class, () -> roleServiceImpl.deleteRole("1"));
	}

}
