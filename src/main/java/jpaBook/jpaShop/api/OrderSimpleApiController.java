package jpaBook.jpaShop.api;


import jpaBook.jpaShop.Repository.OrderRepository;
import jpaBook.jpaShop.Repository.OrderSearch;
import jpaBook.jpaShop.Repository.order.simplequery.OrderSimpleQueryRepository;
import jpaBook.jpaShop.Repository.order.simplequery.SimpleOrderQueryDto;
import jpaBook.jpaShop.domain.Address;
import jpaBook.jpaShop.domain.Order;
import jpaBook.jpaShop.domain.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XToOne
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy forced initation
            order.getDelivery().getAddress(); //Lazy forced initation
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        //N+1의 문제 1+회원 N + 배송 N
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<SimpleOrderDto> collect = orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orderList = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orderList.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return collect;

    }
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4() {
        List<SimpleOrderQueryDto> orderList = orderSimpleQueryRepository.findOrderDtos();
        return orderList;
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId= order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address= order.getDelivery().getAddress();
        }
    }

}
