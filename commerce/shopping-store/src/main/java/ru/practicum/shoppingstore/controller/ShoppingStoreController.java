package ru.practicum.shoppingstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shoppingstore.service.ShoppingStoreService;
import shoppingStore.dto.Pageable;
import shoppingStore.dto.ProductDto;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping
@RestController
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    List<ProductDto> getProducts(@RequestParam("category") ProductDto.ProductCategory category,
                                 @Valid @RequestParam("pageableDto") Pageable pageable) {
        return shoppingStoreService.getProductsByCategory(category, pageable);
    }
}
