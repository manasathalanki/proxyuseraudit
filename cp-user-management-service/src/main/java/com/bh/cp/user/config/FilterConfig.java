package com.bh.cp.user.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bh.cp.user.filter.ExpectCTFilter;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<ExpectCTFilter> expectCTFilter() {
		FilterRegistrationBean<ExpectCTFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new ExpectCTFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}
