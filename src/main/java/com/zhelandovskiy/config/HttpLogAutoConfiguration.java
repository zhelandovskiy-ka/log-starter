package com.zhelandovskiy.config;

import com.zhelandovskiy.aspect.LogAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HttpLogProperties.class)
@ConditionalOnProperty(name = "http-log.enabled", havingValue = "true")
public class HttpLogAutoConfiguration {

    @Bean
    public LogAspect logAspect() {
        return new LogAspect();
    }

}