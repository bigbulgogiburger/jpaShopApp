package jpaBook.jpaShop.Repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery("select new jpaBook.jpaShop.Repository.order.simplequery.SimpleOrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address)  from Order o " +
                "join o.member m " +
                "join o.delivery d", SimpleOrderQueryDto.class).getResultList();
    }

}