package com.marcuseisele.example.twolayercache.example;

import com.marcuseisele.example.twolayercache.aop.TwoLayerRedisCacheable;
import com.marcuseisele.example.twolayercache.model.AnotherDTO;
import com.marcuseisele.example.twolayercache.model.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @TwoLayerRedisCacheable(firstLayerTtl = 1L, secondLayerTtl = 5L, key = "'orders_'.concat(#id).concat(#another)")
    public Order getOrder(int id, String other, String another) {
        //in reality this call is really expensive and error-prone - trust me!
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Order(id, Math.round(Math.random() * 100000));
    }

    @TwoLayerRedisCacheable(firstLayerTtl = 2L, secondLayerTtl = 10L, key = "'another_'.concat(#id).concat(#another)")
    public AnotherDTO getAnother(int id, String other, String another) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new AnotherDTO(id, other, another);
    }
}
