package ru.practicum.shoppingstore.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shoppingStore.dto.Pageable;
import shoppingStore.dto.ProductDto;

import java.util.List;


@RequiredArgsConstructor
@Service
public class ShoppingStoreService {
    public List<ProductDto> getProductsByCategory(ProductDto.ProductCategory category, @Valid Pageable pageable) {
        return null;
    }
}
