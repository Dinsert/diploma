package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO для представления списка объявлений в ответах API.
 * Содержит общее количество объявлений и список объектов объявлений.
 */
@Data
public class Ads {

    /**
     * Общее количество объявлений.
     */
    @Schema(type = "integer", format = "int32", description = "общее количество объявлений")
    private int count;

    /**
     * Список объявлений.
     */
    @Schema(type = "array")
    private List<Ad> results;
}