package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring")
public interface AdEntityMapper {

    /**
     * Преобразует сущность объявления в базовый DTO.
     *
     * @param entity сущность объявления
     * @return базовый DTO объявления
     */
    @Mapping(source = "author.id", target = "author")
    Ad toDto(AdEntity entity);

    /**
     * Преобразует сущность объявления в расширенный DTO с данными автора.
     *
     * @param adEntity сущность объявления
     * @return расширенный DTO объявления
     */
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "author.username", target = "email")
    @Mapping(source = "author.phone", target = "phone")
    ExtendedAd toExtendedAd(AdEntity adEntity);

    /**
     * Создает сущность объявления на основе данных DTO и автора.
     *
     * @param properties данные объявления
     * @param image      ссылка на изображение
     * @param author     автор объявления
     * @return новая сущность объявления
     */
    @Mapping(source = "author", target = "author")
    @Mapping(source = "image", target = "image")
    AdEntity createAdEntity(CreateOrUpdateAd properties, String image, UserEntity author);

    /**
     * Обновляет существующую сущность объявления на основе данных DTO.
     *
     * @param updateAd данные для обновления
     * @param adEntity целевая сущность для обновления
     */
    void updateAdEntity(CreateOrUpdateAd updateAd, @MappingTarget AdEntity adEntity);
}
