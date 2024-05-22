package com.bh.cp.proxy.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseFormat {
	private int httpCode;
	private String userMessage;
	private String userMessageCode;
	private Object data;
}
