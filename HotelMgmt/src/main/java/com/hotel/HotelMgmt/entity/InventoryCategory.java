package com.hotel.HotelMgmt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


@Entity
@Table(name = "category")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "categoryId")
public class InventoryCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    
    @Column(nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<InventoryProduct> products = new ArrayList<>();

    // Constructors
    public InventoryCategory() {
    }
    
    // Parameterized constructor
    public InventoryCategory(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<InventoryProduct> getProducts() {
        return products;
    }

    public void setProducts(List<InventoryProduct> products) {
        this.products = products;
    }
    // ... other setters
}
