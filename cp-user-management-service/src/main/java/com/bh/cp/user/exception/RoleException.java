package com.bh.cp.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RoleException extends Exception {

	private static final long serialVersionUID = -9170656449815256528L;

	public RoleException(String message) {
		super(message);
	}
}
