package com.bh.cp.audit.trail.util;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtUtil {

	public Map<String, Claim> getClaims(String key) {
		DecodedJWT jwt = JWT.decode(key);
		return jwt.getClaims();
	}

}
