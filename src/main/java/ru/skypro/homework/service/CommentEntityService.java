package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

/**
 * Интерфейс сервиса для управления комментариями.
 * Предоставляет методы для создания, получения, обновления и удаления комментариев.
 */
public interface CommentEntityService {

    /**
     * Получает список комментариев для указанного объявления.
     *
     * @param id идентификатор объявления
     * @return объект DTO с списком комментариев
     */
    Comments getComments(int id);

    /**
     * Создает новый комментарий для указанного объявления.
     *
     * @param id             идентификатор объявления
     * @param createComment  данные для создания комментария
     * @param authentication данные аутентификации автора
     * @return DTO созданного комментария
     */
    Comment addComment(int id, CreateOrUpdateComment createComment, Authentication authentication);

    /**
     * Удаляет комментарий по идентификатору объявления и комментария.
     *
     * @param adId      идентификатор объявления
     * @param commentId идентификатор комментария
     */
    void deleteComment(int adId, int commentId);

    /**
     * Обновляет существующий комментарий.
     *
     * @param adId          идентификатор объявления
     * @param commentId     идентификатор комментария
     * @param updateComment данные для обновления
     * @return обновленное DTO комментария
     */
    Comment updateComment(int adId, int commentId, CreateOrUpdateComment updateComment);

    /**
     * Проверяет, является ли указанный пользователь владельцем комментария.
     *
     * @param username  имя пользователя
     * @param adId      идентификатор объявления
     * @param commentId идентификатор комментария
     * @return true, если пользователь является владельцем, иначе false
     */
    boolean isOwner(String username, int adId, int commentId);
}