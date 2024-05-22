package com.bh.cp.user.dto.response;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO implements Serializable {

	private static final long serialVersionUID = 8930637458398644195L;

	private String id;

	private String name;

	private String email;

	private String title;

	private List<String> privileges;
	
	private String firstName;
	
	private String lastName;
}
