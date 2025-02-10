package com.musinsa.assignment.product.service;

import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.repository.ProductRepository;
import com.musinsa.assignment.product.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import com.musinsa.assignment.product.domain.Category;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Map<String, Object> getLowestPriceEachCategory() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> lowestPriceByCategory = new ArrayList<>();
        int totalPrice = 0;

        for (Category category : Category.values()) {
            List<Product> lowestPriceProducts = productRepository.findByLowestPriceInCategory(category);
            
            if (!lowestPriceProducts.isEmpty()) {
                // Get the first product if there are multiple with same price
                Product lowestPriceProduct = lowestPriceProducts.get(0);
                
                Map<String, Object> categoryInfo = new HashMap<>();
                categoryInfo.put("category", category.getKorName());
                categoryInfo.put("brand", lowestPriceProduct.getBrandName());
                categoryInfo.put("price", lowestPriceProduct.getPrice());
                
                lowestPriceByCategory.add(categoryInfo);
                totalPrice += lowestPriceProduct.getPrice();
            }
        }

        result.put("lowestPriceByCategory", lowestPriceByCategory);
        result.put("totalPrice", totalPrice);
        
        return result;
    }
} 