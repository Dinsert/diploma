package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.UserEntity;

import java.util.Optional;

/**
 * Репозиторий для управления сущностями пользователей в базе данных.
 * Предоставляет стандартные CRUD-операции и кастомный запрос для поиска пользователя.
 */
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {

    /**
     * Находит пользователя по его логину (username).
     *
     * @param username логин пользователя
     * @return Optional с найденным пользователем или пустой Optional, если пользователь не найден
     */
    Optional<UserEntity> findByUsername(String username);
}