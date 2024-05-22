package com.bh.cp.user.dto.request;

import java.util.List;

import com.bh.cp.user.dto.body.AttributeBody;
import com.bh.cp.user.dto.body.IdBody;
import com.bh.cp.user.dto.body.NameBody;
import com.bh.cp.user.dto.body.RoleNameBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDTO {
	
	private String bulkId;
	private String email;
	private boolean active;
	private String userName;
	private NameBody name;
	private List<AttributeBody> attributes;
	private List<RoleNameBody> roles;
	private List<IdBody> groups;
	private List<IdBody> domains;
}
