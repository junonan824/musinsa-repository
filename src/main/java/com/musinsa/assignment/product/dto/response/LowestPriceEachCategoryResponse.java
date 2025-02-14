package com.musinsa.assignment.product.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowestPriceEachCategoryResponse {
    private List<CategoryPrice> lowestPriceByCategory;
    private int totalPrice;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryPrice {
        private String category;
        private String brand;
        private int price;
    }
} 