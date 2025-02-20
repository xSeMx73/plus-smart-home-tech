package ru.yandex.practicum.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.warehouse.dto.BookedProductDto;

@RequiredArgsConstructor
@Component
public class BookingToBookedProductDtoConverter implements Converter<Booking, BookedProductDto> {
    @Override
    public BookedProductDto convert(Booking source) {
        return BookedProductDto.builder()
                .deliveryVolume(source.getDeliveryVolume())
                .deliveryWeight(source.getDeliveryWeight())
                .fragile(source.getFragile())
                .build();
    }
}
