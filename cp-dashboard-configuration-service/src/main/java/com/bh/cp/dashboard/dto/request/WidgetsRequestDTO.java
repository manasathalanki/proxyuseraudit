package com.bh.cp.dashboard.dto.request;

import java.util.List;

import com.bh.cp.dashboard.constants.ExceptionConstants;
import com.bh.cp.dashboard.constants.ValidationConstants;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WidgetsRequestDTO {

	@NotNull(message = ValidationConstants.NULL_LEVEL_MESSAGE)
	@Pattern(regexp = ValidationConstants.LEVELS_REG_EXP, message = ValidationConstants.LEVELS_NOT_VALID_MESSAGE)
	private String level;

	@NotNull(message = ValidationConstants.NULL_WIDGET_TYPE_MESSAGE)
	@Pattern(regexp = ValidationConstants.KPI_SUMMARY_REG_EXP, message = ValidationConstants.KPI_SUMMARY_NOT_VALID_MESSAGE)
	private String type;

	@Valid
	@NotNull(message = ValidationConstants.NULL_WIDGET_ORDER_LIST_MESSAGE)
	@Size(min = 1, message = ExceptionConstants.MSG_CANT_DELETE_ALL_WIDGET)
	private List<WidgetOrderRequestDTO> widgets;

}
