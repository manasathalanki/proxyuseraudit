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
public class AuditTrailUsage implements Serializable {

	private static final long serialVersionUID = -7784514229053266874L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_trail_usage_id_seq")
	@SequenceGenerator(sequenceName = "audit_trail_usage_id_seq", name = "audit_trail_usage_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "sso", referencedColumnName = "sso")
	private Users users;

	@Column(length = 100, nullable = false)
	private String activity;

	@Column(length = 100, nullable = false)
	private String functionality;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
	private Statuses statuses;

	@Column(name = "entry_time", nullable = false)
	private Timestamp entryTime;
	
	@Column(name = "exit_time", nullable = true)
	private Timestamp exitTime;
	
	@Column(name = "service_name")
	private String serviceName;
	
	@Column(name = "thread_name")
	private String threadName;

}
