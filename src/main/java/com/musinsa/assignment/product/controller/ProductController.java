package com.musinsa.assignment.product.controller;

import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lowest-price")
    public ResponseEntity<Map<String, Object>> getLowestPriceEachCategory() {
        Map<String, Object> result = productService.getLowestPriceEachCategory();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/lowest-price-brand")
    public ResponseEntity<Map<String, Object>> getLowestPriceSingleBrand() {
        Map<String, Object> result = productService.getLowestPriceSingleBrand();
        
        // If no brand covers all categories, return 404
        if (result.containsKey("message")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        
        return ResponseEntity.ok(result);
    }
} 