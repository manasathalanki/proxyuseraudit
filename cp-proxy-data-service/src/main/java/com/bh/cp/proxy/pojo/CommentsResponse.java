package com.bh.cp.proxy.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentsResponse {
	
	private Integer issueId;
	private Integer commentId;
	private String  message;
	
	public CommentsResponse(Integer issueId, Integer commentId, String message) {
		super();
		this.issueId = issueId;
		this.commentId = commentId;	
		this.message = message;
	}
}
