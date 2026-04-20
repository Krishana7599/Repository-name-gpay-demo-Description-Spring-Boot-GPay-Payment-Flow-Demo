package com.example.gpaydemo;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final Map<String, Map<String, Object>> paymentStore = new HashMap<>();
    private final Map<String, String> idempotencyStore = new HashMap<>();

    @PostMapping("/gpay")
    public Map<String, Object> processPayment(@RequestBody Map<String, Object> request) {

        String idempotencyKey = (String) request.get("idempotencyKey");

        if (idempotencyKey != null && idempotencyStore.containsKey(idempotencyKey)) {
            String existingPaymentId = idempotencyStore.get(idempotencyKey);
            Map<String, Object> existingPayment = paymentStore.get(existingPaymentId);

            Map<String, Object> duplicateResponse = new HashMap<>(existingPayment);
            duplicateResponse.put("message", "Duplicate request detected. Returning existing payment.");
            return duplicateResponse;
        }

        Map<String, Object> response = new HashMap<>();

        String paymentId = UUID.randomUUID().toString();
        String orderId = (String) request.get("orderId");
        Object amount = request.get("amount");

        response.put("paymentId", paymentId);
        response.put("orderId", orderId);
        response.put("amount", amount);
        response.put("currency", "USD");
        response.put("idempotencyKey", idempotencyKey);

        if (amount == null) {
            response.put("status", "FAILED");
            response.put("message", "Amount is missing");
            paymentStore.put(paymentId, new HashMap<>(response));

            if (idempotencyKey != null) {
                idempotencyStore.put(idempotencyKey, paymentId);
            }

            return response;
        }

        double paymentAmount = Double.parseDouble(amount.toString());

        if (paymentAmount < 5) {
            response.put("status", "FAILED");
            response.put("message", "Payment failed: amount too low");
        } else if (paymentAmount < 20) {
            response.put("status", "PENDING");
            response.put("message", "Payment is being processed");
        } else {
            response.put("status", "SUCCESS");
            response.put("message", "Payment successful");
        }

        paymentStore.put(paymentId, new HashMap<>(response));

        if (idempotencyKey != null) {
            idempotencyStore.put(idempotencyKey, paymentId);
        }

        return response;
    }

    @GetMapping("/{paymentId}")
    public Map<String, Object> getPaymentStatus(@PathVariable String paymentId) {
        Map<String, Object> payment = paymentStore.get(paymentId);

        if (payment == null) {
            Map<String, Object> notFound = new HashMap<>();
            notFound.put("status", "NOT_FOUND");
            notFound.put("message", "Payment not found");
            return notFound;
        }

        return payment;
    }

    @PostMapping("/webhook/{paymentId}")
    public Map<String, Object> webhookUpdate(@PathVariable String paymentId) {
        Map<String, Object> payment = paymentStore.get(paymentId);

        if (payment == null) {
            Map<String, Object> notFound = new HashMap<>();
            notFound.put("status", "NOT_FOUND");
            notFound.put("message", "Payment not found");
            return notFound;
        }

        String currentStatus = (String) payment.get("status");

        if ("PENDING".equals(currentStatus)) {
            payment.put("status", "SUCCESS");
            payment.put("message", "Payment updated by webhook to SUCCESS");
        } else if ("SUCCESS".equals(currentStatus)) {
            payment.put("message", "Payment is already SUCCESS");
        } else if ("FAILED".equals(currentStatus)) {
            payment.put("message", "Failed payment cannot be updated to SUCCESS");
        }

        return payment;
    }
}