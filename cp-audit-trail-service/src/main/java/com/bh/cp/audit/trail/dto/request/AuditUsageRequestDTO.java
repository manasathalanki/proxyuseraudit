package com.bh.cp.audit.trail.dto.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditUsageRequestDTO {

	private Integer id;

	@NotNull
	private String sso;

	private String activity;

	private String functionality;

	private Boolean status;

	private Timestamp entryTime;
	
	private Timestamp exitTime;

	@NotNull
	private String serviceName;
	
	@NotNull
	private String threadName;

}
