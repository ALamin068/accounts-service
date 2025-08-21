package com.JDMGod.accounts_service.service;

import com.JDMGod.accounts_service.api.dto.OrderCreateRequest;
import com.JDMGod.accounts_service.model.Account;
import com.JDMGod.accounts_service.model.Order;
import com.JDMGod.accounts_service.model.OrderItem;
import com.JDMGod.accounts_service.model.Product;
import com.JDMGod.accounts_service.repo.OrderRepository;
import com.JDMGod.accounts_service.repo.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(OrderCreateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account customer = (Account) auth.getPrincipal();

        Order order = new Order(customer, request.getShippingAddress());

        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem(product, itemRequest.getQuantity());
            order.addOrderItem(orderItem);

            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
        }

        return orderRepository.save(order);
    }

    public Page<Order> getMyOrders(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account customer = (Account) auth.getPrincipal();
        return orderRepository.findByCustomerId(customer.getId(), pageable);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);

        if (order.getStatus() == Order.OrderStatus.DELIVERED ||
            order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel order in current status: " + order.getStatus());
        }

        // Restore product stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    public Page<Order> getAllOrders(Pageable pageable, String status) {
        if (status != null && !status.trim().isEmpty()) {
            return orderRepository.findByStatus(Order.OrderStatus.valueOf(status), pageable);
        }
        return orderRepository.findAll(pageable);
    }

    public boolean isOrderOwner(Long orderId, String email) {
        Order order = getOrderById(orderId);
        return order.getCustomer().getEmail().equals(email);
    }
}
