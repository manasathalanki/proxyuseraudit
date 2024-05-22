package com.bh.cp.proxy.helper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.bh.cp.proxy.util.StringUtil;

@Component
public class InputFormatHelper {
	@SuppressWarnings("unchecked")
	public String format(ServicesDirectory service, Map<String, Object> request) {
		if (request.get(ProxyConstants.KEY_DYNAMIC_PRAMETERS) != null) {
			service.setInputData(service.getInputData().replaceAll(ProxyConstants.KEY_DYNAMIC_PRAMETERS,
					(String) request.get(ProxyConstants.KEY_DYNAMIC_PRAMETERS)));
		}
		return StringUtil.replaceAll(service.getInputData(),
				(Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES));
	}
}
