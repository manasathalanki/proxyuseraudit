package com.bh.cp.user.dto.response;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleResponseDTO implements Serializable {

	private static final long serialVersionUID = -5024736152599093548L;

	private String id;

	private String name;

	private boolean editable = true;

	@JsonInclude(Include.NON_NULL)
	private Set<PrivilegesResponseDTO> privileges;

	public RoleResponseDTO(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public RoleResponseDTO(String id, String name, boolean editable) {
		this(id, name, editable, null);
	}

	public RoleResponseDTO(String id, String name, Set<PrivilegesResponseDTO> privileges) {
		this(id, name, true, privileges);
	}

	public RoleResponseDTO(String id, String name, boolean editable, Set<PrivilegesResponseDTO> privileges) {
		super();
		this.id = id;
		this.name = name;
		this.editable = editable;
		this.privileges = privileges;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RoleResponseDTO roleResponseDTO && this.id.equals(roleResponseDTO.id)
				&& this.name.equals(roleResponseDTO.name);
	}

	@Override
	public int hashCode() {
		return (this.id.hashCode() + this.name.hashCode());
	}
}
