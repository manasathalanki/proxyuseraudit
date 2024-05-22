package com.bh.cp.user.entity;

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
public class Widgets implements Serializable {

	private static final long serialVersionUID = -7479290839611360670L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "widgets_id_seq")
	@SequenceGenerator(sequenceName = "widgets_id_seq", name = "widgets_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "widget_type_id", referencedColumnName = "id", nullable = false)
	private WidgetTypes widgetTypes;

	@Column(length = 255, nullable = false)
	private String title;

	@Column(nullable = false, name = "is_paid_service")
	private boolean isPaidService;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
	private Statuses statuses;

	@Column(name = "idm_privilege")
	private String idmPrivilege;

	public boolean isPaidService() {
		return isPaidService;
	}

	public void setPaidService(boolean isPaidService) {
		this.isPaidService = isPaidService;
	}

}
