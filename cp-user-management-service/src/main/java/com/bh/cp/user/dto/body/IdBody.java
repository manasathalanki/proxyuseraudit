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
public class IdBody implements Serializable {

	private static final long serialVersionUID = 1326458654578805414L;
	
	private String id;
}
