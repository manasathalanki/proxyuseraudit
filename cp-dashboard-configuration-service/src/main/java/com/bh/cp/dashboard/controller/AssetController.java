package com.bh.cp.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import com.bh.cp.dashboard.service.AssetService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;

@RestController
@RequestMapping("v1/assets")
@Tag(name = "User Assets Controller")
public class AssetController {

	private AssetService assetService;

	public AssetController(@Autowired AssetService assetService) {
		super();
		this.assetService = assetService;
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Getting all assets for logged-in user.", description = "Getting all assets for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<AddAssetResponseDTO> fetchAssets(HttpServletRequest httpServletRequest,
			@Valid @RequestBody GetAssetRequestDTO requestDto) throws JsonProcessingException, NotFoundException {
		return ResponseEntity.ok(assetService.fetchAssets(httpServletRequest, requestDto));
	}

	@PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Adding assets into dashboard customization.", description = "Adding assets into logged-in user's customization.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<AddAssetResponseDTO> addAssets(HttpServletRequest httpServletRequest,
			@Valid @RequestBody AddAssetRequestDTO requestDto) throws JsonProcessingException, NotFoundException {
		return ResponseEntity.ok(assetService.addAssets(httpServletRequest, requestDto));
	}

	@PatchMapping(value = "/name", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Edit asset name in dashboard", description = "Edit asset name in logged-in user's dashboard.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<ChangeAssetNameResponseDTO> assetNameCustomization(
			@Valid @RequestBody ChangeAssetNameRequestDTO assetRequestDTO, HttpServletRequest httpServletRequest)
			throws JsonProcessingException, NotFoundException {
		return ResponseEntity.ok(assetService.assetNameCustomization(assetRequestDTO, httpServletRequest));
	}

	@DeleteMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete assets from dashboard customization.", description = "Delete assets from logged-in user's customization.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<DeleteAssetResponseDTO> deleteAssets(HttpServletRequest httpServletRequest,
			@Valid @RequestBody DeleteAssetRequestDTO requestDto) throws JsonProcessingException, NotFoundException {
		return ResponseEntity.ok(assetService.deleteAssets(httpServletRequest, requestDto));
	}

	@GetMapping(value = "/default", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get Default Asset for logged-in user.", description = "Get Default Asset for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<UsersAssetsResponseDTO> getDefaultAsset(HttpServletRequest httpServletRequest)
			throws JsonProcessingException, NotFoundException {
		return ResponseEntity.ok(assetService.getDefaultAsset(httpServletRequest));
	}

	@PostMapping(value = "/default", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Set Default Asset for logged-in user.", description = "Set Default Asset for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<UsersAssetsResponseDTO> setDefaultAsset(HttpServletRequest httpServletRequest,
			@Valid @RequestBody UsersDefaultAssetRequestDTO requestDto)
			throws JsonProcessingException, NotFoundException {
		return ResponseEntity.ok(assetService.setDefaultAsset(httpServletRequest, requestDto));
	}

	@GetMapping(value = "/favorite", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all preferred assets for logged-in user.", description = "Get all preferred assets for logged-in user.")
	public ResponseEntity<List<UsersAssetsResponseDTO>> getAllFavoriteAssets(HttpServletRequest httpServletRequest)
			throws JsonProcessingException, NotFoundException {
		return ResponseEntity.ok(assetService.getAllFavoriteAssets(httpServletRequest));
	}

	@PostMapping(value = "/favorite", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Store preferred assets for logged-in user.", description = "Store preferred assets for logged-in user.")
	public ResponseEntity<UsersAssetsResponseDTO> storeFavoriteAssets(HttpServletRequest httpServletRequest,
			@Valid @RequestBody UsersFavoriteAssetsRequestDTO requestDto)
			throws JsonProcessingException, NotFoundException {
		return ResponseEntity.ok(assetService.storeFavoriteAssets(httpServletRequest, requestDto));
	}

}
