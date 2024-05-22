package com.bh.cp.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.GroupRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.user.constants.JSONUtilConstants;
import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.dto.response.GroupResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.exception.DeletionNotPermissableException;
import com.bh.cp.user.pojo.DomainAttribute;
import com.bh.cp.user.service.FetchAssetHierarchyService;
import com.bh.cp.user.service.GenericAssetHierarchyFilterService;
import com.bh.cp.user.service.GroupService;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.util.CustomHttpServletRequestWrapper;
import com.bh.cp.user.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

class DomainServiceImplTest {

	@InjectMocks
	private DomainServiceImpl domainService;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private RestClientWrapperService restClientWrapperService;

	@Mock
	private GenericAssetHierarchyFilterService genericAssetHierarchyFilterService;

	@Mock
	private GroupService groupService;

	@Mock
	private FetchAssetHierarchyService fetchAssetHierarchyService;

	@Mock
	private CustomHttpServletRequestWrapper modifiedHttpServletRequest;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	private HttpHeaders headers;

	private String allDomainsResponse;

	private String master_groupId;

	private GroupRepresentation groupRepresentation;

	private List<String> realmRoles;

	protected Map<String, List<String>> attributes;

	private String token;

	private List<Map<String, Object>> assetHierarchy;

	private Map<String, String> displayNameMap;

	private List<String> projectsList;
	private List<String> plantsList;
	private List<String> trainsList;
	private List<String> lineupsList;
	private List<String> machinesList;

	private class TestConstants {

