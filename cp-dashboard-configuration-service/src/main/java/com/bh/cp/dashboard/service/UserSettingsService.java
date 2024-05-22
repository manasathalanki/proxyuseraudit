package com.bh.cp.dashboard.service;

import com.bh.cp.dashboard.dto.request.UserSettingsRequestDTO;
import com.bh.cp.dashboard.dto.response.UserSettingsResponseDTO;
import com.bh.cp.dashboard.entity.Settings;
import com.bh.cp.dashboard.entity.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

public interface UserSettingsService {

	public Settings getDefaultSettings() throws NotFoundException;

	public Users extractSsoAndReturnUser(HttpServletRequest httpServletRequest) throws NotFoundException;

	public UserSettingsResponseDTO getUserSettings(HttpServletRequest httpServletRequest) throws NotFoundException;

	public UserSettingsResponseDTO updateUserSettings(HttpServletRequest httpServletRequest,
			UserSettingsRequestDTO settingsRequestDTO) throws NotFoundException;

	public void updateToMyDashboard(Users user) throws NotFoundException;

}
