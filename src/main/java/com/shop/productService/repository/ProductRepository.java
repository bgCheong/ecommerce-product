package com.shop.productService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.productService.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> 
{
}