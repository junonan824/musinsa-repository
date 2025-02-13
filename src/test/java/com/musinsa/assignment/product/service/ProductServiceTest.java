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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
        List<BrandCategoryPriceDto> mockData = Arrays.asList(
            new BrandCategoryPriceDto("Nike", Category.TOP, 50000),
            new BrandCategoryPriceDto("Nike", Category.PANTS, 40000),
            new BrandCategoryPriceDto("Nike", Category.SNEAKERS, 60000),
            new BrandCategoryPriceDto("Nike", Category.OUTER, 70000),
            new BrandCategoryPriceDto("Nike", Category.BAG, 20000),
            new BrandCategoryPriceDto("Nike", Category.HAT, 15000),
            new BrandCategoryPriceDto("Nike", Category.SOCKS, 3000),
            new BrandCategoryPriceDto("Nike", Category.ACCESSORY, 12000),
            new BrandCategoryPriceDto("Adidas", Category.TOP, 45000),
            // ... Adidas의 나머지 카테고리들
        );

        when(productRepository.findLowestPricesByBrandAndCategory())
            .thenReturn(mockData);

        // when
        Map<String, Object> result = productService.getLowestPriceSingleBrand();

        // then
        assertThat(result.get("brand")).isEqualTo("Nike");
        // ... 나머지 검증 로직
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

    @Test
    @DisplayName("카테고리별 최고/최저가 정보를 조회한다")
    void getCategoryPriceInfo() {
        // given
        Category category = Category.TOP;
        Product highestProduct = Product.builder()
            .id(1L)
            .brandName("Nike")
            .category(category)
            .price(100000)
            .build();
        
        Product lowestProduct = Product.builder()
            .id(2L)
            .brandName("Adidas")
            .category(category)
            .price(50000)
            .build();

        when(productRepository.findByHighestPriceInCategory(category))
            .thenReturn(Collections.singletonList(highestProduct));
        when(productRepository.findByLowestPriceInCategory(category))
            .thenReturn(Collections.singletonList(lowestProduct));

        // when
        Map<String, Object> result = productService.getCategoryPriceInfo(category);

        // then
        @SuppressWarnings("unchecked")
        Map<String, Object> highest = (Map<String, Object>) result.get("highest");
        @SuppressWarnings("unchecked")
        Map<String, Object> lowest = (Map<String, Object>) result.get("lowest");

        assertThat(highest)
            .containsEntry("brand", "Nike")
            .containsEntry("price", 100000);
        
        assertThat(lowest)
            .containsEntry("brand", "Adidas")
            .containsEntry("price", 50000);
    }

    @Test
    @DisplayName("상품 목록을 ID 내림차순으로 조회한다")
    void getAllProducts() {
        // given
        List<Product> products = Arrays.asList(
            Product.builder().id(2L).brandName("Nike").category(Category.TOP).price(50000).build(),
            Product.builder().id(1L).brandName("Adidas").category(Category.PANTS).price(45000).build()
        );
        Page<Product> page = new PageImpl<>(products);
        
        when(productRepository.findAllOrderByIdDesc(any(Pageable.class)))
            .thenReturn(page);

        // when
        Page<Product> result = productService.getAllProducts(0, 10);

        // then
        assertThat(result.getContent())
            .hasSize(2)
            .extracting("id")
            .containsExactly(2L, 1L);
    }
} 