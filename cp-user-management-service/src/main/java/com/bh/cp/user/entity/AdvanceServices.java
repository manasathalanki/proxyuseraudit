package com.bh.cp.user.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AdvanceServices implements Serializable {

	private static final long serialVersionUID = 4755617412472071325L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advance_services_id_seq")
	@SequenceGenerator(sequenceName = "advance_services_id_seq", name = "advance_services_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;
	
	private String serviceName;
}
