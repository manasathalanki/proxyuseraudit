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
public class WidgetsInfo implements Serializable {

	private static final long serialVersionUID = -222210750775505809L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "widgets_info_id_seq")
	@SequenceGenerator(sequenceName = "widgets_info_id_seq", name = "widgets_info_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 255, nullable = true)
	private String title;

	@Column(length = 1000, nullable = true)
	private String description;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "info_image_id", referencedColumnName = "id", nullable = true)
	private CommonBlobs infoImage;

	@Column(length = 255, nullable = true)
	private String footer;

}
