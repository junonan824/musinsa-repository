package com.musinsa.assignment.product.repository;

import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.price = " +
           "(SELECT MIN(p2.price) FROM Product p2 WHERE p2.category = :category)")
    List<Product> findByLowestPriceInCategory(Category category);

    @Query("SELECT DISTINCT p.brandName FROM Product p")
    Set<String> findAllBrandNames();

    @Query("SELECT p FROM Product p WHERE p.brandName = :brandName AND p.category = :category AND p.price = " +
           "(SELECT MIN(p2.price) FROM Product p2 WHERE p2.brandName = :brandName AND p2.category = :category)")
    List<Product> findLowestPriceProductByBrandAndCategory(String brandName, Category category);

    @Query("SELECT COUNT(DISTINCT p.category) FROM Product p WHERE p.brandName = :brandName")
    long countCategoriesByBrand(String brandName);
}