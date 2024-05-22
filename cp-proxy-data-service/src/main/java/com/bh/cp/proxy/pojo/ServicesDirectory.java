package com.bh.cp.proxy.pojo;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicesDirectory {
	private int id;
	private String communicationFormat;
	private String headers;
	private String inputData;
	private String method;
	private String outputHandler;
	private String serviceType;
	private String uri;
	private Integer widgetId;
	private Set<ServicesDynamicParameters> dynamicParameters;
}
