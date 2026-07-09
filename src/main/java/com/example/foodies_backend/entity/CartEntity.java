// The `CartEntity` class represents a MongoDB document for storing cart information.
// It uses Lombok annotations to reduce boilerplate code and Spring Data annotations for MongoDB mapping.

package com.example.foodies_backend.entity;

import lombok.AllArgsConstructor; // Generates a constructor with all fields as parameters.
import lombok.Builder; // Enables the builder pattern for creating instances of the class.
import lombok.Data; // Generates getters, setters, equals, hashCode, and toString methods.
import lombok.NoArgsConstructor; // Generates a no-argument constructor.
import org.springframework.data.annotation.Id; // Marks the field as the primary key for the MongoDB document.
import org.springframework.data.mongodb.core.mapping.Document; // Maps the class to a MongoDB collection.

import java.util.HashMap; // Provides a default implementation for the `items` map.
import java.util.Map; // Represents the cart items as a map of food IDs to quantities.

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "carts") // Specifies the MongoDB collection name as "carts".
public class CartEntity {
@Id // Marks the `id` field as the unique identifier for the document.
private String id; // The unique identifier for the cart.
private String userId; // The ID of the user who owns the cart.
private Map<String, Integer> items = new HashMap<>(); // A map of food item IDs to their quantities.

// Custom constructor to initialize the cart with a user ID and items.
public CartEntity(String userId, Map<String, Integer> items) {
    this.userId = userId;
    this.items = items;
}
}