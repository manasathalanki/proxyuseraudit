package com.bh.cp.user.pojo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role implements Serializable {

	private static final long serialVersionUID = 2098115566718568044L;

	private String id;

	private String name;

	boolean clientRole;

	private List<String> privileages;

	
}
