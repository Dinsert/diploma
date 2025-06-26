package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.UserEntityService;

import javax.validation.Valid;
import java.io.IOException;

/**
 * Контроллер для управления сущностями пользователей.
 * Предоставляет API для обновления пароля, получения информации о пользователе,
 * обновления профиля и аватара.
 */
@Tag(name = "Пользователи")
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserEntityService userEntityService;

    /**
     * Обновляет пароль текущего аутентифицированного пользователя.
     *
     * @param newPassword    данные нового и текущего пароля
     * @param authentication объект аутентификации для получения логина пользователя
     */
    @Operation(summary = "Обновление пароля", operationId = "setPassword")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/set_password")
    public void setPassword(@Valid @RequestBody NewPassword newPassword, Authentication authentication) {
        userEntityService.setPassword(newPassword, authentication);
    }

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @param authentication объект аутентификации для получения логина пользователя
     * @return DTO с данными пользователя
     */
    @Operation(summary = "Получение информации об авторизованном пользователе", operationId = "getUser")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/me")
    public User getUser(Authentication authentication) {
        return userEntityService.getUser(authentication);
    }

    /**
     * Обновляет информацию о текущем аутентифицированном пользователе.
     *
     * @param updateUser     данные для обновления (имя, фамилия, телефон)
     * @param authentication объект аутентификации для получения логина пользователя
     * @return обновленный DTO пользователя
     */
    @Operation(summary = "Обновление информации об авторизованном пользователе", operationId = "updateUser")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UpdateUser.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PatchMapping("/me")
    public UpdateUser updateUser(@Valid @RequestBody UpdateUser updateUser, Authentication authentication) {
        return userEntityService.updateUser(updateUser, authentication);
    }

    /**
     * Обновляет аватар текущего аутентифицированного пользователя.
     *
     * @param image          файл изображения
     * @param authentication объект аутентификации для получения логина пользователя
     * @throws IOException если произошла ошибка при обработке изображения
     */
    @Operation(summary = "Обновление аватара авторизованного пользователя", operationId = "updateUserImage")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateUserImage(@RequestParam MultipartFile image, Authentication authentication) throws IOException {
        userEntityService.updateUserImage(image, authentication);
    }
}
