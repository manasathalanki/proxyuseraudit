package com.bh.cp.user.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDTO {

	private String field;

	private List<String> values;

	private Integer limit;

}
