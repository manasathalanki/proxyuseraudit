package com.bh.cp.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetAccessResponseDTO {

	private boolean applicability = true;

	private boolean enabled = true;

	private boolean hasAccess = true;

	private boolean activeServicesPersona = false;

}
