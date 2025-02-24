@Getter
@NoArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "브랜드명은 필수입니다")
    @Size(max = 50, message = "브랜드명은 50자를 초과할 수 없습니다")
    private String brandName;

    @NotNull(message = "카테고리는 필수입니다")
    private Category category;

    @Min(value = 0, message = "상품 가격은 0원 이상이어야 합니다")
    private int price;

    public Product toEntity() {
        return Product.builder()
            .brandName(brandName)
            .category(category)
            .price(price)
            .build();
    }
} 