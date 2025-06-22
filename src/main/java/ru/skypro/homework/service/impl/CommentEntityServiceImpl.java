package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AdEntityNotFoundException;
import ru.skypro.homework.exception.CommentEntityNotFoundException;
import ru.skypro.homework.exception.UserEntityNotFoundException;
import ru.skypro.homework.mapper.CommentEntityMapper;
import ru.skypro.homework.repository.AdEntityRepository;
import ru.skypro.homework.repository.CommentEntityRepository;
import ru.skypro.homework.repository.UserEntityRepository;
import ru.skypro.homework.service.CommentEntityService;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления сущностями комментариев.
 * Предоставляет методы для обновления, получения, добавления и удаления комментариев.
 */
@RequiredArgsConstructor
@Service
public class CommentEntityServiceImpl implements CommentEntityService {

    /**
     * Репозиторий для работы с сущностями комментариев в базе данных.
     */
    private final CommentEntityRepository commentEntityRepository;

    /**
     * Маппер для преобразования между сущностями и DTO.
     */
    private final CommentEntityMapper commentEntityMapper;

    /**
     * Репозиторий для работы с сущностями объявлений в базе данных.
     */
    private final AdEntityRepository adEntityRepository;

    /**
     * Репозиторий для работы с сущностями пользователей в базе данных.
     */
    private final UserEntityRepository userEntityRepository;


    /**
     * Получает комментарии объявления. Доступно только аутентифицированным пользователям.
     *
     * @param id уникальный идентификатор объявления
     * @return DTO с данными комментариев
     * @throws AdEntityNotFoundException если объявление не найдено
     */
    @Transactional(readOnly = true)
    @Override
    public Comments getComments(int id) {
        AdEntity adEntity = adEntityRepository.findById(id).orElseThrow(() -> new AdEntityNotFoundException("Объявление не найдено"));
        List<Comment> results = adEntity.getCommentEntities().stream().map(commentEntityMapper::toDto).toList();

        Comments comments = new Comments();
        comments.setResults(results);
        comments.setCount(results.size());
        return comments;
    }

    /**
     * Добавляет комментарий к объявлению. Доступно только аутентифицированным пользователям.
     *
     * @param id             уникальный идентификатор объявления
     * @param createComment  данные для создания (текст комментария)
     * @param authentication объект аутентификации для получения логина пользователя
     * @return DTO с данными комментария
     * @throws UserEntityNotFoundException если пользователь не найден
     * @throws AdEntityNotFoundException   если объявление не найдено
     */
    @Transactional
    @Override
    public Comment addComment(int id, CreateOrUpdateComment createComment, Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserEntityNotFoundException("Пользователь не найден"));
        AdEntity adEntity = adEntityRepository.findById(id).orElseThrow(() -> new AdEntityNotFoundException("Объявление не найдено"));
        List<CommentEntity> commentEntities = adEntity.getCommentEntities();

        CommentEntity commentEntity = commentEntityMapper.createCommentEntity(userEntity, adEntity, createComment);
        commentEntities.add(commentEntity);
        CommentEntity savedCommentEntity = commentEntityRepository.save(commentEntity);

        return commentEntityMapper.toDto(savedCommentEntity);
    }

    /**
     * Удаляет комментарий в объявлении. Доступно только аутентифицированным пользователям.
     *
     * @param adId      уникальный идентификатор объявления
     * @param commentId уникальный идентификатор комментария
     * @throws CommentEntityNotFoundException если комментарий не найден
     */
    @Transactional
    @Override
    public void deleteComment(int adId, int commentId) {
        CommentEntity commentEntity = commentEntityRepository.findByAdEntity_PkAndPk(adId, commentId).orElseThrow(() -> new CommentEntityNotFoundException("Комментарий не найден"));

        commentEntityRepository.delete(commentEntity);
    }

    /**
     * Обновляет комментарий в объявлении. Доступно только аутентифицированным пользователям.
     *
     * @param adId          уникальный идентификатор объявления
     * @param commentId     уникальный идентификатор комментария
     * @param updateComment данные для редактирования (текст комментария)
     * @return DTO с данными комментария
     * @throws CommentEntityNotFoundException если комментарий не найден
     */
    @Transactional
    @Override
    public Comment updateComment(int adId, int commentId, CreateOrUpdateComment updateComment) {
        CommentEntity commentEntity = commentEntityRepository.findByAdEntity_PkAndPk(adId, commentId).orElseThrow(() -> new CommentEntityNotFoundException("Комментарий не найден"));

        commentEntity.setText(updateComment.getText());
        commentEntityRepository.save(commentEntity);

        return commentEntityMapper.toDto(commentEntity);
    }

    /**
     * Проверяет принадлежность комментария к аутентифицированному пользователю.
     *
     * @param username  логин пользователя
     * @param adId      уникальный идентификатор объявления
     * @param commentId уникальный идентификатор комментария
     * @return true если комментарий/объявление не найден(о) или комментарий принадлежит пользователю,
     * false если комментарий не принадлежит пользователю
     */
    @Transactional(readOnly = true)
    @Override
    public boolean isOwner(String username, int adId, int commentId) {
        Optional<CommentEntity> byAdEntityPkAndPk = commentEntityRepository.findByAdEntity_PkAndPk(adId, commentId);

        if (byAdEntityPkAndPk.isEmpty()) {
            return true;
        }

        return byAdEntityPkAndPk
                .map(comm -> comm.getAuthor().getUsername().equals(username))
                .orElse(false);
    }
}
