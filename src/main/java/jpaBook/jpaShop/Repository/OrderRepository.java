package jpaBook.jpaShop.Repository;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpaBook.jpaShop.Repository.order.simplequery.SimpleOrderQueryDto;
import jpaBook.jpaShop.domain.*;
import jpaBook.jpaShop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static jpaBook.jpaShop.domain.QMember.member;
import static jpaBook.jpaShop.domain.QOrder.*;

@Repository
public class OrderRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderRepository(EntityManager em) {
        this.em=em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class,id);
    }

//    public List<Order> findAll(OrderSearch ordersearch){
//
//        String jpql = "select o from Order o join o.member m";
//        return em.createQuery(jpql).getResultList();
//
//    }

    public List<Order> findAll(OrderSearch ordersearch){

        return query
                .select(QOrder.order)
                .from(order)
                .join(order.member,member)
//                .where(statusEq(ordersearch.getOrderStatus()))
                .where(statusEq(ordersearch.getOrderStatus()), nameLike(ordersearch, member))
                .limit(1000)
                .fetch();

    }


    private static BooleanExpression nameLike(OrderSearch ordersearch, QMember member) {
        if(!StringUtils.hasText(ordersearch.getMemberName())){
            return null;
        }
        return member.name.like(ordersearch.getMemberName());
    }

    private BooleanExpression statusEq(OrderStatus statusCond){
        if(statusCond==null){
            return null;
        }
        return order.status.eq(statusCond);
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //????????? ??????
        List<Predicate> criteria = new ArrayList<>();
//?????? ?????? ??????
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
//?????? ?????? ??????
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //?????? 1000???
        return query.getResultList();
    }


    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o "+
                        "join fetch o.member m "+
                        "join fetch o.delivery d ",Order.class).getResultList();

    }

    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery("select new jpaBook.jpaShop.Repository.order.simplequery.SimpleOrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address)  from Order o " +
                "join o.member m " +
                "join o.delivery d", SimpleOrderQueryDto.class).getResultList();
    }

    public List<Order> findAllWithItem() {
        // distinct -> db??? distinct + jpa??? ?????? : ?????? ????????? ?????????(????????????) ???????????????. but ????????? ??????(limit offset??????)
        return em.createQuery("select distinct o from Order o " +
                " join fetch o.member m" +
                " join fetch o.delivery d " +
                " join fetch o.orderItems oi " +
                "join fetch oi.item i", Order.class)
                .setFirstResult(0) // ?????? ?????? ??????
                .setMaxResults(100) // 1:N?????? ????????? ??? ????????? ???????????? ?????? ??? ??????????????? ?????????
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o "+
                        "join fetch o.member m "+
                        "join fetch o.delivery d ",Order.class).setFirstResult(offset)
                .setMaxResults(limit).getResultList();

    }
}
