package com.musinsa.assignment.product.dto;

import com.musinsa.assignment.product.domain.Category;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class BrandCategoryPriceDto {
    private String brandName;
    private Category category;
    private int price;
} 