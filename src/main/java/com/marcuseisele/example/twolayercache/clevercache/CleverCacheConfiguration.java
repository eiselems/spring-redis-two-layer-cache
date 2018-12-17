package com.marcuseisele.example.twolayercache.clevercache;

import com.marcuseisele.example.twolayercache.model.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CleverCacheConfiguration {

    @Bean
    public RedisTemplate<String, List<Order>> orderTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, List<Order>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

}