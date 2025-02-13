package com.musinsa.assignment.product.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryPriceInfoResponse {
    private BrandPrice highest;
    private BrandPrice lowest;

    @Getter
    @Builder
    public static class BrandPrice {
        private String brand;
        private int price;
    }
} 