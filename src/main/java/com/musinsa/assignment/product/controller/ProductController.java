package com.musinsa.assignment.product.controller;

import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<Product> productPage = productService.getAllProducts(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getContent());
        response.put("currentPage", productPage.getNumber());
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lowest-price-by-category")
    public ResponseEntity<Map<String, Object>> getLowestPriceByCategory() {
        Map<String, Object> result = productService.getLowestPriceEachCategory();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/lowest-price-single-brand")
    public ResponseEntity<Map<String, Object>> getLowestPriceSingleBrand() {
        Map<String, Object> result = productService.getLowestPriceSingleBrand();
        
        if (result.containsKey("message")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/category-price-info/{category}")
    public ResponseEntity<Map<String, Object>> getCategoryPriceInfo(@PathVariable String category) {
        Map<String, Object> result = productService.getCategoryPriceInfo(Category.valueOf(category));
        return ResponseEntity.ok(result);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
        @PathVariable Long id,
        @RequestBody Product product
    ) {
        product.setId(id);
        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }
} 