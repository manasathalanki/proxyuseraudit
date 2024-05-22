package com.bh.cp.user.service;

import java.util.List;

import com.bh.cp.user.pojo.PolicyResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface PrivilegeService {

	public List<PolicyResponse> getAllPrivilege(HttpServletRequest httpServletRequest);

	public String getAllPrivileges(HttpServletRequest httpServletRequest);
}
