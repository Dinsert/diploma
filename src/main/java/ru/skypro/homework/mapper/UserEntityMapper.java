package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring")
public interface UserEntityMapper {

    /**
     * Преобразует сущность пользователя в DTO.
     *
     * @param entity сущность пользователя
     * @return DTO пользователя
     */
    @Mapping(source = "username", target = "email")
    @Mapping(target = "role", expression = "java(ru.skypro.homework.dto.Role.valueOf(entity.getAuthority().replace(\"ROLE_\",\"\")))")
    User toDTO(UserEntity entity);

    /**
     * Создает сущность пользователя на основе данных регистрации.
     *
     * @param register данные для регистрации
     * @return новая сущность пользователя
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authority", expression = "java(\"ROLE_\" + register.getRole())")
    UserEntity registerUser(Register register);

    /**
     * Обновляет существующую сущность пользователя на основе данных DTO.
     *
     * @param updateUser данные для обновления
     * @param userEntity целевая сущность для обновления
     */
    void updateUserEntity(UpdateUser updateUser, @MappingTarget UserEntity userEntity);
}