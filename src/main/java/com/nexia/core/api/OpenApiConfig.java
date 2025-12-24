package com.nexia.core.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Nexia Core API",
                version = "v1",
                description = "Core service for Nexia (users and foundational APIs)."
        )
)
public class OpenApiConfig {}
