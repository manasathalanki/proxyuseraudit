package com.bh.cp.proxy.dto.request;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class PerformanceRequestDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 304121347205091427L;
	private Timestamp startTime;
	private Timestamp endTime;
	private String inputDetails;
	private String uri;
	private String serviceName;
	private String threadName;
	private Long totalExecutionTimeMs;
	private Boolean status;
	private String sso;
	private String module;
	private String moduleDescription;
	private Integer widgetId;
	private Integer serviceId;

	
}
