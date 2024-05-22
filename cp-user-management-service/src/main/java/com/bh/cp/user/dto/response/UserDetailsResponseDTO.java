package com.bh.cp.user.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.bh.cp.user.dto.body.AttributeBody;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsResponseDTO implements Serializable {

	private static final long serialVersionUID = 2558941132418319193L;

	@JsonInclude(Include.NON_NULL)
	private String id;

	@JsonInclude(Include.NON_NULL)
	private String name;

	@JsonInclude(Include.NON_NULL)
	private String email;

	@JsonInclude(Include.NON_NULL)
	private String userName;

	@JsonInclude(Include.NON_NULL)
	private String surName;

	@JsonInclude(Include.NON_NULL)
	private String enabled;

	@JsonInclude(Include.NON_NULL)
	private String title;

	@JsonInclude(Include.NON_NULL)
	private String status;

	@JsonInclude(Include.NON_NULL)
	private List<RoleResponseDTO> roles;

	@JsonInclude(Include.NON_NULL)
	private List<AttributeBody> attributes;

	@JsonInclude(Include.NON_NULL)
	private Set<PrivilegesResponseDTO> privileges;

	@JsonInclude(Include.NON_NULL)
	private List<GroupResponseDTO> groups;

	@JsonInclude(Include.NON_NULL)
	private List<DomainResponseDTO> domains;

	public UserDetailsResponseDTO(String id, String name, String email) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
	}

}
