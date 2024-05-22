package com.bh.cp.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserRequestDTO {

	private String id;

	private boolean active;

}
