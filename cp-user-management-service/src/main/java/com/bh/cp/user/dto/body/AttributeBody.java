package com.bh.cp.user.dto.body;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeBody implements Serializable {

	private static final long serialVersionUID = -8715096132578666942L;

	private String key;

	private List<String> value;

}