package com.bh.cp.proxy.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Device Data Request DTO", description = "Device data request DTO for fleetsdata service input.")

public class DeviceDataRequestDTO {

	@NotNull
	private Integer serviceId;

	private String plantId;

	private String deviceId;

}
