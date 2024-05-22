package com.bh.cp.user.pojo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Privilege {

	String clientId;
	List<Policy> policies;

}
