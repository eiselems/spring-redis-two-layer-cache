# spring-redis-two-layer-cache

A Cache which instead of evicting an entry on expiration keeps it until it is able to get new Data.

Technically this is done via Spring AOP.
We create an Around Advice which is guarding our real invocation.

It was created as a drop-in replacement for @Cacheable.

## Usage

The usage is pretty similar to @Cacheable known from the Spring Cache Abstraction.

```java
@Service
public class OrderService {

    @TwoLayerRedisCacheable(firstLayerTtl = 1L, secondLayerTtl = 5L, key = "'orders_'.concat(#id).concat(#another)")
    public Order getOrder(int id, String other, String another) {
        Order order = getOrderViaHTTP(id);
        return order;
    }
}
```
