package com.marcuseisele.example.twolayercache.example;

import com.marcuseisele.example.twolayercache.model.AnotherDTO;
import com.marcuseisele.example.twolayercache.model.Order;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ExampleController {

    private OrderService orderService;


    @GetMapping(value = "/")
    public Order getOrder() {
        //hardcoded to make call easier
        int orderNumber = 42;
        return orderService.getOrder(orderNumber, "Test", "CacheSuffix");
    }

    @GetMapping(value = "/another")
    public AnotherDTO getAnother() {
        //hardcoded to make call easier
        int orderNumber = 42;
        return orderService.getAnother(orderNumber, "Test", "CacheSuffix");
    }
}
