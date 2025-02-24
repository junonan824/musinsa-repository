@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponse {
    private List<ProductResponse> products;
    private int currentPage;
    private long totalItems;
    private int totalPages;

    public static ProductPageResponse of(Page<Product> productPage) {
        return ProductPageResponse.builder()
            .products(productPage.getContent().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList()))
            .currentPage(productPage.getNumber())
            .totalItems(productPage.getTotalElements())
            .totalPages(productPage.getTotalPages())
            .build();
    }
} 