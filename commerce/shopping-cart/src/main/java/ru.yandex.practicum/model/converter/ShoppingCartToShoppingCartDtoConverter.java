package ru.yandex.practicum.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.shoppingCart.dto.ShoppingCartDto;

@RequiredArgsConstructor
@Component
public class ShoppingCartToShoppingCartDtoConverter implements Converter<ShoppingCart, ShoppingCartDto> {

    @Override
    public ShoppingCartDto convert(ShoppingCart source) {

        return ShoppingCartDto.builder()
                .shoppingCartId(source.getShoppingCartId())
                .products(source.getProducts())
                .build();
    }
}
