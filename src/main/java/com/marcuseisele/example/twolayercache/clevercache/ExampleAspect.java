package com.marcuseisele.example.twolayercache.clevercache;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class ExampleAspect {

    private Map<String, RedisTemplate> templates;

    public ExampleAspect(Map<String, RedisTemplate> redisTemplateMap) {
        this.templates = redisTemplateMap;
    }

    @Pointcut("@annotation(cleverCache)")
    public void CleverCachePointcut(CleverCache cleverCache) {
    }

    @Around("CleverCachePointcut(cleverCache)")
    public Object clevercache(ProceedingJoinPoint joinPoint, CleverCache cleverCache) throws Throwable {
        long ttl = cleverCache.ttl();
        long grace = cleverCache.graceTtl();
        String expression = cleverCache.key();
        //FIXME: Please add working code here :D - extracting the key by interpreting the passed SpEL Expression in expression





        //TODO: replace this with the key provided by the SpEL Expression in expression
        //since the following is having a lot of issues (don't get me started)
        String cacheKey = joinPoint.getSignature() + "_" + SimpleKeyGenerator.generateKey(joinPoint.getArgs()).hashCode();

        long start = System.currentTimeMillis();
        RedisTemplate redisTemplate = templates.get(cleverCache.redisTemplate());
        Object result;
        if (redisTemplate.hasKey(cacheKey)) {
            result = redisTemplate.opsForValue().get(cacheKey);
            log.info("Reading from cache ..." + result.toString());

            if (redisTemplate.getExpire(cacheKey, TimeUnit.MINUTES) < grace) {
                log.info("Entry is in Grace period - trying to refresh it");
                try {
                    result = joinPoint.proceed();
                    redisTemplate.opsForValue().set(cacheKey, result, grace+ttl, TimeUnit.MINUTES);
                    log.info("Fetch was successful - new value will be returned");
                } catch (Exception e) {
                    log.warn("An error occured while trying to refresh the value - extending the old one", e);
                    //TODO: think about only adding 5 minutes on top of grace, or 50% of ttl on top of grace
                    //if protected by a circuit breaker we could go REALLY low here
                    redisTemplate.opsForValue().getOperations().expire(cacheKey, grace+ttl, TimeUnit.MINUTES);
                }

            }

        } else {
            result = joinPoint.proceed();
            log.info("Giving from method ..." + result.toString());
            redisTemplate.opsForValue().set(cacheKey, result, ttl + grace, TimeUnit.MINUTES);
        }

        long executionTime = System.currentTimeMillis() - start;
        log.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);
        log.info("Result: {}", result);
        return result;
    }
}