package com.bh.cp.audit.trail.dto.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceRequestDTO {

	private Timestamp startTime;
	private Timestamp endTime;
	private String inputDetails;
	private String uri;
	private String moduleDescription;
	@NotNull
	private String serviceName;
	@NotNull
	private String threadName;
	private Long totalExecutionTimeMs;
	private Boolean status;
	@NotNull
	private String sso;
	private String module;
	private String widgetId;
	private String serviceId;

}
