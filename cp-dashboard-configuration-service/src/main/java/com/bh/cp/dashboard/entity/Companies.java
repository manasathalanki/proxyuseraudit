package com.bh.cp.dashboard.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class Companies implements Serializable {

	private static final long serialVersionUID = -4611878694582044028L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "companies_id_seq")
	@SequenceGenerator(sequenceName = "companies_id_seq", name = "companies_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 255, nullable = false)
	private String name;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "icon_image_id", referencedColumnName = "id", nullable = true)
	private CommonBlobs iconImages;

}
