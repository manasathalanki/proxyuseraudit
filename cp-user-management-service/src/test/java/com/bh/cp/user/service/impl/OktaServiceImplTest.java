package com.bh.cp.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.user.dto.request.SearchRequestDTO;
import com.bh.cp.user.dto.response.OktaUserDetailsResponseDTO;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.util.CustomHttpServletRequestWrapper;
import com.bh.cp.user.util.JwtUtil;

class OktaServiceImplTest {

	@InjectMocks
	private OktaServiceImpl oktaServiceImpl;

	@Mock
	private RestClientWrapperService restClientWrapperService;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	private SearchRequestDTO searchRequestDto;
	private List<String> emailsList;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsIn");
		emailsList = new ArrayList<>();
		emailsList.add("test.user@bakerhughes.com");
		searchRequestDto = new SearchRequestDTO();
		searchRequestDto.setField("profile.email");
		searchRequestDto.setValues(emailsList);
		ReflectionTestUtils.setField(oktaServiceImpl, "oktaUsersUri", "/api/v1/users");
		ReflectionTestUtils.setField(oktaServiceImpl, "oktaCredValue", "test_secret");
		ReflectionTestUtils.setField(oktaServiceImpl, "oktaSearchQueryParam", "search");
		ReflectionTestUtils.setField(oktaServiceImpl, "oktaSearchQueryParamEquals", "eq");
		ReflectionTestUtils.setField(oktaServiceImpl, "oktaSearchQueryParamOr", "or");
		ReflectionTestUtils.setField(oktaServiceImpl, "keycloakUsersUri", "/users/");
		ReflectionTestUtils.setField(oktaServiceImpl, "keycloakSearchQueryParam", "email");
	}

	@Test
	@DisplayName("GetUsersByField - Searching the Okta Users")
	void testGetUsersByField() {
		when(restClientWrapperService.getResponseFromUrl(any(CustomHttpServletRequestWrapper.class),anyString()))
		.thenReturn(ResponseEntity.of(Optional.of("[{\"id\":\"TESTID1\",\"status\":\"ACTIVE\",\"profile\":{\"email\":\"test.user@bakerhughes.com\",\"lastName\":\"USER\",\"firstName\":\"TEST\",\"login\":\"test.user@bakerhughes.com\",\"samAccountName\":\"test\",\"uid\":\"test\"}}]")))
		.thenReturn(ResponseEntity.of(Optional.of("[{\"id\":\"TESTID1\",\"username\":\"TESTUSER\",\"enabled\":true,\"firstName\":\"TEST\",\"lastName\":\"USER\",\"email\":\"test.user@bakerhughes.com\",\"attributes\":{\"title\":[\"EXTERNAL\"]}}]")));
		List<OktaUserDetailsResponseDTO> oktaUserDetailsResponseList=	oktaServiceImpl.getUsersByField(mockHttpServletRequest, searchRequestDto);
		assertNotNull(oktaUserDetailsResponseList);
		assertEquals("TESTID1", oktaUserDetailsResponseList.get(0).getId());
		assertEquals("ACTIVE", oktaUserDetailsResponseList.get(0).getStatus());
		assertEquals("test.user@bakerhughes.com", oktaUserDetailsResponseList.get(0).getAuth().getEmail());
	}
}
