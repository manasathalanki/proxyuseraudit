package com.bh.cp.user.dto.request;

import java.util.List;

import com.bh.cp.user.dto.body.AttributeBody;
import com.bh.cp.user.dto.body.NameBody;
import com.bh.cp.user.dto.body.OpBody;
import com.bh.cp.user.dto.body.RoleOpBody;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditUserRequestDTO {

	private String id;

	@JsonInclude(Include.NON_NULL)
	private String email;

	@JsonInclude(Include.NON_NULL)
	private NameBody name;

	private boolean active;

	@JsonInclude(Include.NON_NULL)
	private List<AttributeBody> attributes;

	@JsonInclude(Include.NON_NULL)
	private List<RoleOpBody> roles;

	@JsonInclude(Include.NON_NULL)
	private List<OpBody> groups;

	@JsonInclude(Include.NON_NULL)
	private List<OpBody> domains;
}
