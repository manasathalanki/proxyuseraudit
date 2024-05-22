package com.bh.cp.dashboard.dto.request;

import com.bh.cp.dashboard.constants.ValidationConstants;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayoutRequestDTO {

	@Schema(defaultValue = "PR_TEST")
	@Pattern(regexp = ValidationConstants.VALID_VID_REG_EXP, message = ValidationConstants.VID_NOT_VALID_MESSAGE)
	private String vid;

	@Schema(defaultValue = "false")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private boolean showSiblings;

}
