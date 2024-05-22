package com.bh.cp.audit.trail.entity;

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
public class Companies implements Serializable {

	private static final long serialVersionUID = -4611878694582044028L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "companies_id_seq")
	@SequenceGenerator(sequenceName = "companies_id_seq", name = "companies_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 255, nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "icon_image_id", referencedColumnName = "id")
	private CommonBlobs iconImages;

}
