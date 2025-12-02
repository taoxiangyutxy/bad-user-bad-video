package com.ttt.one.waiguagg.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类 - 基于OpenAPI 3规范
 * 适配Spring Boot 2.7.3
 * 
 * @author ttt
 * @date 2024
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置OpenAPI信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TTT外挂举报平台API文档")
                        .description("游戏外挂举报平台API接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("TTT团队")
                                .email("496427196@qq.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

    /**
     * 配置外挂举报模块API分组
     */
    @Bean
    public GroupedOpenApi waiguaApi() {
        return GroupedOpenApi.builder()
                .group("外挂举报模块")
                .pathsToMatch("/waiguagg/**")
                .build();
    }

    /**
     * 配置全部API分组
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("全部接口")
                .pathsToMatch("/**")
                .build();
    }
}
