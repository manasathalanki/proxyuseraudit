package com.bh.cp.user.dto.response;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OktaUserDetailsResponseDTO implements Serializable {

	private static final long serialVersionUID = 2558941132418319193L;

	@JsonInclude(Include.NON_NULL)
	private String id;

	@JsonInclude(Include.NON_NULL)
	private String status;

	@JsonInclude(Include.NON_NULL)
	private Profile profile;

	@JsonInclude(Include.NON_NULL)
	private UserDetailsResponseDTO auth;

	@Getter
	private class Profile implements Serializable {

		private static final long serialVersionUID = -3583175145801029583L;

		private String firstName;

		private String lastName;

		private String login;

		private String email;

		private String samAccountName;
		
		private String uid;

		public Profile(Map<String, Object> profileMap) {
			this.firstName = (String) profileMap.getOrDefault("firstName",null);
			this.lastName = (String) profileMap.getOrDefault("lastName",null);
			this.login = (String) profileMap.getOrDefault("login",null);
			this.email = (String) profileMap.getOrDefault("email",null);
			this.samAccountName = (String) profileMap.getOrDefault("samAccountName", null);
			this.uid = (String) profileMap.getOrDefault("uid", null);
		}
	}

	public void setProfile(Map<String, Object> profileMap) {
		this.profile = new Profile(profileMap);
	}

}
