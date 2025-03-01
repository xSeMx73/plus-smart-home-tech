package ru.yandex.practicum.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.delivery.dto.AddressDto;
import ru.yandex.practicum.shoppingCart.dto.ShoppingCartDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateNewOrderRequest {

    @NotNull
    ShoppingCartDto shoppingCart;

    @NotNull
    AddressDto address;

    @NotNull
    String username;
}
