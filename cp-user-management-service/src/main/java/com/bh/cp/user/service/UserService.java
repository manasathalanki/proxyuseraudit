package com.bh.cp.user.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.bh.cp.user.dto.request.CreateUserRequestDTO;
import com.bh.cp.user.dto.request.DeleteUserRequestDTO;
import com.bh.cp.user.dto.request.EditUserRequestDTO;
import com.bh.cp.user.dto.response.SelectedResponseDTO;
import com.bh.cp.user.dto.response.UserDetailsResponseDTO;
import com.bh.cp.user.exception.AttributeNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

	public List<UserDetailsResponseDTO> getAllUsers(HttpServletRequest httpServletRequest);

	public String createUser(HttpServletRequest httpServletRequest, CreateUserRequestDTO requestDto)
			throws JsonProcessingException;

	public String editUsers(HttpServletRequest httpServletRequest, EditUserRequestDTO requestDto)
			throws JsonProcessingException, AttributeNotFoundException;

	public UserDetailsResponseDTO getUsersCombinedDetails(HttpServletRequest httpServletRequest, String userId)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException;

	public UserDetailsResponseDTO getCurrentUserCombinedDetailsCached(HttpServletRequest httpServletRequest)
			throws AttributeNotFoundException, JsonProcessingException, InterruptedException, ExecutionException;

	public String enableDisableUser(HttpServletRequest httpServletRequest, DeleteUserRequestDTO requestDto)
			throws JsonProcessingException;

	public UserDetailsResponseDTO getDetailsFromUser(HttpServletRequest httpServletRequest, String userId)
			throws AttributeNotFoundException;

	public List<SelectedResponseDTO> getRoleDetailsFromUser(HttpServletRequest httpServletRequest, String userId)
			throws JsonProcessingException;

	public List<SelectedResponseDTO> getDomainDetailsFromUser(HttpServletRequest httpServletRequest, String userId)
			throws JsonProcessingException;

	public List<SelectedResponseDTO> getGroupDetailsFromUser(HttpServletRequest httpServletRequest, String userId)
			throws JsonProcessingException;

}
