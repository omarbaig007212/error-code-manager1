package com.example.error_code_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.error_code_manager.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}