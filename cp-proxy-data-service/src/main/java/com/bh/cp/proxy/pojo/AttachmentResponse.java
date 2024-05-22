package com.bh.cp.proxy.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttachmentResponse {
	
	private Integer issueId;
	private Integer attachmentId;
	private String  message;
	
	public AttachmentResponse(Integer issueId, Integer attachmentId, String message) {
		super();
		this.issueId = issueId;
		this.attachmentId = attachmentId;
		this.message = message;
	}

}
