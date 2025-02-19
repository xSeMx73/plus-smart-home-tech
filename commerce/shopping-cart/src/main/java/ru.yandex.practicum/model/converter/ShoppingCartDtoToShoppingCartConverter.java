package ru.yandex.practicum.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.shoppingCart.dto.ShoppingCartDto;

@RequiredArgsConstructor
@Component
public class ShoppingCartDtoToShoppingCartConverter implements Converter<ShoppingCartDto, ShoppingCart> {

    @Override
    public ShoppingCart convert(ShoppingCartDto source) {
        return ShoppingCart.builder()
                .shoppingCartId(source.getShoppingCartId())
                .products(source.getProducts())
                .build();
    }
}
