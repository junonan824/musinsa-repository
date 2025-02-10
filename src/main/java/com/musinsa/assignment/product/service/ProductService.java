package com.musinsa.assignment.product.service;

import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.repository.ProductRepository;
import com.musinsa.assignment.product.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import com.musinsa.assignment.product.domain.Category;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Map<String, Object> getLowestPriceSingleBrand() {
        Map<String, Object> result = new HashMap<>();
        Set<String> allBrands = productRepository.findAllBrandNames();
        
        // Get all categories that have at least one product
        Set<Category> existingCategories = productRepository.findAll().stream()
            .map(Product::getCategory)
            .collect(Collectors.toSet());
        
        String selectedBrand = null;
        int lowestTotalPrice = Integer.MAX_VALUE;
        
        for (String brand : allBrands) {
            // Check if this brand covers all existing categories
            long categoryCount = productRepository.countCategoriesByBrand(brand);
            if (categoryCount == existingCategories.size()) {
                int brandTotalPrice = 0;
                List<Map<String, Object>> categoryPrices = new ArrayList<>();
                
                // Calculate total price for this brand
                for (Category category : existingCategories) {
                    List<Product> products = productRepository.findLowestPriceProductByBrandAndCategory(brand, category);
                    if (!products.isEmpty()) {
                        Product lowestPriceProduct = products.get(0);
                        brandTotalPrice += lowestPriceProduct.getPrice();
                        
                        Map<String, Object> categoryInfo = new HashMap<>();
                        categoryInfo.put("category", category.getKorName());
                        categoryInfo.put("price", lowestPriceProduct.getPrice());
                        categoryPrices.add(categoryInfo);
                    }
                }
                
                // Update selected brand if this one has lower total price
                if (brandTotalPrice < lowestTotalPrice) {
                    lowestTotalPrice = brandTotalPrice;
                    selectedBrand = brand;
                    result.put("items", categoryPrices);
                }
            }
        }
        
        if (selectedBrand == null) {
            result.put("message", "No brand covers all categories.");
        } else {
            result.put("brand", selectedBrand);
            result.put("totalPrice", lowestTotalPrice);
        }
        
        return result;
    }

    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }
} 