package com.bh.cp.user.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.bh.cp.user.dto.request.CreateGroupRequestDTO;
import com.bh.cp.user.dto.request.DeleteGroupRequestDTO;
import com.bh.cp.user.dto.request.EditGroupRequestDTO;
import com.bh.cp.user.dto.response.GroupResponseDTO;
import com.bh.cp.user.dto.response.SelectedResponseDTO;
import com.bh.cp.user.exception.DeletionNotPermissableException;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

public interface GroupService {

	public List<GroupResponseDTO> getAllGroups(HttpServletRequest httpServletRequest);

	public GroupResponseDTO getGroupDetails(HttpServletRequest httpServletRequest, String groupId)
			throws InterruptedException, ExecutionException;

	public String createGroup(HttpServletRequest httpServletRequest, CreateGroupRequestDTO requestDto)
			throws JsonProcessingException;

	public String editGroup(HttpServletRequest httpServletRequest, EditGroupRequestDTO requestDto)
			throws JsonProcessingException, InterruptedException, ExecutionException;

	public List<SelectedResponseDTO> getUserDetailsFromGroup(HttpServletRequest httpServletRequest, String groupId)
			throws JsonProcessingException, InterruptedException, ExecutionException;

	public List<SelectedResponseDTO> getRoleDetailsFromGroup(HttpServletRequest httpServletRequest, String groupId)
			throws JsonProcessingException, InterruptedException, ExecutionException;

	public List<SelectedResponseDTO> getDomainDetailsFromGroup(HttpServletRequest httpServletRequest, String groupId)
			throws JsonProcessingException, InterruptedException, ExecutionException;

	public String deleteGroup(HttpServletRequest httpServletRequest, DeleteGroupRequestDTO requestDto)
			throws JsonProcessingException, DeletionNotPermissableException, InterruptedException, ExecutionException;
}
