package com.bh.cp.audit.trail.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;

class SecurityFilterTest {

	@InjectMocks
	private SecurityFilter filter;

	@Mock
	MockHttpServletRequest httpServletRequest;

	@Mock
	MockHttpServletResponse httpServletResponse;

	@Mock
	MockFilterChain filterChain;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testDoFilterInternal() throws ServletException, IOException {
		filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
		assertNotNull(httpServletRequest);
	}

}
