package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
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
  private final OrderSimpleQueryRepository orderSimpleQueryRepository;

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

  /*
  * V3. 엔티티 조회 후 DTO로 변환(fetch join O)
  *   - fetch join으로 쿼리 1번 호출
  * 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요)
  * */
  @GetMapping("/api/v3/simple-orders")
  public List<SimpleOrderDto> orderV3() {
    return orderRepository
        .findAllWithMemberAndDelivery()
        .stream()
        .map(SimpleOrderDto::from)
        .toList();
  }

  /*
  * V4. JPA에서 DTO로 바로 조회
  *   - 쿼리 1회 호출
  *   - SELECT 절에서 원하는 데이터만 선택해서 조회
  *   - 모든 컬럼을 SELECT해서 DTO에 필요한 컬럼만 넣는 것이 아닌, SELECT 절에서부터 필요한 컬럼만 퍼올림
  * */
  @GetMapping("/api/v4/simple-orders")
  public List<OrderSimpleQueryDto> orderV4() {
    return orderSimpleQueryRepository.findOrderDtos();
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
