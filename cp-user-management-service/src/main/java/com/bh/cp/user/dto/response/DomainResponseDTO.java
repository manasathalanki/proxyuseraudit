package com.bh.cp.user.dto.response;

import java.io.Serializable;
import java.util.List;

import com.bh.cp.user.dto.body.AttributeBody;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DomainResponseDTO implements Serializable {

	private static final long serialVersionUID = 4287138232373811972L;

	private String id;

	private String name;

	@JsonIgnore
	private List<AttributeBody> attributes;

	private Boolean editable = Boolean.TRUE;

	public DomainResponseDTO(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof DomainResponseDTO domain && this.id.equals(domain.id) && this.name.equals(domain.name);
	}

	@Override
	public int hashCode() {
		return (this.id.hashCode() + this.name.hashCode());
	}

}
