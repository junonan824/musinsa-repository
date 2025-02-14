package com.musinsa.assignment.product.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPriceInfoResponse {
    private BrandPrice highest;
    private BrandPrice lowest;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandPrice {
        private String brand;
        private int price;
    }
} 