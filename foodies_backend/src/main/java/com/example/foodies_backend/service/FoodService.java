package com.example.foodies_backend.service;

import com.example.foodies_backend.dto.FoodRequest;
import com.example.foodies_backend.dto.FoodResponse;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {

String uploadFile(MultipartFile file);

FoodResponse addFood(FoodRequest request, MultipartFile file);

List<FoodResponse> readFoods();

FoodResponse readFood(String id);

boolean deleteFile(String filename);

void deleteFood(String id);
}
