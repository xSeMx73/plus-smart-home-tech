package ru.yandex.practicum.shoppingStore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetProductQuantityStateRequest {
    @NotNull
    UUID productId;

    @NotNull
    QuantityState quantityState;



    public enum QuantityState {
        ENDED,
        FEW,
        ENOUGH,
        MANY;

        public static QuantityState determineState(int quantity) {
            if (quantity == 0) {
                return QuantityState.ENDED;
            } else if (quantity > 0 && quantity < 5) {
                return QuantityState.FEW;
            } else if (quantity >= 5 && quantity <= 20) {
                return QuantityState.ENOUGH;
            } else {
                return QuantityState.MANY;
            }
        }
    }
}