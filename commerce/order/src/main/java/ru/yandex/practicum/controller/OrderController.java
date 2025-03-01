package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    List<OrderDto> getClientOrders(@RequestParam String username) {
        log.info("Получение заказов клиента {}", username);
        return orderService.getClientOrders(username);
    }

    @PutMapping
    OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest createNewOrderRequest) {
        log.info("Создание нового заказ {}", createNewOrderRequest);
        return orderService.createNewOrder(createNewOrderRequest);
    }

    @PostMapping("/return")
    OrderDto productReturn(@RequestBody ProductReturnRequest productReturnRequest) {
        log.info("Возврат заказа {}", productReturnRequest);
        return orderService.productReturn(productReturnRequest);
    }

    @PostMapping("/payment")
    OrderDto payment(@RequestParam UUID orderId) {
        log.info("Оплата заказа с ID: {}", orderId);
        return  orderService.payment(orderId);
    }

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestParam UUID orderId) {
        log.info("Ошибка оплаты заказа С ID: {}", orderId);
        return  orderService.paymentFailed(orderId);
    }

    @PostMapping("/delivery")
    OrderDto delivery(@RequestParam UUID orderId) {
        log.info("Доставка заказа c ID: {}", orderId);
        return orderService.delivery(orderId);
    }

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestParam UUID orderId) {
        log.info("Доставка заказа с ID: {} произошла с ошибкой", orderId);
        return orderService.deliveryFailed(orderId);
    }

    @PostMapping("/completed")
    OrderDto completed(@RequestParam UUID orderId) {
        log.info("Заказ с ID: {} завершен", orderId);
        return orderService.completed(orderId);
    }

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestParam UUID orderId) {
        log.info("Расчёт стоимости заказа с Id {}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestParam UUID orderId) {
        log.info("Расчёт стоимости доставки заказа с ID: {}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @PostMapping("/assembly")
    OrderDto assembly(@RequestParam UUID orderId) {
        log.info("Сборка заказа  с ID: {}", orderId);
        return orderService.assembly(orderId);
    }

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestParam UUID orderId) {
        log.info("Сборка заказа c ID: {} произошла с ошибкой", orderId);
        return orderService.assemblyFailed(orderId);
    }
}
