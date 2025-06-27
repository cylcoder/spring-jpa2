package jpabook.jpashop.api;

import java.util.List;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

  private final OrderRepository orderRepository;

  /*
  * V1. 엔티티 직접 노출
  *   - Hibernate5Module 모듈 등록, Lazy = null 처리
  *   - 양방향 관계 문제 발생 -> @JsonIgnore
  * */
  @GetMapping("/api/v1/orders")
  public List<Order> ordersV1() {
    List<Order> orders = orderRepository.findAll();
    for (Order order : orders) {
      // Member, Delivery, OrderItem.Item Lazy 강제 초기화
      order.getMember().getName();
      order.getDelivery().getAddress();
      order.getOrderItems().forEach(o -> o.getItem().getName());
    }
    return orders;
  }

}
