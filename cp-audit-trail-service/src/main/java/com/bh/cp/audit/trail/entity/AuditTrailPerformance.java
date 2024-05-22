package com.bh.cp.audit.trail.entity;

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
import lombok.Setter;

@Entity
@Getter
@Setter
public class AuditTrailPerformance {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,generator = "audit_trail_performance_id_seq")
	@SequenceGenerator(name = "audit_trail_performance_id_seq",sequenceName = "audit_trail_performance_id_seq" ,initialValue = 1000, allocationSize = 1)
	private Integer id;
	private String serviceName;
	private String threadName;
	private String module; 
	private String moduleDescription;
	private String uri;
	private Timestamp startTime;
	private Timestamp endTime;
	private Long totalExecutionTimeMs;
	@ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinColumn(name = "status",referencedColumnName = "id")	
	private Statuses status;
	@ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
	@JoinColumn(name = "sso",referencedColumnName = "sso")
	private Users user;
	@Column(name="widget_id")
	private Integer widgetId;
	@Column(name="service_id")
	private Integer serviceId;
	@Column(name="input_details",length=2000)
	private String inputDetails;

}
