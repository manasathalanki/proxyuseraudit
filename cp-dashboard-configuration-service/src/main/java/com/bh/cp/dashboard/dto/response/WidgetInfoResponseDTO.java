package com.bh.cp.dashboard.dto.response;

import com.bh.cp.dashboard.entity.WidgetsInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetInfoResponseDTO {

	private String title;

	private String description;

	private Integer infoImageId;

	private String footer;

	public WidgetInfoResponseDTO(WidgetsInfo widgetInfo) {
		this.title = widgetInfo.getTitle();
		this.description = widgetInfo.getDescription();
		this.infoImageId = widgetInfo.getInfoImage() != null ? widgetInfo.getInfoImage().getId() : null;
		this.footer = widgetInfo.getFooter();
	}

}
