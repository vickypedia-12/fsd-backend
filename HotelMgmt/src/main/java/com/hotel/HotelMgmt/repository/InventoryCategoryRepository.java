package com.hotel.HotelMgmt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel.HotelMgmt.entity.InventoryCategory;

public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory, Long> {

}
