package com.bh.cp.dashboard.dto.request;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuditTrailUserActionDTO {
	
	private Integer id;

	private String application;

	private String schema;
	
	private String tableName;

	private Integer primaryKey;

	private String userAction;

	private Timestamp actionDate;
	
	private String data;
	
	private String sso;
}
