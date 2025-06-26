package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO для создания или обновления комментария.
 * Содержит данные для отправки текста комментария через API.
 */
@Data
public class CreateOrUpdateComment {

    /**
     * Текст комментария.
     */
    @Schema(type = "string", description = "текст комментария", minLength = 8, maxLength = 64)
    @NotBlank(message = "Текст комментария не может быть пустым или не указанным")
    @Size(min = 8, max = 64, message = "Текст комментария не может быть меньше 8 или больше 64")
    private String text;
}