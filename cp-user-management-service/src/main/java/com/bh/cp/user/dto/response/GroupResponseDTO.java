package com.bh.cp.user.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.bh.cp.user.dto.body.AttributeBody;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponseDTO implements Serializable {

	private static final long serialVersionUID = 396318961377790160L;

	private String id;

	private String name;

	@JsonInclude(Include.NON_NULL)
	private List<AttributeBody> attributes;

	@JsonInclude(Include.NON_NULL)
	private List<RoleResponseDTO> roles;

	@JsonInclude(Include.NON_NULL)
	private List<UserDetailsResponseDTO> users;

	@JsonInclude(Include.NON_NULL)
	private List<DomainResponseDTO> domains;

	@JsonInclude(Include.NON_NULL)
	private Set<PrivilegesResponseDTO> privileges;

	public GroupResponseDTO(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public GroupResponseDTO(String id, String name, List<RoleResponseDTO> roles, List<DomainResponseDTO> domains) {
		super();
		this.id = id;
		this.name = name;
		this.roles = roles;
		this.domains = domains;
	}
}
