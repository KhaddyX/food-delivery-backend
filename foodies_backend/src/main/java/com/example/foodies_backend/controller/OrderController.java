package com.example.foodies_backend.controller;

import com.example.foodies_backend.dto.OrderRequest;
import com.example.foodies_backend.dto.OrderResponse;
import com.example.foodies_backend.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

private final OrderService orderService;

// Create order and initialize Paystack payment
@PostMapping("/create")
@ResponseStatus(HttpStatus.CREATED)
public OrderResponse createOrderWithPayment(@RequestBody OrderRequest request) {
    return orderService.createOrderWithPayment(request);
}

// Verify payment after Paystack redirect or from frontend callback
@PostMapping("/verify")
public void verifyPayment(@RequestBody Map<String, String> paymentData) {
    // Here we use "Paid" as the status label you want to mark orders with
    orderService.verifyPayment(paymentData, "Paid");
}

@GetMapping
public List<OrderResponse> getOrders() {
    return orderService.getUserOrders();
}

@DeleteMapping("/{orderId}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteOrder(@PathVariable String orderId) {
    orderService.removeOrder(orderId);
}

@GetMapping("/all")
public List<OrderResponse> getOrdersOfAllUsers() {
    return orderService.getOrdersOfAllUsers();
}

@PatchMapping("/status/{orderId}")
public void updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
    orderService.updateOrderStatus(orderId, status);
}
}
