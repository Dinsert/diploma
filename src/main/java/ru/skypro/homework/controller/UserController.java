package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;

@Tag(name = "Пользователи")
@RequestMapping("/users")
@RestController
public class UserController {

    @Operation(summary = "Обновление пароля", operationId = "setPassword")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping("/set_password")
    public void setPassword(@RequestBody NewPassword newPassword) {

    }

    @Operation(summary = "Получение информации об авторизованном пользователе", operationId = "getUser")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/me")
    public User getUser() {
        return new User();
    }

    @Operation(summary = "Обновление информации об авторизованном пользователе", operationId = "updateUser")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UpdateUser.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PatchMapping("/me")
    public UpdateUser updateUser(@RequestBody UpdateUser updateUser) {
        return new UpdateUser();
    }

    @Operation(summary = "Обновление аватара авторизованного пользователя", operationId = "updateUserImage")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateUserImage(@RequestParam MultipartFile image) {

    }
}
