package com.bh.cp.user.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.request.LoginRequestDTO;
import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.dto.response.LoginResponseDTO;
import com.bh.cp.user.dto.response.PrivilegesResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.dto.response.UserResponseDTO;
import com.bh.cp.user.exception.AttributeNotFoundException;
import com.bh.cp.user.service.AccessService;
import com.bh.cp.user.service.FetchAssetHierarchyService;
import com.bh.cp.user.service.GenericAssetHierarchyFilterService;
import com.bh.cp.user.service.UserService;
import com.bh.cp.user.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AccessServiceImpl implements AccessService {

	private static final Logger logger = LoggerFactory.getLogger(AccessServiceImpl.class);

	private JwtUtil jwtUtil;

	private UserService userService;

	private GenericAssetHierarchyFilterService genericAssetHierarchyFilterService;

	private FetchAssetHierarchyService fetchAssetHierarchyService;

	public AccessServiceImpl(@Autowired JwtUtil jwtUtil, @Autowired UserService userService,
			@Autowired GenericAssetHierarchyFilterService genericAssetHierarchyFilterService,
			@Autowired FetchAssetHierarchyService fetchAssetHierarchyService) {
		super();
		this.jwtUtil = jwtUtil;
		this.userService = userService;
		this.genericAssetHierarchyFilterService = genericAssetHierarchyFilterService;
		this.fetchAssetHierarchyService = fetchAssetHierarchyService;
	}

	@Override
	public LoginResponseDTO generateAccessToken(LoginRequestDTO loginCredential) {
		return jwtUtil.generateAccessToken(loginCredential);
	}

	@Override
	public UserDetailsResponseDTO getCurrentUserCombinedDetails(HttpServletRequest httpServletRequest)
			throws AttributeNotFoundException, JsonProcessingException, InterruptedException, ExecutionException {
		return userService.getCurrentUserCombinedDetailsCached(httpServletRequest);
	}

	@Override
	@Cacheable(value = "userassethierarchy", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public List<Map<String, Object>> getCurrentUserFilteredHierarchy(HttpServletRequest httpServletRequest)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException {

		List<DomainResponseDTO> domainsList = new ArrayList<>();
		UserDetailsResponseDTO userResponseDTO = getCurrentUserCombinedDetails(httpServletRequest);

		if (userResponseDTO.getDomains() != null && !userResponseDTO.getDomains().isEmpty()) {
			domainsList.addAll(userResponseDTO.getDomains());
		}

		if (domainsList.isEmpty()) {
			throw new AccessDeniedException(
					"No assets are assigned to your user. Please get in touch with Administrator or your iCenter reference contact.");
		}

		logger.info("Checking whether user has All Domain...");
		if (domainsList.stream().anyMatch(domain -> domain.getName().equalsIgnoreCase(UMSConstants.ALL_DOMAIN))) {
			logger.info("User has All Domain Access...");
			List<Map<String, Object>> fullHierarchy = fetchAssetHierarchyService.callAssetHierarchyAPIv2();
			genericAssetHierarchyFilterService.setAdditionalFieldsToHierarchy(fullHierarchy, false);
			return fullHierarchy;
		}

		logger.info("Fetching Domain Information from User...");
		Set<String> assetVids = new HashSet<>();
		for (DomainResponseDTO domain : domainsList) {
			domain.getAttributes().forEach(attribute -> {
				if (attribute.getKey().contains(UMSConstants.ASSET_ATTRIBUTE_SUFFIX)) {
					assetVids.addAll(attribute.getValue());
				}
			});
		}

		if (assetVids.isEmpty()) {
			throw new AccessDeniedException(
					"No assets are assigned to your user. Please get in touch with Administrator or your iCenter reference contact.");
		}

		return genericAssetHierarchyFilterService.getFilteredHierarchy(new ArrayList<>(assetVids), false);
	}

	@Override
	@Cacheable(value = "userprivileges", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public List<String> getCurrentUserPrivileges(HttpServletRequest httpServletRequest)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException {
		UserDetailsResponseDTO userResponseDTO = getCurrentUserCombinedDetails(httpServletRequest);
		return userResponseDTO.getPrivileges().stream().map(PrivilegesResponseDTO::getName).toList();
	}

	@Override
	@Cacheable(value = "userdetails", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public UserResponseDTO getUserDetails(HttpServletRequest httpServletRequest)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException {
		UserDetailsResponseDTO userResponseDTO = getCurrentUserCombinedDetails(httpServletRequest);
		UserResponseDTO user = new UserResponseDTO();
		user.setEmail(userResponseDTO.getEmail());
		user.setId(userResponseDTO.getId());
		user.setName(userResponseDTO.getUserName());
		user.setFirstName(userResponseDTO.getName());
		user.setLastName(userResponseDTO.getSurName());
		user.setPrivileges(userResponseDTO.getPrivileges().stream().map(PrivilegesResponseDTO::getName).toList());
		user.setTitle(userResponseDTO.getTitle());
		return user;
	}

}
