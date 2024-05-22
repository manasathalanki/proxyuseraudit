package com.bh.cp.user.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.bh.cp.user.dto.request.LoginRequestDTO;
import com.bh.cp.user.dto.response.LoginResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.dto.response.UserResponseDTO;
import com.bh.cp.user.exception.AttributeNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

public interface AccessService {

	public LoginResponseDTO generateAccessToken(LoginRequestDTO loginCredential);

	public UserDetailsResponseDTO getCurrentUserCombinedDetails(HttpServletRequest httpServletRequest)
			throws AttributeNotFoundException, JsonProcessingException, InterruptedException, ExecutionException;

	public List<Map<String, Object>> getCurrentUserFilteredHierarchy(HttpServletRequest httpServletRequest)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException;

	public List<String> getCurrentUserPrivileges(HttpServletRequest httpServletRequest)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException;

	public UserResponseDTO getUserDetails(HttpServletRequest httpServletRequest)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException;

}
