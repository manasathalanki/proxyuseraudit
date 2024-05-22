package com.bh.cp.audit.trail.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class AuditTrailUserAction implements Serializable {

	private static final long serialVersionUID = 1953712459851184050L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_trail_user_action_id_seq")
	@SequenceGenerator(sequenceName = "audit_trail_user_action_id_seq", name = "audit_trail_user_action_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "sso", referencedColumnName = "sso", nullable = false)
	private Users users;

	@Column(length = 100, nullable = true)
	private String application;

	@Column(length = 100, nullable = true)
	private String schema;
	
	@Column(length = 100, nullable = false, name = "table_name")
	private String tableName;

	@Column(length = 100, nullable = false, name = "user_action")
	private String userAction;

	@Column(name = "data",length=2000)
	private String data;

	@Column(name = "action_date", nullable = true)
	private Timestamp actionDate;

}