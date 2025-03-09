package ru.yandex.practicum.warehouse.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ShippedToDeliveryRequest {
    UUID orderId;

    UUID deliveryId;
}
