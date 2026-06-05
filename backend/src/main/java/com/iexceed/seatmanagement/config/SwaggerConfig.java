package com.iexceed.seatmanagement.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI seatManagementOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Seat Management System APIs")
                        .description("Enterprise Workspace Management Platform")
                        .version("1.0"));
    }
}