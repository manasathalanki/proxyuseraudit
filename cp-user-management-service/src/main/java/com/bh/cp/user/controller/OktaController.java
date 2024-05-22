package com.bh.cp.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.user.dto.request.SearchRequestDTO;
import com.bh.cp.user.dto.response.OktaUserDetailsResponseDTO;
import com.bh.cp.user.service.OktaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("v1/okta/users")
@Tag(name = "Okta Controller")
public class OktaController {

	private OktaService oktaService;

	public OktaController(@Autowired OktaService oktaService) {
		super();
		this.oktaService = oktaService;
	}

	@PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Search Okta Users by Field", description = "Search and Retrieve Okta Users by Field")
	public List<OktaUserDetailsResponseDTO> getUsersByField(HttpServletRequest httpServletRequest,
			@RequestBody SearchRequestDTO requestDto) {
		return oktaService.getUsersByField(httpServletRequest, requestDto);
	}
}
