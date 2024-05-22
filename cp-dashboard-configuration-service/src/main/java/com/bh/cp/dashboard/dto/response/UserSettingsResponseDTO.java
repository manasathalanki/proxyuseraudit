package com.bh.cp.dashboard.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingsResponseDTO {

	private String sso;

	private String timeZone;

	private String displayAssetName;

	private String retiredAssets;

	private String uom;
	
	@JsonInclude(Include.NON_NULL)
	private String status;

	private List<PersonaResponseDTO> personas;

}
