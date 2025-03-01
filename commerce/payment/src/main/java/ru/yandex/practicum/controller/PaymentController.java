package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    PaymentDto payment(@RequestBody OrderDto orderDto) {
        log.info("Формирование оплаты для заказа {}", orderDto);
        return paymentService.createPayment(orderDto);
    }

    @PostMapping("/totalCost")
    Double getTotalCost(@RequestBody OrderDto orderDto) {
        log.info("Расчёт полной стоимости заказа {}", orderDto);
        return paymentService.getTotalCost(orderDto);
    }

    @PostMapping("/refund")
    void paymentSuccess(@RequestParam UUID paymentId) {
        log.info("Успешная оплата с ID: {}", paymentId);
        paymentService.paymentSuccess(paymentId);
    }

    @PostMapping("/productCost")
    Double productCost(@RequestBody OrderDto orderDto) {
        log.info("Расчёт стоимости товаров в заказе {}", orderDto);
        return paymentService.productCost(orderDto);
    }

    @PostMapping("failed")
    void paymentFailed(@RequestParam UUID paymentId) {
        log.info("Отказа в оплате с ID: {} платежного шлюза", paymentId);
        paymentService.paymentFailed(paymentId);
    }
}
