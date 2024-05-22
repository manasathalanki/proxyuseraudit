package com.bh.cp.user.dto.request;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AuditUsageRequestDTO {

	private Integer id;

	private String sso;

	private String activity;

	private String functionality;

	private Boolean status;

	private Timestamp entryTime;

	private Timestamp exitTime;

	private String serviceName;
	
	private String threadName;

}
