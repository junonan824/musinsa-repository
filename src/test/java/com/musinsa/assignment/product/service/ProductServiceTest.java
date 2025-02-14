package com.musinsa.assignment.product.service;

import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.repository.ProductRepository;
import com.musinsa.assignment.product.dto.BrandCategoryPriceDto;
import com.musinsa.assignment.product.dto.response.LowestPriceEachCategoryResponse;
import com.musinsa.assignment.product.dto.response.LowestPriceSingleBrandResponse;
import com.musinsa.assignment.product.exception.ProductNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    private Product createProduct(String brandName, Category category, int price) {
        return Product.builder()
            .brandName(brandName)
            .category(category)
            .price(price)
            .build();
    }

    @Test
    @DisplayName("각 카테고리별 최저가격 상품을 조회한다")
    void getLowestPriceEachCategory() {
        // given
        when(productRepository.findByLowestPriceInCategory(Category.TOP))
            .thenReturn(Collections.singletonList(createProduct("C", Category.TOP, 10000)));
        when(productRepository.findByLowestPriceInCategory(Category.OUTER))
            .thenReturn(Collections.singletonList(createProduct("E", Category.OUTER, 5000)));
        when(productRepository.findByLowestPriceInCategory(Category.PANTS))
            .thenReturn(Collections.singletonList(createProduct("D", Category.PANTS, 3000)));
        when(productRepository.findByLowestPriceInCategory(Category.SNEAKERS))
            .thenReturn(Collections.singletonList(createProduct("G", Category.SNEAKERS, 9000)));
        when(productRepository.findByLowestPriceInCategory(Category.BAG))
            .thenReturn(Collections.singletonList(createProduct("A", Category.BAG, 2000)));
        when(productRepository.findByLowestPriceInCategory(Category.HAT))
            .thenReturn(Collections.singletonList(createProduct("D", Category.HAT, 1500)));
        when(productRepository.findByLowestPriceInCategory(Category.SOCKS))
            .thenReturn(Collections.singletonList(createProduct("I", Category.SOCKS, 1700)));
        when(productRepository.findByLowestPriceInCategory(Category.ACCESSORY))
            .thenReturn(Collections.singletonList(createProduct("F", Category.ACCESSORY, 1900)));

        // when
        LowestPriceEachCategoryResponse response = productService.getLowestPriceEachCategory();

        // then
        assertThat(response.getTotalPrice()).isEqualTo(34100);
        assertThat(response.getLowestPriceByCategory())
            .hasSize(8)
            .extracting("category", "brand", "price")
            .containsExactlyInAnyOrder(
                tuple("상의", "C", 10000),
                tuple("아우터", "E", 5000),
                tuple("바지", "D", 3000),
                tuple("스니커즈", "G", 9000),
                tuple("가방", "A", 2000),
                tuple("모자", "D", 1500),
                tuple("양말", "I", 1700),
                tuple("액세서리", "F", 1900)
            );
    }

    @Test
    @DisplayName("단일 브랜드의 모든 카테고리 최저가격을 조회한다")
    void getLowestPriceSingleBrand() {
        // given
        List<BrandCategoryPriceDto> mockData = Arrays.asList(
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
    @DisplayName("특정 카테고리의 최고/최저가 정보를 조회한다")
    void getCategoryPriceInfo() {
        // given
        Category category = Category.TOP;
        Product highestProduct = createProduct("I", category, 11400);
        Product lowestProduct = createProduct("C", category, 10000);

        when(productRepository.findByHighestPriceInCategory(category))
            .thenReturn(Collections.singletonList(highestProduct));
        when(productRepository.findByLowestPriceInCategory(category))
            .thenReturn(Collections.singletonList(lowestProduct));

        // when
        CategoryPriceInfoResponse response = productService.getCategoryPriceInfo(category);

        // then
        assertThat(response.getHighest())
            .extracting("brand", "price")
            .containsExactly("I", 11400);
        
        assertThat(response.getLowest())
            .extracting("brand", "price")
            .containsExactly("C", 10000);
    }

    @Test
    @DisplayName("새로운 상품을 추가할 수 있다")
    void createProduct() {
        // given
        Product newProduct = createProduct("NewBrand", Category.TOP, 15000);
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        // when
        Product savedProduct = productService.save(newProduct);

        // then
        assertThat(savedProduct)
            .extracting("brandName", "category", "price")
            .containsExactly("NewBrand", Category.TOP, 15000);
    }

    @Test
    @DisplayName("존재하는 상품을 ID로 조회할 수 있다")
    void findById() {
        // given
        Long productId = 1L;
        Product product = createProduct("TestBrand", Category.TOP, 15000);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        Product foundProduct = productService.findById(productId);

        // then
        assertThat(foundProduct)
            .extracting("brandName", "category", "price")
            .containsExactly("TestBrand", Category.TOP, 15000);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회시 예외가 발생한다")
    void findById_NotFound() {
        // given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProductNotFoundException.class, () -> 
            productService.findById(productId));
    }

    @Test
    @DisplayName("존재하는 상품을 삭제할 수 있다")
    void deleteProduct() {
        // given
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> productService.deleteById(productId));
    }

    @Test
    @DisplayName("존재하지 않는 상품 삭제시 예외가 발생한다")
    void deleteProduct_NotFound() {
        // given
        Long productId = 999L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // when & then
        assertThrows(ProductNotFoundException.class, () -> 
            productService.deleteById(productId));
    }

    @Test
    @DisplayName("상품 목록을 페이지네이션하여 조회할 수 있다")
    void getAllProducts() {
        // given
        List<Product> products = Arrays.asList(
            createProduct("BrandA", Category.TOP, 15000),
            createProduct("BrandB", Category.PANTS, 12000)
        );
        Page<Product> page = new PageImpl<>(products);
        
        when(productRepository.findAllOrderByIdDesc(any(Pageable.class)))
            .thenReturn(page);

        // when
        Page<Product> result = productService.getAllProducts(0, 10);

        // then
        assertThat(result.getContent())
            .hasSize(2)
            .extracting("brandName", "price")
            .containsExactly(
                tuple("BrandA", 15000),
                tuple("BrandB", 12000)
            );
    }
} 