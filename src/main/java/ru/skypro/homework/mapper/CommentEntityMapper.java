package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface CommentEntityMapper {

    /**
     * Преобразует сущность комментария в DTO.
     *
     * @param entity сущность комментария
     * @return DTO комментария
     */
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.image", target = "authorImage")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    Comment toDto(CommentEntity entity);

    /**
     * Создает сущность комментария на основе данных DTO, автора и объявления.
     *
     * @param author        автор комментария
     * @param adEntity      объявление, к которому привязан комментарий
     * @param createComment данные для создания комментария
     * @return новая сущность комментария
     */
    @Mapping(target = "pk", ignore = true)
    @Mapping(source = "adEntity", target = "adEntity")
    @Mapping(source = "author", target = "author")
    CommentEntity createCommentEntity(UserEntity author, AdEntity adEntity, CreateOrUpdateComment createComment);
}