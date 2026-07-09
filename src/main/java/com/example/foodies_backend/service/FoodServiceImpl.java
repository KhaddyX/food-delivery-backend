// The `FoodServiceImpl` class implements the `FoodService` interface to handle food-related operations.
// It includes methods for uploading files to AWS S3, managing food entities in the database, and converting between DTOs and entities.
package com.example.foodies_backend.service;

import com.example.foodies_backend.dto.FoodRequest;
import com.example.foodies_backend.dto.FoodResponse;
import com.example.foodies_backend.entity.FoodEntity;
import com.example.foodies_backend.repository.FoodRepository;
import com.example.foodies_backend.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {

@Autowired
private S3Client s3Client; // AWS S3 client for file operations

@Autowired
private FoodRepository foodRepository; // Repository for accessing food data

@Value("${aws.s3.bucketname}")
private String bucketName; // AWS S3 bucket name from application properties

// Uploads a file to AWS S3 and returns the file URL
@Override
public String uploadFile(MultipartFile file) {
    String filenameExtension = file.getOriginalFilename()
            .substring(file.getOriginalFilename().lastIndexOf(".") + 1); // Extracting file extension
    String key = UUID.randomUUID().toString() + "." + filenameExtension; // Generating a unique file key

    try {
        // Building the S3 PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        // Uploading the file to S3
        PutObjectResponse response = s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
        );

        // Returning the file URL if the upload is successful
        if (response.sdkHttpResponse().isSuccessful()) {
            return "https://" + bucketName + ".s3.amazonaws.com/" + key;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "File upload failed"
            );
        }
    } catch (IOException ex) {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred while uploading the file"
        );
    }
}

// Adds a new food item to the database and uploads its image to S3
@Override
public FoodResponse addFood(FoodRequest request, MultipartFile file) {
    FoodEntity newFoodEntity = convertToEntity(request); // Converting the request DTO to an entity
    String imageUrl = uploadFile(file); // Uploading the image file to S3
    newFoodEntity.setImageUrl(imageUrl); // Setting the image URL in the entity
    newFoodEntity = foodRepository.save(newFoodEntity); // Saving the entity to the database
    return convertToResponse(newFoodEntity); // Converting the entity to a response DTO
}

// Retrieves all food items from the database
@Override
public List<FoodResponse> readFoods() {
    return foodRepository.findAll().stream()
            .map(this::convertToResponse) // Converting each entity to a response DTO
            .collect(Collectors.toList());
}

// Retrieves a specific food item by its ID
@Override
public FoodResponse readFood(String id) {
    FoodEntity existingFood = foodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Food not found for the id: " + id)); // Throwing an exception if not found
    return convertToResponse(existingFood); // Converting the entity to a response DTO
}

// Deletes a file from AWS S3
@Override
public boolean deleteFile(String filename) {
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(filename)
            .build();
    s3Client.deleteObject(deleteObjectRequest); // Deleting the file from S3
    return true;
}

// Deletes a food item from the database and its associated image from S3
@Override
public void deleteFood(String id) {
    FoodResponse response = readFood(id); // Retrieving the food item
    String imageUrl = response.getImageUrl(); // Extracting the image URL
    String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1); // Extracting the file name from the URL
    boolean isFileDeleted = deleteFile(filename); // Deleting the file from S3
    if (isFileDeleted) {
        foodRepository.deleteById(response.getId()); // Deleting the food item from the database
    }
}

// Converts a FoodRequest DTO to a FoodEntity
private FoodEntity convertToEntity(FoodRequest request) {
    return FoodEntity.builder()
            .name(request.getName())
            .description(request.getDescription())
            .category(request.getCategory())
            .price(request.getPrice())
            .build();
}

// Converts a FoodEntity to a FoodResponse DTO
private FoodResponse convertToResponse(FoodEntity entity) {
    return FoodResponse.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .category(entity.getCategory())
            .price(entity.getPrice())
            .imageUrl(entity.getImageUrl())
            .build();
}
}