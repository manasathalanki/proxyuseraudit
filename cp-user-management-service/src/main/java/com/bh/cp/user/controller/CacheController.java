package com.bh.cp.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.user.service.CacheService;
import com.bh.cp.user.util.SecurityUtil;

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
	@Operation(summary = "Clear Application cache", description = "Clears all caches of \"userassethierarchy\", \"widget\", \"userdetails\", \"usercombineddetails\", \"userprivileges\",\r\n"
			+ "\"privileges\", \"domainsmap\"")
	@SecurityRequirement(name = "Keycloak Token")
	@CacheEvict(cacheNames = { "userassethierarchy", "widget", "userdetails", "usercombineddetails", "userprivileges",
			"privileges", "domainsmap" }, allEntries = true)
	public String clearCache() {
		logger.warn("Evict all cache entries...");
		return "Cache has been cleared successfully";
	}

	@GetMapping(value = "/clear/assethierarchy")
	@Operation(summary = "Clear Asset hierarchy cached from Mercurius API", description = "Clears cache of \"fullassethierarchy\"")
	@SecurityRequirement(name = "Keycloak Token")
	@CacheEvict(cacheNames = { "fullassethierarchy" }, allEntries = true)
	public String clearFullassethierarchyCache() {
		logger.warn("Evicted Asset hierarchy cached from Mercurius API...");
		return "Cached Asset hierarchy has been cleared successfully";
	}

	@GetMapping(value = "/clear/admintoken")
	@Operation(summary = "Clear Admin token cache", description = "Clears cache of \"admintoken\"")
	@SecurityRequirement(name = "Keycloak Token")
	@CacheEvict(cacheNames = { "admintoken" }, allEntries = true)
	public String clearAdminTokenCache() {
		logger.warn("Evicted Cached Admin Token...");
		return "Cached Admin Token has been cleared successfully";
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