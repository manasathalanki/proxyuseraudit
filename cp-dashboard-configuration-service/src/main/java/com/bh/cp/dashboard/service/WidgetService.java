package com.bh.cp.dashboard.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.bh.cp.dashboard.dto.request.DeleteWidgetRequestDTO;
import com.bh.cp.dashboard.dto.request.WidgetsRequestDTO;
import com.bh.cp.dashboard.dto.response.WidgetsResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

public interface WidgetService {

	public List<WidgetsResponseDTO> addWidgets(WidgetsRequestDTO widgetsRequest, HttpServletRequest httpRequest,
			boolean isReordered) throws NoSuchElementException, JsonProcessingException, NotFoundException;

	public List<WidgetsResponseDTO> getAllWidgets(String level, String type, Integer customizationId,
			HttpServletRequest httpRequest) throws JsonProcessingException, NotFoundException;

	public List<WidgetsResponseDTO> deleteWidgets(DeleteWidgetRequestDTO widgetsRequestDTOs,
			HttpServletRequest httpServletRequest) throws JsonProcessingException, NotFoundException;

}
