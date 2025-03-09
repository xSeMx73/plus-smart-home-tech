package ru.yandex.practicum.order.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class ProductReturnRequest {
    UUID orderId;
    Map<UUID, Integer> products;
}
