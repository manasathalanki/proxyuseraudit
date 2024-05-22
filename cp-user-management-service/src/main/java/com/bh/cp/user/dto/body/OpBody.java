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
public class OpBody extends IdBody implements Serializable {

	private static final long serialVersionUID = 813291620399057353L;

	private String op;

	public OpBody(String id, String op) {
		super(id);
		this.op = op;
	}

}
