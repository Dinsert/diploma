package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для представления расширенной информации об объявлении в ответах API.
 * Содержит детальные данные об объявлении и авторе.
 */
@Data
public class ExtendedAd {

    /**
     * Уникальный идентификатор объявления.
     */
    @Schema(type = "integer", format = "int32", description = "id объявления")
    private int pk;

    /**
     * Имя автора объявления.
     */
    @Schema(type = "string", description = "имя автора объявления")
    private String authorFirstName;

    /**
     * Фамилия автора объявления.
     */
    @Schema(type = "string", description = "фамилия автора объявления")
    private String authorLastName;

    /**
     * Описание объявления.
     */
    @Schema(type = "string", description = "описание объявления")
    private String description;

    /**
     * Логин (email) автора объявления.
     */
    @Schema(type = "string", description = "логин автора объявления")
    private String email;

    /**
     * Ссылка на картинку объявления.
     */
    @Schema(type = "string", description = "ссылка на картинку объявления")
    private String image;

    /**
     * Телефон автора объявления.
     */
    @Schema(type = "string", description = "телефон автора объявления")
    private String phone;

    /**
     * Цена объявления.
     */
    @Schema(type = "integer", format = "int32", description = "цена объявления")
    private int price;

    /**
     * Заголовок объявления.
     */
    @Schema(type = "string", description = "заголовок объявления")
    private String title;
}