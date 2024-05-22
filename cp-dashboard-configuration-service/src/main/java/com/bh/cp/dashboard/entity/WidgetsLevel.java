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
public class WidgetsLevel implements Serializable {

	private static final long serialVersionUID = 6189546858012613125L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "widgets_level_id_seq")
	@SequenceGenerator(sequenceName = "widgets_level_id_seq", name = "widgets_level_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 100, nullable = false, name = "asset_level")
	private String assetLevel;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "widget_id", referencedColumnName = "id")
	private Widgets widgets;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "icon_image_id", referencedColumnName = "id")
	private CommonBlobs iconImage;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "static_image_id", referencedColumnName = "id")
	private CommonBlobs staticImage;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "greyed_image_id", referencedColumnName = "id")
	private CommonBlobs greyedImage;

}