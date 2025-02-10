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
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
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
} 