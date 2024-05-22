package com.bh.cp.dashboard.dto.request;

import java.util.List;

import com.bh.cp.dashboard.constants.ValidationConstants;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteWidgetRequestDTO {

	@NotNull(message = ValidationConstants.NULL_LEVEL_MESSAGE)
	@Pattern(regexp = ValidationConstants.LEVELS_REG_EXP, message = ValidationConstants.LEVELS_NOT_VALID_MESSAGE)
	private String level;

	@NotNull(message = ValidationConstants.NULL_WIDGET_TYPE_MESSAGE)
	@Pattern(regexp = ValidationConstants.KPI_SUMMARY_REG_EXP, message = ValidationConstants.KPI_SUMMARY_NOT_VALID_MESSAGE)
	private String type;

	@NotNull(message = ValidationConstants.NULL_WIDGET_VID_LIST_MESSAGE)
	@Size(min = 1, message = ValidationConstants.EMPTY_WIDGET_ID_MESSAGE)
	private List<@Min(value = 1, message = ValidationConstants.WIDGET_ID_NOT_VALID_MESSAGE) Integer> widgetIds;

}
