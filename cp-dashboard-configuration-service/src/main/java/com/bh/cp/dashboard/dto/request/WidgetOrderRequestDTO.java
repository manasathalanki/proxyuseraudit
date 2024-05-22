package com.bh.cp.dashboard.dto.request;

import com.bh.cp.dashboard.constants.ValidationConstants;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetOrderRequestDTO {

	@NotNull(message = ValidationConstants.NULL_WIDGET_ID_MESSAGE)
	@Min(value = 1, message = ValidationConstants.WIDGET_ID_NOT_VALID_MESSAGE)
	private Integer widgetId;

	@NotNull(message = ValidationConstants.NULL_ORDER_NUMBER_MESSAGE)
	@Min(value = 1, message = ValidationConstants.ORDER_NUMBER_NOT_VALID_MESSAGE)
	private Integer orderNumber;

	@AssertTrue(message = ValidationConstants.NULL_OR_FALSE_WIDGET_CHECKED_MESSAGE)
	private boolean isChecked;
}
