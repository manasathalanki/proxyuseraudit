package com.bh.cp.audit.trail.dto.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuditTrailUserActionDTO {
	
	private Integer id;

	@NotNull
	private String application;

	private String schema;
	
	private String tableName;

	private Integer primaryKey;

	private String userAction;

	private Timestamp actionDate;
	
	private String data;
	
	@NotNull
	private String sso;

}
