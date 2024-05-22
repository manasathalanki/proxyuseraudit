package com.bh.cp.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WidgetsResponseDTO {

	private Integer widgetId;

	private Integer customizationId;

	private String title;

	private Integer orderNumber;

	private boolean isChecked;

	private boolean isHasAccess;

	private boolean isPaidService;

	private Integer staticImageId;

	private Integer greyedImageId;

	private String description;

	private String lockIconMessage;

	private String footer;

	private WidgetInfoResponseDTO info;

}
