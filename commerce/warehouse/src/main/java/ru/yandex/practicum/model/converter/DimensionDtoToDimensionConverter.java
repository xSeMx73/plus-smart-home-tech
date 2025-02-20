package ru.yandex.practicum.model.converter;


import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.warehouse.dto.DimensionDto;

@RequiredArgsConstructor
@Component
public class DimensionDtoToDimensionConverter implements Converter<DimensionDto, Dimension> {
    @Override
    public Dimension convert(DimensionDto source) {
        return Dimension.builder()
                .depth(source.getDepth())
                .height(source.getHeight())
                .width(source.getWidth())
                .build();
    }
}
