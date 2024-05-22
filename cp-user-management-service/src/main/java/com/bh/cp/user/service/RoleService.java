package com.bh.cp.user.service;

import java.util.List;

import javax.management.relation.InvalidRoleInfoException;

import org.springframework.http.ResponseEntity;

import com.bh.cp.user.dto.response.RoleResponseDTO;
import com.bh.cp.user.exception.RoleException;
import com.bh.cp.user.pojo.Role;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

public interface RoleService {

	public List<RoleResponseDTO> getAllRoles(HttpServletRequest httpServletRequest) throws JsonProcessingException;

	public RoleResponseDTO getPrivilegesForRole(HttpServletRequest httpServletRequest, String roleId);

	public Role createRoleAssociatePrivileges(Role role) throws RoleException, JsonProcessingException;

	public ResponseEntity<Object> updateRoleAssociatePrivileges(Role role) throws RoleException, JsonProcessingException;

	public ResponseEntity<Object> deleteRole(String roleId) throws InvalidRoleInfoException, RoleException;

}
