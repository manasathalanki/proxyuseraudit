package com.bh.cp.proxy.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Cases Data Request DTO", description = "Cases data request DTO for fleetsdata service input.")
public class CasesDataRequestDTO {

	private String level;

	private String vid;

	@NotNull
	private Integer serviceId;

	private String dateRange;

	private List<String> projectId;
	private List<String> plantId;
	private List<String> trainId;
	private List<String> lineupId;
	private List<String> machineId;
	private String caseNumber;
	private List<String> caseType;
	private List<String> catagoryId;
	private List<String> criticalityId;
	private List<String> customerPriorityId;
	private List<String> status;
	private String startDate;
	private String endDate;

	private List<String> parentCaseId;

	private String image;

	private List<String> issueId;

	private List<String> nameLink;

	private String attachment;
	private List<String> attachmentId;

	private String rootCause;

	private String taskId;

	private String action;
	private String commentDesc;
	private String commentId;
	private String commentType;
	private String commentVisible;
	private String userType;

	// Required Fields for Eidt Attachment

	private String file;
	private String mimeType;
	private String attachmentTypeId;
	private String lockType;

}
