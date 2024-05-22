package com.bh.cp.dashboard.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.bh.cp.dashboard.dto.request.LayoutRequestDTO;
import com.bh.cp.dashboard.dto.response.LayoutResponseDTO;
import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.CustomizationsAssets;
import com.bh.cp.dashboard.entity.CustomizationsWidgets;
import com.bh.cp.dashboard.entity.Users;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;

public interface DashboardService {

	public LayoutResponseDTO getUserDashboardLayout(HttpServletRequest httpServletRequest, LayoutRequestDTO requestDto)
			throws IOException, NotFoundException;

	public Map<String, Object> getAccessibleVidsAndLevel(List<Map<String, Object>> filteredHierarchy, String vid,
			boolean showSibilings) throws NotFoundException;

	public Customizations getCustomizationsForView(Users user, String level) throws NotFoundException;

	public Customizations getCustomizationsForModify(HttpServletRequest httpServletRequest, Integer customizationId,
			String level) throws NotFoundException;

	public List<CustomizationsAssets> getCustomizedAssets(Customizations customization);

	public void setAssetResponse(Object responseDTO, List<Map<String, Object>> filteredHierarchy,
			Customizations customization, List<CustomizationsAssets> customizedAssetsList, List<String> accessibleVids,
			boolean fetchAllAssets) throws JsonProcessingException;

	public void saveCustomizationsWidgets(List<CustomizationsWidgets> widgetsCustomizations,
			Customizations savedCustomization);

	public void saveCustomizationsAssets(List<CustomizationsAssets> existingCustomizations,
			Customizations newCustomization);

}
