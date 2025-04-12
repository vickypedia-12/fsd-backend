package com.hotel.HotelMgmt.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
// import java.util.Locale.category;
import java.util.Set;

import com.hotel.HotelMgmt.entity.InventoryCategory;
import com.hotel.HotelMgmt.exception.ResourceNotFoundException;
import com.hotel.HotelMgmt.repository.InventoryCategoryRepository;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "InventoryCategory", description = "Product InventoryCategory")
public class InventoryCategoryController {
    @Autowired
    private InventoryCategoryRepository InventoryCategoryRepository;

    // Get all categories
    @GetMapping
    public List<InventoryCategory> getAllCategories() {
        return InventoryCategoryRepository.findAll();
    }

    // Get InventoryCategory by ID
    @GetMapping("/{id}")
    public ResponseEntity<InventoryCategory> getCategoryById(@PathVariable Long id) {
        return InventoryCategoryRepository.findById(id)
            .map(InventoryCategory -> new ResponseEntity<>(InventoryCategory, HttpStatus.OK))
            .orElseThrow(() -> new ResourceNotFoundException("InventoryCategory not found with id: " + id));
    }

    // Create new InventoryCategory
    @PostMapping
    public ResponseEntity<InventoryCategory> createCategory(@RequestBody InventoryCategory InventoryCategory) {
        InventoryCategory savedCategory = InventoryCategoryRepository.save(InventoryCategory);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    // Update InventoryCategory
    @PutMapping("/{id}")
    public ResponseEntity<InventoryCategory> updateCategory(@PathVariable Long id, @RequestBody InventoryCategory categoryDetails) {
        return InventoryCategoryRepository.findById(id)
            .map(existingCategory -> {
                existingCategory.setCategoryName(categoryDetails.getCategoryName());

                // Optional: You can add more fields here to be updated

                InventoryCategory updatedCategory = InventoryCategoryRepository.save(existingCategory);
                return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
            })
            .orElseThrow(() -> new ResourceNotFoundException("InventoryCategory not found with id: " + id));
    }

    // Patch InventoryCategory (Partial Update)
    @PatchMapping("/{id}")
    public ResponseEntity<InventoryCategory> patchCategory(@PathVariable Long id, @RequestBody InventoryCategory categoryDetails) {
        return InventoryCategoryRepository.findById(id)
            .map(existingCategory -> {
                BeanUtils.copyProperties(categoryDetails, existingCategory, getNullPropertyNames(categoryDetails));
                InventoryCategory updatedCategory = InventoryCategoryRepository.save(existingCategory);
                return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
            })
            .orElseThrow(() -> new ResourceNotFoundException("InventoryCategory not found with id: " + id));
    }

    // Delete InventoryCategory
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        return InventoryCategoryRepository.findById(id)
            .map(InventoryCategory -> {
                InventoryCategoryRepository.delete(InventoryCategory);
                return new ResponseEntity<>("InventoryCategory deleted successfully", HttpStatus.OK);
            })
            .orElseThrow(() -> new ResourceNotFoundException("InventoryCategory not found with id: " + id));
    }

    // Utility method for patch
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
