package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

import java.io.IOException;

/**
 * Интерфейс сервиса для управления объявлениями.
 * Предоставляет методы для создания, получения, обновления и удаления объявлений,
 * а также работы с изображениями.
 */
public interface AdEntityService {

    /**
     * Получает список всех объявлений.
     *
     * @return объект DTO со списком объявлений
     */
    Ads getAllAds();

    /**
     * Создает новое объявление с прикрепленным изображением.
     *
     * @param properties     данные объявления
     * @param image          файл изображения
     * @param authentication данные аутентификации автора
     * @return DTO созданного объявления
     * @throws IOException если произошла ошибка при обработке изображения
     */
    Ad addAd(CreateOrUpdateAd properties, MultipartFile image, Authentication authentication) throws IOException;

    /**
     * Получает детальную информацию об объявлении по его идентификатору.
     *
     * @param id             идентификатор объявления
     * @param authentication данные аутентификации
     * @return расширенный DTO объявления
     */
    ExtendedAd getAds(int id, Authentication authentication);

    /**
     * Удаляет объявление по его идентификатору.
     *
     * @param id идентификатор объявления
     * @throws IOException если произошла ошибка при удалении
     */
    void removeAd(int id) throws IOException;

    /**
     * Обновляет данные существующего объявления.
     *
     * @param id       идентификатор объявления
     * @param updateAd данные для обновления
     * @return обновленное DTO объявления
     */
    Ad updateAds(int id, CreateOrUpdateAd updateAd);

    /**
     * Получает список объявлений текущего аутентифицированного пользователя.
     *
     * @param authentication данные аутентификации
     * @return объект DTO со списком объявлений
     */
    Ads getAdsMe(Authentication authentication);

    /**
     * Обновляет изображение объявления.
     *
     * @param id             идентификатор объявления
     * @param image          новый файл изображения
     * @param authentication данные аутентификации
     * @return массив байтов обновленного изображения
     * @throws IOException если произошла ошибка при обработке изображения
     */
    byte[] updateImage(int id, MultipartFile image, Authentication authentication) throws IOException;

    /**
     * Проверяет, является ли указанный пользователь владельцем объявления.
     *
     * @param username имя пользователя
     * @param id       идентификатор объявления
     * @return true, если пользователь является владельцем, иначе false
     */
    boolean isOwner(String username, int id);
}
