package com.bh.cp.dashboard.entity;

import java.io.Serializable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class CommonBlobs implements Serializable {

	private static final long serialVersionUID = 3870140443975619283L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "common_blobs_id_seq")
	@SequenceGenerator(sequenceName = "common_blobs_id_seq", name = "common_blobs_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@Lob
	@JdbcTypeCode(SqlTypes.BINARY)
	private byte[] material;

	private String extension;
}
