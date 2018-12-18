package com.marcuseisele.example.twolayercache.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j //Lombok annotation for getting a Slf4j logger accessible via e.g. log.info("someText");
public class TwoLayerRedisCacheableAspect {

    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    private RedisTemplate<String, Object> redisTemplate;

    public TwoLayerRedisCacheableAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(twoLayerRedisCacheable)")
    public void TwoLayerRedisCacheablePointcut(TwoLayerRedisCacheable twoLayerRedisCacheable) {
    }

    private StandardEvaluationContext getContextContainingArguments(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return context;
    }

    private String getCacheKeyFromAnnotationKeyValue(StandardEvaluationContext context, String key){
        Expression expression = expressionParser.parseExpression(key);
        return (String) expression.getValue(context);
    }

    @Around("TwoLayerRedisCacheablePointcut(twoLayerRedisCacheable)")
    public Object cacheTwoLayered(ProceedingJoinPoint joinPoint, TwoLayerRedisCacheable twoLayerRedisCacheable) throws Throwable {
        long firstLayerTtl = twoLayerRedisCacheable.firstLayerTtl();
        long secondLayerTtl = twoLayerRedisCacheable.secondLayerTtl();
        String key = twoLayerRedisCacheable.key();


        StandardEvaluationContext context = getContextContainingArguments(joinPoint);
        String cacheKey = getCacheKeyFromAnnotationKeyValue(context, key);
        log.info("### Cache key: {}", cacheKey);

        long start = System.currentTimeMillis();

        Object result;
        if (redisTemplate.hasKey(cacheKey)) {
            result = redisTemplate.opsForValue().get(cacheKey);
            log.info("Reading from cache ..." + result.toString());

            if (redisTemplate.getExpire(cacheKey, TimeUnit.MINUTES) < secondLayerTtl) {
                log.info("Entry passed firstLevel period - trying to refresh it");
                try {
                    result = joinPoint.proceed();
                    redisTemplate.opsForValue().set(cacheKey, result, secondLayerTtl + firstLayerTtl, TimeUnit.MINUTES);
                    log.info("Fetch was successful - new value was saved and is getting returned");
                } catch (Exception e) {
                    log.warn("An error occured while trying to refresh the value - extending the existing one", e);
                    redisTemplate.opsForValue().getOperations().expire(cacheKey, secondLayerTtl + firstLayerTtl, TimeUnit.MINUTES);
                }

            }
        } else {
            result = joinPoint.proceed();
            log.info("Cache miss: Called original method");
            redisTemplate.opsForValue().set(cacheKey, result, firstLayerTtl + secondLayerTtl, TimeUnit.MINUTES);
        }

        long executionTime = System.currentTimeMillis() - start;
        log.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);
        log.info("Result: {}", result);
        return result;
    }
}