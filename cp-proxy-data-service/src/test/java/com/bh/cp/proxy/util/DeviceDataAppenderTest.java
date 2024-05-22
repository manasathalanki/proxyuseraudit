package com.bh.cp.proxy.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.bh.cp.proxy.dto.request.DeviceDataRequestDTO;

class DeviceDataAppenderTest {

	@InjectMocks
	private DeviceDataAppender handler;

	DeviceDataRequestDTO dataRequest;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		dataRequest = new DeviceDataRequestDTO();

		dataRequest.setPlantId("ABENGOA");
		dataRequest.setDeviceId("48");

	}

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for device not updated count")
	void appendData() throws Exception {
		Map<String, Object> widgetsDataRequest = new HashMap<>();
		widgetsDataRequest = new HashMap<>();

		widgetsDataRequest.put("serviceId", "64");

		Map<String, Object> result = handler.appendRequestData(dataRequest, widgetsDataRequest);
		assertNotNull(result);
	}

}
