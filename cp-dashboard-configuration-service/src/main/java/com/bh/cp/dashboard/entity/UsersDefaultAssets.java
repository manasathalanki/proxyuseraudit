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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "sso" }) })
public class UsersDefaultAssets implements Serializable {

	private static final long serialVersionUID = -3802087794467841825L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_default_assets_seq")
	@SequenceGenerator(sequenceName = "users_default_assets_seq", name = "users_default_assets_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Column(nullable = false, length = 50)
	private String defaultVid;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sso", referencedColumnName = "sso")
	private Users users;
}
