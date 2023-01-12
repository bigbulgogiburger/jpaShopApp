package jpaBook.jpaShop;

import jpaBook.jpaShop.domain.*;
import jpaBook.jpaShop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 총 주문 2개
 *
 *
 */
@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;
        public void dbInit1(){
            Member member = createMember("userA", "seoul", "1", "1111");
            em.persist(member);

            Book book1 = createBook("JPA BOOK", 10000, 100);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 1);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }

        public void dbInit2(){
            Member member = createMember("userB", "daejeon", "21", "22222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 100);

            Book book2 = createBook("SPRING2 BOOK", 40000, 200);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 1);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }

        private Book createBook(String JPA_BOOK, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(JPA_BOOK);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);
            em.persist(book1);
            return book1;
        }

        private static Member createMember(String userB, String daejeon, String street, String zipcode) {
            Member member = new Member();
            member.setName(userB);
            member.setAddress(new Address(daejeon, street, zipcode));
            return member;
        }
    }
}
