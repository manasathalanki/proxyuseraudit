package com.bh.cp.proxy.pojo;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditDate {

	
	Timestamp startTime;
	Timestamp endTime;
	long executionTime;
	public AuditDate(Timestamp startTime, Timestamp endTime, long executionTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.executionTime = executionTime;
	}
	
}
