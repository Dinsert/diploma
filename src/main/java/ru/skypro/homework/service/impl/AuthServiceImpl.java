package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserEntityMapper;
import ru.skypro.homework.repository.UserEntityRepository;
import ru.skypro.homework.service.AuthService;

/**
 * Сервис для управления аутентификации пользователей.
 * Предоставляет методы для регистрации и авторизации.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /**
     * Репозиторий для работы с сущностями пользователей в базе данных.
     */
    private final UserEntityRepository userEntityRepository;

    /**
     * Кодировщик паролей для безопасного хранения.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Маппер для преобразования между сущностями и DTO.
     */
    private final UserEntityMapper userEntityMapper;

    /**
     * Проверяет авторизацию пользователя.
     *
     * @param userName логин пользователя
     * @param password пароль пользователя
     * @return true если логин и пароль корректны, иначе false
     */
    @Transactional(readOnly = true)
    @Override
    public boolean login(String userName, String password) {
        return userEntityRepository.findByUsername(userName)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    /**
     * Регистрирует пользователя.
     *
     * @param register данные для регистрации (логин, пароль, имя, фамилия, телефон и роль пользователя)
     * @return true если регистрация успешна, иначе false
     */
    @Transactional
    @Override
    public boolean register(Register register) {
        if (userEntityRepository.findByUsername(register.getUsername()).isPresent()) {
            return false;
        }

        UserEntity userEntity = userEntityMapper.registerUser(register);
        userEntity.setPassword(passwordEncoder.encode(register.getPassword()));
        userEntityRepository.save(userEntity);

        return true;
    }

}