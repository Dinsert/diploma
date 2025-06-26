package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO для обновления пароля пользователя.
 * Содержит текущий и новый пароли для смены.
 */
@Data
public class NewPassword {

    /**
     * Текущий пароль пользователя.
     */
    @Schema(type = "string", description = "текущий пароль", minLength = 8, maxLength = 16)
    @NotBlank(message = "Текущий пароль не может быть пустым или не указанным")
    @Size(min = 8, max = 16, message = "Текущий пароль не может быть меньше 8 или больше 16")
    private String currentPassword;

    /**
     * Новый пароль пользователя.
     */
    @Schema(type = "string", description = "новый пароль", minLength = 8, maxLength = 16)
    @NotBlank(message = "Новый пароль не может быть пустым или не указанным")
    @Size(min = 8, max = 16, message = "Новый пароль не может быть меньше 8 или больше 16")
    private String newPassword;
}