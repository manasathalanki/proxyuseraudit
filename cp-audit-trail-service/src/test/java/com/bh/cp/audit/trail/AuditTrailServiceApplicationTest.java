package com.bh.cp.audit.trail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bh.ip.ss.adapter.config.AdapterProperties;

import io.micrometer.observation.ObservationRegistry;
import net.minidev.json.parser.ParseException;

class AuditTrailServiceApplicationTest {

	private AuditTrailServiceApplication auditTrailServiceApplication;

	@BeforeEach
	void setUp() throws Exception {
		auditTrailServiceApplication = new AuditTrailServiceApplication();
		ReflectionTestUtils.setField(auditTrailServiceApplication, "authServerUrl", "https://test.com/auth");
		ReflectionTestUtils.setField(auditTrailServiceApplication, "clientId", "abcd");
		ReflectionTestUtils.setField(auditTrailServiceApplication, "dfscUrl", "https://test.com/v1/filterAttributes");
		ReflectionTestUtils.setField(auditTrailServiceApplication, "urlExclusionList", "/test/**");
		ReflectionTestUtils.setField(auditTrailServiceApplication, "tenantInfo",
				"{\"test-relam\":{\"client_secret\":\"abcd\"}}");
		ReflectionTestUtils.setField(auditTrailServiceApplication, "pathsFileLocation", "test_paths.json");
		ReflectionTestUtils.setField(auditTrailServiceApplication, "rbacSupport", "disabled");
		ReflectionTestUtils.setField(auditTrailServiceApplication, "cacheTTL", "0");
		ReflectionTestUtils.setField(auditTrailServiceApplication, "corsOriginUrls", "https://test.com");
		ReflectionTestUtils.setField(auditTrailServiceApplication, "corsOriginPatterns", "https://[.]*\\.com");
	}

	@Test
	@DisplayName("Test addCerts1 -- Validate Null if Key is Stored in System property")
	void testAddCerts_Positive1() throws IOException {
		System.clearProperty("javax.net.ssl.trustStore");
		assertNull(auditTrailServiceApplication.addBHCerts("bhcacerts"));
	}

	@Test
	@DisplayName("Test addCerts2 -- File not deleted")
	void testAddCerts_Negative1() throws IOException {
		System.clearProperty("javax.net.ssl.trustStore");
		try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
			mockedFiles.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);
			assertNull(auditTrailServiceApplication.addBHCerts("bhcacerts"));
		}
	}

	@Test
	@DisplayName("Test adapterProperties -- Validate Created Bean")
	void testAdapterProperties_Positive1() throws IOException, ParseException {
		AdapterProperties adapterProperties = auditTrailServiceApplication.adapterProperties();
		assertInstanceOf(AdapterProperties.class, adapterProperties);
		assertEquals("https://test.com/auth", adapterProperties.getAuthServerUrl());
		assertEquals("abcd", adapterProperties.getClientId());
		assertEquals("https://test.com/v1/filterAttributes", adapterProperties.getDfscUrl());
		assertEquals("/test/**", adapterProperties.getUrlExclusionList()[0]);
		assertEquals("{\"test-relam\":{\"client_secret\":\"abcd\"}}",
				adapterProperties.getTenantInfoJson().toJSONString());
		assertEquals("disabled", adapterProperties.getRbacFlag());
		assertEquals(null, adapterProperties.getAbacSupport());
		assertEquals(0, adapterProperties.getCacheTTL());
		assertEquals("[{path=/v1/test, methods=[{method=GET, scopes=[read_access, write_access]}]}]",
				adapterProperties.getPathInfoJson());
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Test corsConfigurer -- Validate Created Bean")
	void testCorsConfigurer_Positive1() throws IOException {
		WebMvcConfigurer corsConfigurer = auditTrailServiceApplication.corsConfigurer();
		CorsRegistry registry = new CorsRegistry();
		corsConfigurer.addCorsMappings(registry);
		assertInstanceOf(WebMvcConfigurer.class, corsConfigurer);
		List<CorsRegistration> corsRegistationList = (List<CorsRegistration>) ReflectionTestUtils.getField(registry,
				"registrations");
		assertInstanceOf(CorsRegistration.class, corsRegistationList.get(0));
	}

	@Test
	@DisplayName("Test filterChain -- Validate Created Bean")
	void testFilterChain_Positive1() throws Exception {
		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);
		when(mockApplicationContext.getBeanNamesForType(ObservationRegistry.class))
				.thenReturn(new String[] { "observationRegistry" });
		when(mockApplicationContext.getBean("observationRegistry")).thenReturn(mock(ObservationRegistry.class));
		ObjectPostProcessor<Object> objProcessor = new ObjectPostProcessor<Object>() {
			@Override
			public <O> O postProcess(O object) {
				return object;
			}
		};
		HttpSecurity http = new HttpSecurity(objProcessor, mock(),
				Map.of(ApplicationContext.class, mockApplicationContext));
		SecurityFilterChain filterChain = auditTrailServiceApplication.filterChain(http);
		assertInstanceOf(SecurityFilterChain.class, filterChain);
		assertInstanceOf(HeaderWriterFilter.class, filterChain.getFilters().get(0));
	}

}
