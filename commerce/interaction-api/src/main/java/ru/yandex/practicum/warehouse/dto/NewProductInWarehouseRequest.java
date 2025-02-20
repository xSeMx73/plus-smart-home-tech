package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class NewProductInWarehouseRequest {
    @NotNull
    UUID productId;

    @NotNull
    boolean fragile;

    @NotNull
    DimensionDto dimension;

    @DecimalMin(value = "1.0")
    double weight;
}