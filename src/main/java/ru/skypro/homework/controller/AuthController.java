package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.service.AuthService;


/**
 * Контроллер для управления аутентификации пользователей.
 * Предоставляет API для регистрации и авторизации.
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Проверяет авторизацию пользователя.
     *
     * @param login данные для авторизации (логин и пароль пользователя)
     * @return 200 OK если логин и пароль корректны, иначе 403 UNAUTHORIZED
     */
    @Tag(name = "Авторизация")
    @Operation(summary = "Авторизация пользователя", operationId = "login")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login login) {
        if (authService.login(login.getUsername(), login.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Регистрирует пользователя.
     *
     * @param register данные для регистрации (логин, пароль, имя, фамилия, телефон и роль пользователя)
     * @return 201 Created если регистрация успешна, иначе 400 Bad Request
     */
    @Tag(name = "Регистрация")
    @Operation(summary = "Регистрация пользователя", operationId = "register")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Register register) {
        if (authService.register(register)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