		private static final String TEST1 = "TEST1";
		private static final String PL_TEST1 = "PL_TEST1";
		private static final String MC_TEST1 = "MC_TEST1";
		private static final String PR_TEST1 = "PR_TEST1";
		private static final String TR_TEST1 = "TR_TEST1";
		private static final String LN_TEST1 = "LN_TEST1";
		private static final String TESTPRFIELDKEY1 = "testPRFieldKey1";
		private static final String TESTPLFIELDKEY1 = "testPLFieldKey1";
		private static final String TESTRFIELDKEY1 = "testTRFieldKey1";
		private static final String TESTLNFIELDKEY1 = "testLNFieldKey1";
		private static final String TESTMCFIELDKEY1 = "testMCFieldKey1";
		private static final String PR_TEST1VALUE1 = "PR_TEST1Value1";
		private static final String PL_TEST1VALUE1 = "PL_TEST1Value1";
		private static final String TR_TEST1VALUE1 = "TR_TEST1Value1";
		private static final String LN_TEST1VALUE1 = "LN_TEST1Value1";
		private static final String MC_TEST1VALUE1 = "MC_TEST1Value1";
	}

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		List<Map<String, Object>> projects1 = new ArrayList<>();
		Map<String, Object> data = new HashMap<>();
		data.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		data.put(JSONUtilConstants.DATA, projects1);
		List<Map<String, Object>> plants1 = new ArrayList<>();
		Map<String, Object> data1 = new HashMap<>();
		data1.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PLANTS);
		data1.put(JSONUtilConstants.DATA, plants1);
		List<Map<String, Object>> trains1 = new ArrayList<>();
		Map<String, Object> data2 = new HashMap<>();
		data2.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_TRAINS);
		data2.put(JSONUtilConstants.DATA, trains1);
		List<Map<String, Object>> lineups1 = new ArrayList<>();
		Map<String, Object> data3 = new HashMap<>();
		data3.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_LINEUPS);
		data3.put(JSONUtilConstants.DATA, lineups1);
		List<Map<String, Object>> machines1 = new ArrayList<>();
		Map<String, Object> data4 = new HashMap<>();
		data4.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_MACHINES);
		data4.put(JSONUtilConstants.DATA, machines1);

		Map<String, Object> additionalFields = new HashMap<>();
		additionalFields.put(JSONUtilConstants.EQUIPMENTCODES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.TECHNOLOGYCODES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.ENABLEDSERVICES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.GIBSERIALNOS, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.SERIALNOS, List.of("abc", "cde"));

		Map<String, Object> machine1 = new HashMap<>();
		machine1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		machine1.put(JSONUtilConstants.VID, TestConstants.MC_TEST1);
		machine1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.MC_TEST1,
				TestConstants.TESTMCFIELDKEY1, TestConstants.MC_TEST1VALUE1));
		machine1.putAll(additionalFields);
		machines1.add(machine1);

		Map<String, Object> lineup1 = new HashMap<>();
		lineup1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		lineup1.put(JSONUtilConstants.VID, TestConstants.LN_TEST1);
		lineup1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.LN_TEST1,
				TestConstants.TESTLNFIELDKEY1, TestConstants.LN_TEST1VALUE1));
		lineup1.put(JSONUtilConstants.CHILDREN, data4);
		lineup1.putAll(additionalFields);
		lineups1.add(lineup1);

		Map<String, Object> train1 = new HashMap<>();
		train1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		train1.put(JSONUtilConstants.VID, TestConstants.TR_TEST1);
		train1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.TR_TEST1,
				TestConstants.TESTRFIELDKEY1, TestConstants.TR_TEST1VALUE1));
		train1.put(JSONUtilConstants.CHILDREN, data3);
		train1.putAll(additionalFields);
		trains1.add(train1);

		Map<String, Object> plant1 = new HashMap<>();
		plant1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		plant1.put(JSONUtilConstants.VID, TestConstants.PL_TEST1);
		plant1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PL_TEST1,
				TestConstants.TESTPLFIELDKEY1, TestConstants.PL_TEST1VALUE1));
		plant1.put(JSONUtilConstants.CHILDREN, data2);
		plant1.putAll(additionalFields);
		plants1.add(plant1);

		Map<String, Object> project1 = new HashMap<>();
		project1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		project1.put(JSONUtilConstants.VID, TestConstants.PR_TEST1);
		project1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST1,
				TestConstants.TESTPRFIELDKEY1, TestConstants.PR_TEST1VALUE1));
		project1.put(JSONUtilConstants.CHILDREN, data1);
		project1.putAll(additionalFields);
		projects1.add(project1);

		assetHierarchy = new ArrayList<>();
		assetHierarchy.add(data);

		master_groupId = "29ff370d";
		token = "eyJhbGciOiJSUzI1NiIsIn";
		headers = new HttpHeaders();
		headers.set("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		groupRepresentation = new GroupRepresentation();
		groupRepresentation.setName("group1");
		groupRepresentation.setAttributes(new HashMap<>());

		allDomainsResponse = "{\"id\":\"29ff370d\",\"name\":\"TEST_Domain_master\",\"path\":\"/TEST_Domain_master\",\"attributes\":{\"source\":[\"local\"]},\"realmRoles\":[],\"clientRoles\":{},\"subGroups\":[{\"id\":\"87e8e730\",\"name\":\"samenamecheck123\",\"path\":\"/TEST_Domain_master/samenamecheck123\",\"attributes\":{\"train_vids\":[\"TR_TEST\"],\"lineup_vids\":[\"LN_TEST\"],\"plant_vids\":[\"PL_TEST\"],\"project_vids\":[\"PR_TEST\"],\"machine_vids\":[\"MC_TEST\"],\"source\":[\"local\"]},\"realmRoles\":[],\"clientRoles\":{},\"subGroups\":[]}]}";

		displayNameMap = new HashMap<>();
		displayNameMap.put(TestConstants.PR_TEST1, TestConstants.TEST1);
		displayNameMap.put(TestConstants.PL_TEST1, TestConstants.TEST1);
		displayNameMap.put(TestConstants.TR_TEST1, TestConstants.TEST1);
		displayNameMap.put(TestConstants.LN_TEST1, TestConstants.TEST1);
		displayNameMap.put(TestConstants.MC_TEST1, TestConstants.TEST1);

		projectsList = new ArrayList<>();
		projectsList.add(TestConstants.PR_TEST1);
		plantsList = new ArrayList<>();
		plantsList.add(TestConstants.PL_TEST1);
		trainsList = new ArrayList<>();
		trainsList.add(TestConstants.TR_TEST1);
		lineupsList = new ArrayList<>();
		lineupsList.add(TestConstants.LN_TEST1);
		machinesList = new ArrayList<>();
		machinesList.add(TestConstants.MC_TEST1);

		attributes = new HashMap<>();
		attributes.put(UMSConstants.PROJECTS_ATTRIBUTE, projectsList);
		attributes.put(UMSConstants.PLANTS_ATTRIBUTE, plantsList);
		attributes.put(UMSConstants.TRAINS_ATTRIBUTE, trainsList);
		attributes.put(UMSConstants.LINEUPS_ATTRIBUTE, lineupsList);
		attributes.put(UMSConstants.MACHINES_ATTRIBUTE, machinesList);
		realmRoles = new ArrayList<>();
		realmRoles.add("Role_Test1");
		groupRepresentation.setRealmRoles(realmRoles);
		groupRepresentation.setAttributes(attributes);
		ReflectionTestUtils.setField(domainService, "getAllDomainsUri", "/groups/<group_id>");
		ReflectionTestUtils.setField(domainService, "subGroupURI", "/groups/<group_id>/children");
		ReflectionTestUtils.setField(domainService, "groupURI", "/groups/");
	}

	@Test
	@DisplayName("GetAllDomains")
	void testGetAllDomains() {
		when(restClientWrapperService.getResponseFromUrl(any(CustomHttpServletRequestWrapper.class),anyString())).thenReturn((ResponseEntity.of(Optional.of(allDomainsResponse))));
			List<DomainResponseDTO> domainResponseDTOList=domainService.getAllDomains(mockHttpServletRequest);
			assertNotNull(domainResponseDTOList);
			assertEquals("87e8e730", domainResponseDTOList.get(0).getId());
			assertEquals("samenamecheck123", domainResponseDTOList.get(0).getName());
			assertEquals("train_vids", domainResponseDTOList.get(0).getAttributes().get(0).getKey());
			assertEquals(true, domainResponseDTOList.get(0).getEditable());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("CreateDomain - Attributes As Non-Null")
	void testCreateDomain() throws Exception {
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		when(restTemplate.exchange(anyString(),any(HttpMethod.class),any(HttpEntity.class),any(ParameterizedTypeReference.class)))
		.thenReturn((ResponseEntity.of(Optional.of("Domain Created Successfully"))));
		ResponseEntity<String> createDomainResponse=domainService.createDomain(groupRepresentation,mockHttpServletRequest);
			assertNotNull(createDomainResponse);
			assertEquals("Domain Created Successfully", createDomainResponse.getBody());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("CreateDomain - Attributes As Null")
	void testCreateDomainAttributesNull() throws Exception {
		groupRepresentation = new GroupRepresentation();
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(ParameterizedTypeReference.class)))
				.thenReturn((ResponseEntity.of(Optional.of("Domain Created Successfully"))));
		ResponseEntity<String> createDomainResponse = domainService.createDomain(groupRepresentation,
				mockHttpServletRequest);
		assertNotNull(createDomainResponse);
		assertEquals("Domain Created Successfully", createDomainResponse.getBody());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("UpdateDomain - Attributes As Null")
	void testUpdateDomain() throws Exception {
		groupRepresentation = new GroupRepresentation();
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(ParameterizedTypeReference.class)))
				.thenReturn((ResponseEntity.of(Optional.of("Domains updated Successfully"))));
		ResponseEntity<String> createDomainResponse = domainService.updateDomain(groupRepresentation,
				mockHttpServletRequest);
		assertNotNull(createDomainResponse);
		assertEquals("Domains updated Successfully", createDomainResponse.getBody());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("UpdateDomain - Attributes As Non-Null")
	void testUpdateDomainAttributesAsNull() throws Exception {
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(ParameterizedTypeReference.class)))
				.thenReturn((ResponseEntity.of(Optional.of("Domains updated Successfully"))));
		ResponseEntity<String> createDomainResponse = domainService.updateDomain(groupRepresentation,
				mockHttpServletRequest);
		assertNotNull(createDomainResponse);
		assertEquals("Domains updated Successfully", createDomainResponse.getBody());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("GetDomain")
	void testGetDomain() throws Exception {
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(ParameterizedTypeReference.class)))
				.thenReturn((ResponseEntity.of(Optional.of(groupRepresentation))));
		ResponseEntity<GroupRepresentation> getDomainResponse = domainService.getDomain("87e8e730",
				mockHttpServletRequest);
		assertNotNull(getDomainResponse);
		assertEquals(TestConstants.PR_TEST1, getDomainResponse.getBody().getAttributes().get(UMSConstants.PROJECTS_ATTRIBUTE).get(0));
		assertEquals("Role_Test1", getDomainResponse.getBody().getRealmRoles().get(0));
	}

	@Test
	@DisplayName("deleteDomain - Group Name Equals All Domain")
	void testDeleteDomain() throws Exception {
		GroupResponseDTO groupResponseDTO = new GroupResponseDTO(master_groupId, UMSConstants.ALL_DOMAIN);
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		when(groupService.getGroupDetails(any(MockHttpServletRequest.class), anyString())).thenReturn(groupResponseDTO);
		assertThrows(DeletionNotPermissableException.class, () -> {
			domainService.deleteDomain("87e8e730", mockHttpServletRequest);
		});
	}

	@Test
	@DisplayName("DeleteDomain - Users are not Empty In Group")
	void testDeleteDomainUsersNotEmpty() throws Exception {
		UserDetailsResponseDTO users = new UserDetailsResponseDTO();
		users.setName("TESTUSER");
		List<UserDetailsResponseDTO> usersList = new ArrayList<>();
		usersList.add(users);
		GroupResponseDTO groupResponseDTO = new GroupResponseDTO(master_groupId, "TESTGROUP1");
		groupResponseDTO.setUsers(usersList);
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		when(groupService.getGroupDetails(any(MockHttpServletRequest.class), anyString())).thenReturn(groupResponseDTO);
		assertThrows(DeletionNotPermissableException.class, () -> {
			domainService.deleteDomain("87e8e730", mockHttpServletRequest);
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("DeleteDomain - No Exception")
	void testDeleteDomainWithoutException() throws Exception {
		GroupResponseDTO groupResponseDTO = new GroupResponseDTO(master_groupId, "TESTGROUP1");
		groupResponseDTO.setUsers(new ArrayList<>());
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		when(groupService.getGroupDetails(any(MockHttpServletRequest.class), anyString())).thenReturn(groupResponseDTO);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(ParameterizedTypeReference.class)))
				.thenReturn((ResponseEntity.of(Optional.of("Domains Deleted SuccessFully"))));
		ResponseEntity<String> deleteDomain = domainService.deleteDomain("87e8e730", mockHttpServletRequest);
		assertEquals("Domains Deleted SuccessFully", deleteDomain.getBody());
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("ViewDomain - With Group Not Null")
	void testViewDomain() throws Exception {
		groupRepresentation.setAttributes(attributes);
		when(jwtUtil.generateAdminToken()).thenReturn(token);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
				any(ParameterizedTypeReference.class)))
				.thenReturn((ResponseEntity.of(Optional.of(groupRepresentation))));
		when(fetchAssetHierarchyService.callAssetHierarchyAPIv2()).thenReturn(new ObjectMapper().readValue(
				"[{\"level\":\"projects\",\"data\":[{\"id\":\"TEST1\",\"vid\":\"PR_TEST1\",\"fields\":{},\"children\":{\"level\":\"plants\",\"data\":[{\"id\":\"TEST1\",\"vid\":\"PL_TEST1\",\"fields\":{\"equipmentCode\":\"ab\",\"technologyCodeOg\":\"bc\"},\"enabledServices\":[\"CBM2567\"]}]}}]}]",
				List.class));
		when(genericAssetHierarchyFilterService.getDisplayNameMap(anyList())).thenReturn(displayNameMap);
		Map<String, List<DomainAttribute>> viewResponseDTO = domainService.viewDomain("87e8e730",
				mockHttpServletRequest);
		assertNotNull(viewResponseDTO);
		assertEquals(TestConstants.PR_TEST1, viewResponseDTO.get(JSONUtilConstants.LEVEL_PROJECTS).get(0).getVid());
		assertEquals(TestConstants.TEST1,
				viewResponseDTO.get(JSONUtilConstants.LEVEL_PROJECTS).get(0).getDisplayName());
		assertEquals(TestConstants.PL_TEST1, viewResponseDTO.get(JSONUtilConstants.LEVEL_PLANTS).get(0).getVid());
		assertEquals(TestConstants.MC_TEST1, viewResponseDTO.get(JSONUtilConstants.LEVEL_MACHINES).get(0).getVid());
		assertEquals(TestConstants.LN_TEST1, viewResponseDTO.get(JSONUtilConstants.LEVEL_LINEUPS).get(0).getVid());
		assertEquals(TestConstants.TR_TEST1, viewResponseDTO.get(JSONUtilConstants.LEVEL_TRAINS).get(0).getVid());
	}

}
