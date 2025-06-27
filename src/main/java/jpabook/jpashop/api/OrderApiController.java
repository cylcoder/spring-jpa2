package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
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

  @GetMapping("/api/v2/orders")
  public List<OrderDto> ordersV2() {
    return orderRepository.findAll()
        .stream()
        .map(OrderDto::from)
        .toList();
  }

  @GetMapping("/api/v3/orders")
  public List<OrderDto> ordersV3() {
    return orderRepository.findAllWithItem()
        .stream()
        .map(OrderDto::from)
        .toList();
  }

  public record OrderDto(
     Long orderId,
     String name,
     LocalDateTime orderDate,
     OrderStatus orderStatus,
     Address address,
     List<OrderItemDto> orderItems
  ) {

    static OrderDto from(Order order) {
      return new OrderDto(
          order.getId(),
          order.getMember().getName(),
          order.getOrderDate(),
          order.getStatus(),
          order.getDelivery().getAddress(),
          order.getOrderItems()
              .stream()
              .map(OrderItemDto::from)
              .toList()
      );
    }
  }

  public record OrderItemDto(
      String itemName,
      int orderPrice,
      int count
  ) {

    static OrderItemDto from(OrderItem orderItem) {
      return new OrderItemDto(
          orderItem.getItem().getName(),
          orderItem.getOrderPrice(),
          orderItem.getCount()
      );
    }

  }

}
