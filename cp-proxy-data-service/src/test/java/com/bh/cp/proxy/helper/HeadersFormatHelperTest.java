package com.bh.cp.proxy.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.bh.cp.proxy.util.StringUtil;

class HeadersFormatHelperTest {

	@InjectMocks
	private HeadersFormatHelper headerFormatHelper;
	ServicesDirectory servicesDirectory;
	Map<String, Object> request;
	Map<String, String> replaceValues;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		request=new HashMap<>();
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
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - With Proper Output")
	void testFormat() {
		String expectedResult = StringUtil.replaceAll(servicesDirectory.getHeaders(),
				(Map<String, String>) request.get(ProxyConstants.REPLACE_VALUES));
		String actualResult = headerFormatHelper.format(servicesDirectory, request);
		assertEquals(expectedResult, actualResult);
	}

}
