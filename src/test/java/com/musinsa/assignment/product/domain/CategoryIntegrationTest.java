package com.musinsa.assignment.product.domain;

import com.musinsa.assignment.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("모든 카테고리에 대한 상품을 저장하고 조회할 수 있다")
    void saveAndRetrieveProductsForAllCategories() {
        // given
        List<Product> products = Arrays.stream(Category.values())
            .map(category -> Product.builder()
                .brandName("TestBrand")
                .category(category)
                .price(10000)
                .build())
            .toList();

        // when
        List<Product> savedProducts = productRepository.saveAll(products);

        // then
        assertThat(savedProducts).hasSize(Category.values().length);
        assertThat(savedProducts)
            .extracting(Product::getCategory)
            .containsExactlyInAnyOrder(Category.values());
    }

    @Test
    @DisplayName("각 카테고리의 한글 이름이 올바르게 매핑된다")
    void categoryKoreanNameMapping() {
        // given & when & then
        assertThat(Category.TOP.getDisplayName()).isEqualTo("상의");
        assertThat(Category.OUTER.getDisplayName()).isEqualTo("아우터");
        assertThat(Category.PANTS.getDisplayName()).isEqualTo("바지");
        assertThat(Category.SNEAKERS.getDisplayName()).isEqualTo("스니커즈");
        assertThat(Category.BAG.getDisplayName()).isEqualTo("가방");
        assertThat(Category.HAT.getDisplayName()).isEqualTo("모자");
        assertThat(Category.SOCKS.getDisplayName()).isEqualTo("양말");
        assertThat(Category.ACCESSORY.getDisplayName()).isEqualTo("액세서리");
    }

    @Test
    @DisplayName("카테고리별 최저가 상품을 조회할 수 있다")
    void findLowestPriceProductForEachCategory() {
        // given
        Arrays.stream(Category.values())
            .forEach(category -> {
                productRepository.saveAll(Arrays.asList(
                    Product.builder()
                        .brandName("ExpensiveBrand")
                        .category(category)
                        .price(20000)
                        .build(),
                    Product.builder()
                        .brandName("CheapBrand")
                        .category(category)
                        .price(10000)
                        .build()
                ));
            });

        // when & then
        Arrays.stream(Category.values())
            .forEach(category -> {
                List<Product> lowestPriceProducts = productRepository.findByLowestPriceInCategory(category);
                assertThat(lowestPriceProducts)
                    .isNotEmpty()
                    .allSatisfy(product -> {
                        assertThat(product.getCategory()).isEqualTo(category);
                        assertThat(product.getPrice()).isEqualTo(10000);
                        assertThat(product.getBrandName()).isEqualTo("CheapBrand");
                    });
            });
    }

    @Test
    @DisplayName("카테고리별 최고가 상품을 조회할 수 있다")
    void findHighestPriceProductForEachCategory() {
        // given
        Arrays.stream(Category.values())
            .forEach(category -> {
                productRepository.saveAll(Arrays.asList(
                    Product.builder()
                        .brandName("ExpensiveBrand")
                        .category(category)
                        .price(50000)
                        .build(),
                    Product.builder()
                        .brandName("CheapBrand")
                        .category(category)
                        .price(10000)
                        .build()
                ));
            });

        // when & then
        Arrays.stream(Category.values())
            .forEach(category -> {
                List<Product> highestPriceProducts = productRepository.findByHighestPriceInCategory(category);
                assertThat(highestPriceProducts)
                    .isNotEmpty()
                    .allSatisfy(product -> {
                        assertThat(product.getCategory()).isEqualTo(category);
                        assertThat(product.getPrice()).isEqualTo(50000);
                        assertThat(product.getBrandName()).isEqualTo("ExpensiveBrand");
                    });
            });
    }

    @Test
    @DisplayName("카테고리 이름으로 상품을 검색할 수 있다")
    void findProductsByCategory() {
        // given
        Category testCategory = Category.TOP;
        List<Product> products = Arrays.asList(
            Product.builder()
                .brandName("BrandA")
                .category(testCategory)
                .price(15000)
                .build(),
            Product.builder()
                .brandName("BrandB")
                .category(testCategory)
                .price(25000)
                .build()
        );
        productRepository.saveAll(products);

        // when
        List<Product> foundProducts = productRepository.findByCategory(testCategory);

        // then
        assertThat(foundProducts)
            .hasSize(2)
            .extracting(Product::getBrandName, Product::getPrice)
            .containsExactlyInAnyOrder(
                tuple("BrandA", 15000),
                tuple("BrandB", 25000)
            );
    }
} 