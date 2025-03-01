package ru.yandex.practicum.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;

@RequiredArgsConstructor
@Component
public class DeliveryDtoToDeliveryConverter implements Converter<DeliveryDto, Delivery> {

    @Qualifier("conversionService")
    private final ConversionService converter;

    @Override
    public Delivery convert(DeliveryDto source) {
        return Delivery.builder()
                .deliveryId(source.getDeliveryId())
                .fromAddress(converter.convert(source.getFromAddress(), Address.class))
                .toAddress(converter.convert(source.getToAddress(), Address.class))
                .orderId(source.getOrderId())
                .state(source.getState())
                .build();
    }
}
