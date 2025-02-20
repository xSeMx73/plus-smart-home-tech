package ru.yandex.practicum.warehouse.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class BookedProductDto {
    double deliveryWeight;
    double deliveryVolume;
    boolean fragile;
}