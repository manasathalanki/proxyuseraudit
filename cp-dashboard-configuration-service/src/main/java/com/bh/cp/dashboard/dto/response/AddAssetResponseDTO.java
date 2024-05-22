package com.bh.cp.dashboard.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAssetResponseDTO {

	private String level;

	private Integer customizationId;

	private List<LayoutAssetResponseDTO> assets;

}
