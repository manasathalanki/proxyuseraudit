package com.bh.cp.dashboard.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.qos.logback.classic.spi.ILoggingEvent;

class LogMaskConverterTest {

	@InjectMocks
	private LogMaskConverter logMaskConverter;

	@Mock
	private ILoggingEvent iLoggingEvent;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("Test transform1 -- Bearer Token")
	void testTransform_Positive1() {
		assertEquals("'Bearer ****'", logMaskConverter.transform(iLoggingEvent, "'Bearer abcd'"));
	}

	@Test
	@DisplayName("Test transform2 -- Asset Hierarchy")
	void testTransform_Positive2() {
		assertEquals("'Asset Hierarchy of current User'",
				logMaskConverter.transform(iLoggingEvent, "'[{\"level\":\"projects\"}]'"));
	}

	@Test
	@DisplayName("Test transform3 -- Random Text")
	void testTransform_Negative1() {
		assertEquals("Random Text", logMaskConverter.transform(iLoggingEvent, "Random Text"));
	}

}
