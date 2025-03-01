package ru.yandex.practicum.payment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PaymentDto {
    UUID paymentId;
    Double totalPayment;
    Double deliveryPayment;
    Double feePayment;
}
