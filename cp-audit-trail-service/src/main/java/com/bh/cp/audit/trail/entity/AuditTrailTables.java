package com.bh.cp.audit.trail.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class AuditTrailTables implements Serializable {

	private static final long serialVersionUID = 3749417180463241620L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_trail_tables_id_seq")
	@SequenceGenerator(sequenceName = "audit_trail_tables_id_seq", name = "audit_trail_tables_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 100, nullable = true)
	private String application;

	@Column(length = 100, nullable = true)
	private String schema;
	
	@Column(length = 100, nullable = false)
	private String tableName;
	
	@Column(nullable = true)
	private Integer primaryKey;
	
	@Column(name = "created_sso", length = 100)
	private String createdBySso;
	
	@Column(name = "created_date")
	private Timestamp createdDate;
	
	@Column(name = "updated_by_sso")
	private String updatedBySso;
	
	@Column(name = "update_date")
	private Timestamp updatedDate;

}
