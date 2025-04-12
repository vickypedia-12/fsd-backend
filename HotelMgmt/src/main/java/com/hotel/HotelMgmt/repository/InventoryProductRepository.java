package com.hotel.HotelMgmt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel.HotelMgmt.entity.InventoryProduct;

public interface InventoryProductRepository extends JpaRepository <InventoryProduct, Long>{
    
}
