package ru.skypro.homework.util;

import ru.skypro.homework.dto.CreateOrUpdateComment;

public class CommentFixer {

    public static final int commentId = 1;
    public static final int incorrectCommentId = 0;

    public static CreateOrUpdateComment getCreateComment() {
        CreateOrUpdateComment createComment = new CreateOrUpdateComment();
        createComment.setText("Negotiable?");
        return createComment;
    }

    public static CreateOrUpdateComment getUpdateComment() {
        CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
        updateComment.setText("updatedText");
        return updateComment;
    }
}
