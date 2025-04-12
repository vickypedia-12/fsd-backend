package com.hotel.HotelMgmt.controller;

import com.hotel.HotelMgmt.exception.ResourceNotFoundException;
import com.hotel.HotelMgmt.entity.InventorySupplier;
import com.hotel.HotelMgmt.repository.InventorySupplierRepository;

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

@RestController
@RequestMapping("/api/suppliers")
public class InventorySupplierController {
    @Autowired
    private InventorySupplierRepository InventorySupplierRepository;

    // Get all suppliers
    @GetMapping
    public List<InventorySupplier> getAllSuppliers() {
        return InventorySupplierRepository.findAll();
    }

    // Get InventorySupplier by ID
    @GetMapping("/{id}")
    public ResponseEntity<InventorySupplier> getSupplierById(@PathVariable Long id) {
        InventorySupplier InventorySupplier = InventorySupplierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("InventorySupplier not found with id: " + id));
        return ResponseEntity.ok(InventorySupplier);
    }

    // Create new InventorySupplier
    @PostMapping
    public ResponseEntity<InventorySupplier> createSupplier(@RequestBody InventorySupplier InventorySupplier) {
        InventorySupplier savedSupplier = InventorySupplierRepository.save(InventorySupplier);
        return new ResponseEntity<>(savedSupplier, HttpStatus.CREATED);
    }

    // Update InventorySupplier (full update)
    @PutMapping("/{id}")
    public ResponseEntity<InventorySupplier> updateSupplier(@PathVariable Long id, @RequestBody InventorySupplier supplierDetails) {
        InventorySupplier existingSupplier = InventorySupplierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("InventorySupplier not found with id: " + id));

        existingSupplier.setSupplierName(supplierDetails.getSupplierName());
        existingSupplier.setContactInfo(supplierDetails.getContactInfo());
        existingSupplier.setAddress(supplierDetails.getAddress());

        InventorySupplier updatedSupplier = InventorySupplierRepository.save(existingSupplier);
        return new ResponseEntity<>(updatedSupplier, HttpStatus.OK);
    }

    // Patch InventorySupplier (partial update)
    @PatchMapping("/{id}")
    public ResponseEntity<InventorySupplier> patchSupplier(@PathVariable Long id, @RequestBody InventorySupplier supplierDetails) {
        return InventorySupplierRepository.findById(id)
            .map(existingSupplier -> {
                BeanUtils.copyProperties(supplierDetails, existingSupplier, getNullPropertyNames(supplierDetails));
                InventorySupplier updatedSupplier = InventorySupplierRepository.save(existingSupplier);
                return new ResponseEntity<>(updatedSupplier, HttpStatus.OK);
            })
            .orElseThrow(() -> new ResourceNotFoundException("InventorySupplier not found with id: " + id));
    }

    // Delete InventorySupplier
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {
        InventorySupplier InventorySupplier = InventorySupplierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("InventorySupplier not found with id: " + id));
        InventorySupplierRepository.delete(InventorySupplier);
        return new ResponseEntity<>("InventorySupplier deleted successfully", HttpStatus.OK);
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
