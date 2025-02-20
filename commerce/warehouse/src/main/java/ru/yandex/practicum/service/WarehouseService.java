package ru.yandex.practicum.service;


import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.shoppingCart.dto.ShoppingCartDto;
import ru.yandex.practicum.shoppingStore.dto.ProductDto;
import ru.yandex.practicum.shoppingStore.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.shoppingStore.feign.ShoppingStoreClient;
import ru.yandex.practicum.warehouse.dto.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseProductRepository warehouseProductRepository;
    private final BookingRepository bookingRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    @Qualifier("mvcConversionService")
    private final ConversionService converter;

       public void addNewProduct(NewProductInWarehouseRequest request) {
        log.info("Добавление нового товара: {}", request);

        if (warehouseProductRepository.existsById(request.getProductId())) {
            throw new NoResultException("Товар уже зарегистрирован.");
        }

        ProductDto productDto = shoppingStoreClient.getProduct(request.getProductId());
        if (productDto == null) {
            throw new NoSuchElementException("Продукт с указанным ID отсутствует в магазине.");
        }

        WarehouseProduct newProduct = converter.convert(request, WarehouseProduct.class);
        warehouseProductRepository.save(Objects.requireNonNull(newProduct));
    }


    @Transactional
    public void acceptReturn(Map<UUID, Integer> products) {
        log.info("Принятие возврата товаров: {}", products);

        products.forEach((productId, quantity) -> {
            WarehouseProduct product = warehouseProductRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException("Товар не найден на складе."));
            product.setQuantityAvailable(product.getQuantityAvailable() + quantity);
            warehouseProductRepository.save(product);
        });
    }


    @Transactional
    public BookedProductDto bookProductForShoppingCart(ShoppingCartDto shoppingCart) {
        log.info("Бронирование товаров по корзине: {}", shoppingCart);

        double totalWeight = 0;
        double totalVolume = 0;
        boolean fragile = false;

        for (Map.Entry<UUID, Integer> entry : shoppingCart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            int requestedQuantity = entry.getValue();

            WarehouseProduct product = warehouseProductRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException("Товар не найден: " + productId));

            if (product.getQuantityAvailable() < requestedQuantity) {
                throw new NoSuchElementException("Недостаточное количество товара: " + productId);
            }

            product.setQuantityAvailable(product.getQuantityAvailable() - requestedQuantity);
            warehouseProductRepository.save(product);

            SetProductQuantityStateRequest.QuantityState newState = SetProductQuantityStateRequest.QuantityState.determineState(product.getQuantityAvailable());
            shoppingStoreClient.setProductQuantityState(
                    SetProductQuantityStateRequest.builder()
                            .productId(productId)
                            .quantityState(newState)
                            .build()
            );

            totalWeight += product.getWeight() * requestedQuantity;
            totalVolume += product.getDimension().getWidth()
                           * product.getDimension().getHeight()
                           * product.getDimension().getDepth() * requestedQuantity;

            fragile |= product.isFragile();
        }

        Booking booking = Booking.builder()
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .products(shoppingCart.getProducts())
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();

        bookingRepository.save(booking);

        log.info("Сборка заказа завершена для корзины с ID: {}", shoppingCart.getShoppingCartId());

        return converter.convert(booking, BookedProductDto.class);
    }


    @Transactional(readOnly = true)
    public BookedProductDto assemblyProductForOrderFromShoppingCart(AssemblyProductForOrderFromShoppingCartRequest request) {
        log.info("Сборка заказа для корзины с ID: {} и заказа с ID: {}", request.getShoppingCartId(), request.getOrderId());

        Booking booking = bookingRepository.findById(request.getShoppingCartId())
                .orElseThrow(() -> new NoSuchElementException("Бронирование с ID " + request.getShoppingCartId() + " не найдено."));

        if (booking.getProducts() == null || booking.getProducts().isEmpty()) {
            throw new NoSuchElementException("Корзина пуста или не найдена.");
        }

        log.info("Сборка заказа завершена для корзины с ID: {} и заказа с ID: {}", request.getShoppingCartId(), request.getOrderId());

        return converter.convert(booking, BookedProductDto.class);
    }

    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        log.info("Добавление количества товара: {}", request);

        WarehouseProduct product = warehouseProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSuchElementException("Товар не найден на складе."));

        product.setQuantityAvailable(product.getQuantityAvailable() + request.getQuantity());
        WarehouseProduct updatedProduct = warehouseProductRepository.save(product);

        SetProductQuantityStateRequest.QuantityState newState = SetProductQuantityStateRequest.QuantityState.determineState(updatedProduct.getQuantityAvailable());
        shoppingStoreClient.setProductQuantityState(
                SetProductQuantityStateRequest.builder()
                        .productId(request.getProductId())
                        .quantityState(newState)
                        .build()
        );
    }

    public AddressDto getWarehouseAddress() {
        log.info("Получение адреса склада");

        return AddressDto.builder()
                .country("Country")
                .city("City")
                .street("Street")
                .house("1")
                .flat("1")
                .build();
    }
}