package com.bh.cp.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DeletionNotPermissableException extends Exception {

	private static final long serialVersionUID = -6769785271993254694L;

	public DeletionNotPermissableException(String message) {
		super(message);
	}
}
