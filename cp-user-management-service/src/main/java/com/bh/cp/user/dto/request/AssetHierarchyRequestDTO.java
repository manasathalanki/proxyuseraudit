package com.bh.cp.user.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetHierarchyRequestDTO {

	private String vid;

	@JsonInclude(Include.NON_NULL)
	private String field;

}
