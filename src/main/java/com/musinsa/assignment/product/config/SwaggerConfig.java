package com.musinsa.assignment.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("!test")
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("무신사 상품 관리 API")
                .description("무신사 상품 관리 및 가격 분석을 위한 RESTful API")
                .version("1.0.0")
                .contact(new Contact()
                    .name("무신사 개발팀")
                    .email("dev@musinsa.com")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local Server")
            ));
    }
} 