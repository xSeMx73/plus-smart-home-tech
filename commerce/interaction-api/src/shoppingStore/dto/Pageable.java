package shoppingStore.dto;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pageable {

    @NotNull
    @Min(value = 0)
    private int page;

    @NotNull
    @Min(value = 1)
    private int size;

    private List<String> sort;
}
