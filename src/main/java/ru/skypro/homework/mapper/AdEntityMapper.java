package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.entity.AdEntity;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AdEntityMapper {

    Ad toDto(AdEntity entity);

    AdEntity toEntity(Ad dto);

    void updateEntity(CreateOrUpdateAd update, @MappingTarget AdEntity entity);
}
