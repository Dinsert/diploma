package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для представления пользователя в ответах API.
 * Содержит основные данные о пользователе, включая аватар и роль.
 */
@Data
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Schema(type = "integer", format = "int32", description = "id пользователя")
    private int id;

    /**
     * Логин пользователя (email).
     */
    @Schema(type = "string", description = "логин пользователя")
    private String email;

    /**
     * Имя пользователя.
     */
    @Schema(type = "string", description = "имя пользователя")
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Schema(type = "string", description = "фамилия пользователя")
    private String lastName;

    /**
     * Телефон пользователя.
     */
    @Schema(type = "string", description = "телефон пользователя")
    private String phone;

    /**
     * Роль пользователя.
     */
    @Schema(type = "string", description = "роль пользователя")
    private Role role;

    /**
     * Ссылка на аватар пользователя.
     */
    @Schema(type = "string", description = "ссылка на аватар пользователя")
    private String image;
}