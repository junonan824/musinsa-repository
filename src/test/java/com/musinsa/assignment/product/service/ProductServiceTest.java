package com.musinsa.assignment.product.service;

import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.repository.ProductRepository;
import com.musinsa.assignment.product.dto.BrandCategoryPriceDto;
import com.musinsa.assignment.product.dto.response.LowestPriceEachCategoryResponse;
import com.musinsa.assignment.product.dto.response.LowestPriceSingleBrandResponse;
import com.musinsa.assignment.product.dto.response.CategoryPriceInfoResponse;
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
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("각 카테고리별 최저가격 상품을 DTO로 변환하여 반환한다")
    void getLowestPriceEachCategory() {
        // given
        lenient().when(productRepository.findByLowestPriceInCategory(Category.TOP))
            .thenReturn(Collections.singletonList(testProducts.get(1))); // Adidas TOP 45000
        lenient().when(productRepository.findByLowestPriceInCategory(Category.PANTS))
            .thenReturn(Collections.singletonList(testProducts.get(3))); // Adidas PANTS 35000

        // when
        LowestPriceEachCategoryResponse response = productService.getLowestPriceEachCategory();

        // then
        assertThat(response.getTotalPrice()).isEqualTo(80000);
        assertThat(response.getLowestPriceByCategory())
            .hasSize(2)
            .extracting("brand", "price", "category")
            .containsExactlyInAnyOrder(
                tuple("Adidas", 45000, "상의"),
                tuple("Adidas", 35000, "바지")
            );
    }

    @Test
    @DisplayName("모든 카테고리 상품이 있는 브랜드 중 최저가 브랜드를 DTO로 변환하여 반환한다")
    void getLowestPriceSingleBrand() {
        // given
        List<BrandCategoryPriceDto> mockData = Arrays.asList(
            // D 브랜드 데이터 (총합: 36100)
            new BrandCategoryPriceDto("D", Category.TOP, 10100),
            new BrandCategoryPriceDto("D", Category.OUTER, 5100),
            new BrandCategoryPriceDto("D", Category.PANTS, 3000),
            new BrandCategoryPriceDto("D", Category.SNEAKERS, 9500),
            new BrandCategoryPriceDto("D", Category.BAG, 2500),
            new BrandCategoryPriceDto("D", Category.HAT, 1500),
            new BrandCategoryPriceDto("D", Category.SOCKS, 2400),
            new BrandCategoryPriceDto("D", Category.ACCESSORY, 2000)
        );

        when(productRepository.findLowestPricesByBrandAndCategory())
            .thenReturn(mockData);

        // when
        LowestPriceSingleBrandResponse response = productService.getLowestPriceSingleBrand();

        // then
        assertThat(response.getBrand()).isEqualTo("D");
        assertThat(response.getTotalPrice()).isEqualTo(36100);
        assertThat(response.getItems())
            .hasSize(8)
            .extracting("category", "price")
            .containsExactlyInAnyOrder(
                tuple("상의", 10100),
                tuple("아우터", 5100),
                tuple("바지", 3000),
                tuple("스니커즈", 9500),
                tuple("가방", 2500),
                tuple("모자", 1500),
                tuple("양말", 2400),
                tuple("액세서리", 2000)
            );
    }

    @Test
    @DisplayName("특정 카테고리의 최고/최저가 정보를 DTO로 변환하여 반환한다")
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
        CategoryPriceInfoResponse response = productService.getCategoryPriceInfo(category);

        // then
        assertThat(response.getHighest())
            .extracting("brand", "price")
            .containsExactly("Nike", 100000);
        
        assertThat(response.getLowest())
            .extracting("brand", "price")
            .containsExactly("Adidas", 50000);
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

    @Test
    @DisplayName("모든 카테고리를 커버하는 브랜드가 없으면 예외를 던진다")
    void getLowestPriceSingleBrand_NoBrandCoversAllCategories() {
        // given
        List<BrandCategoryPriceDto> mockData = Arrays.asList(
            new BrandCategoryPriceDto("Nike", Category.TOP, 50000),
            new BrandCategoryPriceDto("Nike", Category.PANTS, 40000)
            // 일부 카테고리만 있는 상태
        );

        when(productRepository.findLowestPricesByBrandAndCategory())
            .thenReturn(mockData);

        // when & then
        assertThrows(NoSuchElementException.class, () -> {
            productService.getLowestPriceSingleBrand();
        });
    }
} 