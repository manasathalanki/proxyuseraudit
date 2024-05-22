package com.bh.cp.dashboard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.dashboard.dto.request.AddAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.AssetOrderRequestDTO;
import com.bh.cp.dashboard.dto.request.ChangeAssetNameRequestDTO;
import com.bh.cp.dashboard.dto.request.DeleteAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.UsersDefaultAssetRequestDTO;
import com.bh.cp.dashboard.dto.request.UsersFavoriteAssetsRequestDTO;
import com.bh.cp.dashboard.dto.response.AddAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.ChangeAssetNameResponseDTO;
import com.bh.cp.dashboard.dto.response.DeleteAssetResponseDTO;
import com.bh.cp.dashboard.dto.response.UsersAssetsResponseDTO;
import com.bh.cp.dashboard.service.AssetService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class AssetControllerTest {

	@Mock
	private AssetService assetService;

	@InjectMocks
	private AssetController assetController;

	@Mock
	private MockHttpServletRequest mockHttpServletRequest;

	private MockMvc mockMvc;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(assetController).build();
	}

	@Test
	@DisplayName("GetLayout - with input vid as null and level as Projects -Positive")
	void testfetchAssets_Positive() throws Exception {
		AddAssetResponseDTO expectedResponse = new AddAssetResponseDTO();
		when(assetService.fetchAssets(any(HttpServletRequest.class), any())).thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/assets").content("{\"vid\":null,\"level\":\"projects\"}")
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content()
								.string(new ObjectMapper().writeValueAsString(expectedResponse)));
	}

	@Test
	@DisplayName("Adding assets into logged-in user's customization.")
	void testaddAssets_Positive() throws Exception {
		AddAssetResponseDTO expectedResponse = new AddAssetResponseDTO();
		AddAssetRequestDTO addAssetRequestDTO = new AddAssetRequestDTO();
		addAssetRequestDTO.setParentVid("MC_GT001");
		addAssetRequestDTO.setCustomizationId(1);
		List<AssetOrderRequestDTO> assetOrderRequestDTOlist = new ArrayList<>();
		AssetOrderRequestDTO assetOrderRequestDTO = new AssetOrderRequestDTO();
		assetOrderRequestDTO.setOrderNumber(1);
		assetOrderRequestDTO.setVid("MC_GT001");
		assetOrderRequestDTOlist.add(assetOrderRequestDTO);
		AssetOrderRequestDTO assetOrderRequestDTO2 = new AssetOrderRequestDTO();
		assetOrderRequestDTO2.setOrderNumber(2);
		assetOrderRequestDTO2.setVid("MC_GT002");
		assetOrderRequestDTOlist.add(assetOrderRequestDTO2);
		addAssetRequestDTO.setAssets(assetOrderRequestDTOlist);
		when(assetService.addAssets(any(HttpServletRequest.class), any(AddAssetRequestDTO.class)))
				.thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.put("/v1/assets/").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(addAssetRequestDTO)))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	@DisplayName("Edit asset name in dashboard")
	void testassetNameCustomization_Positive() throws Exception {
		ChangeAssetNameResponseDTO expectedResponse = new ChangeAssetNameResponseDTO();
		ChangeAssetNameRequestDTO addAssetRequestDTO = new ChangeAssetNameRequestDTO();
		addAssetRequestDTO.setParentVid("MC_GT001");
		addAssetRequestDTO.setCustomizationId(1);
		addAssetRequestDTO.setAssetName("test");
		addAssetRequestDTO.setLevel("projects");
		addAssetRequestDTO.setVid("PR_GLNG");
		when(assetService.assetNameCustomization(any(), any())).thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.patch("/v1/assets/name").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(addAssetRequestDTO)))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	@DisplayName("Delete assets from dashboard customization.")
	void testdeleteAssets_Positive() throws Exception {
		DeleteAssetResponseDTO expectedResponse = new DeleteAssetResponseDTO();
		DeleteAssetRequestDTO addAssetRequestDTO = new DeleteAssetRequestDTO();
		addAssetRequestDTO.setParentVid("MC_GT001");
		addAssetRequestDTO.setCustomizationId(1);
		List<String> vids = new ArrayList<>();
		vids.add("PR_NOBLE");
		vids.add("PR_GLNG");
		addAssetRequestDTO.setVids(vids);

		when(assetService.deleteAssets(any(), any())).thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.delete("/v1/assets/").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(addAssetRequestDTO)))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	@DisplayName("Get Default Asset for logged-in user.")
	void testgetDefaultAsset_Positive() throws Exception {
		UsersAssetsResponseDTO expectedResponse = new UsersAssetsResponseDTO();
		when(assetService.getDefaultAsset(any())).thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/assets/default").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(expectedResponse)))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	@DisplayName("set Default Asset for logged-in user.")
	void testsetDefaultAsset_Positive() throws Exception {
		UsersAssetsResponseDTO expectedResponse = new UsersAssetsResponseDTO();
		UsersDefaultAssetRequestDTO addAssetRequestDTO = new UsersDefaultAssetRequestDTO();
		addAssetRequestDTO.setDefaultVid("MC_GT001");

		when(assetService.setDefaultAsset(any(), any())).thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/assets/default").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(addAssetRequestDTO)))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	@DisplayName("Delete assets from dashboard customization.")
	void testgetAllFavoriteAssets_Positive() throws Exception {
		List<UsersAssetsResponseDTO> expectedResponse = new ArrayList<>();
		when(assetService.getAllFavoriteAssets(any())).thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/assets/favorite").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(expectedResponse)))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}
	@Test
	@DisplayName("set Default Asset for logged-in user.")
	void teststoreFavoriteAssets_Positive() throws Exception {
		UsersAssetsResponseDTO expectedResponse = new UsersAssetsResponseDTO();
		UsersFavoriteAssetsRequestDTO addAssetRequestDTO = new UsersFavoriteAssetsRequestDTO();
		addAssetRequestDTO.setVid("MC_GT001");
		addAssetRequestDTO.setMarkAsFavorite(true);

		when(assetService.storeFavoriteAssets(any(), any())).thenReturn(expectedResponse);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/assets/favorite").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(addAssetRequestDTO)))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

}
