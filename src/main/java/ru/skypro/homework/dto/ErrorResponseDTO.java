package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

/**
 * DTO для представления ошибки в ответах API.
 * Используется для унифицированного ответа при возникновении исключений.
 */
@Data
@Schema(description = "DTO для представления ошибки в ответе API")
public class ErrorResponseDTO {

    /**
     * Время возникновения ошибки в формате ISO 8601.
     */
    @Schema(description = "Время возникновения ошибки в формате ISO 8601", example = "2025-05-08T12:34:56Z")
    private Instant timestamp;

    /**
     * HTTP-статус ошибки.
     */
    @Schema(description = "HTTP-статус ошибки", example = "400")
    private int status;

    /**
     * Краткое описание типа ошибки.
     */
    @Schema(description = "Краткое описание типа ошибки", example = "Bad Request")
    private String error;

    /**
     * Подробное сообщение об ошибке.
     */
    @Schema(description = "Подробное сообщение об ошибке", example = "Идентификатор артефакта не может быть 0 или меньше")
    private String message;

    /**
     * Путь запроса, вызвавшего ошибку.
     */
    @Schema(description = "Путь запроса, вызвавшего ошибку", example = "/users/me")
    private String path;

    /**
     * Конструктор для создания объекта ошибки.
     *
     * @param status  HTTP-статус ошибки
     * @param error   тип ошибки
     * @param message сообщение об ошибке
     * @param path    путь запроса
     */
    public ErrorResponseDTO(int status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}