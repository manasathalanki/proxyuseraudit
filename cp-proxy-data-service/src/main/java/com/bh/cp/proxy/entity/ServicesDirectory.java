package com.bh.cp.proxy.entity;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ServicesDirectory implements Serializable {

	private static final long serialVersionUID = 2681545064146017160L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_directory_id_seq")
	@SequenceGenerator(sequenceName = "services_directory_id_seq", name = "services_directory_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 10, nullable = false, name = "service_type")
	private String servicetype;

	@Column(length = 10, nullable = false, name = "communication_format")
	private String communicationFormat;

	@Column(length = 1000, nullable = true)
	private String uri;

	@Column(length = 10)
	private String method;

	@Column(length = 2000)
	private String headers;

	@Column(length = 2000)
	private String inputData;

	@Column(length = 255, name = "output_handler")
	private String outputHandler;

	@Column(length = 255, name = "widget_id")
	private Integer widgetId;

	@Column(name = "is_paid_service")
	private Boolean isPaidService;

	@OneToMany(mappedBy = "servicesDirectory", fetch = FetchType.EAGER)
	private Set<ServicesDynamicParameters> dynamicParameters;
}