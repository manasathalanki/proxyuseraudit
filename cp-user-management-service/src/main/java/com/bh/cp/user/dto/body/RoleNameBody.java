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
public class RoleNameBody extends IdBody implements Serializable {

	private static final long serialVersionUID = 9202559139674712827L;

	private String name;

	public RoleNameBody(String id, String name) {
		super(id);
		this.name = name;
	}

}
