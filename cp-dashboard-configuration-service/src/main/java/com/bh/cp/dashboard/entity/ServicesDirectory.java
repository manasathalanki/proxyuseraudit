package com.bh.cp.dashboard.entity;

import java.io.Serializable;

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
public class ServicesDirectory implements Serializable {

	private static final long serialVersionUID = -7474013741212180258L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_directory_id_seq")
	@SequenceGenerator(sequenceName = "services_directory_id_seq", name = "services_directory_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

}