package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

@Tag(name = "Комментарии")
@RequestMapping("/ads")
@RestController
public class CommentController {

    @Operation(summary = "Получение комментариев объявления", operationId = "getComments")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Comments.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("{id}/comments")
    public Comments getComments(@PathVariable int id) {
        return new Comments();
    }

    @Operation(summary = "Добавление комментария к объявлению", operationId = "addComment")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PostMapping("{id}/comments")
    public Comment addComment(@PathVariable int id) {
        return new Comment();
    }

    @Operation(summary = "Удаление комментария", operationId = "deleteComment")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @DeleteMapping("{adId}/comments/{commentId}")
    public void deleteComment(@PathVariable int adId, @PathVariable int commentId) {

    }

    @Operation(summary = "Обновление комментария", operationId = "updateComment")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PatchMapping("{adId}/comments/{commentId}")
    public Comment updateComment(@PathVariable int adId, @PathVariable int commentId, @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        return new Comment();
    }
}
