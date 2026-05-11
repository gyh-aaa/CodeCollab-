package com.codecollab.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI codeCollabOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("CodeCollab API")
                .version("0.0.1")
                .description("智能项目协作系统接口文档"));
    }
}
