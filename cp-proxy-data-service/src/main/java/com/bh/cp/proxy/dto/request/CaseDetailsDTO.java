package com.bh.cp.proxy.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Cases Detalis Request DTO", description = "Cases data request DTO for fleetsdata service input.")

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseDetailsDTO {
	
	private List<CaseTaskDTO> taskList;
	private List<CaseCommentsDTO> commentsList;
	private List<AttachmentsDTO> attachmentList; 
	private EditCase editCase;
}
