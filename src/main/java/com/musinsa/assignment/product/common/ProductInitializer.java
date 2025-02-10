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
                // NIKE
                createProduct("NIKE", Category.TOP, 11200),
                createProduct("NIKE", Category.OUTER, 5500),
                createProduct("NIKE", Category.PANTS, 4200),
                createProduct("NIKE", Category.SNEAKERS, 9000),
                createProduct("NIKE", Category.BAG, 2000),
                createProduct("NIKE", Category.HAT, 1700),
                createProduct("NIKE", Category.SOCKS, 1800),
                createProduct("NIKE", Category.ACCESSORY, 2300),

                // ADIDAS
                createProduct("ADIDAS", Category.TOP, 10500),
                createProduct("ADIDAS", Category.OUTER, 5900),
                createProduct("ADIDAS", Category.PANTS, 3800),
                createProduct("ADIDAS", Category.SNEAKERS, 9100),
                createProduct("ADIDAS", Category.BAG, 2100),
                createProduct("ADIDAS", Category.HAT, 2000),
                createProduct("ADIDAS", Category.SOCKS, 2000),
                createProduct("ADIDAS", Category.ACCESSORY, 2200),

                // PUMA
                createProduct("PUMA", Category.TOP, 10000),
                createProduct("PUMA", Category.OUTER, 6200),
                createProduct("PUMA", Category.PANTS, 3300),
                createProduct("PUMA", Category.SNEAKERS, 9200),
                createProduct("PUMA", Category.BAG, 2200),
                createProduct("PUMA", Category.HAT, 1900),
                createProduct("PUMA", Category.SOCKS, 2200),
                createProduct("PUMA", Category.ACCESSORY, 2100),

                // FILA
                createProduct("FILA", Category.TOP, 10100),
                createProduct("FILA", Category.OUTER, 5100),
                createProduct("FILA", Category.PANTS, 3000),
                createProduct("FILA", Category.SNEAKERS, 9500),
                createProduct("FILA", Category.BAG, 2500),
                createProduct("FILA", Category.HAT, 1500),
                createProduct("FILA", Category.SOCKS, 2400),
                createProduct("FILA", Category.ACCESSORY, 2000),

                // REEBOK
                createProduct("REEBOK", Category.TOP, 10700),
                createProduct("REEBOK", Category.OUTER, 5000),
                createProduct("REEBOK", Category.PANTS, 3800),
                createProduct("REEBOK", Category.SNEAKERS, 9900),
                createProduct("REEBOK", Category.BAG, 2300),
                createProduct("REEBOK", Category.HAT, 1800),
                createProduct("REEBOK", Category.SOCKS, 2100),
                createProduct("REEBOK", Category.ACCESSORY, 2100),

                // UNDERARMOUR
                createProduct("UNDERARMOUR", Category.TOP, 11200),
                createProduct("UNDERARMOUR", Category.OUTER, 7200),
                createProduct("UNDERARMOUR", Category.PANTS, 4000),
                createProduct("UNDERARMOUR", Category.SNEAKERS, 9300),
                createProduct("UNDERARMOUR", Category.BAG, 2100),
                createProduct("UNDERARMOUR", Category.HAT, 1600),
                createProduct("UNDERARMOUR", Category.SOCKS, 2300),
                createProduct("UNDERARMOUR", Category.ACCESSORY, 1900),

                // NEWBALANCE
                createProduct("NEWBALANCE", Category.TOP, 10500),
                createProduct("NEWBALANCE", Category.OUTER, 5800),
                createProduct("NEWBALANCE", Category.PANTS, 3900),
                createProduct("NEWBALANCE", Category.SNEAKERS, 9000),
                createProduct("NEWBALANCE", Category.BAG, 2200),
                createProduct("NEWBALANCE", Category.HAT, 1700),
                createProduct("NEWBALANCE", Category.SOCKS, 2100),
                createProduct("NEWBALANCE", Category.ACCESSORY, 2000),

                // ASICS
                createProduct("ASICS", Category.TOP, 10800),
                createProduct("ASICS", Category.OUTER, 6300),
                createProduct("ASICS", Category.PANTS, 3100),
                createProduct("ASICS", Category.SNEAKERS, 9700),
                createProduct("ASICS", Category.BAG, 2100),
                createProduct("ASICS", Category.HAT, 1600),
                createProduct("ASICS", Category.SOCKS, 2000),
                createProduct("ASICS", Category.ACCESSORY, 2000),

                // CONVERSE
                createProduct("CONVERSE", Category.TOP, 11400),
                createProduct("CONVERSE", Category.OUTER, 6700),
                createProduct("CONVERSE", Category.PANTS, 3200),
                createProduct("CONVERSE", Category.SNEAKERS, 9500),
                createProduct("CONVERSE", Category.BAG, 2400),
                createProduct("CONVERSE", Category.HAT, 1700),
                createProduct("CONVERSE", Category.SOCKS, 1700),
                createProduct("CONVERSE", Category.ACCESSORY, 2400)
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