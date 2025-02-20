package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DimensionDto {
    @DecimalMin(value = "1.0")
    double width;

    @DecimalMin(value = "1.0")
    double height;

    @DecimalMin(value = "1.0")
    double depth;
}