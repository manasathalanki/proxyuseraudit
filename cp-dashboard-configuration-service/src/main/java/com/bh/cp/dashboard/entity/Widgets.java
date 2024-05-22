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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Widgets implements Serializable {

	private static final long serialVersionUID = -7479290839611360670L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "widgets_id_seq")
	@SequenceGenerator(sequenceName = "widgets_id_seq", name = "widgets_id_seq", allocationSize = 1, initialValue = 1000)
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "widget_type_id", referencedColumnName = "id", nullable = false)
	private WidgetTypes widgetTypes;

	@Column(length = 1000, nullable = false)
	private String description;

	@Column(length = 255, nullable = false)
	private String title;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "service_id", referencedColumnName = "id", nullable = true)
	private ServicesDirectory servicesDirectory;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "card_id", referencedColumnName = "id", nullable = false)
	private Cards cards;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "widgets_info_id", referencedColumnName = "id", nullable = true)
	private WidgetsInfo info;

	@Column(length = 1000, nullable = true, name = "lock_icon_message")
	private String lockIconMessage;

	@Column(length = 1000, nullable = true, name = "details_uri")
	private String detailsUri;

	@Column(nullable = true, name = "is_applicability_check_required")
	private boolean isApplicabilityCheckRequired;

	@Column(nullable = false, name = "is_paid_service")
	private boolean isPaidService;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
	private Statuses statuses;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "advance_service_id", referencedColumnName = "id", nullable = true)
	private AdvanceServices advanceServices;

	@Column(name = "idm_privilege")
	private String idmPrivilege;

	@Column(length = 1000)
	private String footer;

	@Column(name = "is_static")
	private boolean isStatic;

	@Column(name = "is_timeline")
	private boolean isTimeline;

	@Column(name = "order_number", nullable = true)
	private int orderNumber;

	@Column(name = "is_pinned_widget")
	private boolean isPinnedWidget;

	@Column(name = "width_span")
	private Integer widthSpan;

}
