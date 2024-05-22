package com.bh.cp.dashboard.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.dashboard.entity.CommonBlobs;
import com.bh.cp.dashboard.repository.CommonBlobsRepository;

class ImageControllerTest {

	@InjectMocks
	ImageController imageController;

	private MockMvc mockMvc;

	@Mock
	private CommonBlobsRepository commonBlobsRepository;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();

	}

	@Test
	void testRetrieveImage() throws Exception {
		String imageId = "1";
		CommonBlobs commonBlobs = new CommonBlobs();
		commonBlobs.setExtension("jpg");
		byte[] imageData = "image.jpg".getBytes();
		commonBlobs.setMaterial(imageData);
		when(commonBlobsRepository.findById(1)).thenReturn(Optional.of(commonBlobs));
		mockMvc.perform(get("/v1/images/{imageId}", imageId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testRetrieveImageSvg() throws Exception {
		String imageId = "1";
		CommonBlobs commonBlobs = new CommonBlobs();
		commonBlobs.setExtension("svg");
		byte[] imageData = "image.svg".getBytes();
		commonBlobs.setMaterial(imageData);
		when(commonBlobsRepository.findById(1)).thenReturn(Optional.of(commonBlobs));
		mockMvc.perform(get("/v1/images/{imageId}", imageId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
