package com.example.productOrder.app.api.repository;

import com.example.productOrder.app.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%')) ORDER BY p.amount DESC")  // LOWER = case in sensitive
    List<Product> findAllProducts(@Param("searchText") String searchText);

}
