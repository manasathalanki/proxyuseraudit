package com.bh.cp.dashboard.service;

import java.util.List;

import com.bh.cp.dashboard.dto.request.AddAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.ChangeAssetNameRequestDTO;
import com.bh.cp.dashboard.dto.request.DeleteAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.GetAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.UsersDefaultAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.UsersFavoriteAssetsRequestDTO;
import com.bh.cp.dashboard.dto.response.AddAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.ChangeAssetNameResponseDTO;
import com.bh.cp.dashboard.dto.response.DeleteAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.UsersAssetsResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

public interface AssetService {

	public AddAssetResponseDTO fetchAssets(HttpServletRequest httpServletRequest, GetAssetRequestDTO requestDto)
			throws JsonProcessingException, NotFoundException;

	public AddAssetResponseDTO addAssets(HttpServletRequest httpServletRequest, AddAssetRequestDTO requestDto)
			throws NotFoundException, JsonProcessingException;

	public DeleteAssetResponseDTO deleteAssets(HttpServletRequest httpServletRequest, DeleteAssetRequestDTO requestDto)
			throws NotFoundException, JsonProcessingException;

	public ChangeAssetNameResponseDTO assetNameCustomization(ChangeAssetNameRequestDTO assetRequestDTO,
			HttpServletRequest httpRequest) throws JsonProcessingException, NotFoundException;

	public UsersAssetsResponseDTO storeFavoriteAssets(HttpServletRequest httpServletRequest,
			UsersFavoriteAssetsRequestDTO requestDto) throws NotFoundException, JsonProcessingException;

	public List<UsersAssetsResponseDTO> getAllFavoriteAssets(HttpServletRequest httpServletRequest)
			throws NotFoundException, JsonProcessingException;

	public UsersAssetsResponseDTO setDefaultAsset(HttpServletRequest httpServletRequest,
			UsersDefaultAssetRequestDTO requestDto) throws NotFoundException, JsonProcessingException;

	public UsersAssetsResponseDTO getDefaultAsset(HttpServletRequest httpServletRequest)
			throws NotFoundException, JsonProcessingException;

}
