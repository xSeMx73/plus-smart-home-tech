package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.PaymentNotFoundException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.feign.OrderClient;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.enums.PaymentState;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.shoppingStore.dto.ProductDto;
import ru.yandex.practicum.shoppingStore.feign.ShoppingStoreClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;
    @Qualifier("conversionService")
    private final ConversionService converter;

    private static final double VAT_RATE = 0.20;

    public Double productCost(OrderDto order) {
        log.info("Расчет стоимости товаров для заказа: {}", order.getOrderId());
        List<Double> pricesList = new ArrayList<>();
        Map<UUID, Integer> orderProducts = order.getProducts();

        orderProducts.forEach((id, quantity) -> {
            ProductDto product = shoppingStoreClient.getProduct(id);
            double totalProductPrice = product.getPrice() * quantity;
            pricesList.add(totalProductPrice);
        });

        double totalProductCost = pricesList.stream().mapToDouble(Double::doubleValue).sum();

        log.info("Стоимость товаров для заказа {}: {}", order.getOrderId(), totalProductCost);

        return totalProductCost;
    }

    public Double getTotalCost(OrderDto order) {
        log.info("Расчет общей стоимости для заказа: {}", order.getOrderId());

        double productsPrice = order.getProductPrice();
        double deliveryPrice = order.getDeliveryPrice();
        double totalCost = deliveryPrice + productsPrice + (productsPrice * VAT_RATE);

        log.info("Общая стоимость для заказа {}: {}", order.getOrderId(), totalCost);
        return totalCost;
    }

    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("Создание платежа для заказа: {}", orderDto.getOrderId());

        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .productTotal(productCost(orderDto))
                .deliveryTotal(orderDto.getDeliveryPrice())
                .totalPayment(getTotalCost(orderDto))
                .state(PaymentState.PENDING)
                .build();

        paymentRepository.save(payment);

        log.info("Платеж с ID {} создан для заказа {}", payment.getPaymentId(), orderDto.getOrderId());

        return converter.convert(payment, PaymentDto.class);
    }

    @Transactional
    public void paymentSuccess(UUID paymentId) {
        log.info("Обработка успешной оплаты для платежа: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Платеж не найден: " + paymentId));

        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

        orderClient.completed(payment.getOrderId());
    }

    @Transactional
    public void paymentFailed(UUID paymentId) {
        log.info("Обработка отказа в оплате для платежа: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Платеж не найден: " + paymentId));

        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
    }
}
