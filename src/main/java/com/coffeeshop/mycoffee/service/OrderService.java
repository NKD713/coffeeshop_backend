package com.coffeeshop.mycoffee.service;

import com.coffeeshop.mycoffee.dto.orderdto.request.OrderCreationRequest;
import com.coffeeshop.mycoffee.dto.orderdto.request.OrderUpdateRequest;
import com.coffeeshop.mycoffee.dto.orderdto.response.OrderResponse;
import com.coffeeshop.mycoffee.entity.Order;
import com.coffeeshop.mycoffee.entity.Payment;
import com.coffeeshop.mycoffee.entity.User;
import com.coffeeshop.mycoffee.exception.AppException;
import com.coffeeshop.mycoffee.exception.ErrorCode;
import com.coffeeshop.mycoffee.mapper.OrderMapper;
import com.coffeeshop.mycoffee.repository.OrderRepository;
import com.coffeeshop.mycoffee.repository.PaymentRepository;
import com.coffeeshop.mycoffee.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {

    OrderRepository orderRepository;
    OrderMapper orderMapper;
    UserRepository userRepository;
    PaymentRepository paymentRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse createOrder(OrderCreationRequest request){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        log.info("name: {}", name);

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        Order order = orderMapper.toOrder(request);
        order.setUser(user);
        order.setPayment(payment);

        try {
            order = orderRepository.save(order);
        } catch (DataIntegrityViolationException e){
            throw new AppException(ErrorCode.ORDER_EXISTED);
        }

        return OrderResponse.builder()
                .userId(user.getId())
                .paymentId(payment.getId())
                .table(order.getTable())
                .totalPrice(order.getTotal_price())
                .build();

    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getOrders(){
        return orderRepository.findAll().stream().map(order ->
            OrderResponse.builder()
                    .totalPrice(order.getTotal_price())
                    .userId(order.getUser().getId())
                    .table(order.getTable())
                    .paymentId(order.getPayment().getId())
                    .build()
        ).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateOrder(String orderId, OrderUpdateRequest request){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (request.getPaymentId() != null) {
            Payment payment = paymentRepository.findById(request.getPaymentId())
                    .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
            order.setPayment(payment);
        }

        if (request.getTable() != null) {
            order.setTable(request.getTable());
        }

        Order orderResult = orderRepository.save(order);

        return OrderResponse.builder()
                .paymentId(orderResult.getPayment().getId())
                .table(orderResult.getTable())
                .userId(orderResult.getUser().getId())
                .totalPrice(order.getTotal_price())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(String orderId){
        orderRepository.deleteById(orderId);
    }
}
