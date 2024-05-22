package com.bh.cp.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {
	private String username;
	private String password;
}
