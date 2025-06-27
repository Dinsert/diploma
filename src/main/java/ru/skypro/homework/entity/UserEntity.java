package ru.skypro.homework.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Сущность для представления пользователя в базе данных.
 * Содержит данные пользователя, включая имя, пароль и связанные объявления.
 */
@ToString
@EqualsAndHashCode(of = "id")
@Setter
@Getter
@Entity(name = "user_entities")
public class UserEntity {

    /**
     * Уникальный идентификатор пользователя, генерируемый базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Логин пользователя (email).
     */
    @Column(name = "username", unique = true, length = 32)
    private String username;

    /**
     * Хешированный пароль пользователя.
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * Имя пользователя.
     */
    @Column(name = "first_name", nullable = false, length = 16)
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Column(name = "last_name", nullable = false, length = 16)
    private String lastName;

    /**
     * Телефон пользователя.
     */
    @Column(name = "phone", nullable = false, length = 16)
    private String phone;

    /**
     * Роль пользователя (например, USER или ADMIN).
     */
    @Column(name = "authority", nullable = false, length = 10)
    private String authority;

    /**
     * Ссылка на изображение (аватар) пользователя.
     */
    @Column(name = "image", nullable = true, length = 255)
    private String image;

    /**
     * Список объявлений, созданных пользователем.
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "author")
    private List<AdEntity> adEntities;
}