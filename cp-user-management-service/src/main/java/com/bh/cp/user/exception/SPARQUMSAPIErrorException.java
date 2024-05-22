package com.bh.cp.user.exception;

import org.springframework.http.HttpStatus;

public class SPARQUMSAPIErrorException extends RuntimeException {

	private final HttpStatus httpStatus;

	private static final long serialVersionUID = -6769785271993254694L;

	public SPARQUMSAPIErrorException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

}
