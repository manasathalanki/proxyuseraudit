package com.bh.cp.dashboard.dto.response;

import com.bh.cp.dashboard.entity.CustomizationsWidgets;
import com.bh.cp.dashboard.entity.Widgets;
import com.bh.cp.dashboard.entity.WidgetsLevel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayoutWidgetResponseDTO {

	private Integer id;

	private String title;

	private String description;

	private String detailsUri;

	private Integer iconImageId;

	private Integer staticImageId;

	private Integer greyImageId;

	private WidgetInfoResponseDTO info;

	private String lockIconMessage;

	private boolean isPaidService;

	private Integer orderNumber;

	private Integer widgetCustomizationId;

	private String footer;

	private boolean isStatic;

	private boolean isTimeline;

	private boolean isPinnedWidget;

	private Integer widthSpan;

	public LayoutWidgetResponseDTO(CustomizationsWidgets customizationsWidgets, WidgetsLevel widgetLevel) {
		this(customizationsWidgets.getWidgets(), widgetLevel);
		this.widgetCustomizationId = customizationsWidgets.getId();
		this.orderNumber = customizationsWidgets.getOrderNumber();
	}

	public LayoutWidgetResponseDTO(Widgets widget, WidgetsLevel widgetLevel) {
		this.id = widget.getId();
		this.title = widget.getTitle();
		this.description = widget.getDescription();
		this.detailsUri = widget.getDetailsUri();
		this.info = widget.getInfo() != null ? new WidgetInfoResponseDTO(widget.getInfo()) : null;
		this.isPaidService = widget.isPaidService();
		this.lockIconMessage = widget.getLockIconMessage();
		this.footer = widget.getFooter();
		this.isStatic = widget.isStatic();
		this.isTimeline = widget.isTimeline();
		this.isPinnedWidget = widget.isPinnedWidget();
		this.widthSpan = widget.getWidthSpan();
		if (widgetLevel != null) {
			this.iconImageId = widgetLevel.getIconImage() != null ? widgetLevel.getIconImage().getId() : null;
			this.staticImageId = widgetLevel.getStaticImage() != null ? widgetLevel.getStaticImage().getId() : null;
			this.greyImageId = widgetLevel.getGreyedImage() != null ? widgetLevel.getGreyedImage().getId() : null;
		}
	}

	public String toString() {
		return "(id=" + this.getId() + ",title=" + this.title + ",widthSpan=" + this.widthSpan + ",orderNumber=" + this.orderNumber + ")";
	}
}
