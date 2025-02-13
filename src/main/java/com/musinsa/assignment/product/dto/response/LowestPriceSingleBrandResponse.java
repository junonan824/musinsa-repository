package com.musinsa.assignment.product.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LowestPriceSingleBrandResponse {
    private String brand;
    private int totalPrice;
    private List<CategoryPrice> items;

    @Getter
    @Builder
    public static class CategoryPrice {
        private String category;
        private int price;
    }
} 