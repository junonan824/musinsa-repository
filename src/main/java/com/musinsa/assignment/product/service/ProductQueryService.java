package com.musinsa.assignment.product.service;

import com.musinsa.assignment.product.dto.response.ProductResponse;
import com.musinsa.assignment.product.dto.response.LowestPriceEachCategoryResponse;
import com.musinsa.assignment.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {
    private final ProductRepository productRepository;

    public Page<ProductResponse> getAllProducts(int page, int size) {
        return productRepository.findAllOrderByIdDesc(PageRequest.of(page, size))
            .map(ProductResponse::from);
    }

    public LowestPriceEachCategoryResponse getLowestPriceEachCategory() {
        // 기존 로직 이동
    }

    // 조회 관련 메서드들 이동
} 