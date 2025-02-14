package com.musinsa.assignment.product.domain;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "브랜드명은 필수입니다")
    @Size(max = 50, message = "브랜드명은 50자를 초과할 수 없습니다")
    private String brandName;

    @NotNull(message = "카테고리는 필수입니다")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Min(value = 0, message = "상품 가격은 0원 이상이어야 합니다")
    private int price;
}