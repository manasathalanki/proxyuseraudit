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
public class Users implements Serializable {

	private static final long serialVersionUID = 6133186403153935523L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
	@SequenceGenerator(sequenceName = "users_id_seq", name = "users_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(length = 255, name = "email")
	private String email;

	@Column(length = 100, nullable = false, name = "sso", unique = true)
	private String sso;

	@Column(length = 255)
	private String username;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "persona_id", referencedColumnName = "id")
	private Personas personas;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	private Companies companies;
	
	@Column(length = 255, name = "time_zone")
	private String timeZone;
	
	@Column(length = 255, name = "displayed_asset_name")
	private String displayAssetName;
	
	@Column(length = 255, name = "retired_assets")
	private String retiredAssets;
	
	@Column(length = 255, name = "uom")
	private String uom;
}
