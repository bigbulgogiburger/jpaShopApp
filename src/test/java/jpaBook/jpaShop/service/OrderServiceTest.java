package jpaBook.jpaShop.service;

import jpaBook.jpaShop.Repository.OrderRepository;
import jpaBook.jpaShop.domain.Address;
import jpaBook.jpaShop.domain.Exception.NotEnoughStockException;
import jpaBook.jpaShop.domain.Member;
import jpaBook.jpaShop.domain.Order;
import jpaBook.jpaShop.domain.OrderStatus;
import jpaBook.jpaShop.domain.item.Book;
import jpaBook.jpaShop.domain.item.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em ;
    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Item book = createBook("시골 jpa", 10000, 10);

        //when
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), 2);


        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품주문시 상태는 order", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.",1,getOrder.getOrderItems().size());
        assertEquals("주문한 가격은 가격 * 수량이다.",10000*orderCount, getOrder.getTotalPrice());
        assertEquals("주문한 만큼 재고가 줄어들어야 한다.",8,book.getStockQuantity());
    }

    private Item createBook(String bookName, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(bookName);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("서울","강가","123123"));
        em.persist(member);
        return member;
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();

        Item book = createBook("시골 jpa", 10000, 10);

        int orderCount =11;



        //when
        orderService.order(member.getId(),book.getId(),orderCount);


        //then
        fail("재고수량 부족으로 예외가 발생해야 한다.");

    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item book = createBook("편도훈 책", 10000, 10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), 2);


        //when
        orderService.cancelOrder(orderId);


        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 상태는 취소인 cancel이여야 한다.",OrderStatus.CANCEL,getOrder.getStatus());
        assertEquals("재고가 원복 되어야 한다.",10,book.getStockQuantity());

    }


}