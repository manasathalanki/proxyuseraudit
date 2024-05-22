package com.bh.cp.proxy.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name= "Fleet Data Request DTO", description = "Fleet data request DTO for fleetsdata service input.")
public class FleetRequestDTO {

	private String vid;

}
