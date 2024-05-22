package com.bh.cp.user.dto.body;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleOpBody extends RoleNameBody implements Serializable {

	private static final long serialVersionUID = 5895180086508896757L;

	private String op;

	public RoleOpBody(String id, String name, String op) {
		super(id, name);
		this.op = op;
	}
}
