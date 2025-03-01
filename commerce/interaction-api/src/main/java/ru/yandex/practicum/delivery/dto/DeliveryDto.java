package ru.yandex.practicum.delivery.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.delivery.enums.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DeliveryDto {
    UUID deliveryId;
    AddressDto fromAddress;
    AddressDto toAddress;
    UUID orderId;
    DeliveryState state;
}
