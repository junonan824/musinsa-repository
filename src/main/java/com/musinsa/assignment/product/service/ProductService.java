package com.musinsa.assignment.product.service;

import com.musinsa.assignment.product.domain.Category;
import com.musinsa.assignment.product.domain.Product;
import com.musinsa.assignment.product.repository.ProductRepository;
import com.musinsa.assignment.product.exception.ProductNotFoundException;
import com.musinsa.assignment.product.dto.BrandCategoryPriceDto;
import com.musinsa.assignment.product.dto.response.LowestPriceEachCategoryResponse;
import com.musinsa.assignment.product.dto.response.LowestPriceSingleBrandResponse;
import com.musinsa.assignment.product.dto.response.CategoryPriceInfoResponse;
import com.musinsa.assignment.product.exception.InvalidPriceException;
import com.musinsa.assignment.product.exception.InvalidBrandNameException;
import com.musinsa.assignment.product.exception.DuplicateProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 기본적으로 읽기 전용 트랜잭션 적용
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product save(Product product) {
        validateProduct(product);
        checkDuplicateProduct(product);
        return productRepository.save(product);
    }

    private void validateProduct(Product product) {
        if (product.getPrice() < 0) {
            throw new InvalidPriceException("상품 가격은 0원 이상이어야 합니다.");
        }
        if (product.getBrandName() == null || product.getBrandName().trim().isEmpty()) {
            throw new InvalidBrandNameException("브랜드명은 필수입니다.");
        }
        if (product.getBrandName().length() > 50) {
            throw new InvalidBrandNameException("브랜드명은 50자를 초과할 수 없습니다.");
        }
    }

    private void checkDuplicateProduct(Product product) {
        if (productRepository.existsByBrandNameAndCategory(
                product.getBrandName(), product.getCategory())) {
            throw new DuplicateProductException(
                String.format("이미 존재하는 상품입니다. 브랜드: %s, 카테고리: %s", 
                    product.getBrandName(), 
                    product.getCategory().getDisplayName())
            );
        }
    }

    @Transactional
    public Product deleteById(Long id) {
        Product product = findById(id);  // 삭제 전 상품 정보 조회
        productRepository.deleteById(id);
        return product;  // 삭제된 상품 정보 반환
    }

    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllOrderByIdDesc(pageable);
    }

    public LowestPriceEachCategoryResponse getLowestPriceEachCategory() {
        List<BrandCategoryPriceDto> lowestPrices = productRepository.findLowestPriceByCategory();
        
        List<LowestPriceEachCategoryResponse.CategoryPrice> categoryPrices = lowestPrices.stream()
            .map(dto -> {
                return LowestPriceEachCategoryResponse.CategoryPrice.builder()
                    .category(dto.getCategory().getDisplayName())
                    .brand(dto.getBrandName())
                    .price(dto.getPrice())
                    .build();
            })
            .collect(Collectors.toList());

        int totalPrice = lowestPrices.stream()
            .mapToInt(BrandCategoryPriceDto::getPrice)
            .sum();

        return LowestPriceEachCategoryResponse.builder()
            .lowestPriceByCategory(categoryPrices)
            .totalPrice(totalPrice)
            .build();
    }

    public LowestPriceSingleBrandResponse getLowestPriceSingleBrand() {
        Map<String, Map<Category, Integer>> brandCategoryPrices = productRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                Product::getBrandName,
                Collectors.groupingBy(
                    Product::getCategory,
                    Collectors.collectingAndThen(
                        Collectors.minBy(Comparator.comparingInt(Product::getPrice)),
                        opt -> opt.map(Product::getPrice).orElse(Integer.MAX_VALUE)
                    )
                )
            ));

        String selectedBrand = findBrandWithAllCategories(brandCategoryPrices);
        Map<Category, Integer> selectedBrandPrices = brandCategoryPrices.get(selectedBrand);

        List<LowestPriceSingleBrandResponse.CategoryPrice> items = selectedBrandPrices.entrySet().stream()
            .map(entry -> {
                return LowestPriceSingleBrandResponse.CategoryPrice.builder()
                    .category(entry.getKey().getDisplayName())
                    .price(entry.getValue())
                    .build();
            })
            .collect(Collectors.toList());

        int totalPrice = selectedBrandPrices.values().stream().mapToInt(Integer::intValue).sum();

        return LowestPriceSingleBrandResponse.builder()
            .brand(selectedBrand)
            .items(items)
            .totalPrice(totalPrice)
            .build();
    }

    public CategoryPriceInfoResponse getCategoryPriceInfo(Category category) {
        List<Product> products = productRepository.findByCategory(category);
        
        if (products.isEmpty()) {
            throw new NoSuchElementException("No products found for category: " + category);
        }

        Product highest = Collections.max(products, Comparator.comparingInt(Product::getPrice));
        Product lowest = Collections.min(products, Comparator.comparingInt(Product::getPrice));

        return CategoryPriceInfoResponse.builder()
            .highest(CategoryPriceInfoResponse.BrandPrice.builder()
                .brand(highest.getBrandName())
                .price(highest.getPrice())
                .build())
            .lowest(CategoryPriceInfoResponse.BrandPrice.builder()
                .brand(lowest.getBrandName())
                .price(lowest.getPrice())
                .build())
            .build();
    }

    private String findBrandWithAllCategories(Map<String, Map<Category, Integer>> brandCategoryPrices) {
        return brandCategoryPrices.entrySet().stream()
            .filter(entry -> entry.getValue().size() == Category.values().length)
            .min(Comparator.comparingInt(entry -> 
                entry.getValue().values().stream().mapToInt(Integer::intValue).sum()))
            .map(Map.Entry::getKey)
            .orElseThrow(() -> new NoSuchElementException("No brand covers all categories."));
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public Product update(Long id, Product updateProduct) {
        Product existingProduct = findById(id);
        validateProduct(updateProduct);
        
        if (!existingProduct.getBrandName().equals(updateProduct.getBrandName()) ||
            !existingProduct.getCategory().equals(updateProduct.getCategory())) {
            checkDuplicateProduct(updateProduct);
        }
        
        existingProduct.setBrandName(updateProduct.getBrandName());
        existingProduct.setCategory(updateProduct.getCategory());
        existingProduct.setPrice(updateProduct.getPrice());
        
        return existingProduct;
    }
} 