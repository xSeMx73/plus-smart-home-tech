package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto) {
        log.info("Создание новой доставки {}", deliveryDto);
        return deliveryService.planDelivery(deliveryDto);
    }

    @PostMapping("/successful")
    void deliverySuccessful(@RequestParam UUID orderId) {
        log.info("Доставка заказа с ID: {} выполнена", orderId);
        deliveryService.deliverySuccessful(orderId);
    }

    @PostMapping("/picked")
    void deliveryPicked(@RequestParam UUID deliveryId) {
        log.info("Товар для доставки с ID: {} получен", deliveryId);
        deliveryService.deliveryPicked(deliveryId);
    }

    @PostMapping("/failed")
    void deliveryFailed(@RequestParam UUID orderId) {
        log.info("Неуспешная попытка доставки заказа с ID: {}", orderId);
        deliveryService.deliveryFailed(orderId);
    }

    @PostMapping
    Double deliveryCost(@RequestBody OrderDto orderDto) {
        log.info("Расчёт полной стоимости доставки заказа с ID: {}", orderDto.getOrderId());
        return deliveryService.deliveryCost(orderDto);
    }
}
