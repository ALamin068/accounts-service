package com.JDMGod.accounts_service.repo;

import com.JDMGod.accounts_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(String category);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByStatus(String status);
    
    Page<Product> findByStatus(String status, Pageable pageable);
    
    Page<Product> findByCategoryAndStatus(String category, String status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% AND p.status = :status")
    Page<Product> findByNameContainingAndStatus(@Param("name") String name, @Param("status") String status, Pageable pageable);
    
    List<Product> findBySellerId(Long sellerId);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.status = 'ACTIVE'")
    List<Product> findAvailableProducts();
}
