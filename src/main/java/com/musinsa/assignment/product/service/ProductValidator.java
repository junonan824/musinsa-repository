package com.musinsa.assignment.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final ProductRepository productRepository;

    public void validateCreate(ProductCreateRequest request) {
        validatePrice(request.getPrice());
        validateBrandName(request.getBrandName());
        checkDuplicateProduct(request.getBrandName(), request.getCategory());
    }

    private void validatePrice(int price) {
        if (price < 0) {
            throw new InvalidPriceException("상품 가격은 0원 이상이어야 합니다.");
        }
    }

    // 기타 검증 메서드들...
} 