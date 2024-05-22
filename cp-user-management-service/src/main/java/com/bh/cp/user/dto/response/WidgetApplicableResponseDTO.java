package com.bh.cp.user.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetApplicableResponseDTO {

	private List<String> machines;

	public WidgetApplicableResponseDTO(List<String> machines) {
		this.machines = machines;
	}

}
