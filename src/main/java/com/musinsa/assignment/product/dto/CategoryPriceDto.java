package com.musinsa.assignment.product.dto;

import com.musinsa.assignment.product.domain.Category;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class CategoryPriceDto {
    private Category category;
    private String brandName;
    private int price;
} 