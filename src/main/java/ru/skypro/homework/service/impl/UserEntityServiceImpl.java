package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.UserEntityNotFoundException;
import ru.skypro.homework.mapper.UserEntityMapper;
import ru.skypro.homework.repository.UserEntityRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserEntityService;

import java.io.IOException;

/**
 * Сервис для управления сущностями пользователей.
 * Предоставляет методы для обновления пароля, получения информации о пользователе,
 * обновления профиля и аватара.
 */
@Service
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

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
     * Сервис для работы с изображениями.
     */
    private final ImageService imageService;

    /**
     * Обновляет пароль текущего аутентифицированного пользователя.
     *
     * @param newPassword    данные нового и текущего пароля
     * @param authentication объект аутентификации для получения логина пользователя
     * @throws UserEntityNotFoundException если пользователь не найден
     * @throws AccessDeniedException       если текущий пароль неверен
     */
    @Transactional
    @Override
    public void setPassword(NewPassword newPassword, Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserEntityNotFoundException("Пользователь не найден"));

        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), userEntity.getPassword())) {
            throw new AccessDeniedException("Текущий пароль неверен");
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userEntityRepository.save(userEntity);
    }

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @param authentication объект аутентификации для получения логина пользователя
     * @return DTO с данными пользователя
     * @throws UserEntityNotFoundException если пользователь не найден
     */
    @Transactional(readOnly = true)
    @Override
    public User getUser(Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserEntityNotFoundException("Пользователь не найден"));

        return userEntityMapper.toDTO(userEntity);
    }

    /**
     * Обновляет информацию о текущем аутентифицированном пользователе.
     *
     * @param updateUser     данные для обновления (имя, фамилия, телефон)
     * @param authentication объект аутентификации для получения логина пользователя
     * @return обновленный DTO пользователя
     * @throws UserEntityNotFoundException если пользователь не найден
     */
    @Transactional
    @Override
    public UpdateUser updateUser(UpdateUser updateUser, Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserEntityNotFoundException("Пользователь не найден"));

        userEntityMapper.updateUserEntity(updateUser, userEntity);
        userEntityRepository.save(userEntity);

        return updateUser;
    }

    /**
     * Обновляет аватар текущего аутентифицированного пользователя.
     *
     * @param image          файл изображения
     * @param authentication объект аутентификации для получения логина пользователя
     * @throws IOException                 если произошла ошибка при обработке изображения
     * @throws UserEntityNotFoundException если пользователь не найден
     */
    @Transactional
    @Override
    public void updateUserImage(MultipartFile image, Authentication authentication) throws IOException {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserEntityNotFoundException("Пользователь не найден"));
        String userEntityImage = userEntity.getImage();

        String imagePath = imageService.saveImage(image);
        userEntity.setImage(imagePath);
        userEntityRepository.save(userEntity);

        if (userEntityImage != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(800);
                    imageService.deleteImage(userEntityImage);
                } catch (IOException | InterruptedException ignored) {
                }
            }).start();
        }
    }
}
