package jpaBook.jpaShop.api;

import jpaBook.jpaShop.Repository.OrderRepository;
import jpaBook.jpaShop.Repository.OrderSearch;
import jpaBook.jpaShop.Repository.order.query.OrderFlatDto;
import jpaBook.jpaShop.Repository.order.query.OrderItemQueryDto;
import jpaBook.jpaShop.Repository.order.query.OrderQueryDto;
import jpaBook.jpaShop.Repository.order.query.OrderQueryRepository;
import jpaBook.jpaShop.domain.Address;
import jpaBook.jpaShop.domain.Order;
import jpaBook.jpaShop.domain.OrderItem;
import jpaBook.jpaShop.domain.OrderStatus;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    
    
    @GetMapping("api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(orderItem -> orderItem.getItem().getId());

        }
        return all;
    }
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> result = orders.stream().map(OrderDto::new).collect(toList());
        return result;
    }
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        for (Order order : orders) {
            System.out.println("order = " + order+" id="+order.getId());
        }
        List<OrderDto> result = orders.stream().map(OrderDto::new).collect(toList());
        return result;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> result = orders.stream().map(OrderDto::new).collect(toList());
        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        List<OrderQueryDto> orderQueryDtos = orderQueryRepository.findOrderQueryDtos();

        return orderQueryDtos;
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        List<OrderQueryDto> orderQueryDtos = orderQueryRepository.findAllByDto_optimization();

        return orderQueryDtos;
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        List<OrderFlatDto> orderFlatDto = orderQueryRepository.findAllByDto_flat();

        return orderFlatDto.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());

    }




    @Getter
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderStatus = order.getStatus();
            orderDate= order.getOrderDate();
            address = order.getDelivery().getAddress();
            order.getOrderItems().stream().forEach(o->o.getItem().getName());
            orderItems = order.getOrderItems().stream()
                    .map(orderItem->new OrderItemDto(orderItem))
                    .collect(toList());

        }
    }

    @Getter
    static class OrderItemDto{
        private String itemName;
        private int orderPrice;
        private int count;
        public OrderItemDto(OrderItem orderItem) {
            itemName=orderItem.getItem().getName();
            orderPrice=orderItem.getOrderPrice();
            count=orderItem.getCount();
        }
    }


}
