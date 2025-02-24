package com.musinsa.assignment.product.service;

import com.musinsa.assignment.product.dto.request.ProductCreateRequest;
import com.musinsa.assignment.product.dto.response.ProductResponse;
import com.musinsa.assignment.product.entity.Product;
import com.musinsa.assignment.product.repository.ProductRepository;
import com.musinsa.assignment.product.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductRepository productRepository;
    private final ProductValidator productValidator;

    public ProductResponse save(ProductCreateRequest request) {
        productValidator.validateCreate(request);
        Product product = request.toEntity();
        return ProductResponse.from(productRepository.save(product));
    }

    // 수정, 삭제 관련 메서드들 이동
} 