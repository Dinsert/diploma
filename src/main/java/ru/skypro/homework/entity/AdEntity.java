package ru.skypro.homework.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность для представления объявления в базе данных.
 * Содержит информацию об объявлении, его авторе и связанных комментариях.
 */
@ToString
@EqualsAndHashCode(of = "pk")
@Setter
@Getter
@Entity(name = "ad_entities")
public class AdEntity {

    /**
     * Уникальный идентификатор объявления, генерируемый базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", nullable = false)
    private int pk;

    /**
     * Заголовок объявления.
     */
    @Column(name = "title", nullable = false, length = 32)
    private String title;

    /**
     * Описание объявления.
     */
    @Column(name = "description", nullable = false, length = 64)
    private String description;

    /**
     * Цена объявления.
     */
    @Column(name = "price", nullable = false)
    private int price;

    /**
     * Ссылка на изображение объявления.
     */
    @Column(name = "image", nullable = false, length = 255)
    private String image;

    /**
     * Автор объявления (связь с сущностью пользователя).
     */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author", nullable = false)
    private UserEntity author;

    /**
     * Список комментариев, связанных с объявлением.
     */
    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "adEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentEntity> commentEntities;

}