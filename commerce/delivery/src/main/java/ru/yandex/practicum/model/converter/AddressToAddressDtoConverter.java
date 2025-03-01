package ru.yandex.practicum.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery.dto.AddressDto;
import ru.yandex.practicum.model.Address;

@Component
@RequiredArgsConstructor
public class AddressToAddressDtoConverter implements Converter<Address, AddressDto> {

    @Override
    public AddressDto convert(Address source) {
        return AddressDto.builder()
                .city(source.getCity())
                .house(source.getHouse())
                .country(source.getCountry())
                .flat(source.getFlat())
                .street(source.getStreet())
                .build();
    }
}
