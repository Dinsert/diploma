package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AdEntityNotFoundException;
import ru.skypro.homework.exception.UserEntityNotFoundException;
import ru.skypro.homework.mapper.AdEntityMapper;
import ru.skypro.homework.repository.AdEntityRepository;
import ru.skypro.homework.repository.UserEntityRepository;
import ru.skypro.homework.service.AdEntityService;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления сущностями объявлений.
 * Предоставляет методы для обновления, получения, добавления и удаления объявлений.
 */
@RequiredArgsConstructor
@Service
public class AdEntityServiceImpl implements AdEntityService {

    /**
     * Репозиторий для работы с сущностями объявлений в базе данных.
     */
    private final AdEntityRepository adEntityRepository;

    /**
     * Маппер для преобразования между сущностями и DTO.
     */
    private final AdEntityMapper adEntityMapper;

    /**
     * Репозиторий для работы с сущностями пользователей в базе данных.
     */
    private final UserEntityRepository userEntityRepository;

    /**
     * Сервис для работы с изображениями.
     */
    private final ImageService imageService;

    /**
     * Получает все объявления.
     *
     * @return DTO с данными объявлений
     */
    @Transactional(readOnly = true)
    @Override
    public Ads getAllAds() {
        List<Ad> results = adEntityRepository.findAll().stream().map(adEntityMapper::toDto).toList();

        Ads ads = new Ads();
        ads.setResults(results);
        ads.setCount(results.size());
        return ads;
    }

    /**
     * Добавляет объявление. Доступно только аутентифицированным пользователям.
     *
     * @param properties     данные для создания (заголовок, описание и цена объявления)
     * @param image          файл изображения
     * @param authentication объект аутентификации для получения логина пользователя
     * @return DTO с данными объявления
     * @throws UserEntityNotFoundException если пользователь не найден
     */
    @Transactional
    @Override
    public Ad addAd(CreateOrUpdateAd properties, MultipartFile image, Authentication authentication) throws IOException {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserEntityNotFoundException("Пользователь не найден"));

        String imagePath = imageService.saveImage(image);
        AdEntity adEntity = adEntityMapper.createAdEntity(properties, imagePath, userEntity);
        AdEntity savedAdEntity = adEntityRepository.save(adEntity);

        return adEntityMapper.toDto(savedAdEntity);
    }

    /**
     * Получает расширенную информацию объявления текущего аутентифицированного пользователя.
     *
     * @param id уникальный идентификатор объявления
     * @return DTO с расширенными данными объявления
     * @throws AdEntityNotFoundException если объявление не найдено
     */
    @Transactional(readOnly = true)
    @Override
    public ExtendedAd getAds(int id, Authentication authentication) {
        AdEntity adEntity = adEntityRepository.findById(id).orElseThrow(() -> new AdEntityNotFoundException("Объявление не найдено"));

        return adEntityMapper.toExtendedAd(adEntity);
    }

    /**
     * Удаляет объявление. Доступно только аутентифицированным пользователям.
     *
     * @param id уникальный идентификатор объявления
     * @throws AdEntityNotFoundException если объявление не найдено
     * @throws IOException               если произошла ошибка при обработке изображения
     */
    @Transactional
    @Override
    public void removeAd(int id) throws IOException {
        AdEntity adEntity = adEntityRepository.findById(id).orElseThrow(() -> new AdEntityNotFoundException("Объявление не найдено"));
        String adEntityImage = adEntity.getImage();

        adEntityRepository.delete(adEntity);
        imageService.deleteImage(adEntityImage);
    }

    /**
     * Обновляет объявление. Доступно только аутентифицированным пользователям.
     *
     * @param id       уникальный идентификатор объявления
     * @param updateAd данные для редактирования (заголовок, описание и цена объявления)
     * @return DTO с данными объявления
     * @throws AdEntityNotFoundException если комментарий не найден
     */
    @Transactional
    @Override
    public Ad updateAds(int id, CreateOrUpdateAd updateAd) {
        AdEntity adEntity = adEntityRepository.findById(id).orElseThrow(() -> new AdEntityNotFoundException("Объявление не найдено"));

        adEntityMapper.updateAdEntity(updateAd, adEntity);
        adEntityRepository.save(adEntity);

        return adEntityMapper.toDto(adEntity);
    }

    /**
     * Получает объявления текущего аутентифицированного пользователя.
     *
     * @param authentication объект аутентификации для получения логина пользователя
     * @return DTO с данными объявлений
     * @throws UserEntityNotFoundException если пользователь не найден
     */
    @Transactional(readOnly = true)
    @Override
    public Ads getAdsMe(Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserEntityNotFoundException("Пользователь не найден"));
        List<Ad> results = userEntity.getAdEntities().stream().map(adEntityMapper::toDto).toList();

        Ads ads = new Ads();
        ads.setResults(results);
        ads.setCount(results.size());
        return ads;
    }

    /**
     * Обновляет изображение в объявлении. Доступно только аутентифицированным пользователям.
     *
     * @param id             уникальный идентификатор объявления
     * @param image          файл изображения
     * @param authentication объект аутентификации для получения логина пользователя
     * @return массив байт изображения
     * @throws AdEntityNotFoundException если объявление не найдено
     */
    @Transactional
    @Override
    public byte[] updateImage(int id, MultipartFile image, Authentication authentication) throws IOException {
        AdEntity adEntity = adEntityRepository.findById(id).orElseThrow(() -> new AdEntityNotFoundException("Объявление не найдено"));
        String oldAdEntityImage = adEntity.getImage();

        String imagePath = imageService.saveImage(image);
        adEntity.setImage(imagePath);
        adEntityRepository.save(adEntity);
        imageService.deleteImage(oldAdEntityImage);

        return imageService.getImage(imagePath);
    }

    /**
     * Проверяет принадлежность объявления к аутентифицированному пользователю.
     *
     * @param username логин пользователя
     * @param id       уникальный идентификатор объявления
     * @return true если объявление не найдено или принадлежит пользователю,
     * false если объявление не принадлежит пользователю
     */
    @Transactional(readOnly = true)
    @Override
    public boolean isOwner(String username, int id) {
        Optional<AdEntity> byId = adEntityRepository.findById(id);

        if (byId.isEmpty()) {
            return true;
        }

        return byId
                .map(ad -> ad.getAuthor().getUsername().equals(username))
                .orElse(false);
    }
}
