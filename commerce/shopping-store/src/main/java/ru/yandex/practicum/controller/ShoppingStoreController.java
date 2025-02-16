package ru.yandex.practicum.controller;

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

    @PutMapping
    ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto) {
        return shoppingStoreService.createNewProduct(productDto);
    }

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto)  {
        return shoppingStoreService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestParam("productId") UUID productId) {
        return shoppingStoreService.removeProductFromStore(productId);
    }

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request){
        return shoppingStoreService.setProductQuantityState(request);
    }

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable("productId") UUID productId)  {
        return shoppingStoreService.getProduct(productId);
    }
}
