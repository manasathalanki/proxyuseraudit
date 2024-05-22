package com.bh.cp.proxy.helper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.bh.cp.proxy.util.StringUtil;

@Component
public class HeadersFormatHelper {
	@SuppressWarnings("unchecked")
	public String format(ServicesDirectory service, Map<String, Object> request) {
		return StringUtil.replaceAll(service.getHeaders(),
				(Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES));
	}
}
