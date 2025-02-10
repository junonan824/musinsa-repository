package com.musinsa.assignment.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.exception.ProductNotFoundException;
import com.musinsa.assignment.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("전체 상품 목록을 조회한다")
    void getAllProducts() throws Exception {
        // given
        List<Product> products = Arrays.asList(
            Product.builder().id(1L).brandName("Nike").category(Category.TOP).price(50000).build(),
            Product.builder().id(2L).brandName("Adidas").category(Category.PANTS).price(35000).build()
        );
        when(productService.findAll()).thenReturn(products);

        // when & then
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].brandName").value("Nike"))
            .andExpect(jsonPath("$[0].category").value("TOP"))
            .andExpect(jsonPath("$[0].price").value(50000))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].brandName").value("Adidas"));
    }

    @Test
    @DisplayName("새로운 상품을 생성한다")
    void createProduct() throws Exception {
        // given
        Product newProduct = Product.builder()
            .brandName("Nike")
            .category(Category.TOP)
            .price(50000)
            .build();
        
        Product savedProduct = Product.builder()
            .id(1L)
            .brandName("Nike")
            .category(Category.TOP)
            .price(50000)
            .build();

        when(productService.save(any(Product.class))).thenReturn(savedProduct);

        // when & then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.brandName").value("Nike"))
            .andExpect(jsonPath("$.category").value("TOP"))
            .andExpect(jsonPath("$.price").value(50000));
    }

    @Test
    @DisplayName("상품을 삭제한다")
    void deleteProduct() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 상품을 삭제하면 404 응답을 반환한다")
    void deleteProduct_NotFound() throws Exception {
        // given
        doThrow(new ProductNotFoundException(1L))
            .when(productService).deleteById(1L);

        // when & then
        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Product not found with id: 1"));
    }

    @Test
    @DisplayName("카테고리별 최저가격 상품을 조회한다")
    void getLowestPriceByCategory() throws Exception {
        // given
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> lowestPriceByCategory = Arrays.asList(
            Map.of("category", "상의", "brand", "Adidas", "price", 45000),
            Map.of("category", "바지", "brand", "Adidas", "price", 35000),
            Map.of("category", "스니커즈", "brand", "Adidas", "price", 55000)
        );
        response.put("lowestPriceByCategory", lowestPriceByCategory);
        response.put("totalPrice", 135000);

        when(productService.getLowestPriceEachCategory()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/lowest-price-by-category"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPrice").value(135000))
            .andExpect(jsonPath("$.lowestPriceByCategory[0].category").value("상의"))
            .andExpect(jsonPath("$.lowestPriceByCategory[0].brand").value("Adidas"))
            .andExpect(jsonPath("$.lowestPriceByCategory[0].price").value(45000));
    }

    @Test
    @DisplayName("모든 카테고리 상품이 있는 브랜드 중 최저가 브랜드를 조회한다")
    void getLowestPriceSingleBrand() throws Exception {
        // given
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> items = Arrays.asList(
            Map.of("category", "상의", "price", 45000),
            Map.of("category", "바지", "price", 35000),
            Map.of("category", "스니커즈", "price", 55000)
        );
        response.put("brand", "Adidas");
        response.put("totalPrice", 135000);
        response.put("items", items);

        when(productService.getLowestPriceSingleBrand()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/lowest-price-single-brand"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brand").value("Adidas"))
            .andExpect(jsonPath("$.totalPrice").value(135000))
            .andExpect(jsonPath("$.items[0].category").value("상의"))
            .andExpect(jsonPath("$.items[0].price").value(45000));
    }

    @Test
    @DisplayName("모든 카테고리를 커버하는 브랜드가 없으면 404 응답을 반환한다")
    void getLowestPriceSingleBrand_NoBrandCoversAllCategories() throws Exception {
        // given
        Map<String, Object> response = new HashMap<>();
        response.put("message", "No brand covers all categories.");

        when(productService.getLowestPriceSingleBrand()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/lowest-price-single-brand"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("No brand covers all categories."));
    }
} 