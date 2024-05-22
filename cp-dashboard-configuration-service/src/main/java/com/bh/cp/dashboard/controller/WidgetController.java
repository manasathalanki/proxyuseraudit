package com.bh.cp.dashboard.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.constants.JSONUtilConstants;
import com.bh.cp.dashboard.constants.ValidationConstants;
import com.bh.cp.dashboard.dto.request.DeleteWidgetRequestDTO;
import com.bh.cp.dashboard.dto.request.WidgetsRequestDTO;
import com.bh.cp.dashboard.dto.response.WidgetsResponseDTO;
import com.bh.cp.dashboard.service.WidgetService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.NotFoundException;

@RestController
@RequestMapping("v1/widgets")
@Tag(name = "User Widgets Controller")
public class WidgetController {

	private WidgetService widgetService;

	public WidgetController(@Autowired WidgetService widgetService) {
		super();
		this.widgetService = widgetService;
	}

	@PostMapping("/")
	@Operation(summary = "Add widgets for user.", description = "Add KPI/SUMMARY widgets for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public List<WidgetsResponseDTO> addWidgets(@Valid @RequestBody WidgetsRequestDTO widgetsRequestDTOs,
			HttpServletRequest httpServletRequest)
			throws NoSuchElementException, JsonProcessingException, NotFoundException {
		return widgetService.addWidgets(widgetsRequestDTOs, httpServletRequest, false);
	}

	@PatchMapping("/")
	@Operation(summary = "Re-order widgets for user.", description = "Order KPI/SUMMARY widgets for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public List<WidgetsResponseDTO> reorderWidgets(@Valid @RequestBody WidgetsRequestDTO widgetsRequestDTOs,
			HttpServletRequest httpServletRequest)
			throws NoSuchElementException, JsonProcessingException, NotFoundException {
		return widgetService.addWidgets(widgetsRequestDTOs, httpServletRequest, true);
	}

	@GetMapping
	@Operation(summary = "Fetch wigets for user.", description = "Fetch KPI/SUMMARY widgets for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public List<WidgetsResponseDTO> widgets(
			@RequestParam("level") @Schema(defaultValue = JSONUtilConstants.LEVEL_PROJECTS) @Pattern(regexp = ValidationConstants.LEVELS_REG_EXP, message = ValidationConstants.LEVELS_NOT_VALID_MESSAGE) String level,
			@RequestParam("type") @Schema(defaultValue = DashboardConstants.KPI) @Pattern(regexp = ValidationConstants.KPI_SUMMARY_REG_EXP, message = ValidationConstants.KPI_SUMMARY_NOT_VALID_MESSAGE) String type,
			@RequestParam("customization-id") @Schema(defaultValue = "1") @Min(value = 1, message = ValidationConstants.CUSTOMIZATION_ID_NOT_VALID_MESSAGE) Integer customizationId,
			HttpServletRequest httpServletRequest) throws JsonProcessingException, NotFoundException {
		return widgetService.getAllWidgets(level, type, customizationId, httpServletRequest);
	}

	@DeleteMapping("/")
	@Operation(summary = "Delete widgets for user.", description = "Delete KPI/SUMMARY widgets for logged-in user.")
	@SecurityRequirement(name = "Keycloak Token")
	public List<WidgetsResponseDTO> deleteWidgets(@Valid @RequestBody DeleteWidgetRequestDTO widgetsRequestDTO,
			HttpServletRequest httpServletRequest)
			throws NoSuchElementException, JsonProcessingException, NotFoundException {
		return widgetService.deleteWidgets(widgetsRequestDTO, httpServletRequest);
	}

}
