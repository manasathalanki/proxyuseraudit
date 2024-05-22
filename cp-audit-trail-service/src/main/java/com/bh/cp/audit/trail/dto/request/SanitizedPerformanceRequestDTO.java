package com.bh.cp.audit.trail.dto.request;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SanitizedPerformanceRequestDTO {
	
	private Timestamp startTime;
	private Timestamp endTime;
	private String inputDetails;
	private String uri;
	private String moduleDescription;
	private String serviceName;
	private String threadName;
	private Long totalExecutionTimeMs;
	private Boolean status;
	private String sso;
	private String module;
	private Integer widgetId;
	private Integer serviceId;
	
}
