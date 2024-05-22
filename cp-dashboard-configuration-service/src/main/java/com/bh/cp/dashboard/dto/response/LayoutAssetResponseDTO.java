package com.bh.cp.dashboard.dto.response;

import com.bh.cp.dashboard.constants.JSONUtilConstants;
import com.bh.cp.dashboard.entity.CustomizationsAssets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LayoutAssetResponseDTO {

	private Integer assetCustomizationId;

	private String vid;

	private String title;

	private Integer orderNumber;

	private String customerName;

	private Integer imageId;

	private String imageSrc;

	private String defaultImageSrc;

	private Boolean isSelected;

	public LayoutAssetResponseDTO(CustomizationsAssets customizationAssets, String displayName, String customerName,
			String imageSrc, String level, boolean isSelected) {
		this.assetCustomizationId = customizationAssets.getId();
		this.vid = customizationAssets.getVid();
		this.title = customizationAssets.getAssetName() != null ? customizationAssets.getAssetName() : displayName;
		this.orderNumber = customizationAssets.getOrderNumber();
		this.imageId = assetImage(this.vid);
		this.customerName = customerName;
		this.imageSrc = imageSrc;
		this.defaultImageSrc = assetDefaultImage(level);
		this.isSelected = isSelected;
	}

	public static Integer assetImage(String vid) {
		String prefix = vid.substring(0, 3);
		switch (prefix) {
		case "PR_":
			return 38;
		case "PL_":
			return 39;
		case "TR_":
			return 40;
		case "LN_":
			return 41;
		case "MC_":
			return 42;
		default:
			return 0;
		}
	}

	public static String assetDefaultImage(String level) {
		return "assets/images/" + level + JSONUtilConstants.IMAGE_EXTENSION;
	}
}
