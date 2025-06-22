package ru.skypro.homework.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * Сущность для представления комментария в базе данных.
 * Содержит текст, автора и связь с объявлением.
 */
@ToString
@EqualsAndHashCode(of = "pk")
@Setter
@Getter
@Entity(name = "comment_entities")
public class CommentEntity {

    /**
     * Уникальный идентификатор комментария, генерируемый базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", nullable = false)
    private int pk;

    /**
     * Текст комментария.
     */
    @Column(name = "text", nullable = false, length = 64)
    private String text;

    /**
     * Дата и время создания комментария в миллисекундах.
     */
    @Column(name = "created_at", nullable = false)
    private long createdAt;

    /**
     * Автор комментария (связь с сущностью пользователя).
     */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author", nullable = false)
    private UserEntity author;

    /**
     * Объявление, к которому относится комментарий.
     */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ad_entity", nullable = false)
    private AdEntity adEntity;

    /**
     * Устанавливает начальное значение времени создания перед сохранением.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == 0) {
            createdAt = new Date().getTime();
        }
    }
}