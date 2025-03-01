package ru.yandex.practicum.service;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.AddressDto;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.delivery.feign.DeliveryClient;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.order.enums.OrderState;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.feign.PaymentClient;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.dto.BookedProductDto;
import ru.yandex.practicum.warehouse.feign.WarehouseClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService  {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Qualifier("conversionService")
    private final ConversionService converter;



    public List<OrderDto> getClientOrders(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedException("Имя пользователя не должно быть пустым");
        }

        List<Order> orders = orderRepository.findByUsername(username);

        return orders.stream()
                .map(o -> converter.convert(o, OrderDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Создание нового заказа");

        BookedProductDto bookedProducts = warehouseClient.checkShoppingCart(request.getShoppingCart());
        Order order = converter.convert(request, Order.class);
        Objects.requireNonNull(order).setDeliveryWeight(bookedProducts.getDeliveryWeight());
        order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        order.setFragile(bookedProducts.isFragile());
        order = orderRepository.save(order);

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        DeliveryDto newDelivery = DeliveryDto.builder()
                .fromAddress(warehouseAddress)
                .toAddress(request.getAddress())
                .orderId(order.getOrderId())
                .state(DeliveryState.CREATED)
                .build();
        newDelivery = deliveryClient.planDelivery(newDelivery);
        order.setDeliveryId(newDelivery.getDeliveryId());

        order = orderRepository.save(order);
        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        log.info("Оплата для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        PaymentDto paymentDto = paymentClient.payment(converter.convert(order, OrderDto.class));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.PAID);

        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Обработка неуспешной оплаты для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("Создание доставки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Обработка неуспешной доставки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto completed(UUID orderId) {
        log.info("Завершение заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Расчет общей стоимости для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        Double totalPrice = paymentClient.getTotalCost(converter.convert(order, OrderDto.class));
        order.setTotalPrice(totalPrice);

        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Расчет стоимости доставки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        Double deliveryPrice = deliveryClient.deliveryCost(converter.convert(order, OrderDto.class));
        order.setDeliveryPrice(deliveryPrice);

        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("Обработка успешной сборки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.ASSEMBLED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Обработка неуспешной сборки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        log.info("Обработка возврата для заказа: {}", productReturnRequest.getOrderId());
        Order order = getOrderById(productReturnRequest.getOrderId());

        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);

        return converter.convert(order, OrderDto.class);
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден: " + orderId));
    }
}
