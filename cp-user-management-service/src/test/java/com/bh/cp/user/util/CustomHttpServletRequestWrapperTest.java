package com.bh.cp.user.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import com.bh.cp.user.constants.SecurityUtilConstants;

class CustomHttpServletRequestWrapperTest {

	private MockHttpServletRequest mockHttpServletRequest;

	@Mock
	private JwtUtil jwtUtil;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Test_Header", "Test_Header_Value");
		mockHttpServletRequest.addHeader(SecurityUtilConstants.KEY_AUTHORIZATION, "Test_Auth");
	}

	@Test
	@DisplayName("Test getHeader1 -- Static Token")
	void testGetHeader_Positive1() {
		CustomHttpServletRequestWrapper customHttpServletRequestWrapper = new CustomHttpServletRequestWrapper(
				mockHttpServletRequest, "Static Token");
		assertEquals("Static Token",
				customHttpServletRequestWrapper.getHeader(SecurityUtilConstants.KEY_AUTHORIZATION));
	}

	@Test
	@DisplayName("Test getHeader2 -- Dynamic Token")
	void testGetHeader_Positive2() {
		when(jwtUtil.generateAdminToken()).thenReturn("Dynamic Token");
		CustomHttpServletRequestWrapper customHttpServletRequestWrapper = new CustomHttpServletRequestWrapper(
				mockHttpServletRequest, jwtUtil);
		assertEquals("Bearer Dynamic Token",
				customHttpServletRequestWrapper.getHeader(SecurityUtilConstants.KEY_AUTHORIZATION));
	}

	@Test
	@DisplayName("Test getHeader3 -- Different Header")
	void testGetHeader_Negative1() {
		CustomHttpServletRequestWrapper customHttpServletRequestWrapper = new CustomHttpServletRequestWrapper(
				mockHttpServletRequest, "Dynamic Token");
		assertEquals("Test_Header_Value", customHttpServletRequestWrapper.getHeader("Test_Header"));
	}

}
