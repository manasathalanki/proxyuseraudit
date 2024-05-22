package com.bh.cp.dashboard.dto.request;

import java.util.List;

import com.bh.cp.dashboard.constants.ValidationConstants;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAssetRequestDTO {

	@Schema(defaultValue = "PR_TEST")
	@Pattern(regexp = ValidationConstants.VALID_VID_REG_EXP, message = ValidationConstants.VID_NOT_VALID_MESSAGE)
	private String parentVid;

	@NotNull(message = ValidationConstants.NULL_CUSTOMIZATION_ID_MESSAGE)
	@Min(value = 1, message = ValidationConstants.CUSTOMIZATION_ID_NOT_VALID_MESSAGE)
	private Integer customizationId;

	@Valid
	@NotNull(message = ValidationConstants.NULL_ASSET_ORDER_LIST_MESSAGE)
	@Size(min = 1, message = ValidationConstants.EMPTY_ASSET_ORDER_MESSAGE)
	private List<AssetOrderRequestDTO> assets;

}
