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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "asset_level", "persona_id" }),
		@UniqueConstraint(columnNames = { "asset_level", "sso", "is_default" }) })
public class Customizations implements Serializable {

	private static final long serialVersionUID = 165276319438439025L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customizations_seq")
	@SequenceGenerator(sequenceName = "customizations_seq", allocationSize = 1, name = "customizations_seq", initialValue = 1000)
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "persona_id", referencedColumnName = "id")
	private Personas personas;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sso", referencedColumnName = "sso")
	private Users users;

	@Column(nullable = false, length = 10, name = "asset_level")
	private String assetLevel;

	@Column(length = 10, name = "date_range")
	private String dateRange;

	@Column(name = "is_default", nullable = false)
	private boolean isDefault;

	@Column(name = "default_vid", nullable = true, length = 50)
	private String defaultVid;
	
	@Column(name = "is_reordered", nullable = true)
	private boolean isReordered;
}
