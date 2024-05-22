package com.bh.cp.dashboard.entity;

import java.io.Serializable;

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
@Setter
@Getter
public class Settings implements Serializable {

	private static final long serialVersionUID = -3423716933140467964L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "settings_id_seq")
	@SequenceGenerator(sequenceName = "settings_id_seq", name = "settings_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 50, nullable = false, name = "default_role")
	private String defaultRole;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "default_persona_id", referencedColumnName = "id", nullable = false)
	private Personas defaultPersonas;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "default_company_id", referencedColumnName = "id", nullable = false)
	private Companies companies;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "default_my_dashboard_id", referencedColumnName = "id", nullable = false)
	private Personas myDashboardPersona;
}
