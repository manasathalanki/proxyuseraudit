package com.bh.cp.dashboard.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {

	@Schema(defaultValue = "your_username")
	private String username;

	@Schema(defaultValue = "your_password")
	private String password;
}
