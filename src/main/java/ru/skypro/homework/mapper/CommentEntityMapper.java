package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentEntityMapper {

    Comment toDto(CommentEntity entity);

    CommentEntity toEntity(Comment dto);

    void updateEntity(CreateOrUpdateComment update, @MappingTarget CommentEntity entity);
}
