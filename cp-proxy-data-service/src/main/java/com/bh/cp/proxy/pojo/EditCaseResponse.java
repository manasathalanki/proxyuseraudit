package com.bh.cp.proxy.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EditCaseResponse {

	private String caseId;
	private String rev;
	
	public EditCaseResponse(String caseId, String rev) {
		super();
		this.caseId = caseId;
		this.rev = rev;		
	}
}
