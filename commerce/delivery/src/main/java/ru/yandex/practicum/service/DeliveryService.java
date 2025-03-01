package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.feign.OrderClient;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.feign.WarehouseClient;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService  {

    private final DeliveryRepository deliveryRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    @Qualifier("conversionService")
    private final ConversionService converter;

    private static final double BASE_RATE = 5.0;
    private static final double WAREHOUSE_1_ADDRESS_MULTIPLIER = 1;
    private static final double WAREHOUSE_2_ADDRESS_MULTIPLIER = 2;
    private static final double FRAGILE_MULTIPLIER = 0.2;
    private static final double WEIGHT_MULTIPLIER = 0.3;
    private static final double VOLUME_MULTIPLIER = 0.2;
    private static final double STREET_MULTIPLIER = 0.2;

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Планирование доставки: {}", deliveryDto);

        deliveryDto.setState(DeliveryState.CREATED);

        Delivery delivery = converter.convert(deliveryDto, Delivery.class);

        deliveryRepository.save(Objects.requireNonNull(delivery));

        log.info("Доставка с ID {} запланирована", delivery.getDeliveryId());
        return converter.convert(delivery, DeliveryDto.class);
    }

    public void deliverySuccessful(UUID orderId) {
        log.info("Обработка успешной доставки для заказа: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Не найден заказ: " + orderId));

        delivery.setState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.delivery(delivery.getOrderId());
    }

    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        log.info("Получение товара для доставки: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Доставка не найдена: " + deliveryId));

        delivery.setState(DeliveryState.IN_DELIVERY);
        deliveryRepository.save(delivery);

        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .orderId(delivery.getOrderId())
                .deliveryId(deliveryId)
                .build();

        warehouseClient.shippedToDelivery(request);
    }

    @Transactional
    public void deliveryFailed(UUID orderId) {
        log.info("Обработка неуспешной доставки для заказа: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Не найден заказ: " + orderId));
        delivery.setState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.deliveryFailed(orderId);
    }

    public Double deliveryCost(OrderDto order) {
        Delivery delivery = getDelivery(order.getDeliveryId());
        Address warehouseAddress = delivery.getFromAddress();
        Address destinationAddress = delivery.getToAddress();

        double totalCost = BASE_RATE;

        totalCost += warehouseAddress.getCity().equals("ADDRESS_1")
                ? totalCost * WAREHOUSE_1_ADDRESS_MULTIPLIER : totalCost * WAREHOUSE_2_ADDRESS_MULTIPLIER;

        totalCost += Boolean.TRUE.equals(order.getFragile()) ? totalCost * FRAGILE_MULTIPLIER : 0;

        totalCost += order.getDeliveryWeight() * WEIGHT_MULTIPLIER;

        totalCost += order.getDeliveryVolume() * VOLUME_MULTIPLIER;

        totalCost += warehouseAddress.getStreet().equals(destinationAddress.getStreet())
                ? 0 : totalCost * STREET_MULTIPLIER;

        log.info("Стоимость доставки для заказа {}: {}", order.getOrderId(), totalCost);
        return totalCost;
    }

    private Delivery getDelivery(UUID id) {
        return deliveryRepository.findById(id).orElseThrow(() -> {
            log.info("Не найдена доставка для расчёта с ID: {}", id);
            return new NotFoundException();
        });
    }
}
