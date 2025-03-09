package ru.yandex.practicum.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

@FeignClient(name = "payment-service", path = "/api/v1/payment")
public interface PaymentClient {

    @PostMapping("/productCost")
    Double productCost(@RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    Double getTotalCost(@RequestBody OrderDto orderDto);

    @PostMapping
    PaymentDto payment(@RequestBody OrderDto orderDto);
}
