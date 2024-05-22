package com.bh.cp.proxy.aop;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.Executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class TaskPoolConfigurationTest {

	@InjectMocks
	private TaskPoolConfiguration taskPoolConfiguration;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(taskPoolConfiguration, "corePoolSize", 50);
		ReflectionTestUtils.setField(taskPoolConfiguration, "maxPoolSize", 1000);
		ReflectionTestUtils.setField(taskPoolConfiguration, "queueCapacity", 1000);
	}

	@Test
	void testGetAsyncExecutor() {
		Executor executorObj = taskPoolConfiguration.getAsyncExecutor();
		assertNotNull(executorObj);
	}
}
