package ru.yandex.practicum.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery.dto.AddressDto;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.model.Delivery;

@RequiredArgsConstructor
@Component
public class DeliveryToDeliveryDtoConverter implements Converter<Delivery, DeliveryDto> {

    @Qualifier("conversionService")
    private final ConversionService converter;


    @Override
    public DeliveryDto convert(Delivery source) {
        return DeliveryDto.builder()
                .deliveryId(source.getDeliveryId())
                .fromAddress(converter.convert(source.getFromAddress(), AddressDto.class))
                .toAddress(converter.convert(source.getToAddress(), AddressDto.class))
                .orderId(source.getOrderId())
                .state(source.getState())
                .build();
    }
}
