package com.musinsa.assignment.product.service;

import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testProducts = Arrays.asList(
            Product.builder().id(1L).brandName("Nike").category(Category.TOP).price(50000).build(),
            Product.builder().id(2L).brandName("Adidas").category(Category.TOP).price(45000).build(),
            Product.builder().id(3L).brandName("Nike").category(Category.PANTS).price(40000).build(),
            Product.builder().id(4L).brandName("Adidas").category(Category.PANTS).price(35000).build(),
            Product.builder().id(5L).brandName("Nike").category(Category.SNEAKERS).price(60000).build(),
            Product.builder().id(6L).brandName("Adidas").category(Category.SNEAKERS).price(55000).build()
        );

        // Setup common mock behavior
        lenient().when(productRepository.findAll()).thenReturn(testProducts);
        
        // Setup mock for all possible categories
        for (Category category : Category.values()) {
            lenient().when(productRepository.findByLowestPriceInCategory(category))
                    .thenReturn(Collections.emptyList());
        }
    }

    @Test
    @DisplayName("각 카테고리별 최저가격 상품을 조회한다")
    void getLowestPriceEachCategory() {
        // given
        lenient().when(productRepository.findByLowestPriceInCategory(Category.TOP))
            .thenReturn(Collections.singletonList(testProducts.get(1))); // Adidas TOP 45000
        lenient().when(productRepository.findByLowestPriceInCategory(Category.PANTS))
            .thenReturn(Collections.singletonList(testProducts.get(3))); // Adidas PANTS 35000
        lenient().when(productRepository.findByLowestPriceInCategory(Category.SNEAKERS))
            .thenReturn(Collections.singletonList(testProducts.get(5))); // Adidas SNEAKERS 55000

        // when
        Map<String, Object> result = productService.getLowestPriceEachCategory();

        // then
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lowestPriceByCategory = (List<Map<String, Object>>) result.get("lowestPriceByCategory");
        int totalPrice = (int) result.get("totalPrice");

        assertThat(lowestPriceByCategory).hasSize(3);
        assertThat(totalPrice).isEqualTo(135000); // 45000 + 35000 + 55000

        // Verify the presence of all expected prices without order
        assertThat(lowestPriceByCategory)
            .extracting("price")
            .containsExactlyInAnyOrder(35000, 45000, 55000);

        // Verify all brands are Adidas
        assertThat(lowestPriceByCategory)
            .extracting("brand")
            .containsOnly("Adidas");

        // Verify each category has the correct price
        for (Map<String, Object> item : lowestPriceByCategory) {
            String category = (String) item.get("category");
            int price = (int) item.get("price");
            
            switch (category) {
                case "상의" -> assertThat(price).isEqualTo(45000);
                case "바지" -> assertThat(price).isEqualTo(35000);
                case "스니커즈" -> assertThat(price).isEqualTo(55000);
            }
        }
    }

    @Test
    @DisplayName("모든 카테고리 상품이 있는 브랜드 중 최저가 브랜드를 조회한다")
    void getLowestPriceSingleBrand() {
        // given
        Set<Category> existingCategories = new HashSet<>(Arrays.asList(
            Category.TOP, Category.PANTS, Category.SNEAKERS
        ));
        
        when(productRepository.findAllBrandNames())
            .thenReturn(new HashSet<>(Arrays.asList("Nike", "Adidas")));

        when(productRepository.countCategoriesByBrand("Nike"))
            .thenReturn(3L);
        when(productRepository.countCategoriesByBrand("Adidas"))
            .thenReturn(3L);

        // Nike products
        for (Category category : existingCategories) {
            lenient().when(productRepository.findLowestPriceProductByBrandAndCategory("Nike", category))
                .thenReturn(Collections.singletonList(
                    testProducts.stream()
                        .filter(p -> p.getBrandName().equals("Nike") && p.getCategory() == category)
                        .findFirst()
                        .orElseThrow()
                ));
        }

        // Adidas products
        for (Category category : existingCategories) {
            lenient().when(productRepository.findLowestPriceProductByBrandAndCategory("Adidas", category))
                .thenReturn(Collections.singletonList(
                    testProducts.stream()
                        .filter(p -> p.getBrandName().equals("Adidas") && p.getCategory() == category)
                        .findFirst()
                        .orElseThrow()
                ));
        }

        // when
        Map<String, Object> result = productService.getLowestPriceSingleBrand();

        // then
        assertThat(result.get("brand")).isEqualTo("Adidas");
        assertThat(result.get("totalPrice")).isEqualTo(135000);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
        
        assertThat(items).hasSize(3);
        
        // Verify prices without caring about order
        assertThat(items)
            .extracting("price")
            .containsExactlyInAnyOrder(35000, 45000, 55000);

        // Verify each category has the correct price
        for (Map<String, Object> item : items) {
            String category = (String) item.get("category");
            int price = (int) item.get("price");
            
            switch (category) {
                case "상의" -> assertThat(price).isEqualTo(45000);
                case "바지" -> assertThat(price).isEqualTo(35000);
                case "스니커즈" -> assertThat(price).isEqualTo(55000);
            }
        }
    }

    @Test
    @DisplayName("모든 카테고리를 커버하는 브랜드가 없으면 에러 메시지를 반환한다")
    void getLowestPriceSingleBrand_NoBrandCoversAllCategories() {
        // given
        when(productRepository.findAllBrandNames())
            .thenReturn(new HashSet<>(Arrays.asList("Nike", "Adidas")));
        
        when(productRepository.findAll())
            .thenReturn(testProducts);

        when(productRepository.countCategoriesByBrand(any()))
            .thenReturn(2L); // Both brands cover only 2 categories

        // when
        Map<String, Object> result = productService.getLowestPriceSingleBrand();

        // then
        assertThat(result)
            .containsEntry("message", "No brand covers all categories.");
    }
} 