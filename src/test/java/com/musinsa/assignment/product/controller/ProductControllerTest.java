package com.musinsa.assignment.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.exception.ProductNotFoundException;
import com.musinsa.assignment.product.service.ProductService;
import com.musinsa.assignment.product.dto.response.LowestPriceEachCategoryResponse;
import com.musinsa.assignment.product.dto.response.LowestPriceSingleBrandResponse;
import com.musinsa.assignment.product.dto.response.CategoryPriceInfoResponse;
import com.musinsa.assignment.product.exception.DuplicateProductException;
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
import static org.mockito.ArgumentMatchers.eq;

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
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.products[0].id").value(1))
            .andExpect(jsonPath("$.data.products[0].brandName").value("Nike"))
            .andExpect(jsonPath("$.data.products[0].category").value("TOP"))
            .andExpect(jsonPath("$.data.products[0].price").value(50000))
            .andExpect(jsonPath("$.data.products[1].id").value(2))
            .andExpect(jsonPath("$.data.products[1].brandName").value("Adidas"))
            .andExpect(jsonPath("$.data.currentPage").value(0))
            .andExpect(jsonPath("$.data.totalItems").value(2))
            .andExpect(jsonPath("$.data.totalPages").value(1));
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
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.brandName").value("Nike"))
            .andExpect(jsonPath("$.data.category").value("TOP"))
            .andExpect(jsonPath("$.data.price").value(50000));
    }

    @Test
    @DisplayName("상품을 삭제한다")
    void deleteProduct() throws Exception {
        // given
        Product product = Product.builder()
            .id(1L)
            .brandName("TestBrand")
            .category(Category.TOP)
            .price(15000)
            .build();
        when(productService.deleteById(1L)).thenReturn(product);

        // when & then
        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.brandName").value("TestBrand"))
            .andExpect(jsonPath("$.data.category").value("TOP"))
            .andExpect(jsonPath("$.data.price").value(15000));
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
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.status").value(404))
            .andExpect(jsonPath("$.error.message").value("Product not found with id: 1"));
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
                    .build()
            ))
            .totalPrice(80000)
            .build();

        when(productService.getLowestPriceEachCategory()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/lowest-price-by-category"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalPrice").value(80000))
            .andExpect(jsonPath("$.data.lowestPriceByCategory[0].category").value("상의"))
            .andExpect(jsonPath("$.data.lowestPriceByCategory[0].brand").value("Adidas"))
            .andExpect(jsonPath("$.data.lowestPriceByCategory[0].price").value(45000));
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
                    .build()
            ))
            .build();

        when(productService.getLowestPriceSingleBrand()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/lowest-price-single-brand"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.brand").value("Nike"))
            .andExpect(jsonPath("$.data.totalPrice").value(270000))
            .andExpect(jsonPath("$.data.items[0].category").value("상의"))
            .andExpect(jsonPath("$.data.items[0].price").value(50000));
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
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.status").value(404))
            .andExpect(jsonPath("$.error.message").value("No brand covers all categories."));
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
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.highest.brand").value("Nike"))
            .andExpect(jsonPath("$.data.highest.price").value(100000))
            .andExpect(jsonPath("$.data.lowest.brand").value("Adidas"))
            .andExpect(jsonPath("$.data.lowest.price").value(50000));
    }

    @Test
    @DisplayName("상품을 수정한다")
    void updateProduct() throws Exception {
        // given
        Long productId = 1L;
        Product updateProduct = Product.builder()
            .brandName("Nike")
            .category(Category.TOP)
            .price(50000)
            .build();

        Product updatedProduct = Product.builder()
            .id(productId)
            .brandName("Nike")
            .category(Category.TOP)
            .price(50000)
            .build();

        when(productService.update(eq(productId), any(Product.class))).thenReturn(updatedProduct);

        // when & then
        mockMvc.perform(put("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(productId))
            .andExpect(jsonPath("$.data.brandName").value("Nike"))
            .andExpect(jsonPath("$.data.category").value("TOP"))
            .andExpect(jsonPath("$.data.price").value(50000));
    }

    @Test
    @DisplayName("잘못된 가격으로 상품 생성 시 400 에러가 발생한다")
    void createProduct_InvalidPrice() throws Exception {
        // given
        Product product = Product.builder()
            .brandName("TestBrand")
            .category(Category.TOP)
            .price(-1000)
            .build();

        // when & then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.message").value("Validation failed"))
            .andExpect(jsonPath("$.error.details.price").value("상품 가격은 0원 이상이어야 합니다"));
    }

    @Test
    @DisplayName("중복된 상품 생성 시 409 에러가 발생한다")
    void createProduct_Duplicate() throws Exception {
        // given
        Product product = Product.builder()
            .brandName("TestBrand")
            .category(Category.TOP)
            .price(10000)
            .build();

        when(productService.save(any(Product.class)))
            .thenThrow(new DuplicateProductException("이미 존재하는 상품입니다."));

        // when & then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.message").value("이미 존재하는 상품입니다."));
    }
} 