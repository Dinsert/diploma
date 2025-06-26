package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * DTO для создания или обновления объявления.
 * Содержит данные о заголовке, описании и цене объявления.
 */
@Data
public class CreateOrUpdateAd {

    /**
     * Заголовок объявления.
     */
    @Schema(type = "string", description = "заголовок объявления", minLength = 4, maxLength = 32)
    @NotBlank(message = "Заголовок объявления не может быть пустым или не указанным")
    @Size(min = 4, max = 32, message = "Размер заголовка не может быть меньше 4-ёх символов и не больше 32-ух символов")
    private String title;

    /**
     * Описание объявления.
     */
    @Schema(type = "string", description = "описание объявления", minLength = 8, maxLength = 64)
    @NotBlank(message = "Описание объявления не может быть пустым или не указанным")
    @Size(min = 8, max = 64, message = "Размер описания не должен быть меньше 8 символом и не превышать 64 символа")
    private String description;

    /**
     * Цена объявления.
     */
    @Schema(type = "integer", format = "int32", description = "цена объявления", minimum = "0", maximum = "10000000")
    @NotNull(message = "Цена объявления должна быть указана")
    @PositiveOrZero(message = "Цена объявления должна быть 0 или больше")
    @Max(value = 10_000_000, message = "Цена объявления не может быть больше 10_000_000")
    private Integer price;
}