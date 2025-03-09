package ru.yandex.practicum.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery.dto.AddressDto;
import ru.yandex.practicum.model.Address;


@RequiredArgsConstructor
@Component
public class AddressDtoToAddressConverter implements Converter<AddressDto, Address> {

    @Override
    public Address convert(AddressDto source) {
        return Address.builder()
                .country(source.getCountry())
                .city(source.getCity())
                .street(source.getStreet())
                .house(source.getHouse())
                .flat(source.getFlat())
                .build();
    }
}
