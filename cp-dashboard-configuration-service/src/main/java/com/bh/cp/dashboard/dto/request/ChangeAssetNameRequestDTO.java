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
public class ChangeAssetNameRequestDTO {

	@NotNull(message = ValidationConstants.NULL_LEVEL_MESSAGE)
	@Pattern(regexp = ValidationConstants.LEVELS_REG_EXP, message = ValidationConstants.LEVELS_NOT_VALID_MESSAGE)
	private String level;

	@NotNull(message = ValidationConstants.NULL_CUSTOMIZATION_ID_MESSAGE)
	@Min(value = 1, message = ValidationConstants.CUSTOMIZATION_ID_NOT_VALID_MESSAGE)
	private Integer customizationId;

	@Schema(defaultValue = "PR_TEST")
	@NotNull(message = ValidationConstants.NULL_VID_MESSAGE)
	@Pattern(regexp = ValidationConstants.VALID_VID_REG_EXP, message = ValidationConstants.VID_NOT_VALID_MESSAGE)
	private String vid;

	@Schema(defaultValue = "PR_TEST_NAME")
	@NotNull(message = ValidationConstants.NULL_ASSET_NAME_MESSAGE)
	@Pattern(regexp = ValidationConstants.ASSET_NAME_REG_EXP, message = ValidationConstants.ASSET_NAME_NOT_VALID_MESSAGE)
	private String assetName;

	@Schema(defaultValue = "PR_TEST")
	@Pattern(regexp = ValidationConstants.VALID_VID_REG_EXP, message = ValidationConstants.VID_NOT_VALID_MESSAGE)
	private String parentVid;

}
