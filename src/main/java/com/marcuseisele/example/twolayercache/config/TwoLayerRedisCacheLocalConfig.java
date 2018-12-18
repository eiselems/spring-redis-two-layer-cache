package com.marcuseisele.example.twolayercache.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheConfigurationProperties.class)
//@Profile("local") this is disabled for the example application
@Slf4j
//Pretty sure this is not even necessary, leaving this here since our cloud configuration is using an equivalent
//config and I want to keep it similar to the "REAL" thing
public class TwoLayerRedisCacheLocalConfig extends CachingConfigurerSupport {

    @Bean
    public JedisConnectionFactory redisConnectionFactory(CacheConfigurationProperties properties) {
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        redisConnectionFactory.setHostName(properties.getRedisHost());
        redisConnectionFactory.setPort(properties.getRedisPort());
        return redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        return new RedisCacheManager(redisTemplate);
    }
}