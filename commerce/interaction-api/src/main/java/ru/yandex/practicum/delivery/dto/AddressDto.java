package ru.yandex.practicum.delivery.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class AddressDto {
    String country;
    String city;
    String street;
    String house;
    String flat;
}
