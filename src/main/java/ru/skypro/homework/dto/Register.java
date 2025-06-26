package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DTO для регистрации нового пользователя.
 * Содержит данные для создания учетной записи.
 */
@Data
public class Register {

    /**
     * Логин пользователя (email).
     */
    @Schema(type = "string", description = "логин", minLength = 4, maxLength = 32)
    @NotBlank(message = "Логин не может быть пустым или не указанным")
    @Email(message = "Логин должен быть формата электронной почты: example@mail.ru")
    @Size(min = 4, max = 32, message = "Логин не может быть меньше 4 символов и не больше 32 символов")
    private String username;

    /**
     * Пароль пользователя.
     */
    @Schema(type = "string", description = "пароль", minLength = 8, maxLength = 16)
    @NotBlank(message = "Пароль не может быть пустым или не указанным")
    @Size(min = 8, max = 16, message = "Пароль не может быть меньше 8 символов и не больше 16 символов")
    private String password;

    /**
     * Имя пользователя.
     */
    @Schema(type = "string", description = "имя пользователя", minLength = 2, maxLength = 16)
    @NotBlank(message = "Имя пользователя не может быть пустым или не указанным")
    @Size(min = 2, max = 16, message = "Имя пользователя не может быть меньше 2 символов и не больше 16 символов")
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Schema(type = "string", description = "фамилия пользователя", minLength = 2, maxLength = 16)
    @NotBlank(message = "Фамилия пользователя не может быть пустым или не указанным")
    @Size(min = 2, max = 16, message = "Фамилия пользователя не может быть меньше 2 символов и не больше 16 символов")
    private String lastName;

    /**
     * Телефон пользователя.
     */
    @Schema(type = "string", description = "телефон пользователя", pattern = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    @NotBlank(message = "Телефон пользователя не может быть пустым или не указанным")
    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}", message = "Номер телефона должен быть указан в формате: +7(987)654-32-10/+79876543210")
    private String phone;

    /**
     * Роль пользователя.
     */
    @Schema(type = "string", description = "роль пользователя")
    private Role role;
}