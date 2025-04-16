package com.hotel.HotelMgmt.controller;

import com.hotel.HotelMgmt.entity.User;
import com.hotel.HotelMgmt.entity.UserPreferences;
import com.hotel.HotelMgmt.repository.UserPreferencesRepository;
import com.hotel.HotelMgmt.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user-preferences")
public class UserPreferencesController {

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create-or-update")
    public ResponseEntity<?> createOrUpdatePreferences(
            @RequestParam Long userId,
            @Valid @RequestBody UserPreferences preferences) {
        // Validate user existence
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found!");
        }

        // Check if preferences already exist for the user
        Optional<UserPreferences> existingPreferences = userPreferencesRepository.findByUserId(userId);
        if (existingPreferences.isPresent()) {
            // Update existing preferences
            UserPreferences existing = existingPreferences.get();
            existing.setDietType(preferences.getDietType());
            existing.setFavoriteCuisines(preferences.getFavoriteCuisines());
            existing.setSpiceToleranceLevel(preferences.getSpiceToleranceLevel());
            existing.setFavoriteDish(preferences.getFavoriteDish());
            existing.setFoodAllergies(preferences.getFoodAllergies());
            existing.setDiningPreference(preferences.getDiningPreference());
            existing.setUsuallyDineWith(preferences.getUsuallyDineWith());
            userPreferencesRepository.save(existing);
            return ResponseEntity.ok("User preferences updated successfully!");
        }

        // Create new preferences
        preferences.setUser(userOptional.get());
        userPreferencesRepository.save(preferences);
        return ResponseEntity.ok("User preferences created successfully!");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPreferences(@PathVariable Long userId) {
        // Validate user existence
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found!");
        }

        // Fetch user preferences
        Optional<UserPreferences> preferences = userPreferencesRepository.findByUserId(userId);
        if (preferences.isEmpty()) {
            return ResponseEntity.badRequest().body("No preferences found for this user!");
        }

        return ResponseEntity.ok(preferences.get());
    }
}