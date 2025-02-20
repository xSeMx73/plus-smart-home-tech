package ru.yandex.practicum.shoppingCart.dto;


import jakarta.validation.constraints.Min;
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
public class ChangeProductQuantityRequest {

    @NotNull
    UUID productId;

    @Min(value = 0)
    int newQuantity;
}