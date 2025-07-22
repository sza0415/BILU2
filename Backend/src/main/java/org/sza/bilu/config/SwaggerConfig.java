package org.sza.bilu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类 - 使用SpringDoc OpenAPI 3
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("笔录系统 API文档")
                        .description("笔录系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SZA")
                                .url("http://www.example.com")
                                .email("admin@example.com")));
    }
}