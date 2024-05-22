package com.bh.cp.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@OpenAPIDefinition(security = { @SecurityRequirement(name = "Keycloak Token") })
@SecurityScheme(name = "Keycloak Token", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SwaggerConfig {

	@Value("${springdoc.swagger-ui.path}")
	private String swaggerUiPath;

	@Bean
	public OpenAPI microserviceOpenAPI() {
		Contact contact = new Contact();
		contact.setEmail("support@bakerhughes.com");
		contact.setName("Baker Hughes");
		contact.setUrl("https://www.bakerhughes.com");

		OpenAPI openApi = new OpenAPI();

		io.swagger.v3.oas.models.servers.Server server = new io.swagger.v3.oas.models.servers.Server();
		server.setUrl(swaggerUiPath);
		server.setDescription("Dev Server URL");

		return openApi.addServersItem(server)
				.info(new Info().title("CUSTOMER PORTAL PROXY SERVICE")
						.description("CUSTOMER PORTAL - Proxy Data Service Documentation").version("Beta 1.0")
						.contact(contact));
	}

}
