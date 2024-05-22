package com.bh.cp.dashboard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.dashboard.service.CacheService;
import com.bh.cp.dashboard.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("v1/cache")
@Tag(name = "Cache Controller")
public class CacheController {

	private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

	private CacheService cacheService;

	public CacheController(@Autowired CacheService cacheService) {
		super();
		this.cacheService = cacheService;
	}

	@GetMapping(value = "/clear")
	@Operation(summary = "Clear Application cache", description = "Clears all caches of \"userassethierarchy\", \"userprivileges\", \"widgetsubscription\"")
	@SecurityRequirement(name = "Keycloak Token")
	@CacheEvict(cacheNames = { "userassethierarchy", "userprivileges", "widgetsubscription" }, allEntries = true)
	public String clearCache() {
		logger.warn("Evict all cache entries...");
		return "Cache has been cleared successfully";
	}

	@GetMapping(value = "/clear/mycache")
	@Operation(summary = "Clear current Logged in User related caches", description = "Clears all caches related to Logged in User's Token")
	@SecurityRequirement(name = "Keycloak Token")
	public String clearCurrentUserCache(HttpServletRequest httpServletRequest) {
		SecurityUtil.sanitizeLogging(logger, Level.INFO, "Evicted Logged in User -{} related cache...");
		cacheService.clearCacheWithPattern("*", "*" + httpServletRequest.getHeader("Authorization") + "*");
		return "Cache related to Logged in User has been cleared successfully";
	}

}