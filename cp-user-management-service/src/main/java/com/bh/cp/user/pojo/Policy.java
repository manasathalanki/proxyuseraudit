package com.bh.cp.user.pojo;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Policy {

	String id;
	String name;
	String type;
	String description;
	List<LinkedHashMap<String, String>> roles;
}
