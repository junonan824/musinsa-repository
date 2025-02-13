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
import com.musinsa.assignment.product.dto.BrandCategoryPriceDto;

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
        List<BrandCategoryPriceDto> pricesByBrandAndCategory = productRepository.findLowestPricesByBrandAndCategory();

        // 브랜드별로 그룹화
        Map<String, List<BrandCategoryPriceDto>> brandGroups = pricesByBrandAndCategory.stream()
            .collect(Collectors.groupingBy(BrandCategoryPriceDto::getBrandName));

        String selectedBrand = null;
        int lowestTotalPrice = Integer.MAX_VALUE;
        List<Map<String, Object>> selectedItems = null;

        // 각 브랜드별로 처리
        for (Map.Entry<String, List<BrandCategoryPriceDto>> entry : brandGroups.entrySet()) {
            String brand = entry.getKey();
            List<BrandCategoryPriceDto> items = entry.getValue();

            // 모든 카테고리(8개)를 가지고 있는지 확인
            if (items.size() == Category.values().length) {
                int totalPrice = items.stream()
                    .mapToInt(BrandCategoryPriceDto::getPrice)
                    .sum();

                if (totalPrice < lowestTotalPrice) {
                    lowestTotalPrice = totalPrice;
                    selectedBrand = brand;
                    selectedItems = items.stream()
                        .map(item -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("category", item.getCategory().getKorName());
                            map.put("price", item.getPrice());
                            return map;
                        })
                        .collect(Collectors.toList());
                }
            }
        }

        if (selectedBrand == null) {
            result.put("message", "No brand covers all categories.");
        } else {
            result.put("brand", selectedBrand);
            result.put("totalPrice", lowestTotalPrice);
            result.put("items", selectedItems);
        }

        return result;
    }

    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllOrderByIdDesc(pageable);
    }

    public Map<String, Object> getCategoryPriceInfo(Category category) {
        Map<String, Object> result = new HashMap<>();
        
        // 최고가 상품 조회
        List<Product> highestPriceProducts = productRepository.findByHighestPriceInCategory(category);
        // 최저가 상품 조회
        List<Product> lowestPriceProducts = productRepository.findByLowestPriceInCategory(category);
        
        if (!highestPriceProducts.isEmpty() && !lowestPriceProducts.isEmpty()) {
            Product highestProduct = highestPriceProducts.get(0);
            Product lowestProduct = lowestPriceProducts.get(0);
            
            Map<String, Object> highest = new HashMap<>();
            highest.put("brand", highestProduct.getBrandName());
            highest.put("price", highestProduct.getPrice());
            
            Map<String, Object> lowest = new HashMap<>();
            lowest.put("brand", lowestProduct.getBrandName());
            lowest.put("price", lowestProduct.getPrice());
            
            result.put("highest", highest);
            result.put("lowest", lowest);
        }
        
        return result;
    }
} 