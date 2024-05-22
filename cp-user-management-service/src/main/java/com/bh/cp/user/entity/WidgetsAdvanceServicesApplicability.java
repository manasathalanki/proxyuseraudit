package com.bh.cp.user.entity;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
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

@Getter
@Setter
@Entity
@NoArgsConstructor
public class WidgetsAdvanceServicesApplicability implements Serializable {

	private static final long serialVersionUID = 7446483527820981239L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "widgets_advance_services_applicability_seq")
	@SequenceGenerator(sequenceName = "widgets_advance_services_applicability_seq", name = "widgets_advance_services_applicability_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "widget_id", referencedColumnName = "id")
	private Widgets widgets;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "advance_service_id", referencedColumnName = "id")
	private AdvanceServices advanceServices;

}
