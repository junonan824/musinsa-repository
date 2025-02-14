package com.musinsa.assignment.product.domain;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "상품 정보")
public class Product {

    @Schema(description = "상품 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "브랜드명", example = "Nike", required = true)
    @NotBlank(message = "브랜드명은 필수입니다")
    @Size(max = 50, message = "브랜드명은 50자를 초과할 수 없습니다")
    @Column(nullable = false)
    private String brandName;

    @Schema(description = "카테고리", example = "TOP", required = true)
    @NotNull(message = "카테고리는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Schema(description = "가격", example = "50000", minimum = "0")
    @Min(value = 0, message = "상품 가격은 0원 이상이어야 합니다")
    @Column(nullable = false)
    private int price;
}