package com.marcuseisele.example.twolayercache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "cache")
@Data
public class CacheConfigurationProperties {
    private int redisPort = 6379;
    private String redisHost = "localhost";
}
