@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String brandName;
    private String category;
    private int price;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
            .id(product.getId())
            .brandName(product.getBrandName())
            .category(product.getCategory().getDisplayName())
            .price(product.getPrice())
            .build();
    }
} 