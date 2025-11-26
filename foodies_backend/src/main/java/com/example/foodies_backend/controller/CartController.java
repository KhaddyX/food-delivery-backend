package com.example.foodies_backend.controller;

// Importing necessary classes for handling HTTP requests and responses
import com.example.foodies_backend.dto.CartRequest;
import com.example.foodies_backend.dto.CartResponse;
import com.example.foodies_backend.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// Marking this class as a REST controller and mapping it to the "/api/cart" endpoint
@RestController
@RequestMapping("/api/cart")
// Generating a constructor with all required fields using Lombok
@AllArgsConstructor
public class CartController {

// Injecting the CartService to handle cart-related operations
private final CartService cartService;

// Defining a POST endpoint to add an item to the cart
@PostMapping
public CartResponse addToCart(@RequestBody CartRequest request) {
    // Validating the foodId in the request
    String foodId = request.getFoodId();
    if (foodId == null || foodId.isEmpty()) {
        // Throwing a BAD_REQUEST exception if foodId is missing or empty
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "foodId not found");
    }
    // Delegating the add-to-cart operation to the CartService
    return cartService.addToCart(request);
}

// Defining a GET endpoint to retrieve the current cart
@GetMapping
public CartResponse getCart() {
    // Delegating the get-cart operation to the CartService
    return cartService.getCart();
}

// Defining a DELETE endpoint to clear the cart
@DeleteMapping
@ResponseStatus(HttpStatus.NO_CONTENT) // Setting the response status to NO_CONTENT
public void clearCart() {
    // Delegating the clear-cart operation to the CartService
    cartService.clearCart();
}

// Defining a POST endpoint to remove an item from the cart
@PostMapping("/remove")
public CartResponse removeFromCart(@RequestBody CartRequest request) {
    // Validating the foodId in the request
    String foodId = request.getFoodId();
    if (foodId == null || foodId.isEmpty()) {
        // Throwing a BAD_REQUEST exception if foodId is missing or empty
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "foodId not found");
    }
    // Delegating the remove-from-cart operation to the CartService
    return cartService.removeFromCart(request);
}
}