package com.bh.cp.dashboard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.dashboard.entity.CommonBlobs;
import com.bh.cp.dashboard.repository.CommonBlobsRepository;
import com.bh.cp.dashboard.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.NotFoundException;

@RestController
@RequestMapping("v1")
@Tag(name = "Images Controller")
public class ImageController {

	private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

	private CommonBlobsRepository commonBlobRepository;

	public ImageController(@Autowired CommonBlobsRepository commonBlobRepository) {
		super();
		this.commonBlobRepository = commonBlobRepository;
	}

	@GetMapping(value = "/images/{imageId}")
	@Operation(summary = "Getting image given for Image ID.", description = "Getting iamge content based on id.")
	@SecurityRequirement(name = "Keycloak Token")
	public ResponseEntity<byte[]> retrieveImage(
			@PathVariable(name = "imageId") @Schema(defaultValue = "1") String imageId) throws NotFoundException {

		SecurityUtil.sanitizeLogging(logger, Level.DEBUG, "Given imageId=>{}", imageId);
		CommonBlobs commonBlob = commonBlobRepository.findById(Integer.parseInt(imageId.split("\\.")[0]))
				.orElseThrow(() -> new NotFoundException("Image Not Found for Id " + imageId));
		HttpHeaders header = new HttpHeaders();
		String extension = commonBlob.getExtension();
		if (extension.equals("svg")) {
			header.set("Content-Type", "application/xml");
		} else {
			header.set("Content-Type", "image/" + extension);
		}
		return ResponseEntity.status(HttpStatus.OK).headers(header).body(commonBlob.getMaterial());
	}
}
