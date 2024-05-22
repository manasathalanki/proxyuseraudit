package com.bh.cp.user.dto.request;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
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
