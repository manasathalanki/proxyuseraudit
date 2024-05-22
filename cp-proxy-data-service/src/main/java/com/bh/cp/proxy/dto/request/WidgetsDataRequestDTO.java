package com.bh.cp.proxy.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name= "Widgets Data Request DTO", description = "Widgets data request DTO for widgetdata service input.")
public class WidgetsDataRequestDTO {

	private String level;
	
	private String vid;
	
	private Integer widgetId;

	private String dateRange;

}
