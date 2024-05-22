package com.bh.cp.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.body.AttributeBody;
import com.bh.cp.user.dto.request.LoginRequestDTO;
import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.dto.response.LoginResponseDTO;
import com.bh.cp.user.dto.response.PrivilegesResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.dto.response.UserResponseDTO;
import com.bh.cp.user.service.FetchAssetHierarchyService;
import com.bh.cp.user.service.GenericAssetHierarchyFilterService;
import com.bh.cp.user.service.UserService;
import com.bh.cp.user.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class AccessServiceImplTest {

	@InjectMocks
	private AccessServiceImpl accessServiceImpl;

	@Mock
	private UserService userService;

	@Mock
	private FetchAssetHierarchyService fetchAssetHierarchyService;

	@Mock
	private GenericAssetHierarchyFilterService genericAssetHierarchyFilterService;

	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private JwtUtil jwtUtil;

	private LoginRequestDTO loginRequestDTO;

	private LoginResponseDTO loginResponseDTO;

	private UserResponseDTO userResponseDTO;

	private UserDetailsResponseDTO userDetailsResponseDTO;

	private DomainResponseDTO domainResponseDTO;

	private PrivilegesResponseDTO privilegesResponseDTO;

	private AttributeBody attributeBody;

	private List<DomainResponseDTO> domains;

	private Set<PrivilegesResponseDTO> privilegesResponseDTOSet;

	private List<String> vidsList;

	private List<AttributeBody> attributes;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		loginRequestDTO = new LoginRequestDTO();
		loginRequestDTO.setPassword("test@password");
		loginRequestDTO.setUsername("testUser");
		loginResponseDTO = new LoginResponseDTO();
		loginResponseDTO.setToken("token");
		loginResponseDTO.setUsername("testUser");
		userResponseDTO = new UserResponseDTO();
		userResponseDTO.setEmail("test@gmail.com");
		userResponseDTO.setFirstName("test");
		userResponseDTO.setLastName("test");
		userResponseDTO.setName("test");
		userResponseDTO.setTitle("Internal");
		userResponseDTO.setPrivileges(new ArrayList<>());
		privilegesResponseDTO = new PrivilegesResponseDTO();
		privilegesResponseDTO.setId("1");
		privilegesResponseDTO.setName("privileage1");
		privilegesResponseDTOSet = new HashSet<>();
		privilegesResponseDTOSet.add(privilegesResponseDTO);
		vidsList = new ArrayList<>();
		vidsList.add("PR_TEST");
		attributeBody = new AttributeBody();
		attributeBody.setKey(UMSConstants.ASSET_ATTRIBUTE_SUFFIX);
		attributeBody.setValue(vidsList);
		attributes = new ArrayList<>();
		attributes.add(attributeBody);
		domains = new ArrayList<>();
		domains.add(new DomainResponseDTO("test-domain-id", "test-domain"));
		domains.add(new DomainResponseDTO("test-domain-id2", UMSConstants.ALL_DOMAIN));
		userDetailsResponseDTO = new UserDetailsResponseDTO();
		userDetailsResponseDTO.setAttributes(new ArrayList<>());
		userDetailsResponseDTO.setDomains(domains);
		userDetailsResponseDTO.setPrivileges(privilegesResponseDTOSet);
	}

	@Test
	@DisplayName("Login - generating token")
	void testLogin() throws JsonProcessingException, Exception {
		   when(jwtUtil.generateAccessToken(any(LoginRequestDTO.class))).thenReturn(loginResponseDTO);
		   LoginResponseDTO expected=accessServiceImpl.generateAccessToken(loginRequestDTO);
		   assertNotNull(expected);
		  assertEquals(loginResponseDTO.getUsername(), expected.getUsername());
		  assertEquals(loginResponseDTO.getToken(), expected.getToken());
	}

	@Test
	@DisplayName("RetrieveCurrentUserDetail - Current User Details")
	void testRetrieveCurrentUserDetail() throws JsonProcessingException, Exception {
		  when(userService.getCurrentUserCombinedDetailsCached(any(HttpServletRequest.class))).thenReturn(userDetailsResponseDTO);
		  UserDetailsResponseDTO expected=accessServiceImpl.getCurrentUserCombinedDetails(httpServletRequest);
		  assertNotNull(expected);
		  assertEquals(userDetailsResponseDTO.getAttributes(), expected.getAttributes());
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("RetrieveCurrentUserHierarchy - Getting User's Asset Hierarchy")
	void testRetrieveCurrentUserHierarchy() throws JsonProcessingException, Exception {
		  when(userService.getCurrentUserCombinedDetailsCached(any(HttpServletRequest.class))).thenReturn(userDetailsResponseDTO);
		  when(fetchAssetHierarchyService.callAssetHierarchyAPIv2()).thenReturn(new ObjectMapper().readValue("[{\"level\": \"projects\"}]",List.class));
		  List<Map<String,Object>> expected = accessServiceImpl.getCurrentUserFilteredHierarchy(httpServletRequest);
		  assertNotNull(expected);
	}

	@Test
	@DisplayName("RetrieveCurrentUserHierarchy - With All Domains Not Found")
	void testRetrieveCurrentUserHierarchyAllDomains() throws JsonProcessingException, Exception {
		List<Map<String, Object>> userAssetHierarchy = new ArrayList<>();
		Map<String, Object> useruserAssetHierarchyMap = new HashMap<>();
		useruserAssetHierarchyMap.put("level", "projects");
		useruserAssetHierarchyMap.put("data", new ArrayList<>());
		userAssetHierarchy.add(useruserAssetHierarchyMap);
		domains = new ArrayList<>();
		domainResponseDTO = new DomainResponseDTO("test-domain-id", "test-domain", attributes, true);
		domains.add(domainResponseDTO);
		userDetailsResponseDTO = new UserDetailsResponseDTO();
		userDetailsResponseDTO.setDomains(domains);
		when(userService.getCurrentUserCombinedDetailsCached(any(HttpServletRequest.class)))
				.thenReturn(userDetailsResponseDTO);
		when(genericAssetHierarchyFilterService.getFilteredHierarchy(anyList(), anyBoolean()))
				.thenReturn(userAssetHierarchy);
		List<Map<String, Object>> expected = accessServiceImpl.getCurrentUserFilteredHierarchy(httpServletRequest);
		assertNotNull(expected);
	}

	@Test
	@DisplayName("RetrieveCurrentUserPrivileges - Getting Current User Privileges")
	void testRetrieveCurrentUserPrivileges() throws JsonProcessingException, Exception {
		   when(userService.getCurrentUserCombinedDetailsCached(any(HttpServletRequest.class))).thenReturn(userDetailsResponseDTO);
		  List<String> expected=accessServiceImpl.getCurrentUserPrivileges(httpServletRequest);
		   assertNotNull(expected);
	}

	@Test
	@DisplayName("GetUserDetails - User Details")
	void testGetUserDetails() throws JsonProcessingException, Exception {
		   when(userService.getCurrentUserCombinedDetailsCached(any(HttpServletRequest.class))).thenReturn(userDetailsResponseDTO);
		   UserResponseDTO expected = accessServiceImpl.getUserDetails(httpServletRequest);
		   assertNotNull(expected);
		   assertEquals(userDetailsResponseDTO.getName(), expected.getEmail());
	}

}
