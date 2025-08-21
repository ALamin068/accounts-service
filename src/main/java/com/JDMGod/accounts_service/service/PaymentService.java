package com.JDMGod.accounts_service.service;

import com.JDMGod.accounts_service.api.dto.PaymentRequest;
import com.JDMGod.accounts_service.model.Order;
import com.JDMGod.accounts_service.repo.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.secret.key:sk_test_dummy}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret:whsec_dummy}")
    private String webhookSecret;

    private final OrderRepository orderRepository;

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public Map<String, String> createPaymentIntent(PaymentRequest request) {
        try {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Convert amount to cents for Stripe
            long amountInCents = request.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency())
                    .putMetadata("orderId", String.valueOf(order.getId()))
                    .putMetadata("customerId", String.valueOf(order.getCustomer().getId()))
                    .setDescription("Payment for Order #" + order.getId())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Update order with payment intent ID
            order.setPaymentIntentId(paymentIntent.getId());
            order.setPaymentStatus(Order.PaymentStatus.PROCESSING);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("paymentIntentId", paymentIntent.getId());
            response.put("status", paymentIntent.getStatus());

            return response;

        } catch (StripeException e) {
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage());
        }
    }

    public Map<String, String> confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            Map<String, String> response = new HashMap<>();
            response.put("status", paymentIntent.getStatus());
            response.put("paymentIntentId", paymentIntent.getId());

            // Update order status based on payment status
            if ("succeeded".equals(paymentIntent.getStatus())) {
                updateOrderPaymentStatus(paymentIntentId, Order.PaymentStatus.COMPLETED);
                response.put("message", "Payment successful");
            } else if ("requires_action".equals(paymentIntent.getStatus())) {
                response.put("message", "Payment requires additional action");
            } else {
                response.put("message", "Payment failed");
            }

            return response;

        } catch (StripeException e) {
            throw new RuntimeException("Failed to confirm payment: " + e.getMessage());
        }
    }

    public void handleWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (paymentIntent != null) {
                        updateOrderPaymentStatus(paymentIntent.getId(), Order.PaymentStatus.COMPLETED);
                        updateOrderStatus(paymentIntent.getId(), Order.OrderStatus.CONFIRMED);
                    }
                    break;
                case "payment_intent.payment_failed":
                    PaymentIntent failedPayment = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (failedPayment != null) {
                        updateOrderPaymentStatus(failedPayment.getId(), Order.PaymentStatus.FAILED);
                    }
                    break;
                default:
                    // Handle other event types as needed
                    break;
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to handle webhook: " + e.getMessage());
        }
    }

    public Map<String, String> refundPayment(String paymentIntentId) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();

            Refund refund = Refund.create(params);

            updateOrderPaymentStatus(paymentIntentId, Order.PaymentStatus.REFUNDED);

            Map<String, String> response = new HashMap<>();
            response.put("refundId", refund.getId());
            response.put("status", refund.getStatus());
            response.put("amount", refund.getAmount().toString());

            return response;

        } catch (StripeException e) {
            throw new RuntimeException("Failed to refund payment: " + e.getMessage());
        }
    }

    private void updateOrderPaymentStatus(String paymentIntentId, Order.PaymentStatus status) {
        orderRepository.findByPaymentIntentId(paymentIntentId)
                .ifPresent(order -> {
                    order.setPaymentStatus(status);
                    order.setUpdatedAt(LocalDateTime.now());
                    orderRepository.save(order);
                });
    }

    private void updateOrderStatus(String paymentIntentId, Order.OrderStatus status) {
        orderRepository.findByPaymentIntentId(paymentIntentId)
                .ifPresent(order -> {
                    order.setStatus(status);
                    order.setUpdatedAt(LocalDateTime.now());
                    orderRepository.save(order);
                });
    }
}
