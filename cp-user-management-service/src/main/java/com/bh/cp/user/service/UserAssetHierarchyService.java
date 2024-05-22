package com.bh.cp.user.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.bh.cp.user.dto.request.AssetHierarchyRequestDTO;
import com.bh.cp.user.dto.request.WidgetAccessRequestDTO;
import com.bh.cp.user.dto.request.WidgetApplicableRequestDTO;
import com.bh.cp.user.dto.response.AssetResponseDTO;
import com.bh.cp.user.dto.response.WidgetAccessResponseDTO;
import com.bh.cp.user.dto.response.WidgetApplicableResponseDTO;
import com.bh.cp.user.exception.AttributeNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

public interface UserAssetHierarchyService {

	public WidgetAccessResponseDTO checkWidgetAccess(HttpServletRequest httpServletRequest,
			WidgetAccessRequestDTO requestDto)
			throws AttributeNotFoundException, NotFoundException, InterruptedException, ExecutionException, IOException;

	public Map<String, Boolean> checkWidgetKeycloakAccess(HttpServletRequest httpServletRequest, Integer widgetId)
			throws JsonProcessingException, AttributeNotFoundException, InterruptedException, ExecutionException;

	public List<Map<String, Object>> getUserAssetHierarchyChildren(HttpServletRequest httpServletRequest,
			AssetHierarchyRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException;

	public Map<String, String> getUserAssetHierarchyField(HttpServletRequest httpServletRequest,
			AssetHierarchyRequestDTO requestDto, boolean fetchFromParent)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException;

	public AssetResponseDTO getUserAssetHierarchyAssets(HttpServletRequest httpServletRequest,
			AssetHierarchyRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException;

	public WidgetApplicableResponseDTO getUserApplicableMachinesForWidget(HttpServletRequest httpServletRequest,
			WidgetApplicableRequestDTO requestDto)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException;

	public Map<String,Boolean> checkWidgetAdvanceServicesAccess(HttpServletRequest httpServletRequest, WidgetAccessRequestDTO accessRequestDTO)
			throws AttributeNotFoundException, InterruptedException, ExecutionException, IOException;

}
