package com.musinsa.assignment.product.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LowestPriceEachCategoryResponse {
    private List<CategoryPrice> lowestPriceByCategory;
    private int totalPrice;

    @Getter
    @Builder
    public static class CategoryPrice {
        private String category;
        private String brand;
        private int price;
    }
} 