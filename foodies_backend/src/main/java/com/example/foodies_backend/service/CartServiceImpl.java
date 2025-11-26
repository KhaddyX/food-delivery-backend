package com.example.foodies_backend.service;

import com.example.foodies_backend.dto.CartRequest;
import com.example.foodies_backend.dto.CartResponse;
import com.example.foodies_backend.entity.CartEntity;
import com.example.foodies_backend.repository.CartRespository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

// Injecting the CartRespository to interact with the database
private final CartRespository cartRespository;

// Injecting the UserService to fetch the logged-in user's details
private final UserService userService;

// Adds an item to the cart for the logged-in user
@Override
public CartResponse addToCart(CartRequest request) {
    // Fetching the logged-in user's ID
    String loggedInUserId = userService.findByUserId();

    // Retrieving the user's cart or creating a new one if it doesn't exist
    Optional<CartEntity> cartOptional = cartRespository.findByUserId(loggedInUserId);
    CartEntity cart = cartOptional.orElseGet(() -> new CartEntity(loggedInUserId, new HashMap<>()));

    // Adding the item to the cart or updating its quantity
    Map<String, Integer> cartItems = cart.getItems();
    cartItems.put(request.getFoodId(), cartItems.getOrDefault(request.getFoodId(), 0) + 1);
    cart.setItems(cartItems);

    // Saving the updated cart to the database
    cart = cartRespository.save(cart);

    // Converting the cart entity to a response DTO and returning it
    return convertToResponse(cart);
}

// Retrieves the cart for the logged-in user
@Override
public CartResponse getCart() {
    // Fetching the logged-in user's ID
    String loggedInUserId = userService.findByUserId();

    // Retrieving the user's cart or creating an empty one if it doesn't exist
    CartEntity entity = cartRespository.findByUserId(loggedInUserId)
            .orElse(new CartEntity(null, loggedInUserId, new HashMap<>()));

    // Converting the cart entity to a response DTO and returning it
    return convertToResponse(entity);
}

// Clears the cart for the logged-in user
@Override
public void clearCart() {
    // Fetching the logged-in user's ID
    String loggedInUserId = userService.findByUserId();

    // Deleting the user's cart from the database
    cartRespository.deleteByUserId(loggedInUserId);
}

// Removes an item from the cart for the logged-in user
@Override
public CartResponse removeFromCart(CartRequest cartRequest) {
    // Fetching the logged-in user's ID
    String loggedInUserId = userService.findByUserId();

    // Retrieving the user's cart or throwing an exception if it doesn't exist
    CartEntity entity = cartRespository.findByUserId(loggedInUserId)
            .orElseThrow(() -> new RuntimeException("Cart is not found"));

    // Removing the item from the cart or reducing its quantity
    Map<String, Integer> cartItems = entity.getItems();
    if (cartItems.containsKey(cartRequest.getFoodId())) {
        int currentQty = cartItems.get(cartRequest.getFoodId());
        if (currentQty > 0) {
            cartItems.put(cartRequest.getFoodId(), currentQty - 1);
        } else {
            cartItems.remove(cartRequest.getFoodId());
        }
        // Saving the updated cart to the database
        entity = cartRespository.save(entity);
    }

    // Converting the cart entity to a response DTO and returning it
    return convertToResponse(entity);
}

// Converts a CartEntity to a CartResponse DTO
private CartResponse convertToResponse(CartEntity cartEntity) {
    return CartResponse.builder()
            .id(cartEntity.getId())
            .userId(cartEntity.getUserId())
            .items(cartEntity.getItems())
            .build();
}
}