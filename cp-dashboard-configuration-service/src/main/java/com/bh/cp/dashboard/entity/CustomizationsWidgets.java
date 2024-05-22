package com.bh.cp.dashboard.entity;

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
import lombok.Setter;

@Entity
@Setter
@Getter
public class CustomizationsWidgets extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -4309805506037753556L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customizations_widgets_seq")
	@SequenceGenerator(sequenceName = "customizations_widgets_seq", name = "customizations_widgets_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "customization_id", referencedColumnName = "id", nullable = false)
	private Customizations customizations;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "widget_id", referencedColumnName = "id")
	private Widgets widgets;

	@Column(nullable = false, name = "order_number")
	private Integer orderNumber;

}