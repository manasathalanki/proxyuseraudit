package com.bh.cp.user.pojo;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;

@Data
public class Privileges {

	String decisionStrategy;
	String id;
	String logic;
	String name;
	List<LinkedHashMap<String, String>> roles;
	String type;
	String description;

}
