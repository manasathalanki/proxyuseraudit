package com.bh.cp.dashboard.dto.request;

import com.bh.cp.dashboard.constants.ValidationConstants;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetOrderRequestDTO {

	@Schema(defaultValue = "PR_TEST")
	@NotNull(message = ValidationConstants.NULL_VID_MESSAGE)
	@Pattern(regexp = ValidationConstants.VALID_VID_REG_EXP, message = ValidationConstants.VID_NOT_VALID_MESSAGE)
	private String vid;

	@NotNull(message = ValidationConstants.NULL_ORDER_NUMBER_MESSAGE)
	@Min(value = 1, message = ValidationConstants.ORDER_NUMBER_NOT_VALID_MESSAGE)
	private Integer orderNumber;

}
