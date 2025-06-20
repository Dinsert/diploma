package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Login {

    @Schema(type = "string", description = "логин", minLength = 4, maxLength = 32)
    private String username;

    @Schema(type = "string", description = "пароль", minLength = 8, maxLength = 16)
    private String password;
}
