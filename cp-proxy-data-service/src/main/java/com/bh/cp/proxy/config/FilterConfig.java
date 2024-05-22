package com.bh.cp.proxy.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bh.cp.proxy.filter.ExpectCTFilter;

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
