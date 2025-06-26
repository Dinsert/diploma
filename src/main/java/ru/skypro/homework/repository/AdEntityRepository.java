package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.AdEntity;

/**
 * Репозиторий для управления сущностями объявлений в базе данных.
 * Предоставляет стандартные CRUD-операции через Spring Data JPA.
 */
@Repository
public interface AdEntityRepository extends JpaRepository<AdEntity, Integer> {
}
