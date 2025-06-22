package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.CommentEntity;

import java.util.Optional;

/**
 * Репозиторий для управления сущностями комментариев в базе данных.
 * Предоставляет стандартные CRUD-операции и кастомный запрос для поиска комментария.
 */
@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity, Integer> {

    /**
     * Находит комментарий по идентификатору объявления и идентификатору комментария.
     *
     * @param adId      идентификатор объявления
     * @param commentId идентификатор комментария
     * @return Optional с найденным комментарием или пустой Optional, если комментарий не найден
     */
    Optional<CommentEntity> findByAdEntity_PkAndPk(int adId, int commentId);
}