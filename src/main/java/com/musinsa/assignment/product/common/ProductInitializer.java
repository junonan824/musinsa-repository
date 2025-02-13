package com.musinsa.assignment.product.common;

import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ProductInitializer {

    private final ProductRepository productRepository;

    @Bean
    public CommandLineRunner initProductData() {
        return args -> {
            if (productRepository.count() > 0) {
                return; // Skip if data already exists
            }

            List<Product> initialProducts = Arrays.asList(
                // A
                createProduct("A", Category.TOP, 11200),
                createProduct("A", Category.OUTER, 5500),
                createProduct("A", Category.PANTS, 4200),
                createProduct("A", Category.SNEAKERS, 9000),
                createProduct("A", Category.BAG, 2000),
                createProduct("A", Category.HAT, 1700),
                createProduct("A", Category.SOCKS, 1800),
                createProduct("A", Category.ACCESSORY, 2300),

                // B
                createProduct("B", Category.TOP, 10500),
                createProduct("B", Category.OUTER, 5900),
                createProduct("B", Category.PANTS, 3800),
                createProduct("B", Category.SNEAKERS, 9100),
                createProduct("B", Category.BAG, 2100),
                createProduct("B", Category.HAT, 2000),
                createProduct("B", Category.SOCKS, 2000),
                createProduct("B", Category.ACCESSORY, 2200),

                // C
                createProduct("C", Category.TOP, 10000),
                createProduct("C", Category.OUTER, 6200),
                createProduct("C", Category.PANTS, 3300),
                createProduct("C", Category.SNEAKERS, 9200),
                createProduct("C", Category.BAG, 2200),
                createProduct("C", Category.HAT, 1900),
                createProduct("C", Category.SOCKS, 2200),
                createProduct("C", Category.ACCESSORY, 2100),

                // D
                createProduct("D", Category.TOP, 10100),
                createProduct("D", Category.OUTER, 5100),
                createProduct("D", Category.PANTS, 3000),
                createProduct("D", Category.SNEAKERS, 9500),
                createProduct("D", Category.BAG, 2500),
                createProduct("D", Category.HAT, 1500),
                createProduct("D", Category.SOCKS, 2400),
                createProduct("D", Category.ACCESSORY, 2000),

                // E
                createProduct("E", Category.TOP, 10700),
                createProduct("E", Category.OUTER, 5000),
                createProduct("E", Category.PANTS, 3800),
                createProduct("E", Category.SNEAKERS, 9900),
                createProduct("E", Category.BAG, 2300),
                createProduct("E", Category.HAT, 1800),
                createProduct("E", Category.SOCKS, 2100),
                createProduct("E", Category.ACCESSORY, 2100),

                // F
                createProduct("F", Category.TOP, 11200),
                createProduct("F", Category.OUTER, 7200),
                createProduct("F", Category.PANTS, 4000),
                createProduct("F", Category.SNEAKERS, 9300),
                createProduct("F", Category.BAG, 2100),
                createProduct("F", Category.HAT, 1600),
                createProduct("F", Category.SOCKS, 2300),
                createProduct("F", Category.ACCESSORY, 1900),

                // G
                createProduct("G", Category.TOP, 10500),
                createProduct("G", Category.OUTER, 5800),
                createProduct("G", Category.PANTS, 3900),
                createProduct("G", Category.SNEAKERS, 9000),
                createProduct("G", Category.BAG, 2200),
                createProduct("G", Category.HAT, 1700),
                createProduct("G", Category.SOCKS, 2100),
                createProduct("G", Category.ACCESSORY, 2000),

                // H
                createProduct("H", Category.TOP, 10800),
                createProduct("H", Category.OUTER, 6300),
                createProduct("H", Category.PANTS, 3100),
                createProduct("H", Category.SNEAKERS, 9700),
                createProduct("H", Category.BAG, 2100),
                createProduct("H", Category.HAT, 1600),
                createProduct("H", Category.SOCKS, 2000),
                createProduct("H", Category.ACCESSORY, 2000),

                // I
                createProduct("I", Category.TOP, 11400),
                createProduct("I", Category.OUTER, 6700),
                createProduct("I", Category.PANTS, 3200),
                createProduct("I", Category.SNEAKERS, 9500),
                createProduct("I", Category.BAG, 2400),
                createProduct("I", Category.HAT, 1700),
                createProduct("I", Category.SOCKS, 1700),
                createProduct("I", Category.ACCESSORY, 2400)
            );

            productRepository.saveAll(initialProducts);
        };
    }

    private Product createProduct(String brandName, Category category, int price) {
        return Product.builder()
            .brandName(brandName)
            .category(category)
            .price(price)
            .build();
    }
}