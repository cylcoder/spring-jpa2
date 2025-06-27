package jpabook.jpashop.repository.order.simplequery;

import java.time.LocalDateTime;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;

public record OrderSimpleQueryDto(

    Long orderId,
    String name,
    LocalDateTime orderDate,
    OrderStatus orderStatus,
    Address address

) {

}
