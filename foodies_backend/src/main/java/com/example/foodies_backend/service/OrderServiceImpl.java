package com.example.foodies_backend.service;

import com.example.foodies_backend.dto.OrderRequest;
import com.example.foodies_backend.dto.OrderResponse;
import com.example.foodies_backend.entity.OrderEntity;
import com.example.foodies_backend.repository.CartRespository;
import com.example.foodies_backend.repository.OrderRepository;
import com.example.foodies_backend.service.OrderService;
import com.example.foodies_backend.service.UserService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

@Autowired
private OrderRepository orderRepository;

@Autowired
private UserService userService;

@Autowired
private CartRespository cartRespository;

@Value("${paystack.secret.key}")
private String PAYSTACK_SECRET_KEY;

@Override
public OrderResponse createOrderWithPayment(OrderRequest request) {
    // Convert and set user
    OrderEntity newOrder = convertToEntity(request);
    String loggedInUserId = userService.findByUserId();
    newOrder.setUserId(loggedInUserId);

    // Save with userId immediately
    newOrder = orderRepository.save(newOrder);

    // Defensive checks
    if (newOrder.getEmail() == null || newOrder.getEmail().isEmpty()) {
        throw new RuntimeException("Order email is missing — cannot initialize payment");
    }

    if (newOrder.getAmount() <= 0) {
        throw new RuntimeException("Invalid order amount — cannot initialize payment");
    }

    try {
        HttpResponse<JsonNode> response = Unirest.post("https://api.paystack.co/transaction/initialize")
                .header("Authorization", "Bearer " + PAYSTACK_SECRET_KEY)
                .header("Content-Type", "application/json")
                .body(new JSONObject()
                        .put("email", newOrder.getEmail())
                        .put("amount", (int) (newOrder.getAmount() * 100))) // convert to kobo
                .asJson();

        System.out.println("Paystack response: " + response.getBody());

        if (response.getStatus() == 200 && response.getBody().getObject().getBoolean("status")) {
            JSONObject data = response.getBody().getObject().getJSONObject("data");
            String authorizationUrl = data.getString("authorization_url");
            String reference = data.getString("reference");

            newOrder.setPaymentReference(reference);
            newOrder = orderRepository.save(newOrder);

            OrderResponse orderResponse = convertToResponse(newOrder);
            orderResponse.setAuthorizationUrl(authorizationUrl);
            return orderResponse;
        } else {
            System.err.println("Paystack error response: " + response.getBody());
            throw new RuntimeException("Payment initialization failed with Paystack: " + response.getBody());
        }
    } catch (Exception e) {
        System.err.println("Exception during Paystack initialization: " + e.getMessage());
        throw new RuntimeException("Payment initialization failed with Paystack", e);
    }
}



@Override
public void verifyPayment(Map<String, String> paymentData, String status) {
    String reference = paymentData.get("reference");
    OrderEntity existingOrder = orderRepository.findByPaymentReference(reference)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    // Verify transaction with Paystack
    HttpResponse<JsonNode> response = Unirest.get("https://api.paystack.co/transaction/verify/" + reference)
            .header("Authorization", "Bearer " + PAYSTACK_SECRET_KEY)
            .asJson();

    if (response.getStatus() == 200 &&
            response.getBody().getObject().getJSONObject("data").getString("status").equalsIgnoreCase("success")) {

        existingOrder.setPaymentStatus("paid");
        existingOrder.setOrderStatus("preparing");
        orderRepository.save(existingOrder);
        cartRespository.deleteByUserId(existingOrder.getUserId());
    } else {
        throw new RuntimeException("Payment verification failed or payment was not successful");
    }
}

@Override
public List<OrderResponse> getUserOrders() {
    String loggedInUserId = userService.findByUserId();
    List<OrderEntity> list = orderRepository.findByUserId(loggedInUserId);
    return list.stream().map(this::convertToResponse).collect(Collectors.toList());
}

@Override
public void removeOrder(String orderId) {
    orderRepository.deleteById(orderId);
}

@Override
public List<OrderResponse> getOrdersOfAllUsers() {
    List<OrderEntity> list = orderRepository.findAll();
    return list.stream().map(this::convertToResponse).collect(Collectors.toList());
}

@Override
public void updateOrderStatus(String orderId, String status) {
    OrderEntity entity = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    entity.setOrderStatus(status);
    orderRepository.save(entity);
}

private OrderResponse convertToResponse(OrderEntity newOrder) {
    return OrderResponse.builder()
            .id(newOrder.getId())
            .amount(newOrder.getAmount())
            .userAddress(newOrder.getUserAddress())
            .userId(newOrder.getUserId())
            .paymentReference(newOrder.getPaymentReference())
            .paymentStatus(newOrder.getPaymentStatus())
            .orderStatus(newOrder.getOrderStatus())
            .email(newOrder.getEmail())
            .phoneNumber(newOrder.getPhoneNumber())
            .orderedItems(newOrder.getOrderedItems())
            .authorizationUrl(null) // default null, will be set in createOrder
            .build();
}

private OrderEntity convertToEntity(OrderRequest request) {
    return OrderEntity.builder()
            .userAddress(request.getUserAddress())
            .amount(request.getAmount())
            .orderedItems(request.getOrderedItems())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .orderStatus(request.getOrderStatus())
            .build();
}
}
