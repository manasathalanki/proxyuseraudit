package com.bh.cp.proxy.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.bh.cp.proxy.pojo.ServicesDynamicParameters;
import com.bh.cp.proxy.util.StringUtil;

class InputFormatHelperTest {

	@InjectMocks
	private InputFormatHelper inputFormatHelper;
	ServicesDirectory servicesDirectory;
	Map<String, Object> request;
	Map<String, String> replaceValues;
	ServicesDynamicParameters servicesDynamicParameters;
	Set<ServicesDynamicParameters> servicesDynamicParametersSet;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		request = new HashMap<>();
		replaceValues = new HashMap<>();
		servicesDirectory = new ServicesDirectory();
		servicesDirectory.setInputData("{\"filterValues\":[<TEST_MACHINE_ID_CSV>]}");
		servicesDirectory.setMethod("GET");
		servicesDirectory.setId(2);
		servicesDirectory.setUri("http://test.com");
		servicesDirectory.setHeaders("Content-Type: application/json");
		servicesDirectory.setOutputHandler("com.bh.cp");
		replaceValues.put("<TEST_MACHINE_ID_CSV>", "MC_TEST");
		request.put(ProxyConstants.REPLACE_VALUES, replaceValues);
		servicesDynamicParameters = new ServicesDynamicParameters(2, "issueId", "issueId=<FILTER_VALUE>");
		servicesDynamicParametersSet = new HashSet<>();
		servicesDynamicParametersSet.add(servicesDynamicParameters);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - When Dynamic Parameters are Null")
	void testFormatWithDynamicParametersAsNull() {
		String expectedResult = StringUtil.replaceAll(servicesDirectory.getInputData(),
				(Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES));
		String actualResult = inputFormatHelper.format(servicesDirectory, request);
		assertEquals(expectedResult, actualResult);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - When Dynamic Parameters are Non Null")
	void testFormatWithDynamicParametersNotNull() {
		request.put(ProxyConstants.KEY_DYNAMIC_PRAMETERS, "?issueId=123456");
		servicesDirectory.setInputData(ProxyConstants.KEY_DYNAMIC_PRAMETERS);
		servicesDirectory.setDynamicParameters(servicesDynamicParametersSet);
		servicesDirectory.setInputData(servicesDirectory.getInputData().replaceAll(ProxyConstants.KEY_DYNAMIC_PRAMETERS,
				(String) request.get(ProxyConstants.KEY_DYNAMIC_PRAMETERS)));
		replaceValues.put(ProxyConstants.KEY_DYNAMIC_PRAMETERS, "?issueId=123456");
		String expectedResult = StringUtil.replaceAll(servicesDirectory.getInputData(),
				(Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES));
		String actualResult = inputFormatHelper.format(servicesDirectory, request);
		assertEquals(expectedResult, actualResult);
	}

}
