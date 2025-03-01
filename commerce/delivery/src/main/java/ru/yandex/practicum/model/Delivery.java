package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.delivery.enums.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    UUID deliveryId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "from_address_id", nullable = false)
    Address fromAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "to_address_id", nullable = false)
    Address toAddress;

    @Column(name = "order_id", nullable = false)
    UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state", nullable = false)
    DeliveryState state;

    @Column(name = "delivery_weight", nullable = false)
    Double deliveryWeight;

    @Column(name = "delivery_volume", nullable = false)
    Double deliveryVolume;

    @Column(name = "fragile", nullable = false)
    Boolean fragile;
}
