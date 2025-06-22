package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentEntityService;

import javax.validation.Valid;

/**
 * Контроллер для управления сущностями комментариев.
 * Предоставляет API для обновления, получения, добавления и удаления комментариев.
 */
@Tag(name = "Комментарии")
@RequestMapping("/ads")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentEntityService commentEntityService;

    /**
     * Получает комментарии объявления. Доступно только аутентифицированным пользователям.
     *
     * @param id уникальный идентификатор объявления
     * @return DTO с данными комментариев
     */
    @Operation(summary = "Получение комментариев объявления", operationId = "getComments")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Comments.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("{id}/comments")
    public Comments getComments(@PathVariable int id) {
        return commentEntityService.getComments(id);
    }

    /**
     * Добавляет комментарий к объявлению. Доступно только аутентифицированным пользователям.
     *
     * @param id             уникальный идентификатор объявления
     * @param createComment  данные для создания (текст комментария)
     * @param authentication объект аутентификации для получения логина пользователя
     * @return DTO с данными комментария
     */
    @Operation(summary = "Добавление комментария к объявлению", operationId = "addComment")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PostMapping("{id}/comments")
    public Comment addComment(@PathVariable int id, @Valid @RequestBody CreateOrUpdateComment createComment, Authentication authentication) {
        return commentEntityService.addComment(id, createComment, authentication);
    }

    /**
     * Удаляет комментарий в объявлении. Доступно только аутентифицированным пользователям,
     * которые являются авторами комментария или пользователям с ролью ADMIN.
     *
     * @param adId      уникальный идентификатор объявления
     * @param commentId уникальный идентификатор комментария
     */
    @Operation(summary = "Удаление комментария", operationId = "deleteComment")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PreAuthorize("hasRole('USER') and @commentEntityServiceImpl.isOwner(authentication.name, #adId, #commentId) or hasRole('ADMIN')")
    @DeleteMapping("{adId}/comments/{commentId}")
    public void deleteComment(@PathVariable int adId, @PathVariable int commentId, Authentication authentication) {
        commentEntityService.deleteComment(adId, commentId);
    }

    /**
     * Обновляет комментарий в объявлении. Доступно только аутентифицированным пользователям,
     * которые являются авторами комментария или пользователям с ролью ADMIN.
     *
     * @param adId          уникальный идентификатор объявления
     * @param commentId     уникальный идентификатор комментария
     * @param updateComment данные для редактирования (текст комментария)
     * @return DTO с данными комментария
     */
    @Operation(summary = "Обновление комментария", operationId = "updateComment")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PreAuthorize("hasRole('USER') and @commentEntityServiceImpl.isOwner(authentication.name, #adId, #commentId) or hasRole('ADMIN')")
    @PatchMapping("{adId}/comments/{commentId}")
    public Comment updateComment(@PathVariable int adId, @PathVariable int commentId, @Valid @RequestBody CreateOrUpdateComment updateComment, Authentication authentication) {
        return commentEntityService.updateComment(adId, commentId, updateComment);
    }
}
