package com.bh.cp.proxy.helper;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class WidgetSubscriptionCheckHelper {

	private UMSClientService umsClientService;

	private Integer maintenanceOptimizerWidgetId;

	private Integer healthIndexWidgetId;

	private String healthIndexPrivilege;

	private String maintainanceOptimizerPrivilege;

	public WidgetSubscriptionCheckHelper(@Autowired UMSClientService umsClientService,
			@Value("${maintenance.optimizer.widget-id}") Integer maintenanceOptimizerWidgetId,
			@Value("${health.index.widget-id}") Integer healthIndexWidgetId,
			@Value("${healthIndex.privilege-name}") String healthIndexPrivilege,
			@Value("${maintainanceOptimizer.privilege-name}") String maintainanceOptimizerPrivilege) {
		this.umsClientService = umsClientService;
		this.healthIndexPrivilege = healthIndexPrivilege;
		this.maintainanceOptimizerPrivilege = maintainanceOptimizerPrivilege;
		this.maintenanceOptimizerWidgetId = maintenanceOptimizerWidgetId;
		this.healthIndexWidgetId = healthIndexWidgetId;
	}

	public Boolean checkAdvanceServicePrivilegeAccess(HttpServletRequest httpServletRequest, String vid)
			throws JsonProcessingException {
		List<String> privileagesList = umsClientService.getUserPrivileges(httpServletRequest);
		Boolean criticalityFlag = privileagesList.contains(healthIndexPrivilege)
				|| privileagesList.contains(maintainanceOptimizerPrivilege);
		Map<String, Boolean> moAccess = umsClientService.getWidgetAdvanceServicesAccess(httpServletRequest, vid,
				healthIndexWidgetId);
		Map<String, Boolean> hiAccess = umsClientService.getWidgetAdvanceServicesAccess(httpServletRequest, vid,
				maintenanceOptimizerWidgetId);
		return (Boolean.TRUE.equals(criticalityFlag) && (moAccess.get(JSONUtilConstants.ENABLEDSERVICES)
				|| hiAccess.get(JSONUtilConstants.ENABLEDSERVICES)));
	}
}
