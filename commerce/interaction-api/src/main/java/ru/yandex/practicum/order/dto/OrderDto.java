package ru.yandex.practicum.order.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.order.enums.OrderState;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class OrderDto {
    UUID orderId;
    UUID shoppingCartId;
    Map<UUID, Integer> products;
    UUID paymentId;
    OrderState state;
    UUID deliveryId;
    Double deliveryWeight;
    Double deliveryVolume;
    Boolean fragile;
    Double totalPrice;
    Double deliveryPrice;
    Double productPrice;
}
