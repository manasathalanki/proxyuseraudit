package com.bh.cp.user.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Roles {
	String id;
	String name;
	boolean clientRole;

	@JsonIgnore
	List<String> privileages;

}
