package com.bh.cp.dashboard.dto.request;

import java.util.List;

import com.bh.cp.dashboard.constants.ValidationConstants;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteAssetRequestDTO {

	@Schema(defaultValue = "PR_TEST")
	@Pattern(regexp = ValidationConstants.VALID_VID_REG_EXP, message = ValidationConstants.VID_NOT_VALID_MESSAGE)
	private String parentVid;

	@NotNull(message = ValidationConstants.NULL_CUSTOMIZATION_ID_MESSAGE)
	@Min(value = 1, message = ValidationConstants.CUSTOMIZATION_ID_NOT_VALID_MESSAGE)
	private Integer customizationId;

	@ArraySchema(schema = @Schema(defaultValue = "PR_TEST"))
	@NotNull(message = ValidationConstants.NULL_ASSET_VID_LIST_MESSAGE)
	@Size(min = 1, message = ValidationConstants.EMPTY_ASSET_VID_MESSAGE)
	private List<@NotNull(message = ValidationConstants.NULL_VID_MESSAGE) @Pattern(regexp = ValidationConstants.VALID_VID_REG_EXP, message = ValidationConstants.VID_NOT_VALID_MESSAGE) String> vids;

}
