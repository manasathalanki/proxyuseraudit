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
public class CustomizationsAssets extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -1494461989993493699L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customizations_assets_seq")
	@SequenceGenerator(sequenceName = "customizations_assets_seq", name = "customizations_assets_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(nullable = false, name = "order_number")
	private Integer orderNumber;

	@Column(nullable = false, length = 50)
	private String vid;

	@Column(nullable = true, length = 100, name = "asset_name")
	private String assetName;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "customization_id", referencedColumnName = "id", nullable = false)
	private Customizations customizations;
}
