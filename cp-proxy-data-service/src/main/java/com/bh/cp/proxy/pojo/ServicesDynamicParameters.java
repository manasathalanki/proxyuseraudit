package com.bh.cp.proxy.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServicesDynamicParameters {
	private Integer id;
	private String field;
	private String inputData;
}
