package com.bh.cp.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UsersAssetsResponseDTO {

	private String vid;

	@JsonInclude(Include.NON_NULL)
	private String displayName;

	@JsonInclude(Include.NON_NULL)
	private String sso;

	@JsonInclude(Include.NON_NULL)
	private String status;

	public UsersAssetsResponseDTO(String vid, String displayName, String sso) {
		super();
		this.vid = vid;
		this.displayName = displayName;
		this.sso = sso;
	}

}
