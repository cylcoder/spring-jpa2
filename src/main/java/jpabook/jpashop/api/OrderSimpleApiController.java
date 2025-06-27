package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* xToOne
* Order
* Order -> Member (ManyToOne)
* Order -> Delivery (OneToOne)
* */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

  private final OrderRepository orderRepository;

  /*
  * V1. 엔티티 직접 노출
  *   - Hibernate5Module 모듈 등록 -> Lazy = null 처리
  *   - 양방향 관계 문제 발생 -> @JsonIgnore
  * */
  @GetMapping("/api/v1/simple-orders")
  public List<Order> ordersV1() {
    List<Order> orders = orderRepository.findAll();
    orders.forEach(o -> {
          o.getMember().getName();
          o.getDelivery().getAddress();
        });
    return orders;
  }

  /*
  * V2. 엔티티 조회 후 DTO로 변환(fetch join X)
  *   - 단점: 지연로딩으로 쿼리 N번 호출
  * */
  @GetMapping("/api/v2/simple-orders")
  public List<SimpleOrderDto> orderV2() {
    return orderRepository
        .findAll()
        .stream()
        .map(SimpleOrderDto::from)
        .toList();
  }

  public record SimpleOrderDto(
      Long orderId,
      String name,
      LocalDateTime orderDate,
      OrderStatus orderStatus,
      Address address
  ) {

    public static SimpleOrderDto from(Order order) {
      return new SimpleOrderDto(
          order.getId(),
          order.getMember().getName(),
          order.getOrderDate(),
          order.getStatus(),
          order.getDelivery().getAddress());
    }
  }

}
