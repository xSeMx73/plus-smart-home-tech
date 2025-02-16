package shoppingStore.dto;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private UUID productId;

    @NotBlank
    private String productName;
    @NotBlank
    private String description;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private double rating;

    @NotNull
    private ProductCategory productCategory;

    @DoubleMin(value = "1.0")
    private Double price;

    public enum ProductCategory {
        LIGHTING,
        CONTROL,
        SENSORS
    }

    public enum ProductState {
        ACTIVE,
        DEACTIVATE
    }
}
