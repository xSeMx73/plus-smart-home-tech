package ru.yandex.practicum.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.dto.AddressDto;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.shoppingCart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.warehouse.dto.BookedProductDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PutMapping
    public void addNewProduct(@RequestBody @Valid NewProductInWarehouseRequest request) {
        warehouseService.addNewProduct(request);
    }

    @PostMapping("/return")
    public void acceptReturn(@RequestBody Map<UUID, Integer> products) {
        warehouseService.acceptReturn(products);
    }

    @PostMapping("/booking")
    public BookedProductDto bookProductForShoppingCart(@RequestBody @Valid ShoppingCartDto cartDto) {
        return warehouseService.bookProductForShoppingCart(cartDto);
    }

    @PostMapping("/assembly")
    public BookedProductDto assemblyProductForOrderFromShoppingCart(
            @RequestBody @Valid AssemblyProductForOrderFromShoppingCartRequest request) {
        return warehouseService.assemblyProductForOrderFromShoppingCart(request);
    }

    @PostMapping("/add")
    public void addProductQuantity(@RequestBody @Valid AddProductToWarehouseRequest request) {
        warehouseService.addProductQuantity(request);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }

}