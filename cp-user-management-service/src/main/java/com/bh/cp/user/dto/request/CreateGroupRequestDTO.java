package com.bh.cp.user.dto.request;

import java.util.List;

import com.bh.cp.user.dto.body.AttributeBody;
import com.bh.cp.user.dto.body.IdBody;
import com.bh.cp.user.dto.body.RoleNameBody;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequestDTO {

	@JsonInclude(Include.NON_NULL)
	private String bulkId;

	private String displayName;

	private String source;

	@JsonInclude(Include.NON_NULL)
	private List<AttributeBody> attributes;

	private List<RoleNameBody> roles;

	@JsonInclude(Include.NON_NULL)
	private List<IdBody> members;

	private List<IdBody> domains;
}
