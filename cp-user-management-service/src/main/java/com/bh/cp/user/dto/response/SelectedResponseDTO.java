package com.bh.cp.user.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SelectedResponseDTO implements Serializable {

	private static final long serialVersionUID = 6832461738885229065L;

	@JsonInclude(Include.NON_NULL)
	private String id;

	@JsonInclude(Include.NON_NULL)
	private String name;

	@JsonInclude(Include.NON_NULL)
	private String email;

	private boolean selected;

	private boolean editable = true;

	public SelectedResponseDTO(String id, String name, boolean selected, boolean editable) {
		super();
		this.id = id;
		this.name = name;
		this.selected = selected;
		this.editable = editable;
	}

	public SelectedResponseDTO(String id, String name, String email, boolean selected, boolean editable) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.selected = selected;
		this.editable = editable;
	}

}
