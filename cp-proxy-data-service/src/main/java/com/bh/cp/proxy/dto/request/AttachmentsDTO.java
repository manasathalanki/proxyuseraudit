package com.bh.cp.proxy.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Cases Detalis Request DTO", description = "Cases data request DTO for fleetsdata service input.")
public class AttachmentsDTO {
	
	private String action;
	private String attachmentTypeId;
	private String attachtId;
	private String caseId;
	private String fileName;
	private String mimeType;
	private String userType;

}
