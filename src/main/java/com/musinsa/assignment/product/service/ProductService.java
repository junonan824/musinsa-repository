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
import java.util.Map;
import java.util.ArrayList;
import com.musinsa.assignment.product.domain.Category;
import java.util.stream.Collectors;
import com.musinsa.assignment.product.dto.BrandCategoryPriceDto;
import com.musinsa.assignment.product.dto.response.LowestPriceEachCategoryResponse;
import com.musinsa.assignment.product.dto.response.LowestPriceSingleBrandResponse;
import com.musinsa.assignment.product.dto.response.CategoryPriceInfoResponse;
import java.util.NoSuchElementException;

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

    public LowestPriceEachCategoryResponse getLowestPriceEachCategory() {
        List<LowestPriceEachCategoryResponse.CategoryPrice> categoryPrices = new ArrayList<>();
        int totalPrice = 0;

        for (Category category : Category.values()) {
            List<Product> lowestPriceProducts = productRepository.findByLowestPriceInCategory(category);
            
            if (!lowestPriceProducts.isEmpty()) {
                Product lowestPriceProduct = lowestPriceProducts.get(0);
                
                categoryPrices.add(LowestPriceEachCategoryResponse.CategoryPrice.builder()
                    .category(category.getKorName())
                    .brand(lowestPriceProduct.getBrandName())
                    .price(lowestPriceProduct.getPrice())
                    .build());
                
                totalPrice += lowestPriceProduct.getPrice();
            }
        }

        return LowestPriceEachCategoryResponse.builder()
            .lowestPriceByCategory(categoryPrices)
            .totalPrice(totalPrice)
            .build();
    }

    public LowestPriceSingleBrandResponse getLowestPriceSingleBrand() {
        List<BrandCategoryPriceDto> pricesByBrandAndCategory = productRepository.findLowestPricesByBrandAndCategory();
        Map<String, List<BrandCategoryPriceDto>> brandGroups = pricesByBrandAndCategory.stream()
            .collect(Collectors.groupingBy(BrandCategoryPriceDto::getBrandName));

        String selectedBrand = null;
        int lowestTotalPrice = Integer.MAX_VALUE;
        List<LowestPriceSingleBrandResponse.CategoryPrice> selectedItems = null;

        for (Map.Entry<String, List<BrandCategoryPriceDto>> entry : brandGroups.entrySet()) {
            String brand = entry.getKey();
            List<BrandCategoryPriceDto> items = entry.getValue();

            if (items.size() == Category.values().length) {
                int totalPrice = items.stream()
                    .mapToInt(BrandCategoryPriceDto::getPrice)
                    .sum();

                if (totalPrice < lowestTotalPrice) {
                    lowestTotalPrice = totalPrice;
                    selectedBrand = brand;
                    selectedItems = items.stream()
                        .map(item -> LowestPriceSingleBrandResponse.CategoryPrice.builder()
                            .category(item.getCategory().getKorName())
                            .price(item.getPrice())
                            .build())
                        .collect(Collectors.toList());
                }
            }
        }

        if (selectedBrand == null) {
            throw new NoSuchElementException("No brand covers all categories.");
        }

        return LowestPriceSingleBrandResponse.builder()
            .brand(selectedBrand)
            .totalPrice(lowestTotalPrice)
            .items(selectedItems)
            .build();
    }

    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllOrderByIdDesc(pageable);
    }

    public CategoryPriceInfoResponse getCategoryPriceInfo(Category category) {
        List<Product> highestPriceProducts = productRepository.findByHighestPriceInCategory(category);
        List<Product> lowestPriceProducts = productRepository.findByLowestPriceInCategory(category);
        
        if (highestPriceProducts.isEmpty() || lowestPriceProducts.isEmpty()) {
            throw new NoSuchElementException("No products found for category: " + category);
        }

        Product highestProduct = highestPriceProducts.get(0);
        Product lowestProduct = lowestPriceProducts.get(0);
        
        return CategoryPriceInfoResponse.builder()
            .highest(CategoryPriceInfoResponse.BrandPrice.builder()
                .brand(highestProduct.getBrandName())
                .price(highestProduct.getPrice())
                .build())
            .lowest(CategoryPriceInfoResponse.BrandPrice.builder()
                .brand(lowestProduct.getBrandName())
                .price(lowestProduct.getPrice())
                .build())
            .build();
    }
} 