package com.bh.cp.proxy.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.util.StringUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.ForbiddenException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityFilter extends OncePerRequestFilter {

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, ForbiddenException {
		request.setAttribute(ProxyConstants.PERF_AUDIT_THREAD_ID, UUID.randomUUID().toString());
		response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		MDC.put(ProxyConstants.PERF_AUDIT_THREAD_ID, StringUtil.getUUID());
		filterChain.doFilter(request, response);
	}

}
