package ru.yandex.practicum.shoppingStore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableDto {

    @NotNull
    @Min(value = 0)
    int page;

    @NotNull
    @Min(value = 1)
    int size;

    List<String> sort;
}
