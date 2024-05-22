package com.bh.cp.dashboard.dto.request;

import com.bh.cp.dashboard.constants.ValidationConstants;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingsRequestDTO {

	@Schema(defaultValue = "UTC")
	@Pattern(regexp = ValidationConstants.SETTING_VALUE_REG_EXP, message = ValidationConstants.SETTING_VALUE_NOT_VALID_MESSAGE)
	private String timeZone;

	@Schema(defaultValue = "Customer")
	@Pattern(regexp = ValidationConstants.SETTING_VALUE_REG_EXP, message = ValidationConstants.SETTING_VALUE_NOT_VALID_MESSAGE)
	private String displayAssetName;

	@Schema(defaultValue = "All Assets")
	@Pattern(regexp = ValidationConstants.SETTING_VALUE_REG_EXP, message = ValidationConstants.SETTING_VALUE_NOT_VALID_MESSAGE)
	private String retiredAssets;

	@Schema(defaultValue = "UK")
	@Pattern(regexp = ValidationConstants.SETTING_VALUE_REG_EXP, message = ValidationConstants.SETTING_VALUE_NOT_VALID_MESSAGE)
	private String uom;

	@Min(value = 1, message = ValidationConstants.PERSONA_NOT_VALID_MESSAGE)
	private Integer personaId;

}
