package com.bh.cp.dashboard.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Statuses implements Serializable {

	private static final long serialVersionUID = 3191477598617761611L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statuses_id_seq")
	@SequenceGenerator(sequenceName = "statuses_id_seq", name = "statuses_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 50)
	private String statusType;

	@Column(length = 100)
	private String description;

	@Column(name = "status_indicator", unique = true)
	private Integer statusIndicator;
}
