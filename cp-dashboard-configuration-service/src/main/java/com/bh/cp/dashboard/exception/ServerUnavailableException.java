package com.bh.cp.dashboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ServerUnavailableException extends Exception {

	private static final long serialVersionUID = 289430938587759587L;

	public ServerUnavailableException(String message) {
		super(message);
	}

}
