package com.example.foodies_backend.controller;

// Importing necessary classes for handling HTTP requests and responses
import com.example.foodies_backend.dto.FoodRequest;
import com.example.foodies_backend.dto.FoodResponse;
import com.example.foodies_backend.service.FoodService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Marking this class as a REST controller and mapping it to the "/api/foods" endpoint
@RestController
@RequestMapping("/api/foods")
// Generating a constructor with all required fields using Lombok
@AllArgsConstructor
// Allowing cross-origin requests from any origin
@CrossOrigin("*")
public class FoodController {

// Injecting the FoodService to handle food-related operations
private final FoodService foodService;

// Defining a POST endpoint to add a new food item
@PostMapping
public FoodResponse addFood(@RequestPart("food") String foodString,
                            @RequestPart("file") MultipartFile file) {
    // Creating an ObjectMapper instance to parse JSON
    ObjectMapper objectMapper = new ObjectMapper();
    FoodRequest request = null;
    try {
        // Parsing the JSON string into a FoodRequest object
        request = objectMapper.readValue(foodString, FoodRequest.class);
    } catch (JsonProcessingException ex) {
        // Throwing a BAD_REQUEST exception if the JSON format is invalid
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON format");
    }
    // Delegating the add-food operation to the FoodService
    FoodResponse response = foodService.addFood(request, file);
    return response;
}

// Defining a GET endpoint to retrieve all food items
@GetMapping
public List<FoodResponse> readFoods() {
    // Delegating the read-foods operation to the FoodService
    return foodService.readFoods();
}

// Defining a GET endpoint to retrieve a specific food item by ID
@GetMapping("/{id}")
public FoodResponse readFood(@PathVariable String id) {
    // Delegating the read-food operation to the FoodService
    return foodService.readFood(id);
}

// Defining a DELETE endpoint to delete a specific food item by ID
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT) // Setting the response status to NO_CONTENT
public void deleteFood(@PathVariable String id) {
    // Delegating the delete-food operation to the FoodService
    foodService.deleteFood(id);
}
}