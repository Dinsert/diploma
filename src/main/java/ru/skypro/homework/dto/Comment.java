package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для представления комментария в ответах API.
 * Содержит данные о комментарии, авторе и времени создания.
 */
@Data
public class Comment {

    /**
     * Идентификатор автора комментария.
     */
    @Schema(type = "integer", format = "int32", description = "id автора комментария")
    private int author;

    /**
     * Имя создателя комментария.
     */
    @Schema(type = "string", description = "имя создателя комментария")
    private String authorFirstName;

    /**
     * Ссылка на аватар автора комментария.
     */
    @Schema(type = "string", description = "ссылка на аватар автора комментария")
    private String authorImage;

    /**
     * Дата и время создания комментария в миллисекундах с 00:00:00 01.01.1970.
     */
    @Schema(type = "integer", format = "int64", description = "дата и время создания комментария в миллисекундах с 00:00:00 01.01.1970")
    private long createdAt;

    /**
     * Уникальный идентификатор комментария.
     */
    @Schema(type = "integer", format = "int32", description = "id комментария")
    private int pk;

    /**
     * Текст комментария.
     */
    @Schema(type = "string", description = "текст комментария")
    private String text;
}