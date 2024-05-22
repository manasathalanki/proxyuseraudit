package com.bh.cp.proxy.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Cases Detalis Request DTO", description = "Cases data request DTO for fleetsdata service input.")

public class CaseCommentsDTO {
	
	private String action;
	private String caseId;
	private String commentDesc;
	private String commentId;
	private String commentType;
	private String commentVisible;
	private String userType;
}
