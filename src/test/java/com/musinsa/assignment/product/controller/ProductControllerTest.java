package com.musinsa.assignment.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.exception.ProductNotFoundException;
import com.musinsa.assignment.product.service.ProductService;
import com.musinsa.assignment.product.dto.response.LowestPriceEachCategoryResponse;
import com.musinsa.assignment.product.dto.response.LowestPriceSingleBrandResponse;
import com.musinsa.assignment.product.dto.response.CategoryPriceInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        Page<Product> page = new PageImpl<>(products);
        when(productService.getAllProducts(0, 10)).thenReturn(page);

        // when & then
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.products[0].id").value(1))
            .andExpect(jsonPath("$.products[0].brandName").value("Nike"))
            .andExpect(jsonPath("$.products[0].category").value("TOP"))
            .andExpect(jsonPath("$.products[0].price").value(50000))
            .andExpect(jsonPath("$.products[1].id").value(2))
            .andExpect(jsonPath("$.products[1].brandName").value("Adidas"))
            .andExpect(jsonPath("$.currentPage").value(0))
            .andExpect(jsonPath("$.totalItems").value(2))
            .andExpect(jsonPath("$.totalPages").value(1));
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
        LowestPriceEachCategoryResponse response = LowestPriceEachCategoryResponse.builder()
            .lowestPriceByCategory(Arrays.asList(
                LowestPriceEachCategoryResponse.CategoryPrice.builder()
                    .category("상의")
                    .brand("Adidas")
                    .price(45000)
                    .build(),
                LowestPriceEachCategoryResponse.CategoryPrice.builder()
                    .category("바지")
                    .brand("Adidas")
                    .price(35000)
                    .build()
            ))
            .totalPrice(80000)
            .build();

        when(productService.getLowestPriceEachCategory()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/lowest-price-by-category"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPrice").value(80000))
            .andExpect(jsonPath("$.lowestPriceByCategory[0].category").value("상의"))
            .andExpect(jsonPath("$.lowestPriceByCategory[0].brand").value("Adidas"))
            .andExpect(jsonPath("$.lowestPriceByCategory[0].price").value(45000));
    }

    @Test
    @DisplayName("단일 브랜드 최저가격 상품을 조회한다")
    void getLowestPriceSingleBrand() throws Exception {
        // given
        LowestPriceSingleBrandResponse response = LowestPriceSingleBrandResponse.builder()
            .brand("Nike")
            .totalPrice(270000)
            .items(Arrays.asList(
                LowestPriceSingleBrandResponse.CategoryPrice.builder()
                    .category("상의")
                    .price(50000)
                    .build(),
                LowestPriceSingleBrandResponse.CategoryPrice.builder()
                    .category("바지")
                    .price(40000)
                    .build()
            ))
            .build();

        when(productService.getLowestPriceSingleBrand()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/lowest-price-single-brand"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brand").value("Nike"))
            .andExpect(jsonPath("$.totalPrice").value(270000))
            .andExpect(jsonPath("$.items[0].category").value("상의"))
            .andExpect(jsonPath("$.items[0].price").value(50000));
    }

    @Test
    @DisplayName("모든 카테고리를 커버하는 브랜드가 없으면 404 응답을 반환한다")
    void getLowestPriceSingleBrand_NoBrandCoversAllCategories() throws Exception {
        // given
        when(productService.getLowestPriceSingleBrand())
            .thenThrow(new NoSuchElementException("No brand covers all categories."));

        // when & then
        mockMvc.perform(get("/api/lowest-price-single-brand"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("No brand covers all categories."));
    }

    @Test
    @DisplayName("카테고리별 최고/최저가 정보를 조회한다")
    void getCategoryPriceInfo() throws Exception {
        // given
        CategoryPriceInfoResponse response = CategoryPriceInfoResponse.builder()
            .highest(CategoryPriceInfoResponse.BrandPrice.builder()
                .brand("Nike")
                .price(100000)
                .build())
            .lowest(CategoryPriceInfoResponse.BrandPrice.builder()
                .brand("Adidas")
                .price(50000)
                .build())
            .build();

        when(productService.getCategoryPriceInfo(Category.TOP)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/category-price-info/TOP"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.highest.brand").value("Nike"))
            .andExpect(jsonPath("$.highest.price").value(100000))
            .andExpect(jsonPath("$.lowest.brand").value("Adidas"))
            .andExpect(jsonPath("$.lowest.price").value(50000));
    }

    @Test
    @DisplayName("상품을 수정한다")
    void updateProduct() throws Exception {
        // given
        Product product = Product.builder()
            .id(1L)
            .brandName("Nike")
            .category(Category.TOP)
            .price(50000)
            .build();

        when(productService.save(any(Product.class))).thenReturn(product);

        // when & then
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.brandName").value("Nike"))
            .andExpect(jsonPath("$.category").value("TOP"))
            .andExpect(jsonPath("$.price").value(50000));
    }
} 