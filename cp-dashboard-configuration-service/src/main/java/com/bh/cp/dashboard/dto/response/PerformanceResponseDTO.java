package com.bh.cp.dashboard.dto.response;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class PerformanceResponseDTO {
	
	private Integer id;
	private String sso;
	private Timestamp startTime;
	private Timestamp endTime;
	private String status;
	private String inputDetails;
	private String serviceName;
	private String totalExecutionTime;
}
