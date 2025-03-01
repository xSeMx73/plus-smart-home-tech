package ru.yandex.practicum.model.conveter;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.payment.dto.PaymentDto;

public class PaymentToPaymentDto implements Converter<Payment, PaymentDto> {

    @Override
    public PaymentDto convert(Payment source) {
        return PaymentDto.builder()
                .paymentId(source.getPaymentId())
                .feePayment(source.getProductTotal())
                .deliveryPayment(source.getDeliveryTotal())
                .totalPayment(source.getTotalPayment())
                .build();
    }
}
