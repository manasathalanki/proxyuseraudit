package com.bh.cp.dashboard.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bh.cp.dashboard.constants.DashboardConstants;

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
		request.setAttribute(DashboardConstants.PERF_AUDIT_THREAD_ID, UUID.randomUUID().toString());
		response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		filterChain.doFilter(request, response);
	}

}
