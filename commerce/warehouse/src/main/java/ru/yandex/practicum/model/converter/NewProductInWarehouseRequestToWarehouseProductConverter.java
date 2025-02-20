package ru.yandex.practicum.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

@RequiredArgsConstructor
@Component
public class NewProductInWarehouseRequestToWarehouseProductConverter implements Converter<NewProductInWarehouseRequest, WarehouseProduct> {

    @Qualifier("mvcConversionService")
    private final ConversionService converter;

    @Override
    public WarehouseProduct convert(NewProductInWarehouseRequest source) {
        return WarehouseProduct.builder()
                .productId(source.getProductId())
                .dimension(converter.convert(source.getDimension(), Dimension.class))
                .weight(source.getWeight())
                .fragile(source.isFragile())
                .quantityAvailable(0)
                .build();
    }
}
