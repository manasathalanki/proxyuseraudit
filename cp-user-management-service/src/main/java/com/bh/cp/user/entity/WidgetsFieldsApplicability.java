package com.bh.cp.user.entity;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class WidgetsFieldsApplicability implements Serializable {

	private static final long serialVersionUID = -8788167647636652000L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "widgets_fields_applicability_seq")
	@SequenceGenerator(sequenceName = "widgets_fields_applicability_seq", allocationSize = 1, initialValue = 1000, name = "widgets_fields_applicability_seq")
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "widgets_id", referencedColumnName = "id")
	private Widgets widgets;

	@Column(name = "technology_code", length = 100)
	private String technologyCode;

	@Column(name = "equipment_code", length = 100)
	private String equipmentCode;

	@Column(name = "low_nox", length = 100)
	private String lowNox;
}
