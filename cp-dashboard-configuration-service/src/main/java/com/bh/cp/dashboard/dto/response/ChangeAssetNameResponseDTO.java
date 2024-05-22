package com.bh.cp.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeAssetNameResponseDTO {

	private String level;

	private Integer customizationId;

	private String vid;

	private String assetName;
}
