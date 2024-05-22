package com.bh.cp.user.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegesResponseDTO implements Serializable {

	private static final long serialVersionUID = 5271029532037083969L;

	private String id;

	private String name;

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PrivilegesResponseDTO privilegesResponseDTO && this.id.equals(privilegesResponseDTO.id)
				&& this.name.equals(privilegesResponseDTO.name);
	}

	@Override
	public int hashCode() {
		return (this.id.hashCode() + this.name.hashCode());
	}

}
