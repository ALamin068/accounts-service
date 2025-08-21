package com.JDMGod.accounts_service.api.controller;

import com.JDMGod.accounts_service.api.dto.PaymentRequest;
import com.JDMGod.accounts_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-payment-intent")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@Valid @RequestBody PaymentRequest request) {
        Map<String, String> response = paymentService.createPaymentIntent(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm-payment")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> confirmPayment(@RequestParam String paymentIntentId) {
        Map<String, String> response = paymentService.confirmPayment(paymentIntentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> refundPayment(@RequestParam String paymentIntentId) {
        Map<String, String> response = paymentService.refundPayment(paymentIntentId);
        return ResponseEntity.ok(response);
    }
}
