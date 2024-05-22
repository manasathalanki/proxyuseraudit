package com.bh.cp.proxy.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class CaseConditions {

	
	private List<String> projectId;
	private List<String> plantId;
	private List<String> trainId;
	private List<String> lineupId;
	private List<String> machineId;
	private String caseNumber;
	private String caseType;
	private String catagoryId;
	private String criticalityId;
	private String customerPriorityId;	
	private String status;
	private String startDate;
	private String endDate;
}
