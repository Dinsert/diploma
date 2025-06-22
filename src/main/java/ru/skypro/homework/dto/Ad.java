package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для представления объявления в ответах API.
 * Содержит основные данные об объявлении, такие как автор, заголовок и цена.
 */
@Data
public class Ad {

    /**
     * Идентификатор автора объявления.
     */
    @Schema(type = "integer", format = "int32", description = "id автора объявления")
    private int author;

    /**
     * Ссылка на изображение объявления.
     */
    @Schema(type = "string", description = "ссылка на картинку объявления")
    private String image;

    /**
     * Уникальный идентификатор объявления.
     */
    @Schema(type = "integer", format = "int32", description = "id объявления")
    private int pk;

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
