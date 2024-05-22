package com.bh.cp.proxy.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskResponse {
	private Integer taskId;
	private String status;
	private String rootCause;
	private String message;
	
	public TaskResponse(Integer taskId, String status, String rootCause, String message) {
		super();
		this.taskId = taskId;
		this.status = status;
		this.rootCause = rootCause;
		this.message = message;
	}
	
	
}
