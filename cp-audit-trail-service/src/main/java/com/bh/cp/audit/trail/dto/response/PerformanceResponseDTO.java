package com.bh.cp.audit.trail.dto.response;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PerformanceResponseDTO {
	
	private Integer id;
	private String sso;
	private Timestamp startTime;
	private Timestamp endTime;
	private String status;
	private String inputDetails;
	private String serviceName;
	private Long totalExecutionTime;
	private String module;
}
