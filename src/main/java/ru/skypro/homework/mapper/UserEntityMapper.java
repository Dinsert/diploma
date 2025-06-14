package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserEntityMapper {

    User toDTO(UserEntity entity);

    UserEntity toEntity(User dto);

    void updateEntity(UpdateUser update, @MappingTarget UserEntity entity);
}
