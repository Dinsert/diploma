package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO для представления списка комментариев в ответах API.
 * Содержит общее количество комментариев и список объектов комментариев.
 */
@Data
public class Comments {

    /**
     * Общее количество комментариев.
     */
    @Schema(type = "integer", format = "int32", description = "общее количество комментариев")
    private int count;

    /**
     * Список комментариев.
     */
    @Schema(type = "array")
    private List<Comment> results;
}