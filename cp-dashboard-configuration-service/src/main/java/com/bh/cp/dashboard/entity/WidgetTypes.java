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
public class WidgetTypes implements Serializable {

	private static final long serialVersionUID = 942542371943164125L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "widget_types_id_seq")
	@SequenceGenerator(sequenceName = "widget_types_id_seq", name = "widget_types_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 50, nullable = false)
	private String description;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
	private Statuses statuses;
}
