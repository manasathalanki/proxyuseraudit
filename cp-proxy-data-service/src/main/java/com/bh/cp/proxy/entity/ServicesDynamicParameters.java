package com.bh.cp.proxy.entity;

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
public class ServicesDynamicParameters implements Serializable {

	private static final long serialVersionUID = -4133784534446027112L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_dynamic_parameters_id_seq")
	@SequenceGenerator(sequenceName = "services_dynamic_parameters_id_seq", name = "services_dynamic_parameters_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
	private ServicesDirectory servicesDirectory;

	@Column(length = 100, nullable = false)
	private String field;

	@Column(length = 2000, nullable = false)
	private String inputData;
}
