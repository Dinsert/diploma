package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;

import java.io.IOException;

/**
 * Интерфейс сервиса для управления данными пользователей.
 * Предоставляет методы для обновления пароля, получения профиля, изменения данных и аватара.
 */
public interface UserEntityService {

    /**
     * Обновляет пароль текущего аутентифицированного пользователя.
     *
     * @param newPassword    данные нового и текущего пароля
     * @param authentication данные аутентификации
     */
    void setPassword(NewPassword newPassword, Authentication authentication);

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @param authentication данные аутентификации
     * @return DTO с данными пользователя
     */
    User getUser(Authentication authentication);

    /**
     * Обновляет данные текущего аутентифицированного пользователя.
     *
     * @param updateUser     данные для обновления
     * @param authentication данные аутентификации
     * @return обновленное DTO пользователя
     */
    UpdateUser updateUser(UpdateUser updateUser, Authentication authentication);

    /**
     * Обновляет аватар текущего аутентифицированного пользователя.
     *
     * @param image          файл изображения аватара
     * @param authentication данные аутентификации
     * @throws IOException если произошла ошибка при обработке изображения
     */
    void updateUserImage(MultipartFile image, Authentication authentication) throws IOException;
}