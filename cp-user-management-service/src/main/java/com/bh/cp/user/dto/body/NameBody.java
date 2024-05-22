package com.bh.cp.user.dto.body;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameBody implements Serializable {

	private static final long serialVersionUID = -7647720293825278535L;

	private String firstName;

	private String lastName;
}
