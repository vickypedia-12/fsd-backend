package com.hotel.HotelMgmt.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hotel.HotelMgmt.entity.InventoryProduct;
import com.hotel.HotelMgmt.exception.ResourceNotFoundException;
import com.hotel.HotelMgmt.repository.InventoryProductRepository;

@RestController
@RequestMapping("/api/products")
public class InventoryProductController {

    @Autowired
    private InventoryProductRepository InventoryProductRepository;

    // Get all products
    @GetMapping
    public List<InventoryProduct> getAllProducts() {
        return InventoryProductRepository.findAll();
    }

    // Get InventoryProduct by ID
    @GetMapping("/{id}")
    public ResponseEntity<InventoryProduct> getProductById(@PathVariable Long id) {
        InventoryProduct InventoryProduct = InventoryProductRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("InventoryProduct not found with id: " + id));
        return ResponseEntity.ok(InventoryProduct);
    }

    // Create new InventoryProduct
    @PostMapping
    public ResponseEntity<InventoryProduct> createProduct(@RequestBody InventoryProduct InventoryProduct) {
        InventoryProduct savedProduct = InventoryProductRepository.save(InventoryProduct);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // Update InventoryProduct (full update)
    @PutMapping("/{id}")
    public ResponseEntity<InventoryProduct> updateProduct(@PathVariable Long id, @RequestBody InventoryProduct productDetails) {
        InventoryProduct existingProduct = InventoryProductRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("InventoryProduct not found with id: " + id));

        existingProduct.setProductName(productDetails.getProductName());
        existingProduct.setProductDescription(productDetails.getProductDescription());
        existingProduct.setUnitPrice(productDetails.getUnitPrice());
        existingProduct.setReorderLevel(productDetails.getReorderLevel());
        existingProduct.setQuantityInStock(productDetails.getQuantityInStock());
        existingProduct.setCategory(productDetails.getCategory());
        existingProduct.setSupplier(productDetails.getSupplier());

        InventoryProduct updatedProduct = InventoryProductRepository.save(existingProduct);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    // Patch InventoryProduct (partial update)
    @PatchMapping("/{id}")
    public ResponseEntity<InventoryProduct> patchProduct(@PathVariable Long id, @RequestBody InventoryProduct productDetails) {
        return InventoryProductRepository.findById(id)
            .map(existingProduct -> {
                BeanUtils.copyProperties(productDetails, existingProduct, getNullPropertyNames(productDetails));
                InventoryProduct updatedProduct = InventoryProductRepository.save(existingProduct);
                return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
            })
            .orElseThrow(() -> new ResourceNotFoundException("InventoryProduct not found with id: " + id));
    }

    // Delete InventoryProduct
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        InventoryProduct InventoryProduct = InventoryProductRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("InventoryProduct not found with id: " + id));
        InventoryProductRepository.delete(InventoryProduct);
        return new ResponseEntity<>("InventoryProduct deleted successfully", HttpStatus.OK);
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
