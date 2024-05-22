package com.bh.cp.proxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.ip.ss.adapter.config.AdapterProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

@SpringBootApplication
@EnableJpaRepositories
@EnableEncryptableProperties
@EnableCaching
@ComponentScan(basePackages = { "com.bh.ip.user", "com.bh.ip.group", "com.bh.ip.ss.adapter", "com.bh.ip.exception",
		"com.bh.ip.ss.logger", "com.bh.tenant.config.handler", "com.bh.ip.user.cronjobs", "com.bh.*" })
public class ProxyDataServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(ProxyDataServiceApplication.class);

	@Value("${keycloak.auth.url}")
	private String authServerUrl;

	@Value("${keycloak.client-id}")
	private String clientId;

	@Value("${tms.url:null}")
	private String tmsUrl;

	@Value("${authorization.type:null}")
	private String authorizationType;

	@Value("${dfcs.url:null}")
	private String dfscUrl;

	@Value("${abac.support:null}")
	private String abacSupport;

	@Value("${cache.ttl:0}")
	private String cacheTTL;

	@Value("${filter.url.exclusion-list:null}")
	private String urlExclusionList;

	@Value("${default.tenant.configuration}")
	private String tenantInfo;

	@Value("${paths.file.location}")
	private String pathsFileLocation;

	@Value("${rbac.support}")
	private String rbacSupport;

	@Value("${cors.origin.urls}")
	private String corsOriginUrls;

	@Value("${cors.origin.patterns}")
	private String corsOriginPatterns;

	public static void main(String[] args) {
		SpringApplication.run(ProxyDataServiceApplication.class, args);
	}

	@Bean
	public String addBHCerts(@Value("${bh.cert.filename}") String bhCertFileName) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource(bhCertFileName);
		InputStream inputStream = classPathResource.getInputStream();
		logger.info("Creating Temp cert file...");
		File newFile = new File("test.txt");
		logger.info("Setting executable permission --- {}",
				newFile.setExecutable(false) ? ProxyConstants.SUCCESS : ProxyConstants.FAILURE);
		logger.info("Setting readable permission and allowed only for owner --- {}",
				newFile.setReadable(true, true) ? ProxyConstants.SUCCESS : ProxyConstants.FAILURE);
		logger.info("Setting writable permission and allowed only for owner --- {}",
				newFile.setWritable(true, true) ? ProxyConstants.SUCCESS : ProxyConstants.FAILURE);
		try {
			java.nio.file.Files.copy(inputStream, newFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} finally {
			inputStream.close();
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					logger.info("Deleting temp cert file previously created --- {}",
							Files.deleteIfExists(newFile.toPath()) ? ProxyConstants.SUCCESS : ProxyConstants.FAILURE);
				} catch (Exception e) {
					logger.error("Could not delete temp file. Please delete manually.", e);
				}
			}));
		}
		return System.setProperty("javax.net.ssl.trustStore", newFile.getPath());
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean(name = "adapterProperties")
	public AdapterProperties adapterProperties() throws IOException, ParseException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("tenant_info", tenantInfo);
		jsonObject.put("auth_server_url", authServerUrl);
		jsonObject.put("url_exclusion_list", urlExclusionList);
		ClassPathResource classPathResource = new ClassPathResource(pathsFileLocation);
		try (InputStream inputStream = classPathResource.getInputStream()) {
			String paths = new ObjectMapper().readValue(inputStream, Object.class).toString();
			jsonObject.put("path_info", paths);
		}
		jsonObject.put("rbac_support", rbacSupport);
		jsonObject.put("authorization_type", authorizationType);
		jsonObject.put("abac_support", abacSupport);
		jsonObject.put("dfsc_url", dfscUrl);
		jsonObject.put("cache_ttl", cacheTTL);
		jsonObject.put("client_id", clientId);
		return new AdapterProperties(jsonObject.toJSONString());
	}
 
	@Bean
	public WebClient webClient() {
		return WebClient.builder().build();
	}

	// Add Request Method based on the controllers
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedHeaders(
								"Access-Control-Request-Headers,Accept,Accept-Language,Content-Language,Content-Type")
						.allowedMethods("GET", "POST").allowedOrigins(corsOriginUrls.split(","))
						.allowedOriginPatterns(corsOriginPatterns.split(","));
			}
		};
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.headers(headersCustomizer -> headersCustomizer.xssProtection(
				xssCustomizer -> xssCustomizer.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
				.contentSecurityPolicy(contentSecurityCustomizer -> contentSecurityCustomizer
						.policyDirectives("form-action 'self'").policyDirectives("script-src 'self'")))
				.build();
	}

}
