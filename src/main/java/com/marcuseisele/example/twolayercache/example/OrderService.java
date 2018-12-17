package com.marcuseisele.example.twolayercache.example;

import com.marcuseisele.example.twolayercache.clevercache.CleverCache;
import com.marcuseisele.example.twolayercache.model.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @CleverCache(ttl = 1L, graceTtl = 5L, redisTemplate = "orderTemplate", key = "'orders_'.concat(#id)")
    //similar to @Cacheable(cacheNames = "myCache", key = "'orders_'.concat(#id)")
    public Order getOrder(int id) {
        //in reality this call is really expensive and error-prone - trust me!
        return new Order(id, 12345L);
    }
}
