package com.bh.cp.user.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.bh.cp.user.filter.ExpectCTFilter;

class FilterConfigTest {

	@Test
	@DisplayName("Test filterRegistrationBean1 -- Validate Created Bean")
	void testFilterRegistrationBean_Positive1() {
		FilterRegistrationBean<ExpectCTFilter> output = new FilterConfig().expectCTFilter();
		assertEquals(Set.of("/*"), output.getUrlPatterns());
		assertEquals(ExpectCTFilter.class, output.getFilter().getClass());
	}

}
