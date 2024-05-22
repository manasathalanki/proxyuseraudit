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

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Personas implements Serializable {

	private static final long serialVersionUID = 8213653881865807736L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personas_id_seq")
	@SequenceGenerator(sequenceName = "personas_id_seq", name = "personas_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 255, nullable = false)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
	private Statuses statuses;
}