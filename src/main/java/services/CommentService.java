package services;

import dto.CommentDto;
import models.Comment;

import java.util.UUID;

public interface CommentService {
    Comment getCommentById(UUID id);

    Comment createComment(CommentDto commentDto);

    Comment updateComment(UUID id, CommentDto commentDto);

    void deleteComment(UUID id);
}
