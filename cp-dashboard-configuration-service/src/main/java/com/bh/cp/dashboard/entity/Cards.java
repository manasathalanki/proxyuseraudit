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
@Getter
@Setter
public class Cards implements Serializable {

	private static final long serialVersionUID = 6965231819490895498L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cards_id_seq")
	@SequenceGenerator(sequenceName = "cards_id_seq", name = "cards_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 50)
	private String cardType;

	@Column(length = 100)
	private String description;
}
