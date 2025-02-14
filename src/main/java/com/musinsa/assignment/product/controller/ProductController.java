package com.musinsa.assignment.product.controller;

import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.service.ProductService;
import com.musinsa.assignment.product.dto.response.ApiResponse;
import com.musinsa.assignment.product.dto.response.LowestPriceEachCategoryResponse;
import com.musinsa.assignment.product.dto.response.LowestPriceSingleBrandResponse;
import com.musinsa.assignment.product.dto.response.CategoryPriceInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<Product> productPage = productService.getAllProducts(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getContent());
        response.put("currentPage", productPage.getNumber());
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lowest-price-by-category")
    public ResponseEntity<ApiResponse<LowestPriceEachCategoryResponse>> getLowestPriceByCategory() {
        return ResponseEntity.ok(ApiResponse.success(productService.getLowestPriceEachCategory()));
    }

    @GetMapping("/lowest-price-single-brand")
    public ResponseEntity<ApiResponse<LowestPriceSingleBrandResponse>> getLowestPriceSingleBrand() {
        return ResponseEntity.ok(ApiResponse.success(productService.getLowestPriceSingleBrand()));
    }

    @GetMapping("/category-price-info/{category}")
    public ResponseEntity<ApiResponse<CategoryPriceInfoResponse>> getCategoryPriceInfo(
        @PathVariable String category
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(productService.getCategoryPriceInfo(Category.valueOf(category)))
        );
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @Valid @RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(productService.save(product)));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(
            ApiResponse.success(productService.update(id, product)));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(productService.deleteById(id)));
    }
} 