package com.bh.cp.proxy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WidgetApplicableRequestDTO {

	private String vid;

	private Integer widgetId;

	private String field;
}
