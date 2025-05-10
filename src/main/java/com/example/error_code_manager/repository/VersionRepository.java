package com.example.error_code_manager.repository;

import com.example.error_code_manager.entity.Product;
import com.example.error_code_manager.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<Version, Integer> {
    List<Version> findByProduct(Product product);
    List<Version> findByProductProductId(Integer productId);
}