package ru.yandex.practicum.shoppingCart.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartDto {

    UUID shoppingCartId;

    Map<UUID, Integer> products;
}