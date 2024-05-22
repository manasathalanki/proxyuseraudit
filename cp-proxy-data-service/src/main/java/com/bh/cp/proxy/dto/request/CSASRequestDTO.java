package com.bh.cp.proxy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CSASRequestDTO {

	@NotNull
	private Integer serviceId;

	private String plantId;

	private String dateRange;

	private String fromDate;

	private String toDate;

	private String lineupId;

	private String issueId;

	private String tokenQuantity;
}
