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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "상품 관리", description = "상품 CRUD 및 가격 분석 API")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "상품 목록 조회", description = "전체 상품 목록을 페이지네이션하여 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청"
        )
    })
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllProducts(
        @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {
        Page<Product> productPage = productService.getAllProducts(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getContent());
        response.put("currentPage", productPage.getNumber());
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "카테고리별 최저가 조회", description = "각 카테고리별 최저가 상품을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
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

    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "등록 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 입력값"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", 
            description = "중복된 상품"
        )
    })
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Product>> createProduct(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 정보")
        @Valid @RequestBody Product product
    ) {
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