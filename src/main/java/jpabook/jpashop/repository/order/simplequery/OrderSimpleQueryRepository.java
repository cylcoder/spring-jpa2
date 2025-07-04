package jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

  private final EntityManager em;

  public List<OrderSimpleQueryDto> findOrderDtos() {
    return em.createQuery(
        "SELECT new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto("
            + "o.id, m.name, o.orderDate, o.status, d.address"
            + ") "
            + "FROM Order o "
            + "JOIN o.member m "
            + "JOIN o.delivery d", OrderSimpleQueryDto.class
    ).getResultList();
  }

}
