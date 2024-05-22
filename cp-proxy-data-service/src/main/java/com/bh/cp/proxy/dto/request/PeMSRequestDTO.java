package com.bh.cp.proxy.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PeMSRequestDTO {

	private Integer serviceId;

	private List<String> assetIdList;

	private List<String> vidList;

	private String from;

	private String to;

	private String sampling;

	private String mode;

}
