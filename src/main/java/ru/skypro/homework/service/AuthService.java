package ru.skypro.homework.service;

import ru.skypro.homework.dto.Register;

/**
 * Интерфейс сервиса для обработки авторизации и регистрации пользователей.
 * Предоставляет методы для проверки логина и регистрации новых пользователей.
 */
public interface AuthService {

    /**
     * Выполняет проверку логина пользователя.
     *
     * @param userName имя пользователя
     * @param password пароль пользователя
     * @return true, если авторизация успешна, иначе false
     */
    boolean login(String userName, String password);

    /**
     * Регистрирует нового пользователя на основе предоставленных данных.
     *
     * @param register данные для регистрации
     * @return true, если регистрация успешна, иначе false
     */
    boolean register(Register register);
}