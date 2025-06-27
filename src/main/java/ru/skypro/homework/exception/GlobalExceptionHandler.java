package ru.skypro.homework.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.skypro.homework.dto.ErrorResponseDTO;


/**
 * Глобальный обработчик исключений для API.
 * Обрабатывает различные типы исключений и возвращает унифицированные ответы в формате ErrorResponseDTO.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение, возникающее при отсутствии пользователя.
     *
     * @param e       исключение о не найденном пользователе
     * @param request запрос, вызвавший исключение
     * @return ответ с кодом 404 и сообщением об ошибке
     */
    @ExceptionHandler(UserEntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserEntityNotFound(UserEntityNotFoundException e, HttpServletRequest request) {
        log.warn("Пользователь не найден: {}", e.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, "UserEntity Not Found", e.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает исключение, возникающее при отсутствии объявления.
     *
     * @param e       исключение о не найденном объявлении
     * @param request запрос, вызвавший исключение
     * @return ответ с кодом 404 и сообщением об ошибке
     */
    @ExceptionHandler(AdEntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAdEntityNotFound(AdEntityNotFoundException e, HttpServletRequest request) {
        log.warn("Объявление не найдено: {}", e.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, "AdEntity Not Found", e.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает исключение, возникающее при отсутствии комментария.
     *
     * @param e       исключение о не найденном комментарии
     * @param request запрос, вызвавший исключение
     * @return ответ с кодом 404 и сообщением об ошибке
     */
    @ExceptionHandler(CommentEntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCommentEntityNotFound(CommentEntityNotFoundException e, HttpServletRequest request) {
        log.warn("Комментарий не найден: {}", e.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, "CommentEntity Not Found", e.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает исключения валидации аргументов метода.
     *
     * @param e       исключение валидации
     * @param request запрос, вызвавший исключение
     * @return ответ с кодом 400 и сообщением об ошибке
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null
                ? String.format("Ошибка в поле '%s': %s", fieldError.getField(), fieldError.getDefaultMessage())
                : "Ошибка валидации";
        log.warn("Ошибка валидации: {}", message);
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", message, request.getRequestURI());
    }

    /**
     * Обрабатывает исключения доступа, связанные с недостатком прав.
     *
     * @param ex      исключение доступа
     * @param request запрос, вызвавший исключение
     * @return ответ с кастомным сообщением об ошибке
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Доступ запрещен для запроса: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);
        String message = "У вас недостаточно прав для выполнения этого действия. Вы не являетесь владельцем объявления.";
        return createErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", message, request.getRequestURI());
    }

    /**
     * Обрабатывает общие исключения, не предусмотренные другими обработчиками.
     *
     * @param e       общее исключение
     * @param request запрос, вызвавший исключение
     * @return ответ с кодом 500 и общим сообщением об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception e, HttpServletRequest request) {
        log.error("Непредвиденная ошибка: {}", e.getMessage(), e);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Произошла непредвиденная ошибка", request.getRequestURI());
    }

    /**
     * Создает унифицированный ответ об ошибке.
     *
     * @param status  HTTP-статус ошибки
     * @param error   тип ошибки
     * @param message сообщение об ошибке
     * @param path    путь запроса
     * @return ответ с телом ошибки
     */
    private ResponseEntity<ErrorResponseDTO> createErrorResponse(HttpStatus status, String error, String message, String path) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(status.value(), error, message, path);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
