package com.bh.cp.user.service;

import java.util.List;

import com.bh.cp.user.dto.request.SearchRequestDTO;
import com.bh.cp.user.dto.response.OktaUserDetailsResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface OktaService {

	public List<OktaUserDetailsResponseDTO> getUsersByField(HttpServletRequest httpServletRequest,
			SearchRequestDTO requestDto);

}
